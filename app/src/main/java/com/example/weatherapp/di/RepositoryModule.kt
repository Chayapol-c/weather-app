package com.example.weatherapp.di

import com.example.weatherapp.model.repository.WeatherRepository
import com.example.weatherapp.model.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindWeatherService(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}