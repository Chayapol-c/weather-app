package com.example.weatherapp.model

import java.io.Serializable

data class CoordinateResponse(
    val lon: Double,
    val lat: Double
): Serializable
