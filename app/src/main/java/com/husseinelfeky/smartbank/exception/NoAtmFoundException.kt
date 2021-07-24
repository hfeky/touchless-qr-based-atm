package com.husseinelfeky.smartbank.exception

class NoAtmFoundException(
    latitude: Double,
    longitude: Double
) : Exception("No ATM was found nearby latitude $latitude and longitude $longitude.")
