package com.husseinelfeky.smartbank.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.husseinelfeky.smartbank.model.TransactionRecord
import com.husseinelfeky.smartbank.util.adapter.DifferentiableItemDiffUtil

class TransactionsAdapter : ListAdapter<TransactionRecord, TransactionRecordViewHolder>(
    DifferentiableItemDiffUtil.getInstance()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionRecordViewHolder {
        return TransactionRecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TransactionRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
