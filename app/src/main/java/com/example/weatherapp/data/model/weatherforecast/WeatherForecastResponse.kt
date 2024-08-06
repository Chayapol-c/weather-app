package com.example.weatherapp.data.model.weatherforecast

import com.example.weatherapp.data.model.CloudsResponse
import com.example.weatherapp.data.model.CoordinateResponse
import com.example.weatherapp.data.model.weatherinfo.Weather
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherForecastResponse(
    @SerializedName("cod")val cod: Int?,
    @SerializedName("message") val message: Int?,
    @SerializedName("cnt") val cnt: Int?,
    @SerializedName("list") val list: List<WeatherForecast>?,
    @SerializedName("city") val city: City?
) : Serializable

data class City (
    val id: Int,
    val name: String,
    val coord: CoordinateResponse,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class WeatherForecast (
    val dt: Long,
    val main: ForecastMain,
    val weather: List<Weather>,
    val clouds: CloudsResponse,
    val wind: ForecastWind,
    val visibility: Long,
    val pop: Double,
    val rain: ForecastRain,
    val sys: ForecastSys,
    @SerializedName("dt_text") val dtText: String
)

data class ForecastSys (
    val pod: String
)

data class ForecastRain (
    @SerializedName("3h") val threeHours: Double
)

data class ForecastMain (
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("sea_level") val seaLevel: Int,
    @SerializedName("grnd_level") val grndLevel: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("temp_kf") val tempKf: Double
)

data class ForecastWind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int,
    @SerializedName("gust") val gust: Double
)
