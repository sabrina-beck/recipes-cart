package com.recipescart.postgres.repository

import com.recipescart.fixtures.aRecipe
import com.recipescart.postgres.SharedPostgresContainer
import com.recipescart.repository.InsertRecipeResult
import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.seed.givenExistentRecipe
import com.recipescart.seed.givenExistentRecipes
import com.zaxxer.hikari.HikariDataSource
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeRepositoryPostgresTest {
    private lateinit var dataSource: HikariDataSource
    private lateinit var productRepository: ProductRepository
    private lateinit var recipeRepository: RecipeRepository

    @BeforeTest
    fun setup() {
        this.dataSource = SharedPostgresContainer.newCartRecipesDb()

        val jdbc = JdbcTemplate(this.dataSource)
        this.recipeRepository = RecipeRepositoryPostgres(jdbc)
        this.productRepository = ProductRepositoryPostgres(jdbc)
    }

    @AfterTest
    fun tearDown() {
        dataSource.close()
    }

    @Test
    fun `insertRecipe should return inserted new recipe on database`() {
        val recipeId = 1
        val expectedRecipe = givenExistentRecipe(recipeRepository, productRepository, recipeId)

        val recipe = recipeRepository.getRecipeById(recipeId)
        assertEquals(expectedRecipe, recipe)
    }

    @Test
    fun `insertRecipe twice should be idempotent`() {
        val recipeId = 1
        val expectedRecipe = givenExistentRecipe(recipeRepository, productRepository, recipeId)

        val result = recipeRepository.insertRecipe(expectedRecipe)
        assertEquals(InsertRecipeResult.Success, result)

        val recipe = recipeRepository.getRecipeById(recipeId)
        assertEquals(expectedRecipe, recipe)
    }

    @Test
    fun `insertRecipe twice with same id but different recipe should return conflict`() {
        val recipeId = 1
        val expectedRecipe = givenExistentRecipe(recipeRepository, productRepository, recipeId)

        val anotherRecipe = aRecipe(recipeId)
        val result = recipeRepository.insertRecipe(anotherRecipe)
        assertEquals(InsertRecipeResult.Conflict, result)

        val recipe = recipeRepository.getRecipeById(recipeId)
        assertEquals(expectedRecipe, recipe)
    }

    @Test
    fun `getRecipeById should return empty when the product doesn't exist`() {
        val recipeId = 1

        val recipe = recipeRepository.getRecipeById(recipeId)
        assertNull(recipe)
    }

    @Test
    fun `getRecipes when there's no recipe should return empty`() {
        val recipes = recipeRepository.getRecipes()
        assert(recipes.isEmpty())
    }

    @Test
    fun `getRecipes should return recipes from db`() {
        val expectedRecipes = givenExistentRecipes(recipeRepository, productRepository)

        val recipes = recipeRepository.getRecipes()
        assertEquals(expectedRecipes, recipes)
    }

    @Test
    fun `getRecipesByIds when there's no recipe id should return empty`() {
        val recipes = recipeRepository.getRecipesByIds(emptyList())
        assert(recipes.isEmpty())
    }

    @Test
    fun `getRecipesByIds when there's no recipe should return empty`() {
        val recipes = recipeRepository.getRecipesByIds(listOf(1, 2, 3))
        assert(recipes.isEmpty())
    }

    @Test
    fun `getRecipesByIds should return recipes from db`() {
        val expectedRecipes = givenExistentRecipes(recipeRepository, productRepository)

        val recipeIds = expectedRecipes.map { it.id }
        val recipes = recipeRepository.getRecipesByIds(recipeIds)
        assertEquals(expectedRecipes, recipes)
    }
}
