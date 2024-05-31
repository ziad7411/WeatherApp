package com.example.weatherapp.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.location.SettingInjectorService
import android.media.audiofx.EnvironmentalReverb
import android.media.audiofx.Equalizer.Settings
import android.media.audiofx.Virtualizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.adapter.ForecastAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.CurrentResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel:WeatherViewModel by viewModels()
    private val calender by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var longtude:Double = 0.0
    var latitude : Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Thread.sleep(3000)
        installSplashScreen()
            //location
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)


        getCurrentLocation()





       window.apply {
            addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        binding.apply {


            var lat = intent.getDoubleExtra("lat",0.0)
            var lon = intent.getDoubleExtra("lon",0.0)
            var name = intent.getStringExtra("name")
            if(lat == 0.0){
                lat=latitude
                lon=longtude
                 name="Your Current Location"
            }

                addCity.setOnClickListener{
                    startActivity(Intent(this@MainActivity,CityListActivity::class.java))
                }
            //Current Weather

            cityTxt.text=name
            progressBar.visibility = View.GONE
            weatherViewModel.loadCurrentWeather(lat,lon,"metric").enqueue(object :
                retrofit2.Callback<CurrentResponseApi> {
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {
                    if (response.isSuccessful){
                        val data = response.body()
                        progressBar.visibility=View.GONE
                        detailLayout.visibility=View.VISIBLE
                        data.let {
                            statusTxt.text=
                                it?.weather?.get(0)?.main?:"-"
                            windTxt.text=
                                it?.wind?.speed?.let { Math.round(it).toString() }+"Km"
                            humidity.text=
                                it?.main?.humidity?.toString()+"%"
                            currentTempTxt.text=
                                it?.main?.temp.let { Math.round(it!!).toString() }+"°"
                            maxTempTxt.text=
                                it?.main?.tempMax.let { Math.round(it!!).toString() }+"°"
                            lowTempTxt.text=
                                it?.main?.tempMin.let { Math.round(it!!).toString() }+"°"
                            setEffectRainSnow(it?.weather?.get(0)?.icon?:"-")

                            val drawable = if (isNightNow())R.drawable.nightbg
                            else{
                                setDynamicallyWallpaper(it?.weather?.get(0)?.icon ?: "-")
                            }


                            bgImage.setImageResource(drawable)

                        }
                    }
                }

                override fun onFailure(p0: Call<CurrentResponseApi>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }

            })


            //settings blur View

            var  radius=10f
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content)as ViewGroup)
            val windowsBackground=decorView.background

            rootView.let {
                blurView.setupWith(it,RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowsBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true

            }

            //Forecast Temp
            weatherViewModel.loadForecastWeather(lat,lon,"metric").enqueue(object :retrofit2.Callback<ForecastResponseApi>{
                override fun onResponse(
                    call: Call<ForecastResponseApi>,
                    response: Response<ForecastResponseApi>
                ) {
                   if (response.isSuccessful){
                       val data = response.body()
                       blurView.visibility=View.VISIBLE

                       data?.let {

                           forecastAdapter.differ.submitList(it.list)
                           forecastView.apply {
                               layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                               adapter = forecastAdapter

                           }

                       }
                   }
                }

                override fun onFailure(p0: Call<ForecastResponseApi>, t: Throwable) {
                    Log.e("ForecastResponseApi", "Failure: ${t.message}", t)
                    showError("Failed to load timezone data")                }

            })



        }
    }

    private fun getCurrentLocation() {
        if (checkPermission()){
            if (isLocationEnabled()){

                //Final latitude and longitude code

                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){task ->

                    val location :Location? = task.result
                    if (location == null)
                    {
                        Toast.makeText(this, "null recieved", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "get success", Toast.LENGTH_SHORT).show()
                        longtude= location.longitude
                        latitude = location.latitude
                    }

                }

            }else{
                //setting open
                Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }

        }else{
        // request permission
            requestPermission()
        }



    }

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object{
    private const val PERMISSION_REQUEST_ACCESS_LOCATION=100

}

    private fun checkPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)==
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Generated", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNightNow():Boolean{
            val time = calender.get(Calendar.HOUR_OF_DAY)

            return time !in 6..18
    }
   private fun setDynamicallyWallpaper(icon:String):Int{
        return when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.bg
            }
            "02","03","04"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.clouds
            }
            "09,","10","11"->{
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainnybg
            }
            "13"->{
                initWeatherView(PrecipType.SNOW)
                R.drawable.snowbg
            } "50"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze
            }
            else ->0
        }
    }

    private fun setEffectRainSnow(icon:String) {
         when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)

            }
            "02","03","04"->{
                initWeatherView(PrecipType.CLEAR)

            }
            "09,","10","11"->{
                initWeatherView(PrecipType.RAIN)

            }
            "13"->{
                initWeatherView(PrecipType.SNOW)

            } "50"->{
                initWeatherView(PrecipType.CLEAR)

            }

        }
    }


    private fun initWeatherView(type:PrecipType){
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }

    }
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}