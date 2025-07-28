dependencies {
    implementation(project(":domain"))
    implementation("org.springframework:spring-webmvc:${property("spring-web-mvc.version")}")
    implementation("org.springframework:spring-context:${property("spring-web-mvc.version")}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${property("jackson.version")}")

    implementation("org.slf4j:slf4j-api:${property("slf4j.version")}")
    implementation("ch.qos.logback:logback-classic:${property("logback.version")}")

    implementation("jakarta.servlet:jakarta.servlet-api:${property("jakarta.servlet-api.version")}")
}
