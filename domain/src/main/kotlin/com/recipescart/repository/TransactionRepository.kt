package com.recipescart.repository

interface TransactionRepository {
    fun <T> execute(fn: () -> T?): T?
}
