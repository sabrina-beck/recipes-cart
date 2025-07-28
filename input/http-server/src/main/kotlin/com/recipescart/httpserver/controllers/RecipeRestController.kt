package com.recipescart.httpserver.controllers

import com.recipescart.features.recipe.GetRecipesUseCase
import com.recipescart.httpserver.adapters.cartitem.toApi
import com.recipescart.httpserver.api.cartitem.RecipeApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recipes")
class RecipeRestController(
    private val getRecipesUseCase: GetRecipesUseCase,
) {
    @GetMapping
    fun listRecipes(): List<RecipeApi> = getRecipesUseCase.execute().map { it.toApi() }
}
