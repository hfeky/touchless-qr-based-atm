package com.husseinelfeky.smartbank.ui.otpverification

import com.husseinelfeky.smartbank.model.TransactionRecord

sealed class OtpVerificationEvent {

    object ProcessingOtp : OtpVerificationEvent()

    class ProceedWithTransaction(val transactionRecord: TransactionRecord) : OtpVerificationEvent()

    open class Error(open val throwable: Throwable?) : OtpVerificationEvent()

    class NetworkError(override val throwable: Throwable) : Error(throwable)

    class InvalidTransactionError(override val throwable: Throwable) : Error(throwable)

    class ExpiredTransactionError(override val throwable: Throwable) : Error(throwable)

    class IncorrectOtpError(override val throwable: Throwable) : Error(throwable)
}
