package com.husseinelfeky.smartbank.ui.transactioncompletion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.husseinelfeky.smartbank.databinding.FragmentTransactionCompleteBinding

class TransactionCompleteFragment : Fragment() {

    private lateinit var binding: FragmentTransactionCompleteBinding

    private val args: TransactionCompleteFragmentArgs by navArgs()

    private val backKeyListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigate(
                TransactionCompleteFragmentDirections.actionTransactionCompleteFragmentToHomeFragment()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionCompleteBinding.inflate(inflater, container, false)
        binding.transactionRecord = args.transactionRecord
        initBackKeyListener()
        initListeners()
        return binding.root
    }

    private fun initBackKeyListener() {
        requireActivity().onBackPressedDispatcher.addCallback(backKeyListener)
    }

    private fun initListeners() {
        binding.btnOk.setOnClickListener {
            findNavController().navigate(
                TransactionCompleteFragmentDirections.actionTransactionCompleteFragmentToHomeFragment()
            )
        }
    }

    override fun onDestroyView() {
        backKeyListener.remove()
        super.onDestroyView()
    }
}
