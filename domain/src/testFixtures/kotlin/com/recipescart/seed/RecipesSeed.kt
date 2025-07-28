package com.recipescart.seed

import com.recipescart.fixtures.aRecipe
import com.recipescart.model.Recipe
import com.recipescart.model.RecipeId
import com.recipescart.repository.InsertProductResult
import com.recipescart.repository.InsertRecipeResult
import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository
import kotlin.random.Random
import kotlin.random.nextInt

class RecipesSeed(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
) {
    fun givenExistentRecipes(): List<Recipe> =
        (1..Random.nextInt(2..20))
            .map { givenExistentRecipe(it) }

    fun givenExistentRecipe(recipeId: RecipeId): Recipe {
        val recipe = aRecipe(recipeId)

        recipe.ingredients
            .map { it.product }
            .map { productRepository.insertProduct(it) }
            .firstOrNull { it != InsertProductResult.Success }
            ?.let { throw IllegalStateException("Failed to insert product: $it") }

        val result = recipeRepository.insertRecipe(recipe)
        if (result != InsertRecipeResult.Success) {
            throw IllegalStateException("Failed to insert recipe: $recipe")
        }

        return recipe
    }
}
