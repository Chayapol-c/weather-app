package com.example.weatherapp.data.repository


import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val activity: Application
) : LocationRepository {
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            onLocationUpdateCallback?.invoke(locationResult)
        }
    }

    private var onLocationUpdateCallback: ((LocationResult) -> Unit)? = null

    @SuppressLint("MissingPermission")
    override fun updateLocation(callback: (LocationResult) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            TimeUnit.MINUTES.toMillis(10)
        ).apply {
            setMinUpdateIntervalMillis(1000L)
        }.build()
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

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun listenToLocation(): Flow<Location> {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            TimeUnit.MINUTES.toMillis(10)
        ).apply {
            setMinUpdateIntervalMillis(1000L)
        }.build()

        return callbackFlow {
            if (hasLocationPermission().not()) {
                throw NoPermissionsException
            }
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.lastLocation?.let {
                        launch {
                            send(it)
                        }
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper(),
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    object NoPermissionsException : Exception()
}