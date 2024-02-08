package com.code.damahe.repo

import com.code.damahe.events.RetrofitServices
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val retrofitServices: RetrofitServices
) {

    suspend fun getWeatherDetails(locationName: String) = retrofitServices.getWeatherDetails(locationName)
}