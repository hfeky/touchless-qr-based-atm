package com.husseinelfeky.smartbank.model

import android.os.Parcelable
import com.husseinelfeky.smartbank.util.adapter.DifferentiableItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@JsonClass(generateAdapter = true)
@Parcelize
data class TransactionRecord(
    val number: Long,
    @Json(name = "time_completed")
    val timeCompleted: Date? = null,
    val amount: Long,
    val type: TransactionType,
    val status: TransactionStatus,
    @Json(name = "atm_name")
    val atmName: String
) : Parcelable, DifferentiableItem {

    val formattedNumber: String
        get() = StringBuilder().apply {
            val numberLength = number.toString().length
            while (length < 9 - numberLength) {
                append('0')
            }
        }.append(number).toString()

    override fun getUniqueIdentifier(): Any = number

    override fun getContent(): String = toString()
}
