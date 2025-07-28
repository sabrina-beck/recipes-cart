package com.recipescart.postgres.utils

import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import org.springframework.dao.DataIntegrityViolationException

fun DataIntegrityViolationException.isForeignKeyViolation(fkName: String): Boolean {
    val cause = this.cause
    return cause is PSQLException &&
        cause.sqlState == PSQLState.FOREIGN_KEY_VIOLATION.state &&
        cause.serverErrorMessage?.constraint == fkName
}
