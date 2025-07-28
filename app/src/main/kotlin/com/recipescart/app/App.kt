package com.recipescart.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.recipescart"])
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
