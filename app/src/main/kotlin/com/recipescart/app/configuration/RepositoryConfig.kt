package com.recipescart.app.configuration

import com.recipescart.postgres.adapters.CartDbAdapter
import com.recipescart.postgres.repository.CartRepositoryPostgres
import com.recipescart.postgres.repository.ProductRepositoryPostgres
import com.recipescart.postgres.repository.RecipeRepositoryPostgres
import com.recipescart.repository.CartRepository
import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class RepositoryConfig {
    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = JdbcTemplate(dataSource)

    @Bean
    fun productRepository(jdbcTemplate: JdbcTemplate): ProductRepository = ProductRepositoryPostgres(jdbcTemplate)

    @Bean
    fun recipeRepository(jdbcTemplate: JdbcTemplate): RecipeRepository = RecipeRepositoryPostgres(jdbcTemplate)

    @Bean
    fun cartDbAdapter(
        productRepository: ProductRepository,
        recipeRepository: RecipeRepository,
    ): CartDbAdapter = CartDbAdapter(productRepository, recipeRepository)

    @Bean
    fun cartRepository(
        jdbcTemplate: JdbcTemplate,
        cartDbAdapter: CartDbAdapter,
        recipeRepository: RecipeRepository,
    ): CartRepository =
        CartRepositoryPostgres(
            jdbcTemplate,
            cartDbAdapter,
            recipeRepository,
        )
}
