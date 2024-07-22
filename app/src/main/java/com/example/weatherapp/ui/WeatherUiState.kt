package com.example.weatherapp.ui

import com.example.weatherapp.data.model.WeatherResponse

data class WeatherUiState (
    val status: AppStatus = AppStatus.Idle,
    val weatherInfo: WeatherResponse? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val errorMessage: String? = null,
)

enum class AppStatus{
    Idle, Loading, Error
}