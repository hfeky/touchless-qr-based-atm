package com.husseinelfeky.smartbank.exception

class AccountBlockedException : Exception(
    "User account has been temporarily blocked for many invalid attempts in making a transaction."
)
