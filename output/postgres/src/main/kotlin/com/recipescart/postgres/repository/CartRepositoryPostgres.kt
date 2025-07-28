package com.recipescart.postgres.repository

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.model.CartItem
import com.recipescart.model.CartItemWithQuantity
import com.recipescart.model.RecipeId
import com.recipescart.postgres.adapters.CartDbAdapter
import com.recipescart.postgres.adapters.toCart
import com.recipescart.postgres.utils.isForeignKeyViolation
import com.recipescart.repository.CartRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.repository.UpsertItemInCart
import com.recipescart.repository.UpsertItemInCartResult
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional

enum class CartItemType(
    val value: String,
) {
    PRODUCT("product"),
    RECIPE("recipe"),
}

data class CartItemId(
    val cartId: CartId,
    val itemId: Int,
    val itemType: CartItemType,
)

class CartRepositoryPostgres(
    private val jdbcTemplate: JdbcTemplate,
    private val cartDbAdapter: CartDbAdapter,
    private val recipeRepository: RecipeRepository,
) : CartRepository {
    companion object {
        const val CARTS_TABLE_NAME = "carts"
        const val CART_ITEMS_TABLE_NAME = "cart_items"

        const val ID_COLUMN = "id"
        const val TOTAL_IN_CENTS_COLUMN = "total_in_cents"
        const val CREATED_AT_COLUMN = "created_at"
        const val UPDATED_AT_COLUMN = "updated_at"

        const val CART_ID_COLUMN = "cart_id"
        const val ITEM_ID_COLUMN = "item_id"
        const val ITEM_TYPE_COLUMN = "item_type"
        const val QUANTITY_COLUMN = "quantity"

        const val CART_ITEMS_CART_ID_FK = "cart_items_cart_id_fkey"
    }

    override fun getCartById(id: CartId): Cart? {
        val cartSql =
            """
            SELECT 
                $ID_COLUMN, 
                $TOTAL_IN_CENTS_COLUMN 
            FROM $CARTS_TABLE_NAME 
            WHERE $ID_COLUMN = ?
            """.trimIndent()
        val cartRow =
            try {
                jdbcTemplate.queryForMap(cartSql, id)
            } catch (_: EmptyResultDataAccessException) {
                return null
            }

        val cartItemsSql =
            """
            SELECT 
                $ITEM_ID_COLUMN, 
                $ITEM_TYPE_COLUMN,
                $QUANTITY_COLUMN
            FROM $CART_ITEMS_TABLE_NAME 
            WHERE $CART_ID_COLUMN = ?    
            """.trimIndent()
        val itemRows = jdbcTemplate.queryForList(cartItemsSql, id)

        val items: List<CartItemWithQuantity> =
            with(cartDbAdapter) {
                itemRows.toCartItemWithQuatities()
            }

        return cartRow.toCart(items)
    }

    @Transactional
    override fun upsertRecipe(input: UpsertItemInCart): UpsertItemInCartResult {
        val recipe =
            recipeRepository.getRecipeById(input.itemId)
                ?: return UpsertItemInCartResult.ItemNotFound

        val newCartItemWithQuantity =
            CartItemWithQuantity(
                item = recipe,
                quantity = input.quantity,
            )
        return upsertCartItem(
            input.cartId,
            CartItemType.RECIPE,
            newCartItemWithQuantity,
        )
    }

    @Transactional
    override fun removeRecipe(
        cartId: CartId,
        recipeId: RecipeId,
    ) {
        val cartItemId =
            CartItemId(
                cartId = cartId,
                itemId = recipeId,
                itemType = CartItemType.RECIPE,
            )
        val recipe = recipeRepository.getRecipeById(recipeId)
        removeCartItem(cartItemId = cartItemId, cartItem = recipe)
    }

    override fun newCart(): Cart {
        val sql =
            """
            INSERT INTO $CARTS_TABLE_NAME (
                $TOTAL_IN_CENTS_COLUMN, 
                $CREATED_AT_COLUMN, 
                $UPDATED_AT_COLUMN
            ) VALUES (0, now(), now())
            RETURNING $ID_COLUMN
            """.trimIndent()

        val cartId =
            jdbcTemplate.queryForObject(sql, CartId::class.java)
                ?: throw IllegalStateException("Insert failed to return an ID")

        return Cart(id = cartId)
    }

    private fun upsertCartItem(
        cartId: CartId,
        itemType: CartItemType,
        cartItemWithQuantity: CartItemWithQuantity,
    ): UpsertItemInCartResult {
        val previousRecipeCartItem =
            CartItemWithQuantity(
                item = cartItemWithQuantity.item,
                quantity =
                    getCartItemQuantity(
                        CartItemId(cartId = cartId, itemId = cartItemWithQuantity.item.id, itemType = itemType),
                    ),
            )

        val upsertCartItemResult =
            upsertCartItemOnly(
                cartId = cartId,
                itemType = CartItemType.RECIPE,
                cartItemWithQuantity = cartItemWithQuantity,
            )
        if (upsertCartItemResult != UpsertItemInCartResult.Success) return upsertCartItemResult

        updateCartTotalInCents(
            cartId = cartId,
            previousCartItemWithQuantity = previousRecipeCartItem,
            newCartItemWithQuantity = cartItemWithQuantity,
        )

        return UpsertItemInCartResult.Success
    }

    private fun getCartItemQuantity(cartItemId: CartItemId): Int {
        val sql =
            """
            SELECT
                $QUANTITY_COLUMN
            FROM $CART_ITEMS_TABLE_NAME
            WHERE $CART_ID_COLUMN = ?
                  AND $ITEM_ID_COLUMN = ?
                  AND $ITEM_TYPE_COLUMN = '${cartItemId.itemType.value}'
            """.trimIndent()

        return try {
            jdbcTemplate.queryForObject(
                sql,
                Int::class.java,
                cartItemId.cartId,
                cartItemId.itemId,
            )
        } catch (_: EmptyResultDataAccessException) {
            0
        }
    }

    private fun upsertCartItemOnly(
        cartId: CartId,
        itemType: CartItemType,
        cartItemWithQuantity: CartItemWithQuantity,
    ): UpsertItemInCartResult {
        val cartItemsSql =
            """
            INSERT INTO $CART_ITEMS_TABLE_NAME (
                $CART_ID_COLUMN, 
                $ITEM_ID_COLUMN, 
                $ITEM_TYPE_COLUMN, 
                $QUANTITY_COLUMN, 
                $CREATED_AT_COLUMN, 
                $UPDATED_AT_COLUMN
            )
            VALUES (?, ?, '${itemType.value}', ?, now(), now())
            ON CONFLICT ($CART_ID_COLUMN, $ITEM_ID_COLUMN, $ITEM_TYPE_COLUMN) DO UPDATE 
            SET $QUANTITY_COLUMN = ?,
                $UPDATED_AT_COLUMN = now()
            """.trimIndent()

        return try {
            jdbcTemplate.update(
                cartItemsSql,
                cartId,
                cartItemWithQuantity.item.id,
                cartItemWithQuantity.quantity,
                cartItemWithQuantity.quantity,
            )

            UpsertItemInCartResult.Success
        } catch (ex: DataIntegrityViolationException) {
            if (ex.isForeignKeyViolation(CART_ITEMS_CART_ID_FK)) {
                UpsertItemInCartResult.CartNotFound
            } else {
                throw ex
            }
        }
    }

    private fun updateCartTotalInCents(
        cartId: CartId,
        previousCartItemWithQuantity: CartItemWithQuantity,
        newCartItemWithQuantity: CartItemWithQuantity,
    ) {
        require(previousCartItemWithQuantity.item == newCartItemWithQuantity.item) {
            "Cart items with quantity must be of the same item :: previous = $previousCartItemWithQuantity, new = $newCartItemWithQuantity"
        }

        val cartSql =
            """
            UPDATE $CARTS_TABLE_NAME
            SET 
                $TOTAL_IN_CENTS_COLUMN = $TOTAL_IN_CENTS_COLUMN - ? + ?,
                $UPDATED_AT_COLUMN = now()
            WHERE $ID_COLUMN = ?
            """.trimIndent()
        jdbcTemplate.update(
            cartSql,
            previousCartItemWithQuantity.totalInCents(),
            newCartItemWithQuantity.totalInCents(),
            cartId,
        )
    }

    private fun removeCartItem(
        cartItemId: CartItemId,
        cartItem: CartItem?,
    ) {
        val cartItemQuantity = getCartItemQuantity(cartItemId)
        removeCartItemOnly(cartItemId)

        cartItem?.let {
            removeCartItemFromTotalInCents(
                cartId = cartItemId.cartId,
                cartItemWithQuantity = CartItemWithQuantity(it, cartItemQuantity),
            )
        }
    }

    private fun removeCartItemOnly(cartItemId: CartItemId) {
        val sql =
            """
            DELETE FROM $CART_ITEMS_TABLE_NAME 
            WHERE $CART_ID_COLUMN = ? AND $ITEM_ID_COLUMN = ? AND $ITEM_TYPE_COLUMN = '${cartItemId.itemType.value}'
            """.trimIndent()

        jdbcTemplate.update(sql, cartItemId.cartId, cartItemId.itemId)
    }

    private fun removeCartItemFromTotalInCents(
        cartId: CartId,
        cartItemWithQuantity: CartItemWithQuantity,
    ) {
        val cartSql =
            """
            UPDATE $CARTS_TABLE_NAME
            SET 
                $TOTAL_IN_CENTS_COLUMN = $TOTAL_IN_CENTS_COLUMN - ?,
                $UPDATED_AT_COLUMN = now()
            WHERE $ID_COLUMN = ?
            """.trimIndent()
        jdbcTemplate.update(
            cartSql,
            cartItemWithQuantity.totalInCents(),
            cartId,
        )
    }
}
