package com.example.happyweather.ui.place


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.happyweather.logic.Repository
import com.example.happyweather.logic.model.PlaceResponse

class WeatherViewModel : ViewModel() {
    private val locationLiveData = MutableLiveData<PlaceResponse.Location>()
    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = PlaceResponse.Location(lng, lat)
    }

    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }
}