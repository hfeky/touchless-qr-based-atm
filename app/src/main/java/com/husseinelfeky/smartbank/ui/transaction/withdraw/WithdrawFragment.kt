package com.husseinelfeky.smartbank.ui.transaction.withdraw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentWithdrawBinding
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.ui.transaction.TransactionFragment
import com.husseinelfeky.smartbank.util.InputUtils

class WithdrawFragment : TransactionFragment() {

    private lateinit var binding: FragmentWithdrawBinding

    private val args: WithdrawFragmentArgs by navArgs()

    private val viewModel: WithdrawViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        initView()
        initTextValidators()
        initListeners()
        return binding.root
    }

    private fun initView() {
        binding.tvAtmName.text = getString(R.string.format_you_are_now_at, args.atm.name)
    }

    private fun initTextValidators() {
        binding.apply {
            binding.efEnterAmount.setTextValidator { text ->
                when {
                    text.isEmpty() -> {
                        efEnterAmount.showError(R.string.error_field_required)
                        false
                    }
                    !InputUtils.isAmountInValidRange(text.toInt()) -> {
                        efEnterAmount.showError(R.string.error_invalid_amount_range)
                        false
                    }
                    !InputUtils.isAmountDivisibleByTen(text.toInt()) -> {
                        efEnterAmount.showError(R.string.error_invalid_amount_divisibility)
                        false
                    }
                    else -> {
                        efEnterAmount.hideError()
                        true
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.apply {
            efEnterAmount.setEndIconOnClickListener {
                if (efEnterAmount.validateInput()) {
                    val amount = efEnterAmount.getText().toString().toLong()
                    requestCameraPermissionThenWithdraw(amount)
                }
            }

            btnWithdraw100.setOnClickListener {
                requestCameraPermissionThenWithdraw(100)
            }

            btnWithdraw200.setOnClickListener {
                requestCameraPermissionThenWithdraw(200)
            }

            btnWithdraw500.setOnClickListener {
                requestCameraPermissionThenWithdraw(500)
            }

            btnWithdraw1000.setOnClickListener {
                requestCameraPermissionThenWithdraw(1000)
            }

            btnWithdraw2000.setOnClickListener {
                requestCameraPermissionThenWithdraw(2000)
            }

            btnWithdraw3000.setOnClickListener {
                requestCameraPermissionThenWithdraw(3000)
            }

            btnWithdraw5000.setOnClickListener {
                requestCameraPermissionThenWithdraw(5000)
            }

            btnWithdraw6000.setOnClickListener {
                requestCameraPermissionThenWithdraw(6000)
            }
        }
    }

    private fun requestCameraPermissionThenWithdraw(amount: Long) {
        viewModel.setWithdrawalAmount(amount)
        requestCameraPermissionThenNavigate()
    }

    override fun navigateToQrCodeScannerScreen() {
        findNavController().navigate(
            WithdrawFragmentDirections.actionWithdrawFragmentToQrCodeScannerFragment(
                args.atm,
                TransactionType.WITHDRAWAL,
                viewModel.amountFlow.value
            )
        )
    }
}
