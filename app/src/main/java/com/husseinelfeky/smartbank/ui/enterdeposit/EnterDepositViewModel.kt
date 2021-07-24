package com.husseinelfeky.smartbank.ui.enterdeposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.husseinelfeky.smartbank.core.SingleLiveEvent
import com.husseinelfeky.smartbank.exception.ExpiredTransactionException
import com.husseinelfeky.smartbank.exception.InvalidTransactionException
import com.husseinelfeky.smartbank.model.QrCode
import com.husseinelfeky.smartbank.model.TransactionStatus
import com.husseinelfeky.smartbank.network.Result
import com.husseinelfeky.smartbank.repository.TransactionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

class EnterDepositViewModel(
    private val transactionRepository: TransactionRepository,
    private val qrCode: QrCode
) : ViewModel() {

    val enterDepositEvent = SingleLiveEvent<EnterDepositEvent>()

    private lateinit var transactionSnapshotListener: ListenerRegistration

    init {
        observeTransaction(qrCode.transactionId)
    }

    private fun observeTransaction(transactionId: String) {
        transactionSnapshotListener = transactionRepository.getTransaction(
            transactionId
        ) { transactionRecord ->
            when (transactionRecord.status) {
                TransactionStatus.PENDING_DEPOSIT_CONFIRMATION -> {
                    enterDepositEvent.value = EnterDepositEvent.ShowConfirmationDialog(
                        transactionRecord
                    )
                }
                TransactionStatus.SUCCESS, TransactionStatus.FAILED, TransactionStatus.EXPIRED, TransactionStatus.DENIED_DEPOSIT -> {
                    enterDepositEvent.value = EnterDepositEvent.ProceedWithTransaction(
                        transactionRecord
                    )
                }
                else -> {
                    // Do nothing.
                }
            }
        }
    }

    fun confirmDeposit(confirmDeposit: Boolean) {
        viewModelScope.launch {
            enterDepositEvent.value = EnterDepositEvent.RespondingToDepositConfirmation

            transactionRepository.confirmDeposit(qrCode, confirmDeposit).collect {
                when (it.status) {
                    Result.Status.SUCCESS -> {
                        enterDepositEvent.value = EnterDepositEvent.ProceedWithTransaction(
                            it.requireData()
                        )
                    }
                    Result.Status.ERROR -> {
                        it.error?.cause?.printStackTrace()
                        enterDepositEvent.value = when (val error = it.error?.cause) {
                            is IOException -> {
                                EnterDepositEvent.NetworkError(error)
                            }
                            is InvalidTransactionException -> {
                                EnterDepositEvent.InvalidTransactionError(error)
                            }
                            is ExpiredTransactionException -> {
                                EnterDepositEvent.ExpiredTransactionError(error)
                            }
                            else -> {
                                EnterDepositEvent.Error(error)
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
