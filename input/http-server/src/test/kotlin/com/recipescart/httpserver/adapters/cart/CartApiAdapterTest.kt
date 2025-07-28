package com.recipescart.httpserver.adapters.cart

import com.recipescart.httpserver.api.cartitem.ProductApi
import com.recipescart.httpserver.api.cartitem.RecipeApi
import com.recipescart.model.Cart
import com.recipescart.model.CartItem
import com.recipescart.model.Ingredient
import com.recipescart.model.Product
import com.recipescart.model.Recipe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CartApiAdapterTest {
    @Test
    fun `toApi should map empty cart correctly`() {
        val cart = Cart(id = 42, items = emptyList())
        val api = cart.toApi()

        assertEquals(42, api.id)
        assertEquals(0, api.totalInCents)
        assertEquals(emptyList(), api.items)
    }

    @Test
    fun `toApi should map Cart with Product and Recipe items correctly`() {
        val product = Product(id = 1, name = "Salt", priceInCents = 100)

        val recipe =
            Recipe(
                id = 2,
                name = "Cake",
                ingredients = listOf(Ingredient(product = product, quantity = 2)),
            )

        val cart =
            Cart(
                id = 99,
                items =
                    listOf(
                        CartItem(item = product, quantity = 3),
                        CartItem(item = recipe, quantity = 1),
                    ),
            )

        val api = cart.toApi()

        assertEquals(99, api.id)
        assertEquals(500, api.totalInCents)

        assertEquals(2, api.items.size)

        val productItem = api.items[0]
        assertEquals(3, productItem.quantity)
        val productApi = assertIs<ProductApi>(productItem.item)
        assertEquals("Salt", productApi.name)
        assertEquals(100, productApi.priceInCents)

        val recipeItem = api.items[1]
        assertEquals(1, recipeItem.quantity)
        val recipeApi = assertIs<RecipeApi>(recipeItem.item)
        assertEquals("Cake", recipeApi.name)
        assertEquals(1, recipeApi.ingredients.size)
        val ingredientApi = recipeApi.ingredients[0]
        assertEquals(2, ingredientApi.quantity)
        val ingredientProductApi = ingredientApi.product
        assertEquals("Salt", ingredientProductApi.name)
        assertEquals(100, ingredientProductApi.priceInCents)
    }
}
