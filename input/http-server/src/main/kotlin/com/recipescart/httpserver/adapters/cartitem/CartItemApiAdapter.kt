package com.recipescart.httpserver.adapters.cartitem

import com.recipescart.httpserver.api.cartitem.IngredientApi
import com.recipescart.httpserver.api.cartitem.ItemApi
import com.recipescart.httpserver.api.cartitem.ProductApi
import com.recipescart.httpserver.api.cartitem.RecipeApi
import com.recipescart.model.Ingredient
import com.recipescart.model.Item
import com.recipescart.model.Product
import com.recipescart.model.Recipe

fun Item.toApi(): ItemApi =
    when (this) {
        is Product -> this.toApi()
        is Recipe -> this.toApi()
    }

fun Recipe.toApi(): RecipeApi =
    RecipeApi(
        id = id,
        name = name,
        ingredients = ingredients.map { it.toApi() },
    )

fun Ingredient.toApi(): IngredientApi =
    IngredientApi(
        product = product.toApi(),
        quantity = quantity,
    )

fun Product.toApi(): ProductApi =
    ProductApi(
        id = id,
        name = name,
        priceInCents = priceInCents,
    )
