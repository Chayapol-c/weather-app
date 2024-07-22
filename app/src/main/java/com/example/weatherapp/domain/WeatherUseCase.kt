package com.example.weatherapp.domain

import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.network.NetworkResult
import com.example.weatherapp.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(request: WeatherRequest): NetworkResult<WeatherResponse> = weatherRepository.getWeatherInfo(request)

}