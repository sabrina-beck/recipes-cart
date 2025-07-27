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

fun givenExistentRecipes(
    recipeRepository: RecipeRepository,
    productRepository: ProductRepository,
): List<Recipe> =
    (0..Random.nextInt(2..20))
        .map { givenExistentRecipe(recipeRepository, productRepository, it) }

fun givenExistentRecipe(
    recipeRepository: RecipeRepository,
    productRepository: ProductRepository,
    recipeId: RecipeId,
): Recipe {
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
