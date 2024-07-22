package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.network.NetworkResult
import com.example.weatherapp.data.network.WeatherService
import retrofit2.HttpException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService,
) : WeatherRepository {
    override suspend fun getWeatherInfo(request: WeatherRequest): NetworkResult<WeatherResponse> {
        return try {
            val response = service.getWeather(
                lat = request.lat,
                lon = request.lon,
                units = request.units,
                appid = request.appid
            )
            val body = response.body()
            if (response.isSuccessful.not() || body == null) {
                throw HttpException(response)
            }
            NetworkResult.Success(body)

        } catch (e: HttpException) {
            NetworkResult.Error(code = e.code(), message = e.message())
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }
}
