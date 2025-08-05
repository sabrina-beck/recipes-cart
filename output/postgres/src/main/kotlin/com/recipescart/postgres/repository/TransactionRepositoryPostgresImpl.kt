package com.recipescart.postgres.repository

import com.recipescart.repository.TransactionRepository
import org.springframework.transaction.support.TransactionTemplate

class TransactionRepositoryPostgresImpl(
    private val transactionTemplate: TransactionTemplate,
) : TransactionRepository {
    override fun <T> execute(fn: () -> T?): T? = transactionTemplate.execute { fn() }
}
