package com.husseinelfeky.smartbank.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinelfeky.smartbank.core.SingleLiveEvent
import com.husseinelfeky.smartbank.exception.LocationMockedException
import com.husseinelfeky.smartbank.exception.NoAtmFoundException
import com.husseinelfeky.smartbank.model.TransactionType
import com.husseinelfeky.smartbank.repository.AtmRepository
import com.husseinelfeky.smartbank.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val userRepository: UserRepository,
    private val atmRepository: AtmRepository
) : ViewModel() {

    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState>
        get() = _homeState

    val transactionEvent = SingleLiveEvent<TransactionEvent>()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            runCatching {
                userRepository.getUserData()
            }.onSuccess {
                _homeState.value = HomeState.Loaded(it)
            }.onFailure {
                _homeState.value = when (it) {
                    is IOException -> {
                        HomeState.NetworkError(it)
                    }
                    else -> {
                        HomeState.Error(it)
                    }
                }
            }
        }
    }

    fun getAtmNearbyUser(
        transactionType: TransactionType,
        location: Location
    ) {
        viewModelScope.launch {
            if (location.isFromMockProvider) {
                transactionEvent.value = TransactionEvent.LocationMockedError(
                    LocationMockedException(location)
                )
                return@launch
            }

            runCatching {
                atmRepository.getAtm(location.latitude, location.longitude)
            }.onSuccess {
                transactionEvent.value = if (transactionType == TransactionType.WITHDRAWAL) {
                    TransactionEvent.ProceedWithdrawal(it)
                } else {
                    TransactionEvent.ProceedDeposit(it)
                }
            }.onFailure {
                transactionEvent.value = when (it) {
                    is IOException -> {
                        TransactionEvent.NetworkError(it)
                    }
                    is NoAtmFoundException -> {
                        TransactionEvent.NoAtmFoundError(it, transactionType)
                    }
                    else -> {
                        TransactionEvent.Error(it)
                    }
                }
            }
        }
    }

    fun startFetchingUserLocation() {
        transactionEvent.value = TransactionEvent.FetchingUserLocation
    }
}
