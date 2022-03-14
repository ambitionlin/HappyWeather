package com.example.happyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.happyweather.logic.Repository
import com.example.happyweather.logic.model.PlaceResponse

class PlaceViewModel:ViewModel() {
    private val searchLiveData = MutableLiveData<String>()
    //placeList用于对界面展示的城市数据进行缓存，原则上与界面相关的数据都应该放到ViewModel中，可保证手机屏幕旋转时，数据不丢失
    val placeList = ArrayList<PlaceResponse.Place>()

    //将传入的query值 赋值给一个searchLiveData对象，并用Transformations的switchMap()方法观察这个对象，否则仓库返回的LiveData对象将无法观察
    fun searchPlaces(query:String){
        searchLiveData.value = query
    }
    //每当searchPlaces()被调用时，switchMap()对应的转换函数就会执行。在转换函数中，只需调用仓库Repository定义的searchPlaces()就可发起网络请求，
    //同时将仓库层返回的LiveData对象转换成一个可供Activity观察的LiveData对象
    val placeLiveData = Transformations.switchMap(searchLiveData){ query ->
        Repository.searchPlaces(query)
    }

}