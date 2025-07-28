package com.recipescart.repository

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.model.RecipeId

sealed interface UpsertItemInCartResult {
    object Success : UpsertItemInCartResult

    object ItemNotFound : UpsertItemInCartResult

    object CartNotFound : UpsertItemInCartResult
}

data class UpsertItemInCart(
    val cartId: CartId,
    val itemId: RecipeId,
    val quantity: Int,
)

interface CartRepository {
    fun getCartById(id: CartId): Cart?

    fun upsertRecipe(input: UpsertItemInCart): UpsertItemInCartResult

    fun removeRecipe(
        cartId: CartId,
        recipeId: RecipeId,
    )

    fun newCart(): Cart
}
