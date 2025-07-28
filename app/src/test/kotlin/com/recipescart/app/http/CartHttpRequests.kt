package com.recipescart.app.http

import com.recipescart.httpserver.api.cart.CartApi
import com.recipescart.httpserver.errors.ErrorResponse
import com.recipescart.model.CartId
import com.recipescart.model.RecipeId
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

fun TestRestTemplate.newCart(port: Int): ResponseEntity<CartApi> =
    this.postForEntity("http://localhost:$port/carts", null, CartApi::class.java)

fun TestRestTemplate.getCart(
    port: Int,
    cartId: CartId,
): ResponseEntity<CartApi> = this.getForEntity("http://localhost:$port/carts/$cartId", CartApi::class.java)

fun TestRestTemplate.postRecipeToCart(
    port: Int,
    cartId: CartId,
    body: Any,
): ResponseEntity<Unit> = this.postForEntity("http://localhost:$port/carts/$cartId/add_recipe", body, Unit::class.java)

fun TestRestTemplate.postRecipeToCartExpectingError(
    port: Int,
    cartId: CartId,
    body: Any,
): ResponseEntity<ErrorResponse> = this.postForEntity("http://localhost:$port/carts/$cartId/add_recipe", body, ErrorResponse::class.java)

fun TestRestTemplate.deleteRecipeFromCart(
    port: Int,
    cartId: CartId,
    recipeId: RecipeId,
): ResponseEntity<Unit> =
    restTemplate.exchange(
        "http://localhost:$port/carts/$cartId/recipes/$recipeId",
        HttpMethod.DELETE,
        null,
        Unit::class.java,
    )
