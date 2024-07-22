package com.example.weatherapp.data.repository


import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    activity: Application
) : LocationRepository {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            onLocationUpdateCallback?.invoke(locationResult)
        }
    }

    private var onLocationUpdateCallback: ((LocationResult) -> Unit)? = null

    @SuppressLint("MissingPermission")
    override fun updateLocation(callback: (LocationResult) -> Unit) {
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        onLocationUpdateCallback = callback

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    override fun stopLocationUpdate() {
        onLocationUpdateCallback = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}