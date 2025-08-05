package com.recipescart.postgres.repository

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.model.CartItem
import com.recipescart.model.CartItemId
import com.recipescart.postgres.adapters.CartDbAdapter
import com.recipescart.postgres.adapters.toCart
import com.recipescart.postgres.adapters.toValue
import com.recipescart.postgres.utils.isForeignKeyViolation
import com.recipescart.repository.CartRepository
import com.recipescart.repository.UpsertItemInCartResult
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate

class CartRepositoryPostgres(
    private val jdbcTemplate: JdbcTemplate,
    private val cartDbAdapter: CartDbAdapter,
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

        val items: List<CartItem> =
            with(cartDbAdapter) {
                itemRows.toCartItemWithQuatities()
            }

        return cartRow.toCart(items)
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

    override fun getCartItemQuantity(cartItemId: CartItemId): Int? {
        val sql =
            """
            SELECT
                $QUANTITY_COLUMN
            FROM $CART_ITEMS_TABLE_NAME
            WHERE $CART_ID_COLUMN = ?
                  AND $ITEM_ID_COLUMN = ?
                  AND $ITEM_TYPE_COLUMN = '${cartItemId.cartItemType.toValue()}'
            """.trimIndent()

        return try {
            jdbcTemplate.queryForObject(
                sql,
                Int::class.java,
                cartItemId.cartId,
                cartItemId.itemId,
            )
        } catch (_: EmptyResultDataAccessException) {
            null
        }
    }

    override fun upsertCartItem(
        cartId: CartId,
        cartItem: CartItem,
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
            VALUES (?, ?, '${cartItem.type().toValue()}', ?, now(), now())
            ON CONFLICT ($CART_ID_COLUMN, $ITEM_ID_COLUMN, $ITEM_TYPE_COLUMN) DO UPDATE 
            SET $QUANTITY_COLUMN = ?,
                $UPDATED_AT_COLUMN = now()
            """.trimIndent()

        return try {
            jdbcTemplate.update(
                cartItemsSql,
                cartId,
                cartItem.item.id,
                cartItem.quantity,
                cartItem.quantity,
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

    override fun updateCartTotalInCents(
        cartId: CartId,
        totalInCents: Int,
    ) {
        val cartSql =
            """
            UPDATE $CARTS_TABLE_NAME
            SET 
                $TOTAL_IN_CENTS_COLUMN = ?,
                $UPDATED_AT_COLUMN = now()
            WHERE $ID_COLUMN = ?
            """.trimIndent()
        jdbcTemplate.update(
            cartSql,
            totalInCents,
            cartId,
        )
    }

    override fun removeCartItem(cartItemId: CartItemId) {
        val sql =
            """
            DELETE FROM $CART_ITEMS_TABLE_NAME 
            WHERE $CART_ID_COLUMN = ? AND $ITEM_ID_COLUMN = ? AND $ITEM_TYPE_COLUMN = '${cartItemId.cartItemType.toValue()}'
            """.trimIndent()

        jdbcTemplate.update(sql, cartItemId.cartId, cartItemId.itemId)
    }
}
