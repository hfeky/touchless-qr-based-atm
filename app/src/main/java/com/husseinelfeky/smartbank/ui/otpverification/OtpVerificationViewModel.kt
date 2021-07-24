package com.husseinelfeky.smartbank.ui.otpverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.husseinelfeky.smartbank.core.SingleLiveEvent
import com.husseinelfeky.smartbank.exception.ExpiredTransactionException
import com.husseinelfeky.smartbank.exception.IncorrectOtpException
import com.husseinelfeky.smartbank.exception.InvalidTransactionException
import com.husseinelfeky.smartbank.model.QrCode
import com.husseinelfeky.smartbank.model.TransactionStatus
import com.husseinelfeky.smartbank.network.Result
import com.husseinelfeky.smartbank.repository.TransactionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

class OtpVerificationViewModel(
    private val transactionRepository: TransactionRepository,
    private val qrCode: QrCode
) : ViewModel() {

    val otpVerificationEvent = SingleLiveEvent<OtpVerificationEvent>()

    private lateinit var transactionSnapshotListener: ListenerRegistration

    init {
        observeTransactionExpiry(qrCode.transactionId)
    }

    private fun observeTransactionExpiry(transactionId: String) {
        transactionSnapshotListener = transactionRepository.getTransaction(
            transactionId
        ) { transactionRecord ->
            when (transactionRecord.status) {
                TransactionStatus.EXPIRED -> {
                    otpVerificationEvent.value = OtpVerificationEvent.ProceedWithTransaction(
                        transactionRecord
                    )
                }
                else -> {
                    // Do nothing.
                }
            }
        }
    }

    fun generateRandomKeypad(): List<String> {
        return (0..9).shuffled().map { it.toString() }
    }

    fun verifyTransactionOtp(otp: String) {
        viewModelScope.launch {
            otpVerificationEvent.value = OtpVerificationEvent.ProcessingOtp

            transactionRepository.verifyTransactionOtp(qrCode, otp).collect {
                when (it.status) {
                    Result.Status.SUCCESS -> {
                        otpVerificationEvent.value = OtpVerificationEvent.ProceedWithTransaction(
                            it.requireData()
                        )
                    }
                    Result.Status.ERROR -> {
                        it.error?.cause?.printStackTrace()
                        otpVerificationEvent.value = when (val error = it.error?.cause) {
                            is IOException -> {
                                OtpVerificationEvent.NetworkError(error)
                            }
                            is InvalidTransactionException -> {
                                OtpVerificationEvent.InvalidTransactionError(error)
                            }
                            is ExpiredTransactionException -> {
                                OtpVerificationEvent.ExpiredTransactionError(error)
                            }
                            is IncorrectOtpException -> {
                                OtpVerificationEvent.IncorrectOtpError(error)
                            }
                            else -> {
                                OtpVerificationEvent.Error(error)
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

    override fun onCleared() {
        transactionSnapshotListener.remove()
        super.onCleared()
    }
}
