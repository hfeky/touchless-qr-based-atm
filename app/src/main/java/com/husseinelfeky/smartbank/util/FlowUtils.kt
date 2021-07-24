package com.husseinelfeky.smartbank.util

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Collect the [Flow] to a [MutableLiveData].
 */
suspend fun <T> Flow<T>.collectTo(liveData: MutableLiveData<T>) {
    collect { liveData.value = it }
}

/**
 * Perform throttleFirst on a [Flow].
 */
suspend fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> {
    return flow {
        var windowStartTime = System.currentTimeMillis()
        var emitted = false

        collect { value ->
            val currentTime = System.currentTimeMillis()
            val delta = currentTime - windowStartTime

            if (delta >= windowDuration) {
                windowStartTime += delta / windowDuration * windowDuration
                emitted = false
            }

            if (!emitted) {
                emit(value)
                emitted = true
            }
        }
    }
}
