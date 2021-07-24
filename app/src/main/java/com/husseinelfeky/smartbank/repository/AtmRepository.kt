package com.husseinelfeky.smartbank.repository

import android.location.Location
import android.location.LocationManager
import com.ckdroid.geofirequery.GeoQuery
import com.ckdroid.geofirequery.model.Distance
import com.ckdroid.geofirequery.utils.BoundingBoxUtils
import com.husseinelfeky.smartbank.Firestore
import com.husseinelfeky.smartbank.exception.NoAtmFoundException
import com.husseinelfeky.smartbank.model.Atm
import com.husseinelfeky.smartbank.model.toLocation
import kotlinx.coroutines.tasks.await

class AtmRepository {

    suspend fun getAtm(latitude: Double, longitude: Double): Atm {
        return GeoQuery().collection(Firestore.Collection.ATMS)
            .whereNearToLocation(
                Location(LocationManager.GPS_PROVIDER).apply {
                    setLatitude(latitude)
                    setLongitude(longitude)
                },
                Distance(ATM_LOCATION_RANGE, BoundingBoxUtils.DistanceUnit.KILOMETERS),
                Firestore.Field.LOCATION
            )
            .limit(1)
            .query
            .get()
            .await()
            .run {
                if (this.documents.isEmpty()) {
                    throw NoAtmFoundException(latitude, longitude)
                } else {
                    this.documents[0].let { document ->
                        Atm(
                            document.id,
                            document.getString(Firestore.Field.NAME)!!,
                            document.getGeoPoint(Firestore.Field.GEO_LOCATION)!!.toLocation()
                        )
                    }
                }
            }
    }

    companion object {
        private const val ATM_LOCATION_RANGE = 0.1
    }
}
