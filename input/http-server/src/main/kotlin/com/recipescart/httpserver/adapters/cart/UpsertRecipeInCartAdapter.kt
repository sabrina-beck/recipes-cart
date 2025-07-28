package com.recipescart.httpserver.adapters.cart

import com.recipescart.httpserver.api.cart.UpsertRecipeInCartApi
import com.recipescart.repository.UpsertItemInCart

fun UpsertRecipeInCartApi.toDomain(cartId: Int): UpsertItemInCart =
    UpsertItemInCart(
        cartId = cartId,
        itemId = this.recipeId,
        quantity = this.quantity,
    )
