package com.example.weatherapp.domain

import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.flow

class WeatherUseCase (
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(request: WeatherRequest) =
        flow {
            val response = weatherRepository.getWeatherInfo(request)
            emit(response)
        }
}