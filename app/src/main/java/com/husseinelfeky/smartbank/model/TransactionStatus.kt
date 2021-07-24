package com.husseinelfeky.smartbank.model

import com.husseinelfeky.smartbank.exception.InvalidTransactionStatusException

enum class TransactionStatus(val code: String) {

    PENDING_OTP("pending_otp"),
    PENDING_DEPOSIT("pending_deposit"),
    PENDING_DEPOSIT_CONFIRMATION("pending_deposit_confirmation"),
    SUCCESS("success"),
    FAILED("failure"),
    EXPIRED("expired"),
    DENIED_DEPOSIT("denied_deposit");

    companion object {
        fun get(code: String): TransactionStatus {
            return values().find {
                it.code == code
            } ?: throw InvalidTransactionStatusException(code)
        }
    }
}
