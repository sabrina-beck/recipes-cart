package com.recipescart.model

typealias RecipeId = Int

data class Recipe(
    val id: RecipeId,
    val name: String,
    val ingredients: List<Ingredient>,
) : CartItem

data class Ingredient(
    val product: Product,
    val quantity: Int,
)
