package com.recipescart.repository

import com.recipescart.model.ProductId
import com.recipescart.model.Recipe
import com.recipescart.model.RecipeId

sealed interface InsertRecipeResult {
    object Success : InsertRecipeResult

    object Conflict : InsertRecipeResult

    class ProductNotFound(
        id: ProductId,
    ) : InsertRecipeResult
}

interface RecipeRepository {
    fun getRecipes(): List<Recipe>

    fun getRecipeById(id: RecipeId): Recipe?

    fun insertRecipe(recipe: Recipe): InsertRecipeResult
}
