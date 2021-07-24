package com.husseinelfeky.smartbank.koin

import com.husseinelfeky.smartbank.model.Atm
import com.husseinelfeky.smartbank.model.QrCode
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.ui.enterdeposit.EnterDepositViewModel
import com.husseinelfeky.smartbank.ui.home.HomeViewModel
import com.husseinelfeky.smartbank.ui.login.LoginViewModel
import com.husseinelfeky.smartbank.ui.otpverification.OtpVerificationViewModel
import com.husseinelfeky.smartbank.ui.qrcode.QrCodeScannerViewModel
import com.husseinelfeky.smartbank.ui.transaction.deposit.DepositViewModel
import com.husseinelfeky.smartbank.ui.transaction.withdraw.WithdrawViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelsModule = module {
    viewModel {
        LoginViewModel(
            authRepository = get()
        )
    }
    viewModel {
        HomeViewModel(
            userRepository = get(),
            atmRepository = get()
        )
    }
    viewModel {
        WithdrawViewModel()
    }
    viewModel {
        DepositViewModel()
    }
    viewModel { (atm: Atm, transactionType: TransactionType, amount: Long) ->
        QrCodeScannerViewModel(
            transactionRepository = get(),
            atm = atm,
            transactionType = transactionType,
            amount = amount
        )
    }
    viewModel { (qrCode: QrCode) ->
        OtpVerificationViewModel(
            transactionRepository = get(),
            qrCode = qrCode
        )
    }
    viewModel { (qrCode: QrCode) ->
        EnterDepositViewModel(
            transactionRepository = get(),
            qrCode = qrCode
        )
    }
}
