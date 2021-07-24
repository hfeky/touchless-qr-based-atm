package com.husseinelfeky.smartbank.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "error_code")
    val code: String,
    val message: String
)
