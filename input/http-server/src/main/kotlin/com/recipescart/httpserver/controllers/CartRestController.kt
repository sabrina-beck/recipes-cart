package com.recipescart.httpserver.controllers

import com.recipescart.features.cart.GetCartUseCase
import com.recipescart.features.cart.RemoveRecipeFromCartUseCase
import com.recipescart.features.cart.UpsertRecipeInCartUseCase
import com.recipescart.httpserver.adapters.cart.toApi
import com.recipescart.httpserver.adapters.cart.toDomain
import com.recipescart.httpserver.api.cart.CartApi
import com.recipescart.httpserver.api.cart.UpsertRecipeInCartApi
import com.recipescart.repository.UpsertItemInCartResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/carts")
class CartRestController(
    private val getCartUseCase: GetCartUseCase,
    private val upsertRecipeInCartUseCase: UpsertRecipeInCartUseCase,
    private val removeRecipeFromCartUseCase: RemoveRecipeFromCartUseCase,
) {
    @GetMapping("/{id}")
    fun getCartById(
        @PathVariable id: Int,
    ): ResponseEntity<CartApi> {
        val cart = getCartUseCase.execute(cartId = id)
        return if (cart != null) {
            ResponseEntity.ok(cart.toApi())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/add_recipe")
    fun addRecipeToCart(
        @PathVariable id: Int,
        @RequestBody payload: UpsertRecipeInCartApi,
    ): ResponseEntity<Any> {
        val result =
            upsertRecipeInCartUseCase.execute(
                payload.toDomain(cartId = id),
            )

        return when (result) {
            UpsertItemInCartResult.Success -> ResponseEntity.ok().build()
            UpsertItemInCartResult.ItemNotFound -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipe not found")
            UpsertItemInCartResult.CartNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found")
        }
    }

    @DeleteMapping("/{cartId}/recipes/{recipeId}")
    fun deleteRecipeFromCart(
        @PathVariable cartId: Int,
        @PathVariable recipeId: Int,
    ): ResponseEntity<Void> {
        removeRecipeFromCartUseCase.execute(cartId, recipeId)
        return ResponseEntity.noContent().build()
    }
}
