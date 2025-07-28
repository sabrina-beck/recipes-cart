package com.recipescart.httpserver.api.cartitem

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RecipeApi::class, name = "recipe"),
    JsonSubTypes.Type(value = ProductApi::class, name = "product"),
)
sealed interface ItemApi {
    val id: Int
}

data class ProductApi(
    override val id: Int,
    val name: String,
    val priceInCents: Int,
) : ItemApi

data class RecipeApi(
    override val id: Int,
    val name: String,
    val ingredients: List<IngredientApi>,
) : ItemApi

data class IngredientApi(
    val product: ProductApi,
    val quantity: Int,
)
