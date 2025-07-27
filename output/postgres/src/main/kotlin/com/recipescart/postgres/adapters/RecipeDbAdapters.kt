package com.recipescart.postgres.adapters

import com.recipescart.model.Ingredient
import com.recipescart.model.Product
import com.recipescart.model.ProductId
import com.recipescart.model.Recipe
import com.recipescart.model.RecipeId
import com.recipescart.postgres.dto.RecipeDb
import com.recipescart.postgres.repository.ProductRepositoryPostgres
import com.recipescart.postgres.repository.RecipeRepositoryPostgres.Companion.NAME_COLUMN
import com.recipescart.postgres.repository.RecipeRepositoryPostgres.Companion.PRODUCT_ID_COLUMN
import com.recipescart.postgres.repository.RecipeRepositoryPostgres.Companion.PRODUCT_NAME_COLUMN
import com.recipescart.postgres.repository.RecipeRepositoryPostgres.Companion.QUANTITY_COLUMN
import com.recipescart.postgres.repository.RecipeRepositoryPostgres.Companion.RECIPE_ID_COLUMN

fun Map<String, Any>.toRecipeDb(): RecipeDb {
    val id =
        (this[RECIPE_ID_COLUMN] as? RecipeId)
            ?: throw IllegalArgumentException("No recipe $RECIPE_ID_COLUMN provided")

    val name = (this[NAME_COLUMN] as? String) ?: throw IllegalArgumentException("No recipe $NAME_COLUMN provided")

    return RecipeDb(id, name)
}

fun Map<RecipeDb, List<Map<String, Any>>>.toRecipes(): List<Recipe> =
    this.map { (recipeDb, ingredientsMap) ->

        val ingredients = ingredientsMap.map { it.toIngredient() }

        Recipe(
            id = recipeDb.id,
            name = recipeDb.name,
            ingredients = ingredients,
        )
    }

private fun Map<String, Any>.toIngredient(): Ingredient =
    Ingredient(
        product =
            Product(
                id =
                    (this[PRODUCT_ID_COLUMN] as? ProductId)
                        ?: throw IllegalArgumentException("No product $PRODUCT_ID_COLUMN provided"),
                name = (this[PRODUCT_NAME_COLUMN] as? String) ?: throw IllegalArgumentException("No product $PRODUCT_NAME_COLUMN provided"),
                priceInCents =
                    (this[ProductRepositoryPostgres.PRICE_IN_CENTS_COLUMN] as? Int)
                        ?: throw IllegalArgumentException("No product ${ProductRepositoryPostgres.PRICE_IN_CENTS_COLUMN} provided"),
            ),
        quantity =
            (this[QUANTITY_COLUMN] as? Int)
                ?: throw IllegalArgumentException("No ingredient $QUANTITY_COLUMN provided"),
    )
