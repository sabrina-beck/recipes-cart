package com.recipescart.app.http

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity

inline fun <reified T> TestRestTemplate.getForEntity(
    port: Int,
    path: String,
): ResponseEntity<T> = this.getForEntity("http://localhost:$port$path", T::class.java)
