plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    jacoco
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
        testImplementation("org.jetbrains.kotlin:kotlin-test:${property("kotlin.test.version")}")
        testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { it.tasks.named("test") })

    executionData.setFrom(
        subprojects.map { file("${it.buildDir}/jacoco/test.exec") }
    )

    sourceDirectories.setFrom(
        subprojects.map { fileTree("${it.projectDir}/src/main/kotlin") }
    )
    classDirectories.setFrom(
        subprojects.map { fileTree("${it.buildDir}/classes/kotlin/main") }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<JacocoReport>().configureEach {
        dependsOn("test")

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}
