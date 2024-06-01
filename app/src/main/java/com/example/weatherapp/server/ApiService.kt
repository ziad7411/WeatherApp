package com.example.weatherapp.server

import com.example.weatherapp.model.CityResponseApi
import com.example.weatherapp.model.CurrentResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import com.example.weatherapp.model.TimeZoneResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String,
    ): Call<CurrentResponseApi>

    @GET("data/2.5/forecast")
    fun getForeCastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String,
    ): Call<ForecastResponseApi>

    @GET("geo/1.0/direct")
    fun getCityList(
        @Query("q") q: String,
        @Query("limit") limit: Int,
        @Query("appid") appid: String
    ): Call<CityResponseApi>

    @GET("data/timezone-by-location")
    fun getTimeZone(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("key") key: String =
            "bdc_a1feeb5978e34ea6a830e0748f23b868"
    ): Call<TimeZoneResponse>

}