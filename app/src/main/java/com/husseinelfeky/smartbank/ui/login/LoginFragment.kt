package com.husseinelfeky.smartbank.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.databinding.FragmentLoginBinding
import com.husseinelfeky.smartbank.util.InputUtils
import com.husseinelfeky.smartbank.util.showError
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        initTextValidators()
        initListeners()
        initObservers()
        return binding.root
    }

    private fun initTextValidators() {
        binding.apply {
            efEmail.setTextValidator { text ->
                when {
                    text.isEmpty() -> {
                        efEmail.showError(R.string.error_field_required)
                        false
                    }
                    !InputUtils.isValidEmailAddress(text) -> {
                        efEmail.showError(R.string.error_invalid_email)
                        false
                    }
                    else -> {
                        efEmail.hideError()
                        true
                    }
                }
            }

            efPassword.setTextValidator { text ->
                when {
                    text.isEmpty() -> {
                        efPassword.showError(R.string.error_field_required)
                        false
                    }
                    else -> {
                        efPassword.hideError()
                        true
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnLogIn.setOnClickListener {
            val email = binding.efEmail.getText().toString()
            val password = binding.efPassword.getText().toString()

            if (areAllFieldsFilledCorrectly()) {
                viewModel.logIn(email, password)
            }
        }
    }

    private fun initObservers() {
        viewModel.loginEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                LoginEvent.Loading -> {
                    binding.btnLogIn.showLoading()
                }
                is LoginEvent.Loaded -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    )
                }
                is LoginEvent.InvalidCredentialsError -> {
                    binding.btnLogIn.hideLoading()
                    showError(R.string.error_invalid_credentials)
                }
                is LoginEvent.Error -> {
                    binding.btnLogIn.hideLoading()
                    showError(event.throwable)
                }
            }
        }
    }

    private fun areAllFieldsFilledCorrectly(): Boolean {
        return binding.efEmail.validateInput() and binding.efPassword.validateInput()
    }
}
