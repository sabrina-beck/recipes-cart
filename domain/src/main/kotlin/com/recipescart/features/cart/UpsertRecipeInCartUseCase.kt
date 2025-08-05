package com.recipescart.features.cart

import com.recipescart.model.CartItem
import com.recipescart.repository.CartRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.repository.TransactionRepository
import com.recipescart.repository.UpsertItemInCart
import com.recipescart.repository.UpsertItemInCartResult

class UpsertRecipeInCartUseCase(
    transactionRepository: TransactionRepository,
    private val recipeRepository: RecipeRepository,
    cartRepository: CartRepository,
) : UpsertItemInCartUseCase(transactionRepository, cartRepository) {
    fun execute(input: UpsertItemInCart): UpsertItemInCartResult {
        val recipe =
            recipeRepository.getRecipeById(input.itemId)
                ?: return UpsertItemInCartResult.ItemNotFound

        val newCartItem =
            CartItem(
                item = recipe,
                quantity = input.quantity,
            )
        return super.upsertCartItem(
            cartId = input.cartId,
            cartItem = newCartItem,
        )
    }
}
