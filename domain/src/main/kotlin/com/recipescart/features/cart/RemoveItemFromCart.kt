package com.recipescart.features.cart

import com.recipescart.model.CartItem
import com.recipescart.model.CartItemId
import com.recipescart.model.Item
import com.recipescart.repository.CartRepository
import com.recipescart.repository.TransactionRepository

abstract class RemoveItemFromCart(
    private val transactionRepository: TransactionRepository,
    private val cartRepository: CartRepository,
) {
    protected fun removeCartItem(
        cartItemId: CartItemId,
        item: Item,
    ) {
        transactionRepository.execute {
            val cartItemQuantity = cartRepository.getCartItemQuantity(cartItemId)
            val cart = cartRepository.getCartById(cartItemId.cartId)

            if (cartItemQuantity != null && cart != null) {
                cartRepository.removeCartItem(cartItemId)

                val cartItem =
                    CartItem(
                        item = item,
                        quantity = cartItemQuantity,
                    )
                cartRepository.updateCartTotalInCents(
                    cartId = cartItemId.cartId,
                    totalInCents = cart.totalInCents - cartItem.totalInCents(),
                )
            }
        }
    }
}
