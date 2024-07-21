package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.network.WeatherService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
) : WeatherRepository {
    override suspend fun getWeatherInfo(request: WeatherRequest): WeatherResponse {
        return withContext(IO) {
            val response = service.getWeather(
                lat = request.lat,
                lon = request.lon,
                units = request.units,
                appid = request.appid
            )
            if (response.isSuccessful.not()) {
                when (val code = response.code()) {
                    400 -> Log.e("api", "400 Bad Connection")
                    404 -> Log.e("api", "404 Not Found")
                    else -> {
                        Log.e("api", "$code")
                    }
                }
            }
            response.body()!!
        }

    }
}
