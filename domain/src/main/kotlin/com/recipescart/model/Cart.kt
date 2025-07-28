package com.recipescart.model

typealias CartId = Int

data class Cart(
    val id: CartId,
    val items: List<CartItemWithQuantity> = emptyList(),
) {
    val totalInCents: Int = items.sumOf { it.item.priceInCents() * it.quantity }
}

data class CartItemWithQuantity(
    val item: CartItem,
    val quantity: Int,
) {
    fun totalInCents(): Int = this.item.priceInCents() * this.quantity
}

interface CartItem {
    val id: Int

    fun priceInCents(): Int
}
