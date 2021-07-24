package com.husseinelfeky.smartbank.exception

class InvalidTransactionTypeException(
    val code: String
) : Exception("Transaction type with code $code is invalid.")
