package com.recipescart.repository

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.model.RecipeId

interface CartRepository {
    fun getCartById(id: CartId): Cart

    sealed interface UpsertRecipeResult {
        object Success : UpsertRecipeResult

        object RecipeNotFound : UpsertRecipeResult

        object CartNotFound : UpsertRecipeResult
    }

    fun upsertRecipe(
        id: CartId,
        recipe: RecipeId,
    ): UpsertRecipeResult

    fun removeRecipe(
        id: CartId,
        recipe: RecipeId,
    )
}
