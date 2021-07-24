package com.husseinelfeky.smartbank.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.husseinelfeky.smartbank.core.SingleLiveEvent
import com.husseinelfeky.smartbank.repository.AuthRepository
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val loginEvent = SingleLiveEvent<LoginEvent>()

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            loginEvent.value = LoginEvent.Loading

            runCatching {
                authRepository.logIn(email, password)
            }.onSuccess {
                val user = it.user
                loginEvent.value = if (user != null) {
                    LoginEvent.Loaded(user)
                } else {
                    LoginEvent.Error(UnknownError())
                }
            }.onFailure {
                loginEvent.value = when (it) {
                    is IOException -> {
                        LoginEvent.NetworkError(it)
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        LoginEvent.InvalidCredentialsError(it)
                    }
                    else -> {
                        LoginEvent.Error(it)
                    }
                }
            }
        }
    }
}
