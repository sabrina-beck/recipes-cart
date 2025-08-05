package com.recipescart.postgres.adapters

import com.recipescart.model.Cart
import com.recipescart.model.CartItem
import com.recipescart.model.CartItemType
import com.recipescart.model.Product
import com.recipescart.model.Recipe
import com.recipescart.postgres.repository.CartRepositoryPostgres.Companion.ID_COLUMN
import com.recipescart.postgres.repository.CartRepositoryPostgres.Companion.ITEM_ID_COLUMN
import com.recipescart.postgres.repository.CartRepositoryPostgres.Companion.ITEM_TYPE_COLUMN
import com.recipescart.postgres.repository.CartRepositoryPostgres.Companion.QUANTITY_COLUMN
import com.recipescart.postgres.repository.CartRepositoryPostgres.Companion.TOTAL_IN_CENTS_COLUMN
import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository

fun Map<String, Any>.toCart(items: List<CartItem>): Cart {
    val cart =
        Cart(
            id = this[ID_COLUMN] as Int,
            items = items,
        )

    val totalInCentsFromDb = this[TOTAL_IN_CENTS_COLUMN] as Int
    require(cart.totalInCents == totalInCentsFromDb) {
        "Mismatched total in cents: DB says $totalInCentsFromDb, computed ${cart.totalInCents}"
    }

    return cart
}

fun CartItemType.toValue() =
    when (this) {
        CartItemType.RECIPE -> "recipe"
        CartItemType.PRODUCT -> "product"
    }

class CartDbAdapter(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
) {
    fun List<Map<String, Any>>.toCartItemWithQuatities(): List<CartItem> {
        val recipesById =
            this
                .toRecipes()
                .associateBy { it.id }

        val productsById =
            this
                .toProducts()
                .associateBy { it.id }

        return this.mapNotNull { row ->
            val itemId = row[ITEM_ID_COLUMN] as Int
            when (row[ITEM_TYPE_COLUMN]) {
                CartItemType.RECIPE.toValue() -> recipesById[itemId]
                CartItemType.PRODUCT.toValue() -> productsById[itemId]
                else -> null
            }?.let {
                CartItem(item = it, quantity = row[QUANTITY_COLUMN] as Int)
            }
        }
    }

    fun List<Map<String, Any>>.toRecipes(): List<Recipe> =
        this
            .filter { it[ITEM_TYPE_COLUMN] == CartItemType.RECIPE.toValue() }
            .map { it[ITEM_ID_COLUMN] as Int }
            .let { recipeRepository.getRecipesByIds(it) }

    fun List<Map<String, Any>>.toProducts(): List<Product> =
        this
            .filter { it[ITEM_TYPE_COLUMN] == CartItemType.PRODUCT.toValue() }
            .map { it[ITEM_ID_COLUMN] as Int }
            .let { productRepository.getProductsByIds(it) }
}
