package com.example.weatherapp.ui.location

import com.example.weatherapp.ui.weather.AppStatus

data class LocationUiState(
    val status: AppStatus = AppStatus.Idle,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val errorMessage: String? = null,
)
