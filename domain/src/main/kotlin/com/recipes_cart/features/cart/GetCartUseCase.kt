package com.recipes_cart.features.cart

import com.recipes_cart.model.Cart
import com.recipes_cart.model.CartId
import com.recipes_cart.repository.CartRepository

class GetCartUseCase(val cartRepository: CartRepository) {

    fun execute(cartId: CartId): Cart = cartRepository.get_cart_by_id(cartId);

}