package com.example.weatherapp.ui.weatherforecast

import com.example.weatherapp.ui.weather.AppStatus

data class WeatherForecastUiState (
    val status: AppStatus = AppStatus.Idle,
    val errorMessage: String? = null,
)