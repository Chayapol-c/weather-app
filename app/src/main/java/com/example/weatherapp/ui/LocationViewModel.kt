package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.repository.LocationRepository
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {


    fun updateLocation(callback: (LocationResult) -> Unit) {
        locationRepository.updateLocation(
            callback
        )
    }

    fun pauseUpdate() {
        locationRepository.stopLocationUpdate()
    }
}