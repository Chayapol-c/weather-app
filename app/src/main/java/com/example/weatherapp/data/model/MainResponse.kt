package com.example.weatherapp.data.model

import java.io.Serializable

data class MainResponse(
    val temp: Double,
    val pressure: Double,
    val humidity: Int,
    val tempMin: Double,
    val tempMax: Double,
    val seaLevel: Double,
    val grndLevel: Double
) : Serializable
