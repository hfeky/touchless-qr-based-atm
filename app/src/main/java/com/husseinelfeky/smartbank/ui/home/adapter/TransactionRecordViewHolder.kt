package com.husseinelfeky.smartbank.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.husseinelfeky.smartbank.databinding.ItemTransactionRecordBinding
import com.husseinelfeky.smartbank.model.TransactionRecord

class TransactionRecordViewHolder(
    private val binding: ItemTransactionRecordBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(transactionRecord: TransactionRecord) {
        binding.transactionRecord = transactionRecord
        binding.executePendingBindings()
    }

    companion object {
        fun create(container: ViewGroup): TransactionRecordViewHolder {
            val binding = ItemTransactionRecordBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
            return TransactionRecordViewHolder(binding)
        }
    }
}
