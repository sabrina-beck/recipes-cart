dependencies {
    implementation(project(":domain"))
    implementation("org.springframework:spring-webmvc:${property("spring-web-mvc.version")}")
    implementation("org.springframework:spring-context:${property("spring-web-mvc.version")}")
}
