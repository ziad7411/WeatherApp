package com.example.weatherapp.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.Repository.CityRepository
import com.example.weatherapp.server.ApiClient
import com.example.weatherapp.server.ApiService

class CityViewModel (val repository: CityRepository) :ViewModel(){
    constructor():this(CityRepository(ApiClient().getClient().create(ApiService::class.java)))

    fun loadCity(q:String,limit:Int)=
        repository.getCities(q,limit)


}