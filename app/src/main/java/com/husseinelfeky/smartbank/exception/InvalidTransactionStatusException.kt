package com.husseinelfeky.smartbank.exception

class InvalidTransactionStatusException(
    val code: String
) : Exception("Transaction status with code $code is invalid.")
