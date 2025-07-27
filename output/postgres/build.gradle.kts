plugins {
    id("org.flywaydb.flyway") version "11.10.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation("org.flywaydb:flyway-core:${property("flyway.version")}")
    implementation("org.flywaydb:flyway-database-postgresql:${property("flyway.version")}")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:${property("springBoot.version")}")

    testImplementation(testFixtures(project(":domain")))
    testImplementation("org.testcontainers:testcontainers:${property("testcontainers.version")}")
    testImplementation("org.testcontainers:postgresql:${property("testcontainers.version")}")
    testImplementation("org.postgresql:postgresql:${property("postgresql.jdbc.version")}")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:${property("flyway.version")}")
        classpath("org.postgresql:postgresql:${property("postgresql.jdbc.version")}")
    }
}

flyway {
    url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/recipes-cart"
    user = System.getenv("DB_USER") ?: "postgres"
    password = System.getenv("DB_PASSWORD") ?: "postgres"
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
