package com.recipescart.features.cart

import com.recipescart.model.CartId
import com.recipescart.model.RecipeId
import com.recipescart.repository.CartRepository

class RemoveRecipeFromCartUseCase(
    val cartRepository: CartRepository,
) {
    fun execute(
        cartId: CartId,
        recipeId: RecipeId,
    ) {
        cartRepository.removeRecipe(cartId, recipeId)
    }
}
