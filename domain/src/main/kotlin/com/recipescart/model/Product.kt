package com.recipescart.model

typealias ProductId = Int

data class Product(
    override val id: ProductId,
    val name: String,
    val priceInCents: Int,
) : CartItem {
    override fun priceInCents(): Int = this.priceInCents
}
