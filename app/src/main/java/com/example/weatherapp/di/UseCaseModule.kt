package com.example.weatherapp.di

import com.example.weatherapp.data.repository.weather.WeatherRepository
import com.example.weatherapp.domain.WeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideUseCase(
        weatherRepository: WeatherRepository
    ): WeatherUseCase {
        return WeatherUseCase(weatherRepository)
    }
}