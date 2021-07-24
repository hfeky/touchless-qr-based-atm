package com.husseinelfeky.smartbank.network

import com.husseinelfeky.smartbank.exception.InternalServerException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Wrap the response with a [Result] and handle success, error and loading status.
 */
suspend fun <T> FlowCollector<Result<T>>.getResult(
    request: suspend () -> Response<T>,
    errorHandler: ((ErrorResponse) -> Result<T>?)? = null
) {
    emit(Result.loading())

    try {
        val response = request.invoke()

        if (response.isSuccessful) {
            emit(Result.success(response.body()))
        } else {
            val errorResponse = convertErrorBody(response.errorBody())
            if (errorResponse != null) {
                if (errorResponse.code == "internal_error") {
                    emit(Result.error(errorResponse.message, Error(InternalServerException())))
                    return
                }
                if (errorHandler != null) {
                    val errorResult = errorHandler(errorResponse)
                    if (errorResult != null) {
                        emit(errorResult)
                        return
                    }
                }
            }
            emit(Result.error("An unknown error occurred.", null))
        }
    } catch (e: IOException) {
        emit(Result.error("No internet connection is available.", Error(e)))
    } catch (e: Throwable) {
        emit(Result.error("An unknown error occurred.", Error(e)))
    }
}

/**
 * Convert [HttpException] to [ErrorResponse] if possible.
 */
private fun convertErrorBody(errorBody: okhttp3.ResponseBody?): ErrorResponse? {
    return try {
        errorBody?.let {
            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
            moshiAdapter.fromJson(it.string())
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Map the [Flow] result to another result.
 */
fun <T, R> Flow<Result<T>>.mapResultTo(mapper: (T?) -> R?): Flow<Result<R>> {
    return map {
        when (it.status) {
            Result.Status.LOADING -> Result.loading(mapper.invoke(it.data))
            Result.Status.SUCCESS -> Result.success(mapper.invoke(it.data))
            Result.Status.ERROR -> Result.error(it.message!!, it.error)
        }
    }
}
