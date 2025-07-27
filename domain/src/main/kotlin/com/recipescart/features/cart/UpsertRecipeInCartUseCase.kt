package com.recipescart.features.cart

import com.recipescart.adapters.cart.toAddRecipeUseCaseResult
import com.recipescart.model.CartId
import com.recipescart.model.RecipeId
import com.recipescart.repository.CartRepository

sealed class UpsertRecipeInCartUseCaseResult {
    object Success : UpsertRecipeInCartUseCaseResult()

    object RecipeInCartNotFound : UpsertRecipeInCartUseCaseResult()

    object CartNotFound : UpsertRecipeInCartUseCaseResult()
}

class UpsertRecipeInCartUseCase(
    val cartRepository: CartRepository,
) {
    fun execute(
        cartId: CartId,
        recipeId: RecipeId,
    ): UpsertRecipeInCartUseCaseResult = cartRepository.upsertRecipe(cartId, recipeId).toAddRecipeUseCaseResult()
}
