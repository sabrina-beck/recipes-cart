package com.recipescart.postgres.dto

import com.recipescart.model.RecipeId

data class RecipeDb(
    val id: RecipeId,
    val name: String,
)
