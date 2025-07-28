package com.recipescart.app.http

import com.recipescart.httpserver.api.cartitem.RecipeApi
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity

fun TestRestTemplate.getRecipes(port: Int): ResponseEntity<Array<RecipeApi>> = this.getForEntity<Array<RecipeApi>>(port, "/recipes")
