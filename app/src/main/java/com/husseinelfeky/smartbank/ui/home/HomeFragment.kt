package com.husseinelfeky.smartbank.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentHomeBinding
import com.husseinelfeky.smartbank.model.Atm
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.ui.home.adapter.TransactionsAdapter
import com.husseinelfeky.smartbank.util.*
import com.kaopiz.kprogresshud.KProgressHUD
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModel()

    private val locationGetter: LocationGetter by lazy {
        LocationGetter(requireContext())
    }

    private val progressDialog: KProgressHUD by lazy {
        buildProgressDialog()
    }

    private val transactionsAdapter = TransactionsAdapter()

    private val locationPermissionWithdrawResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                fetchUserLocationThenStartTransaction(TransactionType.WITHDRAWAL)
            } else {
                showError(R.string.error_location_permission_needed_withdraw, R.string.settings) {
                    Navigator.navigateToAppSettings(requireContext())
                }
            }
        }

    private val locationPermissionDepositResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                fetchUserLocationThenStartTransaction(TransactionType.DEPOSIT)
            } else {
                showError(R.string.error_location_permission_needed_deposit, R.string.settings) {
                    Navigator.navigateToAppSettings(requireContext())
                }
            }
        }

    private val deviceLocationWithdrawResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            fetchUserLocationThenStartTransaction(TransactionType.WITHDRAWAL, false)
        }

    private val deviceLocationDepositResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            fetchUserLocationThenStartTransaction(TransactionType.DEPOSIT, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initView()
        initListeners()
        initObservers()
        return binding.root
    }

    private fun initView() {
        binding.rvTransactionsHistory.apply {
            adapter = transactionsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        }
    }

    private fun initListeners() {
        binding.apply {
            btnWithdraw.setOnClickListener {
                if (isLocationPermissionGranted()) {
                    fetchUserLocationThenStartTransaction(TransactionType.WITHDRAWAL)
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.rationale_location_permission)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                locationPermissionWithdrawResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            .show()
                    } else {
                        locationPermissionWithdrawResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }

            btnDeposit.setOnClickListener {
                if (isLocationPermissionGranted()) {
                    fetchUserLocationThenStartTransaction(TransactionType.DEPOSIT)
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.rationale_location_permission)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                locationPermissionDepositResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            .show()
                    } else {
                        locationPermissionDepositResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeState.Loading -> {
                    binding.apply {
                        layoutNoTransactions.visibility = View.GONE
                        pbLoading.visibility = View.VISIBLE
                    }
                }
                is HomeState.Loaded -> {
                    binding.apply {
                        val user = state.user

                        pbLoading.visibility = View.GONE
                        layoutNoTransactions.isVisible = user.transactionsHistory.isEmpty()
                        transactionsAdapter.submitList(user.transactionsHistory)

                        tvWelcome.text = getString(R.string.format_welcome, user.name)
                        tvBalance.text = user.availableBalance.formatAsCurrency(requireContext())
                    }
                }
                is HomeState.Error -> {
                    binding.apply {
                        pbLoading.visibility = View.GONE
                        layoutNoTransactions.visibility = View.GONE
                    }

                    showError(state.throwable)
                }
            }
        }

        viewModel.transactionEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                TransactionEvent.FetchingUserLocation -> {
                    progressDialog.show()
                }
                is TransactionEvent.ProceedWithdrawal -> {
                    progressDialog.dismiss()
                    navigateToWithdrawScreen(event.atm)
                }
                is TransactionEvent.ProceedDeposit -> {
                    progressDialog.dismiss()
                    navigateToDepositScreen(event.atm)
                }
                is TransactionEvent.LocationMockedError -> {
                    progressDialog.dismiss()
                    showError(R.string.error_location_mocked)
                }
                is TransactionEvent.NoAtmFoundError -> {
                    progressDialog.dismiss()
                    showError(
                        when (event.transactionType) {
                            TransactionType.WITHDRAWAL -> R.string.error_no_atm_found_withdraw
                            TransactionType.DEPOSIT -> R.string.error_no_atm_found_deposit
                        }
                    )
                }
                is TransactionEvent.Error -> {
                    progressDialog.dismiss()
                    showError(event.throwable)
                }
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun fetchUserLocationThenStartTransaction(
        transactionType: TransactionType,
        resolve: Boolean = true
    ) {
        locationGetter.checkDeviceLocationSettings(
            context = requireContext(),
            resultLauncher = when (transactionType) {
                TransactionType.WITHDRAWAL -> deviceLocationWithdrawResultLauncher
                TransactionType.DEPOSIT -> deviceLocationDepositResultLauncher
            },
            resolve = resolve,
            onSuccessCallback = {
                viewModel.startFetchingUserLocation()

                locationGetter.getLastLocation {
                    viewModel.getAtmNearbyUser(transactionType, it)
                }
            },
            onFailureCallback = {
                showError(
                    when (transactionType) {
                        TransactionType.WITHDRAWAL -> R.string.error_device_location_needed_withdraw
                        TransactionType.DEPOSIT -> R.string.error_device_location_needed_deposit
                    }
                )
            }
        )
    }

    private fun navigateToWithdrawScreen(atm: Atm) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToWithdrawFragment(atm)
        )
    }

    private fun navigateToDepositScreen(atm: Atm) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDepositFragment(atm)
        )
    }
}
