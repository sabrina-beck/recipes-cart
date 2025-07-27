package com.recipes_cart.repository

import com.recipes_cart.model.Cart
import com.recipes_cart.model.CartId
import com.recipes_cart.model.RecipeId

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
