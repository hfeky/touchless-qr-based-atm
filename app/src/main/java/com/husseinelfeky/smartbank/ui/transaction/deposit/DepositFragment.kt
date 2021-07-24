package com.husseinelfeky.smartbank.ui.transaction.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentDepositBinding
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.ui.transaction.TransactionFragment

class DepositFragment : TransactionFragment() {

    private lateinit var binding: FragmentDepositBinding

    private val args: DepositFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositBinding.inflate(inflater, container, false)
        initView()
        initListeners()
        return binding.root
    }

    private fun initView() {
        binding.tvAtmName.text = getString(R.string.format_you_are_now_at, args.atm.name)
    }

    private fun initListeners() {
        binding.btnProceed.setOnClickListener {
            requestCameraPermissionThenNavigate()
        }
    }

    override fun navigateToQrCodeScannerScreen() {
        findNavController().navigate(
            DepositFragmentDirections.actionDepositFragmentToQrCodeScannerFragment(
                args.atm,
                TransactionType.DEPOSIT
            )
        )
    }
}
