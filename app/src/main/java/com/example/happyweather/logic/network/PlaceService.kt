package com.example.happyweather.logic.network

import com.example.happyweather.HappyWeatherApplication
import com.example.happyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//定义访问彩云天气城市搜索API的Retrofit接口
interface PlaceService {
    @GET("v2/place?token=${HappyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse.PlaceResponse>
}