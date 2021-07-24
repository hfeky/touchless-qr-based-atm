package com.husseinelfeky.smartbank.exception

class InvalidTransactionException(
    transactionId: String
) : Exception("Could not find a transaction with ID $transactionId.")
