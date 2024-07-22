package com.example.weatherapp.data.repository

import com.google.android.gms.location.LocationResult

interface LocationRepository {

    fun updateLocation(callback: (LocationResult) -> Unit)

    fun stopLocationUpdate()
}