package com.husseinelfeky.smartbank

object Firestore {

    object Collection {
        const val ATMS = "atms"
        const val USERS = "users"
        const val TRANSACTIONS = "transactions"
    }

    object Field {
        const val AVAILABLE_BALANCE = "available_balance"

        const val NUMBER = "number"
        const val AMOUNT = "amount"
        const val TYPE = "type"
        const val ATM_NAME = "atm_name"
        const val TIME_COMPLETED = "time_completed"
        const val STATUS = "status"

        const val NAME = "name"
        const val LOCATION = "location"
        const val GEO_LOCATION = "geoLocation"
    }
}
