package com.recipescart.httpserver.errors

enum class Error(
    val message: String? = null,
) {
    RECIPE_NOT_FOUND("Recipe not found"),
    CART_NOT_FOUND("Cart not found"),
    ILLEGAL_ARGUMENT,
    INTERNAL_SERVER_ERROR("An unexpected error occurred. Please try again later."),
}

data class ErrorResponse(
    val error: Error,
    val message: String,
)
