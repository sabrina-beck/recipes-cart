plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

group = "com.recipes_cart"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) // Explicitly set jvmTarget
    }
}

allprojects {

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    apply {
        plugin("java")
        plugin("kotlin")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.jetbrains.kotlin:kotlin-test:2.2.0")
        testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    }
}
