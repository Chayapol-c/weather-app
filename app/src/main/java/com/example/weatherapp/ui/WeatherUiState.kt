package com.example.weatherapp.ui

import com.example.weatherapp.data.model.WeatherResponse

data class WeatherUiState (
    val status: AppStatus,
    val weatherInfo: WeatherResponse?,
    val latitude: Double?,
    val longitude: Double?
)

enum class AppStatus{
    Idle, Loading, Error
}