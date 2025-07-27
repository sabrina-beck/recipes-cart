package com.recipescart.postgres.repository

import com.recipescart.fixtures.aProduct
import com.recipescart.postgres.SharedPostgresContainer
import com.recipescart.repository.InsertProductResult
import com.recipescart.repository.ProductRepository
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductRepositoryPostgresTest {
    private lateinit var repository: ProductRepository

    @BeforeTest
    fun setup() {
        val jdbc = JdbcTemplate(SharedPostgresContainer.newCartRecipesDb())
        this.repository = ProductRepositoryPostgres(jdbc)
    }

    @Test
    fun `insertProduct should return inserted new product on database`() {
        val id = 1
        val product = aProduct(id)

        val result = repository.insertProduct(product)
        assertEquals(InsertProductResult.Success, result)

        val savedProduct = repository.getProductById(id)
        assertEquals(product, savedProduct)
    }

    @Test
    fun `insertProduct twice should be idempotent`() {
        val id = 1
        val product = aProduct(id)

        repository.insertProduct(product)
        val result = repository.insertProduct(product)
        assertEquals(InsertProductResult.Success, result)

        val savedProduct = repository.getProductById(id)
        assertEquals(product, savedProduct)
    }

    @Test
    fun `insertProduct twice with same id but different product should return conflict`() {
        val id = 1
        val product = aProduct(id)
        repository.insertProduct(product)

        val result = repository.insertProduct(aProduct(id))
        assertEquals(InsertProductResult.Conflict, result)

        val savedProduct = repository.getProductById(id)
        assertEquals(product, savedProduct)
    }

    @Test
    fun `getProductById should return empty when the product doesn't exist`() {
        val id = 1

        val product = repository.getProductById(id)
        assertNull(product)
    }
}
