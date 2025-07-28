package com.recipescart.app.configuration

import com.recipescart.httpserver.interceptors.RequestResponseWrappingFilter
import jakarta.servlet.Filter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpLoggingConfig {
    @Bean
    fun requestResponseWrappingFilter(): FilterRegistrationBean<Filter> =
        FilterRegistrationBean<Filter>().apply {
            this.filter = RequestResponseWrappingFilter()
            this.order = 1
        }
}
