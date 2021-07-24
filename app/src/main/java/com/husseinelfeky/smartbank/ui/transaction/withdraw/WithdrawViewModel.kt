package com.husseinelfeky.smartbank.ui.transaction.withdraw

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WithdrawViewModel : ViewModel() {

    private val _amountFlow = MutableStateFlow(0L)
    val amountFlow: StateFlow<Long>
        get() = _amountFlow

    fun setWithdrawalAmount(amount: Long) {
        _amountFlow.value = amount
    }
}
