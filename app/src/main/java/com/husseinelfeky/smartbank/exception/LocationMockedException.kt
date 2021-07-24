package com.husseinelfeky.smartbank.exception

import android.location.Location

class LocationMockedException(
    location: Location
) : Exception("User location with latitude ${location.latitude} and longitude ${location.longitude} was mocked.")
