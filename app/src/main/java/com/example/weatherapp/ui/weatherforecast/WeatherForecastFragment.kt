package com.example.weatherapp.ui.weatherforecast

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherapp.R

class WeatherForecastFragment: Fragment(R.layout.fragment_weather_forecast) {

    private val viewModel by viewModels<WeatherForecastViewModel>()

}