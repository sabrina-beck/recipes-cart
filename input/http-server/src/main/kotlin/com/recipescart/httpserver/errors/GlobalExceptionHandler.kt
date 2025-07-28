package com.recipescart.httpserver.errors

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(error = Error.ILLEGAL_ARGUMENT, message = ex.message ?: "Unknown illegal argument"),
            HttpStatus.BAD_REQUEST,
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)

        return ResponseEntity(
            ErrorResponse(
                error = Error.INTERNAL_SERVER_ERROR,
                message = Error.INTERNAL_SERVER_ERROR.message!!
            ),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }
}
