plugins {
    kotlin("plugin.spring") version "1.9.0"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":input:http-server"))
    implementation(project(":output:postgres"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.postgresql:postgresql:${property("postgresql.jdbc.version")}")
    implementation("org.flywaydb:flyway-core:${property("flyway.version")}")
    implementation("com.zaxxer:HikariCP:${property("hikaricp.version")}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${property("jackson.version")}")
}
