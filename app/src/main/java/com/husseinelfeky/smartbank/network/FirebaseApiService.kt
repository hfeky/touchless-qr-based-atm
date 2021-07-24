package com.husseinelfeky.smartbank.network

import com.husseinelfeky.smartbank.model.SimpleMessage
import com.husseinelfeky.smartbank.model.TransactionRecord
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseApiService {

    @POST("startTransaction")
    suspend fun startTransaction(
        @Query("userId") userId: String,
        @Query("atmId") atmId: String,
        @Query("transactionId") transactionId: String,
        @Query("transactionType") transactionType: String,
        @Query("amount") amount: Long
    ): Response<SimpleMessage>

    @POST("verifyTransactionOtp")
    suspend fun verifyTransactionOtp(
        @Query("userId") userId: String,
        @Query("atmId") atmId: String,
        @Query("transactionId") transactionId: String,
        @Query("otp") otp: String
    ): Response<TransactionRecord>

    @POST("confirmDeposit")
    suspend fun confirmDeposit(
        @Query("userId") userId: String,
        @Query("atmId") atmId: String,
        @Query("transactionId") transactionId: String,
        @Query("confirmDeposit") confirmDeposit: Boolean
    ): Response<TransactionRecord>
}
