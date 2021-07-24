package com.husseinelfeky.smartbank.exception

class ExpiredTransactionException(
    transactionId: String
) : Exception("Transaction with ID $transactionId has expired.")
