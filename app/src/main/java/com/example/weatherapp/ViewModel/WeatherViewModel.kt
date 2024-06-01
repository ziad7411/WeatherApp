package com.example.weatherapp.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.Repository.WeatherRepository
import com.example.weatherapp.model.TimeZoneResponse
import com.example.weatherapp.server.ApiClient
import com.example.weatherapp.server.ApiService
import retrofit2.Call
import retrofit2.Response

class WeatherViewModel(val repository: WeatherRepository) : ViewModel() {

    private var _timeZone = MutableLiveData<TimeZoneResponse?>()
    var timeZone: LiveData<TimeZoneResponse?> = _timeZone

    constructor() : this(
        WeatherRepository(
            ApiClient().getWeatherClient().create(ApiService::class.java),
            ApiClient().getTimeZoneClient().create(ApiService::class.java)
        )
    )


    fun loadCurrentWeather(lat: Double, lng: Double, unit: String) =
        repository.getCurrentWeather(lat, lng, unit)


    fun loadForecastWeather(lat: Double, lng: Double, unit: String) =
        repository.getForecastWeather(lat, lng, unit)


    fun getTimeZone(
        latitude: Double,
        longitude: Double,
    ) {
        repository.getTimeZone(latitude, longitude)
            .enqueue(object : retrofit2.Callback<TimeZoneResponse> {
                override fun onResponse(
                    p0: Call<TimeZoneResponse>,
                    response: Response<TimeZoneResponse>
                ) {
                    if (response.isSuccessful) {
                        _timeZone.value = response.body()
                    }
                }

                override fun onFailure(p0: Call<TimeZoneResponse>, p1: Throwable) {
                    Log.e("ForecastResponseApi", "onFailure: ${p1.message}", p1)
                }
            })
    }
}