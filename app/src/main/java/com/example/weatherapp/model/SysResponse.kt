package com.example.weatherapp.model

import java.io.Serializable

data class SysResponse(
    val type: Int,
    val message: Double,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
) : Serializable
