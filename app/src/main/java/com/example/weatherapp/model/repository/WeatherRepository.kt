package com.example.weatherapp.model.repository

import com.example.weatherapp.model.WeatherRequest
import com.example.weatherapp.model.WeatherResponse

interface WeatherRepository {

    suspend fun getWeatherInfo(request: WeatherRequest): WeatherResponse
}