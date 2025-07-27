package com.recipescart.postgres.repository

import com.recipescart.model.Ingredient
import com.recipescart.model.Recipe
import com.recipescart.model.RecipeId
import com.recipescart.postgres.adapters.toRecipeDb
import com.recipescart.postgres.adapters.toRecipes
import com.recipescart.repository.InsertRecipeResult
import com.recipescart.repository.RecipeRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional

class RecipeRepositoryPostgres(
    private val jdbcTemplate: JdbcTemplate,
) : RecipeRepository {
    companion object {
        const val RECIPES_TABLE_NAME = "recipes"
        const val RECIPE_INGREDIENTS_TABLE_NAME = "recipe_ingredients"

        const val ID_COLUMN = "id"
        const val RECIPE_ID_COLUMN = "recipe_id"
        const val NAME_COLUMN = "name"
        const val PRODUCT_ID_COLUMN = "product_id"
        const val PRODUCT_NAME_COLUMN = "product_name"
        const val QUANTITY_COLUMN = "quantity"
        const val CREATED_AT_COLUMN = "created_at"
        const val UPDATED_AT_COLUMN = "updated_at"
    }

    override fun getRecipes(): List<Recipe> {
        val sql =
            """
            SELECT 
                r.$ID_COLUMN AS $RECIPE_ID_COLUMN, 
                r.$NAME_COLUMN AS $NAME_COLUMN,
                ri.$PRODUCT_ID_COLUMN,
                p.${ProductRepositoryPostgres.NAME_COLUMN} AS $PRODUCT_NAME_COLUMN,
                p.${ProductRepositoryPostgres.PRICE_IN_CENTS_COLUMN} ,
                ri.$QUANTITY_COLUMN
            FROM $RECIPES_TABLE_NAME r
            JOIN $RECIPE_INGREDIENTS_TABLE_NAME ri ON ri.$RECIPE_ID_COLUMN = r.$ID_COLUMN
            JOIN ${ProductRepositoryPostgres.PRODUCTS_TABLE_NAME} p ON p.${ProductRepositoryPostgres.ID_COLUMN} = ri.$PRODUCT_ID_COLUMN
            ORDER BY r.$ID_COLUMN
            """.trimIndent()

        return jdbcTemplate
            .queryForList(sql)
            .groupBy { it.toRecipeDb() }
            .toRecipes()
    }

    override fun getRecipeById(id: RecipeId): Recipe? {
        val sql =
            """
            SELECT 
                r.$ID_COLUMN AS $RECIPE_ID_COLUMN, 
                r.$NAME_COLUMN AS $NAME_COLUMN,
                ri.$PRODUCT_ID_COLUMN,
                p.${ProductRepositoryPostgres.NAME_COLUMN} AS $PRODUCT_NAME_COLUMN,
                p.${ProductRepositoryPostgres.PRICE_IN_CENTS_COLUMN} ,
                ri.$QUANTITY_COLUMN
            FROM $RECIPES_TABLE_NAME r
            JOIN $RECIPE_INGREDIENTS_TABLE_NAME ri ON ri.$RECIPE_ID_COLUMN = r.$ID_COLUMN
            JOIN ${ProductRepositoryPostgres.PRODUCTS_TABLE_NAME} p ON p.${ProductRepositoryPostgres.ID_COLUMN} = ri.$PRODUCT_ID_COLUMN
            WHERE r.$ID_COLUMN = ?
            """.trimIndent()

        return jdbcTemplate
            .queryForList(sql, id)
            .groupBy { it.toRecipeDb() }
            .toRecipes()
            .firstOrNull()
    }

    @Transactional
    override fun insertRecipe(recipe: Recipe): InsertRecipeResult {
        val sql =
            """
            INSERT INTO $RECIPES_TABLE_NAME (
                $ID_COLUMN, 
                $NAME_COLUMN, 
                $CREATED_AT_COLUMN, 
                $UPDATED_AT_COLUMN
            ) VALUES (?, ?, now(), now())
            """.trimMargin()

        try {
            jdbcTemplate.update(
                sql,
                recipe.id,
                recipe.name,
            )
        } catch (ex: DuplicateKeyException) {
            val existing = getRecipeById(recipe.id)
            if (existing == recipe) {
                return InsertRecipeResult.Success
            } else {
                return InsertRecipeResult.Conflict
            }
        }

        recipe.ingredients.forEach { insertRecipeIngredient(recipe.id, it) }

        return InsertRecipeResult.Success
    }

    private fun insertRecipeIngredient(
        recipeId: RecipeId,
        ingredient: Ingredient,
    ) {
        val sql =
            """
            INSERT INTO $RECIPE_INGREDIENTS_TABLE_NAME (
                $RECIPE_ID_COLUMN, 
                $PRODUCT_ID_COLUMN, 
                $QUANTITY_COLUMN,
                $CREATED_AT_COLUMN,
                $UPDATED_AT_COLUMN
            ) VALUES (?, ?, ?, now(), now())
            """.trimMargin()

        jdbcTemplate.update(
            sql,
            recipeId,
            ingredient.product.id,
            ingredient.quantity,
        )
    }
}
