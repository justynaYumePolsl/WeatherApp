package com.example.weatherapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface WeatherClient {

    @GET("data/2.5/onecall?exclude=hourly&")
    fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String, @Query("APPID") app_id: String, @Query("units") metric:String): Call<WeatherResponse>

}