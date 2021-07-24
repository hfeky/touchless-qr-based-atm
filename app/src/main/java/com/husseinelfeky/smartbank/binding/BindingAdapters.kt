package com.husseinelfeky.smartbank.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.model.TransactionRecord
import com.husseinelfeky.smartbank.model.TransactionStatus
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.util.formatAsCurrency

@BindingAdapter("transactionRecordAmount")
fun bindTransactionRecordAmount(textView: TextView, transactionRecord: TransactionRecord) {
    val context = textView.context
    if (transactionRecord.status == TransactionStatus.SUCCESS) {
        when (transactionRecord.type) {
            TransactionType.WITHDRAWAL -> {
                textView.text = context.getString(
                    R.string.format_withdrawn_amount,
                    transactionRecord.amount.formatAsCurrency(textView.context)
                )
                textView.setTextColor(ContextCompat.getColor(context, R.color.red_600))
            }
            TransactionType.DEPOSIT -> {
                textView.text = context.getString(
                    R.string.format_deposited_amount,
                    transactionRecord.amount.formatAsCurrency(textView.context)
                )
                textView.setTextColor(ContextCompat.getColor(context, R.color.green_500))
            }
        }
    } else {
        textView.text = context.getString(R.string.none)
    }
}

@BindingAdapter("transactionStatusImageSrc")
fun bindTransactionStatusImageSrc(imageView: ImageView, transactionStatus: TransactionStatus) {
    imageView.setImageResource(
        when (transactionStatus) {
            TransactionStatus.SUCCESS -> {
                R.drawable.ic_transaction_success
            }
            else -> {
                R.drawable.ic_transaction_error
            }
        }
    )
}

@BindingAdapter("transactionStatusText")
fun bindTransactionStatusText(textView: TextView, transactionRecord: TransactionRecord) {
    val context = textView.context
    textView.text = context.getString(
        when (transactionRecord.status) {
            TransactionStatus.SUCCESS -> {
                R.string.transaction_success
            }
            TransactionStatus.EXPIRED -> {
                R.string.transaction_expired
            }
            TransactionStatus.DENIED_DEPOSIT -> {
                R.string.transaction_denied_deposit
            }
            else -> {
                R.string.transaction_failure
            }
        },
        transactionRecord.formattedNumber
    )
}
