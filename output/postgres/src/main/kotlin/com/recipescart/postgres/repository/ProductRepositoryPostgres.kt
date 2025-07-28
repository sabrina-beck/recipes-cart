package com.recipescart.postgres.repository

import com.recipescart.model.Product
import com.recipescart.model.ProductId
import com.recipescart.postgres.adapters.productRowMapper
import com.recipescart.repository.InsertProductResult
import com.recipescart.repository.ProductRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class ProductRepositoryPostgres(
    private val jdbcTemplate: JdbcTemplate,
) : ProductRepository {
    companion object {
        const val PRODUCTS_TABLE_NAME = "products"

        const val ID_COLUMN = "id"
        const val NAME_COLUMN = "name"
        const val PRICE_IN_CENTS_COLUMN = "price_in_cents"
        const val CREATED_AT_COLUMN = "created_at"
        const val UPDATED_AT_COLUMN = "updated_at"
    }

    override fun insertProduct(product: Product): InsertProductResult {
        val sql =
            """
            INSERT INTO $PRODUCTS_TABLE_NAME (
                $ID_COLUMN, 
                $NAME_COLUMN, 
                $PRICE_IN_CENTS_COLUMN, 
                $CREATED_AT_COLUMN, 
                $UPDATED_AT_COLUMN
            ) VALUES (?, ?, ?, now(), now())
            """.trimMargin()

        return try {
            jdbcTemplate.update(
                sql,
                product.id,
                product.name,
                product.priceInCents,
            )
            InsertProductResult.Success
        } catch (_: DuplicateKeyException) {
            val existing = getProductById(product.id)
            if (existing == product) {
                InsertProductResult.Success
            } else {
                InsertProductResult.Conflict
            }
        }
    }

    override fun getProductById(id: ProductId): Product? {
        val sql =
            """
            SELECT
                $ID_COLUMN, 
                $NAME_COLUMN, 
                $PRICE_IN_CENTS_COLUMN
            FROM $PRODUCTS_TABLE_NAME
            WHERE $ID_COLUMN = ?
            """.trimMargin()

        return try {
            jdbcTemplate.queryForObject(sql, productRowMapper, id)
        } catch (_: EmptyResultDataAccessException) {
            null
        }
    }

    override fun getProductsByIds(ids: List<ProductId>): List<Product> {
        if (ids.isEmpty()) return emptyList()

        val sql =
            """
            SELECT
                $ID_COLUMN, 
                $NAME_COLUMN, 
                $PRICE_IN_CENTS_COLUMN
            FROM $PRODUCTS_TABLE_NAME
            WHERE $ID_COLUMN IN (:ids)
            """.trimMargin()

        val namedJdbc = NamedParameterJdbcTemplate(jdbcTemplate)
        return namedJdbc
            .query(sql, mapOf("ids" to ids), productRowMapper)
    }
}
