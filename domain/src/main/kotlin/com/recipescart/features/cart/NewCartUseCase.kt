package com.recipescart.features.cart

import com.recipescart.model.Cart
import com.recipescart.repository.CartRepository

class NewCartUseCase(
    private val cartRepository: CartRepository
) {
    fun execute(): Cart = cartRepository.newCart()
}