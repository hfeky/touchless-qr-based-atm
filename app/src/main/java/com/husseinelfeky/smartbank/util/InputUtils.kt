package com.husseinelfeky.smartbank.util

import android.util.Patterns

object InputUtils {

    private const val PASSWORD_PATTERN =
        "^(?=.{8,})(" +
                "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])|" +
                "(?=.*[0-9])(?=.*[a-z])(?=.*[^0-9a-zA-Z])|" +
                "(?=.*[0-9])(?=.*[A-Z])(?=.*[^0-9a-zA-Z])|" +
                "(?=.*[a-z])(?=.*[A-Z])(?=.*[^0-9a-zA-Z])" +
                ").*$"

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        if (phoneNumber == null) {
            return false
        }
        return phoneNumber.length == 11
                && (phoneNumber.startsWith("010")
                || phoneNumber.startsWith("011")
                || phoneNumber.startsWith("012")
                || phoneNumber.startsWith("015"))
    }

    fun isValidEmailAddress(emailAddress: String?): Boolean {
        if (emailAddress == null) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        if (password == null) {
            return false
        }
        return Regex(PASSWORD_PATTERN).matches(password)
    }

    fun isAmountInValidRange(amount: Int): Boolean {
        return amount in 10..6000
    }

    fun isAmountDivisibleByTen(amount: Int): Boolean {
        return amount % 10 == 0
    }
}
