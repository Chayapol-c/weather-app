package com.example.weatherapp.di

import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.model.network.WeatherService
import com.example.weatherapp.model.repository.WeatherRepository
import com.example.weatherapp.model.repository.WeatherRepositoryImpl
import com.example.weatherapp.ui.WeatherViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideViewModel(repository: WeatherRepository): WeatherViewModel {
        return WeatherViewModel(repository)
    }
}