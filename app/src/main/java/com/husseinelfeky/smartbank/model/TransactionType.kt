package com.husseinelfeky.smartbank.model

import com.husseinelfeky.smartbank.exception.InvalidTransactionTypeException

enum class TransactionType(val code: String) {

    WITHDRAWAL("withdrawal"),
    DEPOSIT("deposit");

    companion object {
        fun get(code: String): TransactionType {
            return values().find {
                it.code == code
            } ?: throw InvalidTransactionTypeException(code)
        }
    }
}
