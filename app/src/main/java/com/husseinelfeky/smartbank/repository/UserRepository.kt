package com.husseinelfeky.smartbank.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.husseinelfeky.smartbank.Firestore
import com.husseinelfeky.smartbank.model.TransactionRecord
import com.husseinelfeky.smartbank.model.TransactionStatus
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firebaseFirestore = FirebaseFirestore.getInstance()

    private fun getFirebaseUser(): FirebaseUser {
        return firebaseAuth.currentUser ?: throw IllegalAccessException("User is not logged in.")
    }

    private fun getUserName(): String {
        return getFirebaseUser().displayName
            ?: throw IllegalAccessException("User does not have a display name.")
    }

    suspend fun getUserData(): User {
        return firebaseFirestore.collection(Firestore.Collection.USERS)
            .document(getFirebaseUser().uid)
            .get()
            .await()
            .run {
                User(
                    getUserName(),
                    this.getLong(Firestore.Field.AVAILABLE_BALANCE)!!,
                    getUserTransactions()
                )
            }
    }

    private suspend fun getUserTransactions(): List<TransactionRecord> {
        return firebaseFirestore.collection(Firestore.Collection.USERS)
            .document(getFirebaseUser().uid)
            .collection(Firestore.Collection.TRANSACTIONS)
            .whereEqualTo(Firestore.Field.STATUS, TransactionStatus.SUCCESS.code)
            .get()
            .await()
            .run {
                this.documents.map { document ->
                    TransactionRecord(
                        document.getLong(Firestore.Field.NUMBER)!!,
                        document.getDate(Firestore.Field.TIME_COMPLETED),
                        document.getLong(Firestore.Field.AMOUNT)!!,
                        TransactionType.get(document.getString(Firestore.Field.TYPE)!!),
                        TransactionStatus.get(document.getString(Firestore.Field.STATUS)!!),
                        document.getString(Firestore.Field.ATM_NAME)!!
                    )
                }.sortedByDescending { it.timeCompleted }
            }
    }
}
