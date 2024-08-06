package com.example.weatherapp.data.repository.weather

import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.model.weatherinfo.WeatherResponse
import com.example.weatherapp.data.network.NetworkResult

interface WeatherRepository {

    suspend fun getWeatherInfo(request: WeatherRequest): NetworkResult<WeatherResponse>
}