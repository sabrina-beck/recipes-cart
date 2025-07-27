package com.recipescart.postgres.adapters

import com.recipescart.model.Product
import com.recipescart.postgres.repository.ProductRepositoryPostgres
import org.springframework.jdbc.core.RowMapper

val productRowMapper =
    RowMapper { rs, _ ->
        Product(
            id =
                rs.getString(ProductRepositoryPostgres.ID_COLUMN)?.toIntOrNull()
                    ?: throw ClassCastException("Couldn't convert ${ProductRepositoryPostgres.ID_COLUMN} into int"),
            name =
                rs.getString(ProductRepositoryPostgres.NAME_COLUMN)
                    ?: throw IllegalArgumentException("No ${ProductRepositoryPostgres.NAME_COLUMN} provided"),
            priceInCents =
                rs.getString(ProductRepositoryPostgres.PRICE_IN_CENTS_COLUMN)?.toIntOrNull()
                    ?: throw ClassCastException("Couldn't convert ${ProductRepositoryPostgres.PRICE_IN_CENTS_COLUMN} into int"),
        )
    }
