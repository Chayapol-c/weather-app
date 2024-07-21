package com.example.weatherapp.data.model

import retrofit2.http.Query

data class WeatherRequest (
    @Query("lat") val lat: Double,
    @Query("lon") val lon: Double,
    @Query("units") val units: String?,
    @Query("appid") val appid: String,
)