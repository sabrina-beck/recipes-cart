package com.recipescart.httpserver.api.cart

data class UpsertRecipeInCartApi(
    val recipeId: Int,
    val quantity: Int,
)
