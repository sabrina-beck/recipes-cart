package com.recipescart.model

typealias RecipeId = Int

data class Recipe(
    val id: RecipeId,
    val name: String,
    val ingredients: List<Product>,
) : CartItem
