package com.example.weatherapp.di

import com.example.weatherapp.data.repository.location.LocationRepository
import com.example.weatherapp.data.repository.location.LocationRepositoryImpl
import com.example.weatherapp.data.repository.weather.WeatherRepository
import com.example.weatherapp.data.repository.weather.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}