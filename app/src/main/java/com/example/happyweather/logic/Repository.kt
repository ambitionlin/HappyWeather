package com.example.happyweather.logic

import androidx.lifecycle.liveData
import com.example.happyweather.logic.model.PlaceResponse
import com.example.happyweather.logic.network.HappyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

//仓库层的统一封装入口   仓库层的主要作用是判断调用方请求的数据应该是从本地数据源中获取还是从网络数据源中获取，并将获得的数据返回给调用方
object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO){   //将liveData()的参数类型指定成Dispatchers.IO，代码均运行在子线程中
        val result = try{
            val placeResponse = HappyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)    //包装获取的城市列表信息
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e:Exception){
            Result.failure<List<PlaceResponse.Place>>(e)
        }
        emit(result)     //将包装的信息发送出去
    }
}