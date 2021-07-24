package com.husseinelfeky.smartbank.exception

class InvalidAmountException(
    amount: Long
) : Exception("Requested amount of $amount EGP is not available in the user's balance.")
