package com.husseinelfeky.smartbank.ui.otpverification

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentOtpVerificationBinding
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.util.buildProgressDialog
import com.kaopiz.kprogresshud.KProgressHUD
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class OtpVerificationFragment : Fragment() {

    private lateinit var binding: FragmentOtpVerificationBinding

    private val args: OtpVerificationFragmentArgs by navArgs()

    private val viewModel: OtpVerificationViewModel by viewModel {
        parametersOf(args.qrCode)
    }

    private val backKeyListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.message_cancel_transaction)
                .setPositiveButton(R.string.ok) { _, _ ->
                    findNavController().navigate(
                        OtpVerificationFragmentDirections.actionOtpVerificationFragmentToHomeFragment()
                    )
                }
                .setNegativeButton(R.string.back) { _, _ ->
                    // Just dismiss the dialog.
                }
                .show()
        }
    }

    private val progressDialog: KProgressHUD by lazy {
        buildProgressDialog()
    }

    private lateinit var keypadNumbers: Array<Button>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        initView()
        initBackKeyListener()
        initObservers()
        return binding.root
    }

    private fun initView() {
        val randomKeypad = viewModel.generateRandomKeypad()

        binding.apply {
            keypadNumbers = arrayOf(
                btnKey0,
                btnKey1,
                btnKey2,
                btnKey3,
                btnKey4,
                btnKey5,
                btnKey6,
                btnKey7,
                btnKey8,
                btnKey9
            )

            keypadNumbers.forEachIndexed { index, button ->
                button.text = randomKeypad[index]
                button.setOnClickListener {
                    pinCodeView.sendKeyEvent(
                        KeyEvent.keyCodeFromString("KEYCODE_${button.text}")
                    )
                }
            }

            btnKeyBackspace.setOnClickListener {
                pinCodeView.sendKeyEvent(KeyEvent.KEYCODE_DEL)
            }

            pinCodeView.setOnCodeTypedListener {
                viewModel.verifyTransactionOtp(it)
            }
        }
    }

    private fun initBackKeyListener() {
        requireActivity().onBackPressedDispatcher.addCallback(backKeyListener)
    }

    private fun initObservers() {
        viewModel.otpVerificationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                OtpVerificationEvent.ProcessingOtp -> {
                    progressDialog.show()
                }
                is OtpVerificationEvent.ProceedWithTransaction -> {
                    progressDialog.dismiss()
                    findNavController().navigate(
                        if (event.transactionRecord.type == TransactionType.WITHDRAWAL) {
                            OtpVerificationFragmentDirections.actionOtpVerificationFragmentToTransactionCompleteFragment(
                                event.transactionRecord
                            )
                        } else {
                            OtpVerificationFragmentDirections.actionOtpVerificationFragmentToEnterDepositFragment(
                                args.atm,
                                args.qrCode
                            )
                        }
                    )
                }
                is OtpVerificationEvent.ExpiredTransactionError -> {
                    progressDialog.dismiss()
                    showHomeNavigationDialog(getString(R.string.error_expired_transaction))
                }
                is OtpVerificationEvent.IncorrectOtpError -> {
                    progressDialog.dismiss()
                    showHomeNavigationDialog(getString(R.string.error_incorrect_otp))
                }
                is OtpVerificationEvent.Error -> {
                    progressDialog.dismiss()
                    showHomeNavigationDialog(
                        event.throwable?.localizedMessage ?: "An unknown error has occurred."
                    )
                }
            }
        }
    }

    private fun showHomeNavigationDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { _, _ ->
                findNavController().navigate(
                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToHomeFragment()
                )
            }
            .show()
    }

    override fun onDestroyView() {
        backKeyListener.remove()
        super.onDestroyView()
    }
}
