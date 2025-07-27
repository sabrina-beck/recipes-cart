package com.recipes_cart.features.recipe

import com.recipes_cart.model.Recipe
import com.recipes_cart.repository.RecipeRepository

class GetRecipesUseCase(
    val recipeRepository: RecipeRepository,
) {
    fun execute(): List<Recipe> = recipeRepository.getRecipes()
}
