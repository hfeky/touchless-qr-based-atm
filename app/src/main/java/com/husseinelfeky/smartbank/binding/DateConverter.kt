package com.husseinelfeky.smartbank.binding

import androidx.databinding.InverseMethod
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)

    @InverseMethod("stringToDate")
    @JvmStatic
    fun dateToString(value: Date?): String {
        return value?.let { dateFormat.format(value) } ?: ""
    }

    @JvmStatic
    fun stringToDate(value: String): Date? {
        return if (value.isNotEmpty()) {
            dateFormat.parse(value)
        } else {
            null
        }
    }
}
