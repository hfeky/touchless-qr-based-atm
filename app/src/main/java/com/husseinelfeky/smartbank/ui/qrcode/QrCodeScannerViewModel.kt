package com.husseinelfeky.smartbank.ui.qrcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.husseinelfeky.smartbank.core.SingleLiveEvent
import com.husseinelfeky.smartbank.exception.AccountBlockedException
import com.husseinelfeky.smartbank.exception.ExpiredQrCodeException
import com.husseinelfeky.smartbank.exception.InvalidAmountException
import com.husseinelfeky.smartbank.exception.InvalidAtmException
import com.husseinelfeky.smartbank.model.Atm
import com.husseinelfeky.smartbank.model.QrCode
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.network.Result
import com.husseinelfeky.smartbank.repository.TransactionRepository
import com.husseinelfeky.smartbank.util.throttleFirst
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

@ExperimentalCoroutinesApi
@FlowPreview
class QrCodeScannerViewModel(
    private val transactionRepository: TransactionRepository,
    atm: Atm,
    private val transactionType: TransactionType,
    private val amount: Long
) : ViewModel() {

    val qrCodeScanEvent = SingleLiveEvent<QrCodeScanEvent>()

    private val qrCodeChannel = MutableSharedFlow<BarcodeResult>()

    private var hasTransactionStarted = false

    init {
        viewModelScope.launch {
            qrCodeChannel.throttleFirst(3500)
                .takeIf { !hasTransactionStarted }
                ?.collect {
                    runCatching {
                        GsonBuilder().create().fromJson(it.text, QrCode::class.java)
                    }.onSuccess {
                        if (it.atmId == atm.id) {
                            qrCodeScanEvent.value = QrCodeScanEvent.ProcessingCode(it)
                            startTransaction(it)
                        } else {
                            qrCodeScanEvent.value = QrCodeScanEvent.InvalidAtmError(
                                InvalidAtmException(
                                    it.atmId,
                                    atm.id
                                )
                            )
                        }
                    }.onFailure {
                        qrCodeScanEvent.value = QrCodeScanEvent.InvalidQrCodeError(it)
                    }
                }
        }
    }

    fun processScannedQrCode(result: BarcodeResult) {
        viewModelScope.launch {
            if (qrCodeScanEvent.value !is QrCodeScanEvent.AccountBlockedError) {
                qrCodeChannel.emit(result)
            }
        }
    }

    private fun startTransaction(qrCode: QrCode) {
        viewModelScope.launch {
            hasTransactionStarted = true

            when (transactionType) {
                TransactionType.WITHDRAWAL -> {
                    transactionRepository.startWithdrawalTransaction(
                        qrCode,
                        transactionType,
                        amount
                    )
                }
                TransactionType.DEPOSIT -> {
                    transactionRepository.startDepositTransaction(qrCode, transactionType)
                }
            }.collect {
                hasTransactionStarted = false

                when (it.status) {
                    Result.Status.SUCCESS -> {
                        qrCodeScanEvent.value = QrCodeScanEvent.ProceedWithTransaction(qrCode)
                    }
                    Result.Status.ERROR -> {
                        qrCodeScanEvent.value = when (val error = it.error?.cause) {
                            is IOException -> {
                                QrCodeScanEvent.NetworkError(error)
                            }
                            is AccountBlockedException -> {
                                QrCodeScanEvent.AccountBlockedError(error)
                            }
                            is InvalidAtmException -> {
                                QrCodeScanEvent.InvalidAtmError(error)
                            }
                            is ExpiredQrCodeException -> {
                                QrCodeScanEvent.ExpiredQrCodeError(error)
                            }
                            is InvalidAmountException -> {
                                QrCodeScanEvent.InvalidAmountError(error)
                            }
                            else -> {
                                QrCodeScanEvent.Error(error)
                            }
                        }
                    }
                    else -> {
                        // Do nothing.
                    }
                }
            }
        }
    }
}
