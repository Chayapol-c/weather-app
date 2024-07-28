package com.example.weatherapp.di

import com.example.weatherapp.domain.WeatherUseCase
import com.example.weatherapp.ui.weather.WeatherViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideViewModel(useCase: WeatherUseCase): WeatherViewModel {
        return WeatherViewModel(useCase)
    }
}