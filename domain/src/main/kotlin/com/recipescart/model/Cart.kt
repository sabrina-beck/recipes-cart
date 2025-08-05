package com.recipescart.model

typealias CartId = Int

data class Cart(
    val id: CartId,
    val items: List<CartItem> = emptyList(),
) {
    val totalInCents: Int = items.sumOf { it.item.priceInCents() * it.quantity }
}

data class CartItem(
    val item: Item,
    val quantity: Int,
) {
    init {
        require(quantity > 0) { "Cart item quantity must be greater than 0" }
    }

    fun totalInCents(): Int = this.item.priceInCents() * this.quantity

    fun type(): CartItemType =
        when (this.item) {
            is Recipe -> CartItemType.RECIPE
            is Product -> CartItemType.PRODUCT
        }
}

enum class CartItemType {
    PRODUCT,
    RECIPE,
}

data class CartItemId(
    val cartId: CartId,
    val itemId: Int,
    val cartItemType: CartItemType,
)
