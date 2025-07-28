package com.recipescart.app.tests

import com.recipescart.app.AbstractIntegrationTest
import com.recipescart.app.configuration.SeedTestConfig
import com.recipescart.app.http.deleteRecipeFromCart
import com.recipescart.app.http.getCart
import com.recipescart.app.http.newCart
import com.recipescart.app.http.postRecipeToCart
import com.recipescart.app.http.postRecipeToCartExpectingError
import com.recipescart.httpserver.adapters.cart.toApi
import com.recipescart.httpserver.adapters.cartitem.toApi
import com.recipescart.httpserver.api.cart.CartApi
import com.recipescart.httpserver.api.cart.UpsertRecipeInCartApi
import com.recipescart.httpserver.api.cartitem.RecipeApi
import com.recipescart.httpserver.errors.Error
import com.recipescart.httpserver.errors.ErrorResponse
import com.recipescart.model.Cart
import com.recipescart.model.RecipeId
import com.recipescart.seed.RecipesSeed
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@Import(SeedTestConfig::class)
class CartIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    lateinit var recipesSeed: RecipesSeed

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should create empty cart`() {
        val cart = newCart()

        val emptyCart = Cart(cart.id)
        assertEquals(emptyCart.toApi(), cart)
    }

    @Test
    fun `should insert and remove recipe in cart successfully`() {
        val recipe = recipesSeed.givenExistentRecipe(10)

        val cart = newCart()

        cart.successfullyAddRecipe(recipe.id, quantity = 2)

        val updatedCart = cart.successfullyGet()
        assertEquals(1, updatedCart.items.size)

        val cartItem = updatedCart.items.first()
        assertEquals(recipe.id, cartItem.item.id)
        assertEquals(2, cartItem.quantity)

        val item = cartItem.item
        assertIs<RecipeApi>(item)
        assertEquals(recipe.toApi(), item)

        cart.removeRecipe(recipe.id)

        val emptyCart = cart.successfullyGet()
        assertEquals(0, emptyCart.items.size)
    }

    @Test
    fun `should add multiple different recipes to cart`() {
        val cart = newCart()
        val recipes = recipesSeed.givenExistentRecipes()

        recipes.forEachIndexed { i, recipe ->
            cart.successfullyAddRecipe(recipe.id, quantity = i + 1)
        }

        val updatedCart = cart.successfullyGet()
        assertEquals(recipes.size, updatedCart.items.size)
    }

    @Test
    fun `should update quantity when adding the same recipe again`() {
        val cart = newCart()
        val recipe = recipesSeed.givenExistentRecipe(1)

        cart.successfullyAddRecipe(recipe.id, quantity = 2)
        cart.successfullyAddRecipe(recipe.id, quantity = 5)

        val updatedCart = cart.successfullyGet()
        val item = updatedCart.items.first { it.item.id == recipe.id }
        assertEquals(5, item.quantity)
    }

    @Test
    fun `should return not found for non-existent cart`() {
        val cart = Cart(99999).toApi()
        val response = cart.get()
        assertEquals(NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should return bad request when recipe does not exist`() {
        val cart = newCart()

        val recipeId = 99999
        val response = cart.addRecipeExpectingError(recipeId, quantity = 1)
        assertEquals(BAD_REQUEST, response.statusCode)
        assertEquals(
            response.body,
            ErrorResponse(
                error = Error.RECIPE_NOT_FOUND,
                message = Error.RECIPE_NOT_FOUND.message!!,
            ),
        )
    }

    @Test
    fun `should return not found when adding recipe to non-existent cart`() {
        val cart = Cart(99999).toApi()

        val recipe = recipesSeed.givenExistentRecipe(1)
        val response = cart.addRecipeExpectingError(recipe.id, quantity = 1)
        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals(
            response.body,
            ErrorResponse(
                error = Error.CART_NOT_FOUND,
                message = Error.CART_NOT_FOUND.message!!,
            ),
        )
    }

    @Test
    fun `delete on missing item or cart should succeed`() {
        val cart = Cart(99999).toApi()
        val recipeId = 99999

        val response = cart.removeRecipe(recipeId)
        assertEquals(NO_CONTENT, response.statusCode)
    }

    @Test
    fun `should return bad request for zero or negative quantity`() {
        val cart = newCart()
        val recipe = recipesSeed.givenExistentRecipe(1)

        listOf(0, -1).forEach {
            val response = cart.addRecipeExpectingError(recipe.id, quantity = it)
            assertEquals(BAD_REQUEST, response.statusCode)
            assertEquals(
                response.body,
                ErrorResponse(
                    error = Error.ILLEGAL_ARGUMENT,
                    message = "Cart item quantity must be greater than 0",
                ),
            )
        }
    }

    @Test
    fun `should return bad request for null parameters`() {
        val cart = newCart()
        val body = mapOf("recipeId" to null, "quantity" to null)
        val response = restTemplate.postRecipeToCart(port, cart.id, body)
        assertEquals(BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should allow idempotent add of same recipe with same quantity`() {
        val cart = newCart()
        val recipe = recipesSeed.givenExistentRecipe(1)

        repeat(3) {
            cart.successfullyAddRecipe(recipe.id, quantity = 2)
        }

        val updatedCart = cart.successfullyGet()
        assertEquals(1, updatedCart.items.size)
        assertEquals(2, updatedCart.items.first().quantity)
    }

    private fun newCart(): CartApi {
        val cart = restTemplate.newCart(port).body
        assertNotNull(cart)

        return cart
    }

    private fun CartApi.addRecipeExpectingError(
        recipeId: RecipeId,
        quantity: Int,
    ): ResponseEntity<ErrorResponse> {
        val addRequest = UpsertRecipeInCartApi(recipeId = recipeId, quantity = quantity)
        return restTemplate.postRecipeToCartExpectingError(port, this.id, addRequest)
    }

    private fun CartApi.successfullyAddRecipe(
        recipeId: RecipeId,
        quantity: Int,
    ) {
        val addRequest = UpsertRecipeInCartApi(recipeId = recipeId, quantity = quantity)
        val postResponse = restTemplate.postRecipeToCart(port, this.id, addRequest)
        assertEquals(NO_CONTENT, postResponse.statusCode)
    }

    private fun CartApi.removeRecipe(recipeId: RecipeId): ResponseEntity<Unit> =
        assertDoesNotThrow {
            restTemplate.deleteRecipeFromCart(port, this.id, recipeId)
        }

    private fun CartApi.get(): ResponseEntity<CartApi> = restTemplate.getCart(port, this.id)

    private fun CartApi.successfullyGet(): CartApi {
        val getResponse: ResponseEntity<CartApi> = this.get()
        assertEquals(OK, getResponse.statusCode)

        val cartApi = getResponse.body
        assertNotNull(cartApi)
        assertEquals(this.id, cartApi.id)

        return cartApi
    }
}
