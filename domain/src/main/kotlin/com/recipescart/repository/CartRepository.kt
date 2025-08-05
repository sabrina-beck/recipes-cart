package com.recipescart.repository

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.model.CartItem
import com.recipescart.model.CartItemId
import com.recipescart.model.RecipeId

sealed interface UpsertItemInCartResult {
    object Success : UpsertItemInCartResult

    object ItemNotFound : UpsertItemInCartResult

    object CartNotFound : UpsertItemInCartResult
}

data class UpsertItemInCart(
    val cartId: CartId,
    val itemId: RecipeId,
    val quantity: Int,
)

interface CartRepository {
    fun getCartById(id: CartId): Cart?

    fun newCart(): Cart

    fun getCartItemQuantity(cartItemId: CartItemId): Int?

    fun upsertCartItem(
        cartId: CartId,
        cartItem: CartItem,
    ): UpsertItemInCartResult

    fun updateCartTotalInCents(
        cartId: CartId,
        totalInCents: Int,
    )

    fun removeCartItem(cartItemId: CartItemId)
}
