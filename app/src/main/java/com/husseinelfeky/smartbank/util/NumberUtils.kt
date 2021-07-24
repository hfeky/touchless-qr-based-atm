package com.husseinelfeky.smartbank.util

import android.content.Context
import com.husseinelfeky.smartbank.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Format the amount as EGP currency to either 2 units or no unit.
 *
 * Examples:
 * 1 -> 1 EGP
 * 1.2 -> 1.20 EGP
 * 1.23 -> 1.23 EGP
 * 1.234 -> 1.23 EGP
 *
 * @param value, the value to be formatted as a price.
 *
 * @return the correct format as a [String] as described above.
 */
fun Number.formatAsCurrency(context: Context): String {
    val formatterSymbols = DecimalFormatSymbols(Locale.ENGLISH)

    val twoUnitsFormatter = DecimalFormat("#,##0.00", formatterSymbols)
    val twoUnitsString = twoUnitsFormatter.format(this)

    // Check if the value can be an integer.
    return if (twoUnitsString.substring(twoUnitsString.length - 2) == "00") {
        // The amount can be represented as an integer. Format the amount with no unit.
        val noUnitString = twoUnitsString.substring(0, twoUnitsString.length - 3)
        context.getString(R.string.format_currency, noUnitString)
    } else {
        // The amount is a double. Format the amount with 2 units.
        context.getString(R.string.format_currency, twoUnitsString)
    }
}
