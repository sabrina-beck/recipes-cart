package com.recipescart.model

typealias RecipeId = Int

data class Recipe(
    override val id: RecipeId,
    val name: String,
    val ingredients: List<Ingredient>,
) : CartItem {
    override fun priceInCents(): Int = ingredients.sumOf { it.quantity * it.product.priceInCents }
}

data class Ingredient(
    val product: Product,
    val quantity: Int,
)
