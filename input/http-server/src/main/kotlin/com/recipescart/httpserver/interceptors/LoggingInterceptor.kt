package com.recipescart.httpserver.interceptors

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val method = request.method
        val uri = request.requestURI
        val query = request.queryString?.let { "?$it" } ?: ""

        logger.info("➡️ $method $uri$query")

        if (request !is ContentCachingRequestWrapper) {
            logger.debug("Request is not wrapped, body will not be logged")
        }

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val req = request as? ContentCachingRequestWrapper
        val res = response as? ContentCachingResponseWrapper

        val method = request.method
        val path = request.requestURI
        val status = response.status

        logger.info("[$method] $path -> $status")

        if (logger.isDebugEnabled && req != null && res != null) {
            val requestBody = req.contentAsByteArray.toString(Charsets.UTF_8)
            val responseBody = res.contentAsByteArray.toString(Charsets.UTF_8)
            logger.debug("[$method] $path -> $status :: Request body: $requestBody")
            logger.debug("[$method] $path -> $status :: Response body: $responseBody")
        }

        res?.copyBodyToResponse()
    }
}
