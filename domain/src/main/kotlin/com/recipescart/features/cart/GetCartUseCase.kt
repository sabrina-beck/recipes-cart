package com.recipescart.features.cart

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.repository.CartRepository

class GetCartUseCase(
    val cartRepository: CartRepository,
) {
    fun execute(cartId: CartId): Cart? = cartRepository.getCartById(cartId)
}
