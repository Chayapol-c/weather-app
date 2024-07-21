package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.Constant.WEATHER_RESPONSE_DATA
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.network.WeatherService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mProgressDialog: Dialog? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getPreferences(MODE_PRIVATE)

        if (isLocationEnabled().not()) {
            Toast.makeText(this, "Your location provider is turned off", Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        requestLocationData()
                    }
                    if (report?.isAnyPermissionPermanentlyDenied == true) {
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
                    showRationalDialogForPermission()
                }

            }).onSameThread().check()

        }
    }

    private fun setUpView() {
        val weatherList = mSharedPreferences.getString(WEATHER_RESPONSE_DATA, "")?.let {
            Gson().fromJson(it, WeatherResponse::class.java)
        }
        if (weatherList == null) {
            return
        }
        weatherList.weather.forEachIndexed { index, weather ->
            with(binding) {
                tvMain.text = weather.main
                tvMainDescription.text = weather.description

                when(weather.icon) {
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
        }
        with(binding) {
            tvTemp.text =
                weatherList.main.temp.toString() + application.resources.configuration.locales.toString().getUnit()

            tvHumidity.text = weatherList.main.humidity.toString() + "per cen"
            tvMin.text = weatherList.main.tempMin.toString() + " min"
            tvMax.text = weatherList.main.tempMax.toString() + " max"
            tvSpeed.text = weatherList.wind.speed.toString()
            tvName.text = weatherList.name
            tvCountry.text = weatherList.sys.country

            tvSunriseTime.text = weatherList.sys.sunrise.unixTime()
            tvSunsetTime.text = weatherList.sys.sunset.unixTime()
        }
    }

    fun Long.unixTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }.let {
            val date = Date(this * 1000L)
            it.format(date)
        }
    }

    fun String.getUnit(): String {
        return if ("US" == this || "LR" == this || "MM" == this) {
            "°F"
        } else {
            "°C"
        }
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this)
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

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                getLocationWeatherDetails(it.latitude, it.longitude)
            }
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (Constant.isNetworkAvailable(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(WeatherService::class.java)
            val listCall = service.getWeather(
                lat = latitude,
                lon = longitude,
                Constant.METRIC_UNIT,
                Constant.APP_ID
            )

            showDialog()

            listCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful.not()) {
                        when (val code = response.code()) {
                            400 -> Log.e("api", "400 Bad Connection")
                            404 -> Log.e("api", "404 Not Found")
                            else -> {
                                Log.e("api", "$code")
                            }
                        }
                    }
                    hideDialog()
                    response.body()?.let {
                        val weatherResponseJsonString = Gson().toJson(it)
                        val editor = mSharedPreferences.edit()
                        editor.putString(WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()
                        setUpView()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, exception: Throwable) {
                    hideDialog()
                    Log.e("api", "${exception.message}")
                }

            })

            Toast.makeText(
                this@MainActivity,
                "No Internet connection available",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog?.run {
            setContentView(binding.root)
            show()
        }
    }

    private fun hideDialog() {
        mProgressDialog?.run {
            dismiss()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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