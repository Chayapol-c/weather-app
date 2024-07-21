package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.model.WeatherResponse

interface WeatherRepository {

    suspend fun getWeatherInfo(request: WeatherRequest): WeatherResponse
}