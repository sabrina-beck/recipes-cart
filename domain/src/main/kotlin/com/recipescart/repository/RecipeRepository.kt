package com.recipescart.repository

import com.recipescart.model.Recipe

interface RecipeRepository {
    fun getRecipes(): List<Recipe>
}
