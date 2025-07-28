package com.recipescart.app.tests

import com.recipescart.app.AbstractIntegrationTest
import com.recipescart.app.configuration.SeedTestConfig
import com.recipescart.app.http.getRecipes
import com.recipescart.httpserver.adapters.cartitem.toApi
import com.recipescart.httpserver.api.cartitem.RecipeApi
import com.recipescart.seed.RecipesSeed
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

@Import(SeedTestConfig::class)
class RecipeIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    lateinit var recipesSeed: RecipesSeed

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should return seeded recipes via HTTP endpoint`() {
        val seededRecipes = recipesSeed.givenExistentRecipes()

        val response: ResponseEntity<Array<RecipeApi>> = restTemplate.getRecipes(port)

        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(seededRecipes.size, response.body!!.size)

        val returnedRecipes = response.body!!.toList()
        val expectedRecipes = seededRecipes.map { it.toApi() }

        assertEquals(expectedRecipes, returnedRecipes)
    }
}
