package com.husseinelfeky.smartbank.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleMessage(val message: String)
