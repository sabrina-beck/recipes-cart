package com.recipescart.features.cart

import com.recipescart.repository.CartRepository
import com.recipescart.repository.UpsertItemInCart
import com.recipescart.repository.UpsertItemInCartResult

class UpsertRecipeInCartUseCase(
    val cartRepository: CartRepository,
) {
    fun execute(input: UpsertItemInCart): UpsertItemInCartResult = cartRepository.upsertRecipe(input)
}
