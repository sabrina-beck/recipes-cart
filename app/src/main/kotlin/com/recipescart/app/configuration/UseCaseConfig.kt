package com.recipescart.app.configuration

import com.recipescart.features.cart.GetCartUseCase
import com.recipescart.features.cart.NewCartUseCase
import com.recipescart.features.cart.RemoveRecipeFromCartUseCase
import com.recipescart.features.cart.UpsertRecipeInCartUseCase
import com.recipescart.features.recipe.GetRecipesUseCase
import com.recipescart.repository.CartRepository
import com.recipescart.repository.RecipeRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {

    @Bean
    fun getRecipesUseCase(recipeRepository: RecipeRepository): GetRecipesUseCase =
        GetRecipesUseCase(
            recipeRepository,
        )

    @Bean
    fun newCartUseCase(cartRepository: CartRepository): NewCartUseCase =
        NewCartUseCase(
            cartRepository,
        )

    @Bean
    fun getCartUseCase(cartRepository: CartRepository): GetCartUseCase =
        GetCartUseCase(
            cartRepository,
        )

    @Bean
    fun upsertRecipeInCartUseCase(cartRepository: CartRepository): UpsertRecipeInCartUseCase =
        UpsertRecipeInCartUseCase(
            cartRepository,
        )

    @Bean
    fun removeRecipeFromCartUseCase(cartRepository: CartRepository): RemoveRecipeFromCartUseCase =
        RemoveRecipeFromCartUseCase(
            cartRepository,
        )
}
