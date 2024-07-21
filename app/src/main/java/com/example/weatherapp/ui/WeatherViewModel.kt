package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Constant.APP_ID
import com.example.weatherapp.Constant.METRIC_UNIT
import com.example.weatherapp.model.repository.WeatherRepository
import com.example.weatherapp.model.WeatherRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WeatherUiState(
            status = AppStatus.Idle,
            weatherInfo = null,
            latitude = null,
            longitude = null
        )
    )
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()


    fun fetchWeatherData() {
        val request = WeatherRequest(
            lat = _uiState.value.latitude ?: 0.0,
            lon = _uiState.value.longitude ?: 0.0,
            units = METRIC_UNIT,
            appid = APP_ID
        )
        _uiState.update {
            it.copy(
                status = AppStatus.Loading
            )
        }

        flow {
            val response = repository.getWeatherInfo(request)
            emit(response)

        }.onStart {
            _uiState.update {
                it.copy(status = AppStatus.Loading)
            }
        }.onCompletion {
            _uiState.update {
                it.copy(status = AppStatus.Idle)
            }
        }.catch {
            _uiState.update {
                it.copy(status = AppStatus.Error)
            }
        }.onEach { response ->
            _uiState.update {
                it.copy(status = AppStatus.Idle, weatherInfo = response)
            }
        }.launchIn(viewModelScope)
    }

    fun updateLatLon(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude
            )
        }
    }
}
