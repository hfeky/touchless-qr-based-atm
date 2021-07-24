package com.husseinelfeky.smartbank.exception

class InvalidAtmException(
    actualAtmId: String,
    expectedAtmId: String? = null
) : Exception(
    if (expectedAtmId != null) {
        "Expected ATM with ID $expectedAtmId but found ATM with ID $actualAtmId."
    } else {
        "Could not find an ATM with id $actualAtmId."
    }
)
