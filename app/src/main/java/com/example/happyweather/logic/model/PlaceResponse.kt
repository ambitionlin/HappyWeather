package com.example.happyweather.logic.model

import com.google.gson.annotations.SerializedName

//定义数据模型，解析服务端返回的JSON数据  本类特指搜索城市数据接口
class PlaceResponse {
    data class PlaceResponse(val status:String, val query:String, val places:List<Place>)
    data class Place(val name:String, val location:Location, @SerializedName("formatted_address")val address:String)
    data class Location(val lng:String, val lat:String)
}