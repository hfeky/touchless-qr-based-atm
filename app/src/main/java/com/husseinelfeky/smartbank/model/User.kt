package com.husseinelfeky.smartbank.model

data class User(
    val name: String,
    val availableBalance: Long,
    val transactionsHistory: List<TransactionRecord>
)
