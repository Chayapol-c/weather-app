package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.network.NetworkResult

interface WeatherRepository {

    suspend fun getWeatherInfo(request: WeatherRequest): NetworkResult<WeatherResponse>
}