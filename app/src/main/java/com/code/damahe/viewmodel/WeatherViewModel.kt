package com.code.damahe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.damahe.model.WeatherResponse
import com.code.damahe.repo.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val weatherDetailsLiveData = MutableLiveData<WeatherResponse>()

    fun fetchWeatherDetails(locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherDetails = weatherRepository.getWeatherDetails(locationName)
                if (weatherDetails.isSuccessful) {
                    weatherDetailsLiveData.postValue(weatherDetails.body())
                }
            } catch (e: Exception){
                Log.d(this@WeatherViewModel::class.java.name,"Fetch Error : $e.message")
            }
        }
    }
}