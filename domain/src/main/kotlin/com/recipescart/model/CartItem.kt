package com.recipescart.model

sealed interface CartItem {
    val id: Int

    fun priceInCents(): Int
}

typealias ProductId = Int

data class Product(
    override val id: ProductId,
    val name: String,
    val priceInCents: Int,
) : CartItem {
    override fun priceInCents(): Int = this.priceInCents
}

typealias RecipeId = Int

data class Recipe(
    override val id: RecipeId,
    val name: String,
    val ingredients: List<Ingredient>,
) : CartItem {
    init {
        require(ingredients.isNotEmpty()) { "Recipe must have at least one ingredient" }
    }

    override fun priceInCents(): Int = ingredients.sumOf { it.quantity * it.product.priceInCents }
}

data class Ingredient(
    val product: Product,
    val quantity: Int,
) {
    init {
        require(quantity > 0) { "Ingredient quantity must be greater than 0" }
    }
}
