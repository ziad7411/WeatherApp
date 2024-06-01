package com.example.weatherapp.Repository

import com.example.weatherapp.server.ApiService

class WeatherRepository(val api: ApiService, val timeZoneAPI: ApiService) {
    fun getCurrentWeather(lat: Double, lng: Double, unit: String) =
        api.getCurrentWeather(lat, lng, unit, "2c183f837b0b3a4d4a96a4a30bebb880")

    fun getForecastWeather(lat: Double, lng: Double, unit: String) =
        api.getForeCastWeather(lat, lng, unit, "2c183f837b0b3a4d4a96a4a30bebb880")

    fun getTimeZone(
        latitude: Double,
        longitude: Double,
    ) = timeZoneAPI.getTimeZone(latitude, longitude)
}