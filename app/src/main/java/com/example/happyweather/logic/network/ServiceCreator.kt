package com.example.happyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//object修饰类表示单例类   创建Retrofit构建器
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    //提供外部可见的create()，并接收一个Class类型参数   eg使用：val appService = ServiceCreator.create(AppService::class.java)
    fun <T> create(serviceClass:Class<T>):T = retrofit.create(serviceClass)
    //泛型实化功能， inline关键字修饰方法，reified关键字修饰泛型      eg使用：val appService = ServiceCreator.create<AppService>()
    inline fun <reified T> create():T = create(T::class.java)
}