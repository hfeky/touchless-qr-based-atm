package com.husseinelfeky.smartbank.ui.enterdeposit

import com.husseinelfeky.smartbank.model.TransactionRecord

sealed class EnterDepositEvent {

    class ShowConfirmationDialog(val transactionRecord: TransactionRecord) : EnterDepositEvent()

    object RespondingToDepositConfirmation : EnterDepositEvent()

    class ProceedWithTransaction(val transactionRecord: TransactionRecord) : EnterDepositEvent()

    open class Error(open val throwable: Throwable?) : EnterDepositEvent()

    class NetworkError(override val throwable: Throwable) : Error(throwable)

    class InvalidTransactionError(override val throwable: Throwable) : Error(throwable)

    class ExpiredTransactionError(override val throwable: Throwable) : Error(throwable)
}
