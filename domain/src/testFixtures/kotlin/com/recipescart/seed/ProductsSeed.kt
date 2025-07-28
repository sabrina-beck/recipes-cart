package com.recipescart.seed

import com.recipescart.fixtures.aProduct
import com.recipescart.model.Product
import com.recipescart.repository.ProductRepository
import kotlin.random.Random
import kotlin.random.nextInt

fun givenProducts(productRepository: ProductRepository): List<Product> =
    (0..Random.nextInt(2..20))
        .map { aProduct(it) }
        .map {
            productRepository.insertProduct(it)
            it
        }
