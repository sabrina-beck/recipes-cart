package com.recipescart.httpserver.api.cart

import com.recipescart.httpserver.api.cartitem.ItemApi

data class CartApi(
    val id: Int,
    val totalInCents: Int,
    val items: List<CartItemApi>,
)

data class CartItemApi(
    val item: ItemApi,
    val quantity: Int,
)
