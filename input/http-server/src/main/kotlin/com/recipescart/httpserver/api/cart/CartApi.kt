package com.recipescart.httpserver.api.cart

import com.recipescart.httpserver.api.cartitem.CartItemApi

data class CartApi(
    val id: Int,
    val totalInCents: Int,
    val items: List<CartItemWithQuantityApi>,
)

data class CartItemWithQuantityApi(
    val item: CartItemApi,
    val quantity: Int,
)
