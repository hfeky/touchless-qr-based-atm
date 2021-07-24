package com.husseinelfeky.smartbank.ui.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentQrCodeScannerBinding
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.util.buildProgressDialog
import com.husseinelfeky.smartbank.util.formatAsCurrency
import com.husseinelfeky.smartbank.util.showError
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
@FlowPreview
class QrCodeScannerFragment : Fragment() {

    private lateinit var binding: FragmentQrCodeScannerBinding

    private val args: QrCodeScannerFragmentArgs by navArgs()

    private val viewModel: QrCodeScannerViewModel by viewModel {
        parametersOf(args.atm, args.transactionType, args.amount)
    }

    private val progressDialog: KProgressHUD by lazy {
        buildProgressDialog()
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            viewModel.processScannedQrCode(result)
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>) {
            // Do nothing.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrCodeScannerBinding.inflate(inflater, container, false)
        initView()
        initObservers()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            barcodeView.setStatusText(getString(R.string.scan_qr_code))
            val intentIntegrator = IntentIntegrator(activity)
            barcodeView.initializeFromIntent(intentIntegrator.createScanIntent())
            barcodeView.decodeContinuous(callback)
        }

        requireActivity().title = when (args.transactionType) {
            TransactionType.WITHDRAWAL -> {
                getString(
                    R.string.format_withdraw_amount,
                    args.amount.formatAsCurrency(requireContext())
                )
            }
            TransactionType.DEPOSIT -> {
                getString(R.string.label_deposit)
            }
        }
    }

    private fun initObservers() {
        viewModel.qrCodeScanEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is QrCodeScanEvent.ProcessingCode -> {
                    progressDialog.show()
                }
                is QrCodeScanEvent.ProceedWithTransaction -> {
                    progressDialog.dismiss()
                    findNavController().navigate(
                        QrCodeScannerFragmentDirections.actionQrCodeScannerFragmentToOtpVerificationFragment(
                            event.qrCode,
                            args.atm
                        )
                    )
                }
                is QrCodeScanEvent.InvalidQrCodeError -> {
                    showError(R.string.error_invalid_qr_code)
                }
                is QrCodeScanEvent.InvalidAtmError -> {
                    showError(R.string.error_invalid_atm)
                }
                is QrCodeScanEvent.AccountBlockedError -> {
                    progressDialog.dismiss()
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.security_notice)
                        .setMessage(R.string.error_account_blocked)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            findNavController().navigate(
                                QrCodeScannerFragmentDirections.actionQrCodeScannerFragmentToHomeFragment()
                            )
                        }
                        .show()
                }
                is QrCodeScanEvent.ExpiredQrCodeError -> {
                    progressDialog.dismiss()
                    showError(R.string.error_expired_qr_code)
                }
                is QrCodeScanEvent.InvalidAmountError -> {
                    progressDialog.dismiss()
                    showError(R.string.error_invalid_amount)
                }
                is QrCodeScanEvent.Error -> {
                    progressDialog.dismiss()
                    showError(event.throwable)
                }
            }
        }
    }

    override fun onResume() {
        binding.barcodeView.resume()
        super.onResume()
    }

    override fun onPause() {
        binding.barcodeView.pause()
        super.onPause()
    }
}
