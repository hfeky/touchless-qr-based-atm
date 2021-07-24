package com.husseinelfeky.smartbank.ui.home

import com.husseinelfeky.smartbank.model.User

sealed class HomeState {

    object Loading : HomeState()

    class Loaded(val user: User) : HomeState()

    open class Error(open val throwable: Throwable?) : HomeState()

    class NetworkError(override val throwable: Throwable) : Error(throwable)
}
