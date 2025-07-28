package com.recipescart.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RecipeTest {
    @Test
    fun `creating ingredient with quantity 0 should throw`() {
        val product = Product(id = 1, name = "Salt", priceInCents = 20)

        val exception =
            assertFailsWith<IllegalArgumentException> {
                Ingredient(product = product, quantity = 0)
            }

        assertEquals("Ingredient quantity must be greater than 0", exception.message)
    }

    @Test
    fun `creating recipe with no ingredients should throw`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Recipe(
                    id = 1,
                    name = "Empty Recipe",
                    ingredients = emptyList(),
                )
            }

        assertEquals("Recipe must have at least one ingredient", exception.message)
    }

    @Test
    fun `priceInCents should calculate correct total for one ingredient`() {
        val product = Product(id = 1, name = "Flour", priceInCents = 100)
        val ingredient = Ingredient(product, quantity = 2)

        val recipe =
            Recipe(
                id = 1,
                name = "Flour Mix",
                ingredients = listOf(ingredient),
            )

        assertEquals(200, recipe.priceInCents())
    }

    @Test
    fun `priceInCents should calculate correct total for multiple ingredients`() {
        val flour = Product(id = 1, name = "Flour", priceInCents = 100)
        val sugar = Product(id = 2, name = "Sugar", priceInCents = 50)
        val eggs = Product(id = 3, name = "Egg", priceInCents = 30)

        val ingredients =
            listOf(
                Ingredient(flour, quantity = 3),
                Ingredient(sugar, quantity = 2),
                Ingredient(eggs, quantity = 4),
            )

        val recipe =
            Recipe(
                id = 1,
                name = "Cake",
                ingredients = ingredients,
            )

        assertEquals(520, recipe.priceInCents())
    }
}
