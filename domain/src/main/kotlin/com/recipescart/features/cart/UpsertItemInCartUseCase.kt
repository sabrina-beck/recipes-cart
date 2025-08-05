package com.recipescart.features.cart

import com.recipescart.model.CartId
import com.recipescart.model.CartItem
import com.recipescart.model.CartItemId
import com.recipescart.repository.CartRepository
import com.recipescart.repository.TransactionRepository
import com.recipescart.repository.UpsertItemInCartResult

abstract class UpsertItemInCartUseCase(
    private val transactionRepository: TransactionRepository,
    private val cartRepository: CartRepository,
) {
    protected fun upsertCartItem(
        cartId: CartId,
        cartItem: CartItem,
    ): UpsertItemInCartResult =
        transactionRepository.execute upsertItem@{
            val previousCartItem =
                cartRepository
                    .getCartItemQuantity(
                        CartItemId(cartId = cartId, itemId = cartItem.item.id, cartItemType = cartItem.type()),
                    )?.let {
                        CartItem(
                            item = cartItem.item,
                            quantity = it,
                        )
                    }

            val cart = cartRepository.getCartById(cartId) ?: return@upsertItem UpsertItemInCartResult.CartNotFound

            val upsertCartItemResult =
                cartRepository.upsertCartItem(
                    cartId = cartId,
                    cartItem = cartItem,
                )
            if (upsertCartItemResult != UpsertItemInCartResult.Success) return@upsertItem upsertCartItemResult

            val previousCartItemQuantity = previousCartItem?.totalInCents() ?: 0
            cartRepository.updateCartTotalInCents(
                cartId = cartId,
                totalInCents = cart.totalInCents - previousCartItemQuantity + cartItem.totalInCents(),
            )

            return@upsertItem UpsertItemInCartResult.Success
        }
            ?: throw Exception(
                "Unexpected error while processing upsert cart item transaction :: cartId = $cartId, cartItemType = ${cartItem.type()}, itemId = ${cartItem.item.id}",
            )
}
