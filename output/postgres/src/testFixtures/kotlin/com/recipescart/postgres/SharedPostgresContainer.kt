package com.recipescart.postgres

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import java.util.UUID

object SharedPostgresContainer {
    val container =
        PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("shared_db")
            withUsername("postgres")
            withPassword("postgres")
            start()
        }

    val jdbcUrl: String get() = container.jdbcUrl
    val username: String get() = container.username
    val password: String get() = container.password

    fun newCartRecipesDb(): HikariDataSource {
        check(container.isRunning) { "Postgres test container is not running" }

        val dbName = "test_${UUID.randomUUID().toString().replace("-", "")}"

        val adminJdbcUrl = "jdbc:postgresql://${container.host}:${container.getMappedPort(5432)}/postgres"

        DriverManager
            .getConnection(adminJdbcUrl, container.username, container.password)
            .use { conn ->
                conn.createStatement().execute("CREATE DATABASE $dbName")
            }

        val config =
            HikariConfig().apply {
                jdbcUrl = "jdbc:postgresql://${container.host}:${container.getMappedPort(5432)}/$dbName"
                username = container.username
                password = container.password
                maximumPoolSize = 5
            }

        val dataSource = HikariDataSource(config)

        Flyway
            .configure()
            .dataSource(dataSource)
            .locations("filesystem:src/main/resources/db/migration")
            .load()
            .migrate()

        return dataSource
    }
}
