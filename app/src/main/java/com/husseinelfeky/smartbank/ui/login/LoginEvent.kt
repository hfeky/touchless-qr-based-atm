package com.husseinelfeky.smartbank.ui.login

import com.google.firebase.auth.FirebaseUser

sealed class LoginEvent {

    object Loading : LoginEvent()

    class Loaded(val user: FirebaseUser) : LoginEvent()

    open class Error(open val throwable: Throwable?) : LoginEvent()

    class NetworkError(override val throwable: Throwable) : Error(throwable)

    class InvalidCredentialsError(override val throwable: Throwable) : Error(throwable)
}
