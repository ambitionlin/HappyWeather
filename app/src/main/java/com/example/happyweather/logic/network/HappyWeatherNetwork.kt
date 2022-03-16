package com.example.happyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//定义一个统一的网络数据源访问入口，对所有网络请求的API进行封装
object HappyWeatherNetwork {
    //首先使用ServiceCreator创建了一个PlaceService接口的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    //然后定义了一个searchPlaces()函数，并在里面调用了PlaceService接口中定义的searchPlaces()方法，以发起搜索城市数据请求。
    //suspend 用作修饰会被暂停的函数，被标记为suspend 的函数只能运行在协程或者其他suspend 函数当中。
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    //当外界调用HappyWeatherNetwork的searchPlaces()函数时，Retrofit会立刻发起网络请求，同时当前协程会被阻塞。直到服务器响应我们的请求后，await()
    //会将解析出来的数据模型对象取出并返回，同时恢复协程的执行，searchPlaces()在得到await()的返回值后会将该数据再返回到上一层
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    //首先使用ServiceCreator创建了一个WeatherService接口的动态代理对象
    private val weatherService = ServiceCreator.create(WeatherService::class.java)
    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(lng, lat).await()
}