package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.extension.getUnit
import com.example.weatherapp.extension.unixTime
import com.example.weatherapp.extension.AlertDialogContext
import com.example.weatherapp.ui.weather.AppStatus
import com.example.weatherapp.ui.location.LocationViewModel
import com.example.weatherapp.ui.weather.WeatherViewModel
import com.example.weatherapp.extension.displayAlert
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mProgressDialog: Dialog? = null
    private val viewModel: WeatherViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.srlMain.setOnRefreshListener {
            requestLocationData()
            binding.srlMain.isRefreshing = false
        }
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.CREATED) {
                viewModel.uiState.collect { state ->
                    when (state.status) {
                        AppStatus.Idle -> {
                            hideLoading()
                            with(binding.clWeatherInfo) {
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
                                        it.main.temp.toString() + application.resources.configuration.locales.toString()
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
                            displayLoading()
                        }

                        AppStatus.Error -> {
                            AlertDialog.Builder(this@MainActivity).apply {
                                setTitle("Error")
                                setMessage(state.errorMessage)
                                setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            }.create().show()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                locationViewModel.uiState.collect { state ->
                    when (state.status) {
                        AppStatus.Idle -> {
                            hideLoading()
                            val lat = state.latitude
                            val lon = state.longitude
                            Log.i("LocationViewModel", "updateLocation: $lat, $lon")
                            if (lat != null && lon != null) {
                                viewModel.fetchWeatherData(lat, lon)
                            }
                        }

                        AppStatus.Loading -> {
                            displayLoading()
                        }

                        AppStatus.Error -> {
                            this@MainActivity.displayAlert(
                                dialogContext = AlertDialogContext(
                                    title = "Error",
                                    message = state.errorMessage ?: "",
                                    primaryBtnText = "OK",
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        validatePermission()
    }

    override fun onPause() {
        super.onPause()
        locationViewModel.pauseUpdate()
    }

    private fun displayLoading() {
        mProgressDialog = Dialog(this@MainActivity)
        mProgressDialog?.run {
            show()
        }
    }

    private fun hideLoading() {
        mProgressDialog = null
    }

    private fun validatePermission() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isLocationEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        Log.d(this.localClassName, "isLocationEnabled: ${isLocationEnabled.not()}")
        if (isLocationEnabled.not()) {
            Toast.makeText(this, "Your location provider is turned off", Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        requestLocationData()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            this@MainActivity,
                            "You have denied location permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permisions: MutableList<PermissionRequest>?, token: PermissionToken?
                ) {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("It looks like you")
                        .setPositiveButton("GO TO SETTINGS") { _, _ ->
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

            }).onSameThread().check()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        locationViewModel.updateLocation()
//        locationViewModel.updateLocation { locationResult ->
//            locationResult.lastLocation?.let {
//                viewModel.updateLatLon(it.latitude, it.longitude)
//                if (Constant.isNetworkAvailable(this)) {
//                    viewModel.fetchWeatherData()
//                    Toast.makeText(
//                        this@MainActivity,
//                        "No Internet connection available",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                requestLocationData()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}