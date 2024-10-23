package com.example.weatherapp.di

import com.example.weatherapp.domain.GetWeatherInfoUseCase
import com.example.weatherapp.ui.weather.WeatherViewModel
import com.example.weatherapp.ui.weatherforecast.WeatherForecastViewModel
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
    fun provideViewModel(useCase: GetWeatherInfoUseCase): WeatherViewModel {
        return WeatherViewModel(useCase)
    }

    @Provides
    @Singleton
    fun provideWeatherForecastViewModel(): WeatherForecastViewModel {
        return WeatherForecastViewModel()
    }
}