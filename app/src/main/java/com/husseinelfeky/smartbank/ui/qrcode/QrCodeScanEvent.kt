package com.husseinelfeky.smartbank.ui.qrcode

import com.husseinelfeky.smartbank.model.QrCode

sealed class QrCodeScanEvent {

    class ProcessingCode(val qrCode: QrCode) : QrCodeScanEvent()

    class ProceedWithTransaction(val qrCode: QrCode) : QrCodeScanEvent()

    open class Error(open val throwable: Throwable?) : QrCodeScanEvent()

    class NetworkError(override val throwable: Throwable) : Error(throwable)

    class InvalidQrCodeError(override val throwable: Throwable) : Error(throwable)

    class InvalidAtmError(override val throwable: Throwable) : Error(throwable)

    class AccountBlockedError(override val throwable: Throwable) : Error(throwable)

    class ExpiredQrCodeError(override val throwable: Throwable) : Error(throwable)

    class InvalidAmountError(override val throwable: Throwable) : Error(throwable)
}
