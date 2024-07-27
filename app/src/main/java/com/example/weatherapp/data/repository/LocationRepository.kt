package com.example.weatherapp.data.repository

import android.location.Location
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun updateLocation(callback: (LocationResult) -> Unit)

    fun stopLocationUpdate()

    fun hasLocationPermission(): Boolean

    fun listenToLocation(): Flow<Location>
}