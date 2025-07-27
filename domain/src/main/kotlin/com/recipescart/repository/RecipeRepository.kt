package com.recipes_cart.repository

import com.recipes_cart.model.Recipe

interface RecipeRepository {
    fun getRecipes(): List<Recipe>
}
