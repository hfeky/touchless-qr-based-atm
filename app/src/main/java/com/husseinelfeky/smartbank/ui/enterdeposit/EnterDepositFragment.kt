package com.husseinelfeky.smartbank.ui.enterdeposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentEnterDepositBinding
import com.husseinelfeky.smartbank.util.buildProgressDialog
import com.husseinelfeky.smartbank.util.formatAsCurrency
import com.kaopiz.kprogresshud.KProgressHUD
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EnterDepositFragment : Fragment() {

    private lateinit var binding: FragmentEnterDepositBinding

    private val args: EnterDepositFragmentArgs by navArgs()

    private val viewModel: EnterDepositViewModel by viewModel {
        parametersOf(args.qrCode)
    }

    private val backKeyListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.message_cancel_transaction)
                .setPositiveButton(R.string.ok) { _, _ ->
                    findNavController().navigate(
                        EnterDepositFragmentDirections.actionEnterDepositFragmentToHomeFragment()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterDepositBinding.inflate(inflater, container, false)
        initView()
        initBackKeyListener()
        initObservers()
        return binding.root
    }

    private fun initView() {
        binding.tvAtmName.text = getString(R.string.format_you_are_now_at, args.atm.name)
    }

    private fun initBackKeyListener() {
        requireActivity().onBackPressedDispatcher.addCallback(backKeyListener)
    }

    private fun initObservers() {
        viewModel.enterDepositEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is EnterDepositEvent.ShowConfirmationDialog -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.deposit_confirmation)
                        .setMessage(
                            getString(
                                R.string.message_confirm_deposit,
                                event.transactionRecord.amount.formatAsCurrency(requireContext())
                            )
                        )
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm) { _, _ ->
                            viewModel.confirmDeposit(true)
                        }
                        .setNegativeButton(R.string.deny) { _, _ ->
                            viewModel.confirmDeposit(false)
                        }
                        .show()
                }
                is EnterDepositEvent.RespondingToDepositConfirmation -> {
                    progressDialog.show()
                }
                is EnterDepositEvent.ProceedWithTransaction -> {
                    progressDialog.dismiss()
                    findNavController().navigate(
                        EnterDepositFragmentDirections.actionEnterDepositFragmentToTransactionCompleteFragment(
                            event.transactionRecord
                        )
                    )
                }
                is EnterDepositEvent.ExpiredTransactionError -> {
                    progressDialog.dismiss()
                    showHomeNavigationDialog(getString(R.string.error_expired_transaction))
                }
                is EnterDepositEvent.Error -> {
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
                    EnterDepositFragmentDirections.actionEnterDepositFragmentToHomeFragment()
                )
            }
            .show()
    }

    override fun onDestroyView() {
        backKeyListener.remove()
        super.onDestroyView()
    }
}
