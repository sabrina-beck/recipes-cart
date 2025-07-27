package com.recipescart.fixtures

import com.recipescart.model.Ingredient
import com.recipescart.model.Recipe
import io.github.serpro69.kfaker.Faker
import kotlin.random.Random

fun aRecipe(id: Int): Recipe =
    Recipe(
        id,
        name = Faker().food.dish(),
        ingredients =
            (1..Random.nextInt(2, 10))
                .map { id * 1000 + it }
                .map { aIngredient(it) },
    )

fun aIngredient(id: Int): Ingredient =
    Ingredient(
        product = aProduct(id),
        quantity = Random.nextInt(1, 10),
    )
