package com.example.weatherapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun updateLocation() {
       locationRepository.listenToLocation()
           .onStart {
               _uiState.update {
                   it.copy(
                       status = AppStatus.Loading
                   )
               }
           }
           .onCompletion {
               _uiState.update {
                   it.copy(
                       status = AppStatus.Idle
                   )
               }
           }
           .catch {
           }
           .onEach {
               _uiState.update { prev ->
                   prev.copy(
                       status = AppStatus.Idle,
                       latitude = it.latitude,
                       longitude = it.longitude
                   )
               }
           }
           .launchIn(viewModelScope)
    }

    fun pauseUpdate() {
        locationRepository.stopLocationUpdate()
    }
}