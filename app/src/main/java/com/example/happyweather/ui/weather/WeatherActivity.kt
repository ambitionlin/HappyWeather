package com.example.happyweather.ui.weather

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.happyweather.R
import com.example.happyweather.databinding.ActivityWeatherBinding
import com.example.happyweather.logic.model.Weather
import com.example.happyweather.logic.model.getSky
import com.example.happyweather.ui.place.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    private lateinit var binding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView  //拿到当前Activity的DecorView
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        //改变系统UI的显示，以下设置将Activity的布局会显示在状态栏上面
//        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT    //将状态栏设置成透明色
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //从Intent中取出经纬度坐标和地区名称，并赋值到WeatherViewModel的相应变量中
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        //通过观察者模式观察weatherLiveData对象，调用showWeatherInfo(weather)解析从服务端返回回来的天气信息
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        //向服务端请求当前位置的天气数据
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    //从Weather对象中获取数据，并显示在相应的控件上
    private fun showWeatherInfo(weather: Weather) {
        binding.now.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.now.currentTemp.text = currentTempText
        binding.now.currentSky.text = (getSky(realtime.skycon)).info
        val currentPM25Text = "空气指数 ${realtime.airQuailty.aqi.chn.toInt()}"
        binding.now.currentAQI.text = currentPM25Text
        binding.now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml布局中的数据
        binding.forecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this)
                .inflate(R.layout.forecast_item, binding.forecast.forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding.forecast.forecastLayout.addView(view)
        }
        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        binding.lifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.lifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.lifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.lifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc

        binding.weatherLayout.visibility = View.VISIBLE
    }
}