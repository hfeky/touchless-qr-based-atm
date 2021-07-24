package com.husseinelfeky.smartbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Atm(
    val id: String,
    val name: String,
    val location: Location
) : Parcelable
