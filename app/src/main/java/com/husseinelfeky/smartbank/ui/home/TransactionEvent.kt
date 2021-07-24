package com.husseinelfeky.smartbank.ui.home

import com.husseinelfeky.smartbank.model.Atm
import com.husseinelfeky.smartbank.model.TransactionType

sealed class TransactionEvent {

    object FetchingUserLocation : TransactionEvent()

    class ProceedWithdrawal(val atm: Atm) : TransactionEvent()

    class ProceedDeposit(val atm: Atm) : TransactionEvent()

    open class Error(open val throwable: Throwable?) : TransactionEvent()

    class NetworkError(override val throwable: Throwable) : Error(throwable)

    class LocationMockedError(override val throwable: Throwable) : Error(throwable)

    class NoAtmFoundError(
        override val throwable: Throwable,
        val transactionType: TransactionType
    ) : Error(throwable)
}
