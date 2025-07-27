package com.recipescart.fixtures

import com.recipescart.model.Product
import io.github.serpro69.kfaker.Faker
import kotlin.random.Random

fun aProduct(id: Int): Product =
    Product(
        id,
        name = Faker().food.ingredients(),
        priceInCents = Random.nextInt(100, 10_000),
    )
