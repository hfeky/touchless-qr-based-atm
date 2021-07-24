package com.husseinelfeky.smartbank.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import timber.log.Timber

class LocationGetter(private val context: Context) {

    private val flpClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun checkDeviceLocationSettings(
        context: Context,
        resultLauncher: ActivityResultLauncher<IntentSenderRequest>,
        resolve: Boolean = true,
        onSuccessCallback: (() -> Unit)? = null,
        onFailureCallback: (() -> Unit)? = null
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(context)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnSuccessListener {
            onSuccessCallback?.invoke()
        }

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    resultLauncher.launch(
                        IntentSenderRequest.Builder(exception.resolution.intentSender).build()
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error getting location settings resolution: %s", sendEx.message)
                }
            } else {
                onFailureCallback?.invoke()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(locationListener: (Location) -> Unit) {
        flpClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            if (location == null) {
                requestNewLocationData(locationListener)
            } else {
                locationListener.invoke(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(locationListener: (Location) -> Unit) {
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationListener.invoke(locationResult.lastLocation)
            }
        }

        with(LocationRequest.create()) {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
            flpClient.requestLocationUpdates(this, locationCallback, Looper.getMainLooper())
        }
    }

    companion object {
        fun isLocationMocked(location: Location): Boolean {
            return location.isFromMockProvider
        }
    }
}
