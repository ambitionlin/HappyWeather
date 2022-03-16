package com.example.happyweather.logic

import androidx.lifecycle.liveData
import com.example.happyweather.logic.dao.PlaceDao
import com.example.happyweather.logic.model.PlaceResponse.Place
import com.example.happyweather.logic.model.Weather
import com.example.happyweather.logic.network.HappyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import kotlin.RuntimeException
import kotlin.coroutines.CoroutineContext

//仓库层的统一封装入口   仓库层的主要作用是判断调用方请求的数据应该是从本地数据源中获取还是从网络数据源中获取，并将获得的数据返回给调用方
object Repository {
    //从本地数据源中获取数据
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    //从网络数据源中获取数据
    fun searchPlaces(query: String) =
        fire(Dispatchers.IO) {   //将liveData()的参数类型指定成Dispatchers.IO，代码均运行在子线程中
            val placeResponse = HappyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)    //包装获取的城市列表信息
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                HappyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                HappyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}