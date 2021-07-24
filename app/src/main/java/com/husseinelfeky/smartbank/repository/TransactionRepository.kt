package com.husseinelfeky.smartbank.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.husseinelfeky.smartbank.Firestore
import com.husseinelfeky.smartbank.exception.*
import com.husseinelfeky.smartbank.model.*
import com.husseinelfeky.smartbank.network.FirebaseApiService
import com.husseinelfeky.smartbank.network.Result
import com.husseinelfeky.smartbank.network.getResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransactionRepository(private val firebaseApiService: FirebaseApiService) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firebaseFirestore = FirebaseFirestore.getInstance()

    private fun getFirebaseUser(): FirebaseUser {
        return firebaseAuth.currentUser ?: throw IllegalAccessException("User is not logged in.")
    }

    private suspend fun startTransaction(
        qrCode: QrCode,
        transactionType: TransactionType,
        amount: Long
    ): Flow<Result<SimpleMessage>> {
        return flow {
            getResult(
                request = {
                    firebaseApiService.startTransaction(
                        userId = getFirebaseUser().uid,
                        atmId = qrCode.atmId,
                        transactionId = qrCode.transactionId,
                        transactionType = transactionType.code,
                        amount = amount
                    )
                },
                errorHandler = {
                    when (it.code) {
                        "invalid_user" -> {
                            Result.error(it.message, Error(InvalidUserException()))
                        }
                        "blocked_account" -> {
                            Result.error(it.message, Error(AccountBlockedException()))
                        }
                        "invalid_atm" -> {
                            Result.error(it.message, Error(InvalidAtmException(qrCode.atmId)))
                        }
                        "expired_qr_code" -> {
                            Result.error(it.message, Error(ExpiredQrCodeException()))
                        }
                        "invalid_amount" -> {
                            Result.error(
                                it.message,
                                Error(InvalidAmountException(amount))
                            )
                        }
                        else -> null
                    }
                }
            )
        }
    }

    suspend fun startWithdrawalTransaction(
        qrCode: QrCode,
        transactionType: TransactionType,
        amount: Long
    ): Flow<Result<SimpleMessage>> {
        return startTransaction(qrCode, transactionType, amount)
    }

    suspend fun startDepositTransaction(
        qrCode: QrCode,
        transactionType: TransactionType
    ): Flow<Result<SimpleMessage>> {
        return startTransaction(qrCode, transactionType, 0)
    }

    suspend fun verifyTransactionOtp(
        qrCode: QrCode,
        otp: String
    ): Flow<Result<TransactionRecord>> {
        return flow {
            getResult(
                request = {
                    firebaseApiService.verifyTransactionOtp(
                        userId = getFirebaseUser().uid,
                        atmId = qrCode.atmId,
                        transactionId = qrCode.transactionId,
                        otp = otp
                    )
                },
                errorHandler = {
                    when (it.code) {
                        "invalid_user" -> {
                            Result.error(it.message, Error(InvalidUserException()))
                        }
                        "invalid_atm" -> {
                            Result.error(it.message, Error(InvalidAtmException(qrCode.atmId)))
                        }
                        "invalid_transaction" -> {
                            Result.error(
                                it.message,
                                Error(InvalidTransactionException(qrCode.transactionId))
                            )
                        }
                        "incorrect_otp" -> {
                            Result.error(it.message, Error(IncorrectOtpException()))
                        }
                        "expired_transaction" -> {
                            Result.error(
                                it.message,
                                Error(ExpiredTransactionException(qrCode.transactionId))
                            )
                        }
                        else -> null
                    }
                }
            )
        }
    }

    suspend fun confirmDeposit(
        qrCode: QrCode,
        confirmDeposit: Boolean
    ): Flow<Result<TransactionRecord>> {
        return flow {
            getResult(
                request = {
                    firebaseApiService.confirmDeposit(
                        userId = getFirebaseUser().uid,
                        atmId = qrCode.atmId,
                        transactionId = qrCode.transactionId,
                        confirmDeposit = confirmDeposit
                    )
                },
                errorHandler = {
                    when (it.code) {
                        "invalid_user" -> {
                            Result.error(it.message, Error(InvalidUserException()))
                        }
                        "invalid_atm" -> {
                            Result.error(it.message, Error(InvalidAtmException(qrCode.atmId)))
                        }
                        "invalid_transaction" -> {
                            Result.error(
                                it.message,
                                Error(InvalidTransactionException(qrCode.transactionId))
                            )
                        }
                        "expired_transaction" -> {
                            Result.error(
                                it.message,
                                Error(ExpiredTransactionException(qrCode.transactionId))
                            )
                        }
                        else -> null
                    }
                }
            )
        }
    }

    fun getTransaction(
        transactionId: String,
        onDocUpdated: (TransactionRecord) -> Unit
    ): ListenerRegistration {
        return firebaseFirestore.collection(Firestore.Collection.USERS)
            .document(getFirebaseUser().uid)
            .collection(Firestore.Collection.TRANSACTIONS)
            .document(transactionId)
            .addSnapshotListener { docSnapshot, _ ->
                if (docSnapshot != null) {
                    onDocUpdated(
                        TransactionRecord(
                            docSnapshot.getLong(Firestore.Field.NUMBER)!!,
                            docSnapshot.getDate(Firestore.Field.TIME_COMPLETED),
                            docSnapshot.getLong(Firestore.Field.AMOUNT)!!,
                            TransactionType.get(docSnapshot.getString(Firestore.Field.TYPE)!!),
                            TransactionStatus.get(docSnapshot.getString(Firestore.Field.STATUS)!!),
                            docSnapshot.getString(Firestore.Field.ATM_NAME)!!
                        )
                    )
                }
            }
    }
}
