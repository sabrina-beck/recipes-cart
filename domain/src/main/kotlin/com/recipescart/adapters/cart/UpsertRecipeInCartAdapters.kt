package com.recipescart.adapters.cart

import com.recipescart.features.cart.UpsertRecipeInCartUseCaseResult
import com.recipescart.repository.CartRepository

fun CartRepository.UpsertRecipeResult.toAddRecipeUseCaseResult(): UpsertRecipeInCartUseCaseResult =
    when (this) {
        is CartRepository.UpsertRecipeResult.Success -> UpsertRecipeInCartUseCaseResult.Success
        is CartRepository.UpsertRecipeResult.RecipeNotFound -> UpsertRecipeInCartUseCaseResult.RecipeInCartNotFound
        is CartRepository.UpsertRecipeResult.CartNotFound -> UpsertRecipeInCartUseCaseResult.CartNotFound
    }
