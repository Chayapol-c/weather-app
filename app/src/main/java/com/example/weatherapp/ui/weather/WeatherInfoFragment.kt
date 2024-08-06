package com.example.weatherapp.ui.weather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentWeatherInfoBinding
import com.example.weatherapp.extension.getUnit
import com.example.weatherapp.extension.unixTime
import com.example.weatherapp.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherInfoFragment : Fragment(R.layout.fragment_weather_info) {

    private val viewModel by viewModels<WeatherViewModel>()
    private lateinit var binding: FragmentWeatherInfoBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentWeatherInfoBinding.inflate(layoutInflater)
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.CREATED) {
                viewModel.uiState.collect { state ->
                    when (state.status) {
                        AppStatus.Idle -> {
                            (activity as? HomeActivity)?.hideLoading()
                            with(binding) {
                                state.weatherInfo?.let {
                                    it.weather.forEach { weather ->
                                        tvMain.text = weather.main
                                        tvMainDescription.text = weather.description
                                        when (weather.icon) {
                                            "01d" -> ivMain.setImageResource(R.drawable.sunny)
                                            "01n" -> ivMain.setImageResource(R.drawable.cloud) // clear sky

                                            "02d" -> ivMain.setImageResource(R.drawable.cloud)
                                            "02n" -> ivMain.setImageResource(R.drawable.cloud)

                                            "03d" -> ivMain.setImageResource(R.drawable.cloud) // scattered clouds
                                            "03n" -> ivMain.setImageResource(R.drawable.cloud)
                                            "04d" -> ivMain.setImageResource(R.drawable.cloud) // broken clouds
                                            "04n" -> ivMain.setImageResource(R.drawable.cloud)

                                            "09d" -> ivMain.setImageResource(R.drawable.rain) // shower rain
                                            "09n" -> ivMain.setImageResource(R.drawable.rain)

                                            "10d" -> ivMain.setImageResource(R.drawable.rain)
                                            "10n" -> ivMain.setImageResource(R.drawable.cloud)

                                            "11d" -> ivMain.setImageResource(R.drawable.storm)
                                            "11n" -> ivMain.setImageResource(R.drawable.rain)

                                            "13d" -> ivMain.setImageResource(R.drawable.snowflake)
                                            "13n" -> ivMain.setImageResource(R.drawable.snowflake)

                                            "50d" -> ivMain.setImageResource(R.drawable.rain) // mist
                                            "50n" -> ivMain.setImageResource(R.drawable.rain)
                                        }
                                    }

                                    tvTemp.text =
                                        it.main.temp.toString() + resources.configuration.locales.toString()
                                            .getUnit()

                                    tvHumidity.text = it.main.humidity.toString() + "per cen"
                                    tvMin.text = it.main.tempMin.toString() + " min"
                                    tvMax.text = it.main.tempMax.toString() + " max"
                                    tvSpeed.text = it.wind.speed.toString()
                                    tvName.text = it.name
                                    tvCountry.text = it.sys.country

                                    tvSunriseTime.text = it.sys.sunrise.unixTime()
                                    tvSunsetTime.text = it.sys.sunset.unixTime()
                                }
                            }
                        }

                        AppStatus.Loading -> {
                            (activity as? HomeActivity)?.displayLoading()
                        }

                        AppStatus.Error -> {
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle("Error")
                                setMessage(state.errorMessage)
                                setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            }.create().show()
                        }
                    }
                }
            }
        }
    }


}