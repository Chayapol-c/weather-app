package com.example.weatherapp.ui.weatherforecast

import androidx.lifecycle.ViewModel
import com.example.weatherapp.ui.weather.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherForecastViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherForecastUiState())
    val uiState: StateFlow<WeatherForecastUiState> = _uiState.asStateFlow()
}