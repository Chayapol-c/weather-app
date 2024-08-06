package com.example.weatherapp.ui.home

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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityHomeBinding
import com.example.weatherapp.extension.AlertDialogContext
import com.example.weatherapp.extension.displayAlert
import com.example.weatherapp.ui.location.LocationUiState
import com.example.weatherapp.ui.location.LocationViewModel
import com.example.weatherapp.ui.weather.AppStatus
import com.example.weatherapp.ui.weather.WeatherInfoFragment
import com.example.weatherapp.ui.weather.WeatherViewModel
import com.example.weatherapp.ui.weatherforecast.WeatherForecastFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : FragmentActivity(R.layout.activity_home) {

    private lateinit var binding: ActivityHomeBinding
    private var mProgressDialog: Dialog? = null

    private val locationViewModel by viewModels<LocationViewModel>()
    private val weatherViewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<WeatherInfoFragment>(R.id.fcvHome)
            }
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.srlMain.setOnRefreshListener {
            requestLocationData()
            binding.srlMain.isRefreshing = false
        }
        initViewPager()
        validatePermission()
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                locationViewModel.uiState.collect { state ->
                    when (state.status) {
                        AppStatus.Idle -> onAppIdle(state)

                        AppStatus.Loading -> onAppLoading(state)

                        AppStatus.Error -> onAppError(state)
                    }
                }
            }
        }
    }

    private fun initViewPager() = with(binding) {
        vpHome.apply {
            adapter = HomePagerAdapter(supportFragmentManager, lifecycle, HOME_FRAGMENT_LIST)
            currentItem = 0
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }

    private fun onAppIdle(state: LocationUiState) {
        hideLoading()
        val lat = state.latitude
        val lon = state.longitude
        Log.i("LocationViewModel", "updateLocation: $lat, $lon")
        if (lat != null && lon != null) {
            weatherViewModel.fetchWeatherData(lat, lon)
        }
    }

    private fun onAppLoading(state: LocationUiState) {
        displayLoading()
    }

    private fun onAppError(state: LocationUiState) {
        hideLoading()
        this@HomeActivity.displayAlert(
            dialogContext = AlertDialogContext(
                title = "Error",
                message = state.errorMessage ?: "",
                primaryBtnText = "OK",
            )
        )
    }

    override fun onResume() {
        super.onResume()
        validatePermission()
    }

    override fun onPause() {
        super.onPause()
        locationViewModel.pauseUpdate()
    }

    fun displayLoading() {
        mProgressDialog = Dialog(this)
        mProgressDialog?.run {
            show()
        }
    }

    fun hideLoading() {
        mProgressDialog = null
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


    fun validatePermission() {
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
                            this@HomeActivity,
                            "You have denied location permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permisions: MutableList<PermissionRequest>?, token: PermissionToken?
                ) {
                    AlertDialog.Builder(this@HomeActivity)
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

    companion object {
        private val HOME_FRAGMENT_LIST = listOf(
            WeatherInfoFragment(),
            WeatherForecastFragment()
        )
    }
}