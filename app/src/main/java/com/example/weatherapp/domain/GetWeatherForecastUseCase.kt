package com.example.weatherapp.domain

import com.example.weatherapp.data.model.weatherforecast.WeatherForecastResponse
import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.network.NetworkResult
import com.example.weatherapp.data.repository.weather.WeatherRepository
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(request: WeatherRequest): NetworkResult<WeatherForecastResponse> =
        weatherRepository.getWeatherForecast(request)

}