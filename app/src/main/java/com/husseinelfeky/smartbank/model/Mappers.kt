package com.husseinelfeky.smartbank.model

import com.google.firebase.firestore.GeoPoint

fun GeoPoint.toLocation(): Location {
    return Location(latitude, longitude)
}
