package com.recipescart.seed

import com.recipescart.fixtures.aProduct
import com.recipescart.model.Product
import com.recipescart.repository.ProductRepository
import kotlin.random.Random
import kotlin.random.nextInt

class ProductsSeed(
    private val productRepository: ProductRepository,
) {
    fun givenProducts(): List<Product> =
        (0..Random.nextInt(2..20))
            .map { aProduct(it) }
            .map {
                productRepository.insertProduct(it)
                it
            }
}
