package com.recipescart.app.configuration

import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.seed.ProductsSeed
import com.recipescart.seed.RecipesSeed
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class SeedTestConfig {
    @Bean
    fun productsSeed(productRepository: ProductRepository): ProductsSeed = ProductsSeed(productRepository)

    @Bean
    fun recipesSeed(
        productRepository: ProductRepository,
        recipeRepository: RecipeRepository,
    ): RecipesSeed = RecipesSeed(productRepository, recipeRepository)
}
