plugins {
    id("org.flywaydb.flyway") version "11.10.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.flywaydb:flyway-core:11.10.4")
    implementation("org.postgresql:postgresql:42.7.7")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:11.10.4")
        classpath("org.postgresql:postgresql:42.7.7")
    }
}

flyway {
    url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/recipes-cart"
    user = System.getenv("DB_USER") ?: "postgres"
    password = System.getenv("DB_PASSWORD") ?: "postgres"
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
