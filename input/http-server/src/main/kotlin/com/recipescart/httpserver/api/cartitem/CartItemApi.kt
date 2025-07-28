package com.recipescart.httpserver.api.cartitem

sealed interface CartItemApi {
    val id: Int
}

data class ProductApi(
    override val id: Int,
    val name: String,
    val priceInCents: Int,
) : CartItemApi

data class RecipeApi(
    override val id: Int,
    val name: String,
    val ingredients: List<IngredientApi>,
) : CartItemApi

data class IngredientApi(
    val product: ProductApi,
    val quantity: Int,
)
