package com.example.happyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class HappyWeatherApplication : Application() {
    companion object {

        const val TOKEN = "ONR8WNWA7fyE5fDO"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}