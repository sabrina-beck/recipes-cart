package com.recipescart.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CartTest {
    @Test
    fun `Cart with no items should have totalInCents as 0`() {
        val cart = Cart(id = 1, items = emptyList())

        assertEquals(0, cart.totalInCents)
    }

    @Test
    fun `Cart with one item should calculate totalInCents correctly`() {
        val item = Product(id = 1, name = "Flour", priceInCents = 200)
        val cart =
            Cart(
                id = 1,
                items = listOf(CartItemWithQuantity(item, quantity = 3)),
            )

        assertEquals(600, cart.totalInCents)
    }

    @Test
    fun `Cart with multiple items should calculate totalInCents correctly`() {
        val item1 = Product(id = 1, name = "Flour", priceInCents = 100)
        val item2 = Product(id = 2, name = "Milk", priceInCents = 250)

        val items =
            listOf(
                CartItemWithQuantity(item1, quantity = 2),
                CartItemWithQuantity(item2, quantity = 1),
            )

        val cart = Cart(id = 1, items = items)

        assertEquals(450, cart.totalInCents)
    }

    @Test
    fun `CartItemWithQuantity should calculate totalInCents correctly`() {
        val item = Product(id = 1, name = "Flour", priceInCents = 90)
        val wrapper = CartItemWithQuantity(item, quantity = 4)

        assertEquals(360, wrapper.totalInCents())
    }
}
