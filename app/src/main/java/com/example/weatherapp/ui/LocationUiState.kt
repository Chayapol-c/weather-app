package com.example.weatherapp.ui

data class LocationUiState(
    val status: AppStatus = AppStatus.Idle,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val errorMessage: String? = null,
)
