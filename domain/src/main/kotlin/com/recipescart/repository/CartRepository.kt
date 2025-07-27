package com.recipescart.repository

import com.recipescart.model.Cart
import com.recipescart.model.CartId
import com.recipescart.model.RecipeId

interface CartRepository {
    fun getCartById(id: CartId): Cart

    fun addRecipe(
        id: CartId,
        recipe: RecipeId,
    )

    fun removeRecipe(
        id: CartId,
        recipe: RecipeId,
    )
}
