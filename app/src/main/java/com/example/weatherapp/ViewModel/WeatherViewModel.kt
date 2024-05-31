package com.example.weatherapp.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.Repository.WeatherRepository
import com.example.weatherapp.server.ApiClient
import com.example.weatherapp.server.ApiService
import retrofit2.create

class WeatherViewModel(val repository: WeatherRepository):ViewModel() {

    constructor():this(WeatherRepository(ApiClient().getClient().create(ApiService::class.java)))

    fun loadCurrentWeather(lat:Double,lng:Double,unit:String)=
        repository.getCurrentWeather(lat, lng, unit)


    fun loadForecastWeather(lat:Double,lng:Double,unit:String)=
        repository.getForecastWeather(lat, lng, unit)
}