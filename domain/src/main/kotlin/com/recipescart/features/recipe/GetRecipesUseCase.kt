package com.recipescart.features.recipe

import com.recipescart.model.Recipe
import com.recipescart.repository.RecipeRepository

class GetRecipesUseCase(
    val recipeRepository: RecipeRepository,
) {
    fun execute(): List<Recipe> = recipeRepository.getRecipes()
}
