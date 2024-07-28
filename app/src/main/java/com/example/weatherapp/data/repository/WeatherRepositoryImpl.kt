package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.BuildConfig
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
            Log.i(this.javaClass.name, "$body")
            NetworkResult.Success(body)

        } catch (e: HttpException) {
            Log.i(this.javaClass.name, "${e.code()}, ${e.message()}")
            NetworkResult.Error(code = e.code(), message = e.message())
        } catch (e: Exception) {
            Log.i(this.javaClass.name, "${e.message}")
            NetworkResult.Exception(e)
        }
    }
}
