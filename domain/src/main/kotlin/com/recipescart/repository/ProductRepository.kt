package com.recipescart.repository

import com.recipescart.model.Product
import com.recipescart.model.ProductId

sealed interface InsertProductResult {
    object Success : InsertProductResult

    object Conflict : InsertProductResult
}

interface ProductRepository {
    fun getProductById(id: ProductId): Product?

    fun getProductsByIds(ids: List<ProductId>): List<Product>

    fun insertProduct(product: Product): InsertProductResult
}
