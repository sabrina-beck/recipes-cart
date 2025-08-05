package com.recipescart.features.cart

import com.recipescart.model.CartId
import com.recipescart.model.CartItemId
import com.recipescart.model.CartItemType
import com.recipescart.model.RecipeId
import com.recipescart.repository.CartRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.repository.TransactionRepository

class RemoveRecipeFromCartUseCase(
    transactionRepository: TransactionRepository,
    private val recipeRepository: RecipeRepository,
    cartRepository: CartRepository,
) : RemoveItemFromCart(transactionRepository, cartRepository) {
    fun execute(
        cartId: CartId,
        recipeId: RecipeId,
    ) {
        val cartItemId =
            CartItemId(
                cartId = cartId,
                itemId = recipeId,
                cartItemType = CartItemType.RECIPE,
            )
        val recipe = recipeRepository.getRecipeById(recipeId) ?: return
        super.removeCartItem(
            cartItemId = cartItemId,
            item = recipe,
        )
    }
}
