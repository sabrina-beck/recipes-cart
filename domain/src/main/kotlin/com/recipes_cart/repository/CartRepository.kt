package com.recipes_cart.repository

import com.recipes_cart.model.Cart
import com.recipes_cart.model.CartId
import com.recipes_cart.model.RecipeId

interface CartRepository {

    fun get_cart_by_id(id: CartId): Cart;

    fun add_recipe(id: CartId, recipe: RecipeId);

    fun remove_recipe(id: CartId, recipe: RecipeId);

}