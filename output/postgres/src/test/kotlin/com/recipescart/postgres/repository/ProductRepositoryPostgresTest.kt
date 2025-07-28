package com.recipescart.postgres.repository

import com.recipescart.fixtures.aProduct
import com.recipescart.postgres.SharedPostgresContainer
import com.recipescart.repository.InsertProductResult
import com.recipescart.repository.ProductRepository
import com.recipescart.seed.givenProducts
import com.zaxxer.hikari.HikariDataSource
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductRepositoryPostgresTest {
    private lateinit var dataSource: HikariDataSource
    private lateinit var repository: ProductRepository

    @BeforeTest
    fun setup() {
        this.dataSource = SharedPostgresContainer.newCartRecipesDb()

        val jdbc = JdbcTemplate(this.dataSource)
        this.repository = ProductRepositoryPostgres(jdbc)
    }

    @AfterTest
    fun tearDown() {
        dataSource.close()
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

    @Test
    fun `getProductsByIds when there's no product id should return empty`() {
        val products = repository.getProductsByIds(emptyList())
        assert(products.isEmpty())
    }

    @Test
    fun `getProductsByIds when there's no product should return empty`() {
        val products = repository.getProductsByIds(listOf(1, 2, 3))
        assert(products.isEmpty())
    }

    @Test
    fun `getProductsByIds should return products from db`() {
        val expectedProducts = givenProducts(repository)

        val productIds = expectedProducts.map { it.id }
        val products = repository.getProductsByIds(productIds)
        assertEquals(expectedProducts, products)
    }
}
