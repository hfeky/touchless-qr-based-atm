package com.husseinelfeky.smartbank.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QrCode(
    @SerializedName("atm_id")
    val atmId: String,
    @SerializedName("transaction_id")
    val transactionId: String
) : Parcelable
