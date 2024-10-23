package com.example.weatherapp.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Constant.APP_ID
import com.example.weatherapp.Constant.METRIC_UNIT
import com.example.weatherapp.data.model.WeatherRequest
import com.example.weatherapp.data.network.onError
import com.example.weatherapp.data.network.onException
import com.example.weatherapp.data.network.onSuccess
import com.example.weatherapp.domain.GetWeatherInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherInfoUseCase: GetWeatherInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()


    fun fetchWeatherData(latitude: Double, longitude: Double) {
        val request = WeatherRequest(
            lat = latitude,
            lon = longitude,
            units = METRIC_UNIT,
            appid = APP_ID
        )
        _uiState.update {
            it.copy(
                status = AppStatus.Loading
            )
        }
        viewModelScope.launch {
            getWeatherInfoUseCase(request)
                .onSuccess { newInfo ->
                    _uiState.update {
                        it.copy(
                            status = AppStatus.Idle,
                            weatherInfo = newInfo
                        )
                    }
                }
                .onError { code, message ->
                    _uiState.update {
                        it.copy(
                            status = AppStatus.Error,
                            errorMessage = "Error[$code]: $message"
                        )
                    }
                }
                .onException { error ->
                    _uiState.update {
                        it.copy(
                            status = AppStatus.Error,
                            errorMessage = "Error: ${error.message}}"
                        )
                    }
                }
        }
    }
}
