package com.example.weatherapp.data.model.weatherinfo

import java.io.Serializable


data class WeatherResponse(
    val coordinate: CoordinateResponse,
    val weather: List<Weather>,
    val base: String,
    val main: MainResponse,
    val visibility: Int,
    val wind: WindResponse,
    val cloulds: CloudsResponse,
    val dt: Int,
    val sys: SysResponse,
    val id: Int,
    val name: String,
    val cod: Int
) : Serializable

data class Weather (
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) : Serializable