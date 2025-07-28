package com.recipescart.httpserver.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(error = Error.ILLEGAL_ARGUMENT, message = ex.message ?: "Unknown illegal argument"),
            HttpStatus.BAD_REQUEST,
        )
}
