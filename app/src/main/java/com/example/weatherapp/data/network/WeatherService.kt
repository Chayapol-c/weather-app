package com.example.weatherapp.data.network

import com.example.weatherapp.data.model.weatherforecast.WeatherForecastResponse
import com.example.weatherapp.data.model.weatherinfo.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("appid") appid: String,
    ) : Response<WeatherResponse>

    @GET("2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("appid") appid: String,
    ) : Response<WeatherForecastResponse>
}