package com.example.weatherapp.api

data class Info(
    val dt: Int,
    val humidity: Int,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val weather: List<Weather>
)