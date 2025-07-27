package com.recipescart.model

typealias CartId = Int

data class Cart(
    val id: CartId,
    val totalInCents: Int,
    val items: List<CartItem>,
)

interface CartItem
