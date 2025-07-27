package com.recipescart.model

typealias ProductId = Int

data class Product(
    val id: ProductId,
    val name: String,
    val priceInCents: Int,
) : CartItem
