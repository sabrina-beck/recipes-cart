package com.recipes_cart.features.cart

import com.recipes_cart.model.CartId
import com.recipes_cart.model.RecipeId
import com.recipes_cart.repository.CartRepository

class RemoveRecipeUseCase(
    val cartRepository: CartRepository,
) {
    fun execute(
        cartId: CartId,
        recipeId: RecipeId,
    ) {
        cartRepository.removeRecipe(cartId, recipeId)
    }
}
