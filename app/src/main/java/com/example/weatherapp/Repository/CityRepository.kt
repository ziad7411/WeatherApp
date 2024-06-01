package com.example.weatherapp.Repository

import com.example.weatherapp.server.ApiService

class CityRepository(val api: ApiService) {
    fun getCities(q: String, limit: Int) =
        api.getCityList(q, limit, "2c183f837b0b3a4d4a96a4a30bebb880")


}