plugins {
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation(kotlin("test"))
    testFixturesImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testFixturesImplementation("io.github.serpro69:kotlin-faker:${property("kotlin-faker.version")}")
}
