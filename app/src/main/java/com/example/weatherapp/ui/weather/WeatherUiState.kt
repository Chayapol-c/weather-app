package com.example.weatherapp.ui.weather

import com.example.weatherapp.data.model.WeatherResponse

data class WeatherUiState (
    val status: AppStatus = AppStatus.Idle,
    val weatherInfo: WeatherResponse? = null,
    val errorMessage: String? = null,
)

enum class AppStatus{
    Idle, Loading, Error
}