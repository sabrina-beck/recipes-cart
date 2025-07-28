package com.recipescart.app.db

import com.recipescart.postgres.SharedPostgresContainer
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class TestDatabaseInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        val ds = SharedPostgresContainer.newCartRecipesDb()

        TestPropertyValues
            .of(
                "spring.datasource.url=${ds.jdbcUrl}",
                "spring.datasource.username=${ds.username}",
                "spring.datasource.password=${ds.password}",
            ).applyTo(context.environment)
    }
}
