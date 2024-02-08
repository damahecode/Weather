package com.code.damahe.events

import com.code.damahe.config.AppConfig
import com.code.damahe.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {

    @GET(AppConfig.BASE_URL_END_POINT)
    suspend fun getWeatherDetails(
        @Query("q") locationName: String,
        @Query("aqi") fetchAqi: String = "yes"
    ): Response<WeatherResponse>
}