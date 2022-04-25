package com.example.weatherapp

import ViewModeState
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.ShapeDrawable
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.constraintlayout.solver.widgets.Rectangle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.location.LocationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.api.WeatherClient
import com.example.weatherapp.api.WeatherResponse
import com.example.weatherapp.ui.theme.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.math.roundToInt

private lateinit var locationCallback: LocationCallback

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
    val requestcode = 2137
    var latitude:Double = 0.0
    var longitude:Double =0.0
    val activity:Activity = this
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)?: return@setContent
    val defaultValue = resources.getString(R.string.saved_view_mode_default_key)
    var viewMode = sharedPref.getString(getString(R.string.saved_view_mode_key), defaultValue)
    var viewModeState=ViewModeState(viewMode!!)


    // ------------- location ----------------

    fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                requestcode
            )
            false
        } else {
            true
        }
    }

    var currentLocation: Location? = null
    var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var locationByGps:Location? = null
            fun isLocationEnabled(context: Context): Boolean {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                return LocationManagerCompat.isLocationEnabled(locationManager)
            }
    val hasGps = isLocationEnabled(this)
    //------------------------------------------------------//

    val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByGps= location

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}


    }
            fun isGpsTurnedOn(context: Context): Boolean {
                val manager =
                    context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }



fun getLocation(){
    if (isLocationPermissionGranted()) {
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }

//------------------------------------------------------//

        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }
//------------------------------------------------------//

//------------------------------------------------------//
        if (locationByGps != null ) {

                currentLocation = locationByGps
                latitude = currentLocation!!.latitude
                longitude = currentLocation!!.longitude
                // use latitude and longitude as per your need
        }


    }

    // ------------- weather -----------------
    var weatherResponse:WeatherResponse?=null

        var BaseUrl = "http://api.openweathermap.org/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherClient::class.java)

        var appId = "7b9ee17a2e9d0cccf7e65a844bc28236"
        val lat = latitude.toBigDecimal().toPlainString()
        val lon = longitude.toBigDecimal().toPlainString()
        val call = service.getCurrentWeatherData(lat, lon, appId,"metric")
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.code() == 200) {
                    weatherResponse = response.body()!!
                    setContent {
                        WeatherView(weather = weatherResponse!!, mode = viewModeState)
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })



}










            //-------------- activity -----------
            Starting()
            getLocation()


        }

    }




}


val latoFamily = FontFamily(
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato, FontWeight.Normal),
    Font(R.font.lato_bold, FontWeight.Bold),
    )



@Composable
fun Starting(){

    Box(modifier= Modifier
        .fillMaxSize()
        .background(color = Snowy)

    ){
        Column(modifier = Modifier
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
            Image(painter = painterResource(id = R.drawable.sunandcloud), contentDescription = null,Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "WeatherApp",  fontSize = 24.sp, color = Color.White)
        }


    }
}

@Composable
fun WeatherView(weather:WeatherResponse,mode:ViewModeState){

    val modeState = remember{mutableStateOf(mode)}
    Box(modifier = Modifier.fillMaxSize()) {
        Weather(modeState.value.currentMode,weather)
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp)
            ) {
                Row(modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {


                    when(modeState.value.currentMode){
                        "standard" -> Image(painter = painterResource(id = R.drawable.view_on), colorFilter = ColorFilter.tint(Color.White), contentDescription = null, modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                mode.changeMode();modeState.value = ViewModeState(mode.currentMode)
                            })
                        "elders" -> Image(painter = painterResource(id = R.drawable.view_off), colorFilter = ColorFilter.tint(Color.White), contentDescription = null, modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                mode.changeMode();modeState.value = ViewModeState(mode.currentMode)
                            })
                    }
                }


            }


        }

    }
}
@Composable
fun Weather(state:String,weather: WeatherResponse){
    when (state) {
        "standard" -> StandardView(weather = weather)
        "elders" -> EldersView(weather = weather)
    }

}

@Composable
fun Forecast(modifier:Modifier,state:String,weather: WeatherResponse,day:String,i:Int){
    var image = R.drawable.sunandcloud
    when (weather.daily[i].weather[0].main){
        "Thunderstorm" -> {image=R.drawable.thunder}
        "Drizzle" -> {image=R.drawable.rain}
        "Rain" -> {image=R.drawable.rain}
        "Snow" -> {image = R.drawable.snow}
        "Mist" -> {image = R.drawable.mist}
        "Fog" -> {image = R.drawable.mist}
        "Smoke" -> {image = R.drawable.mist}
        "Haze" -> {image = R.drawable.mist}
        "Clear" -> {image = R.drawable.sun}
        "Clouds" -> {
            when(weather.daily[i].weather[0].id ) {
                801 -> {image = R.drawable.sunandcloud}
                802 -> {image = R.drawable.clouds}
                else -> {image = R.drawable.clouds}
            }
        }
        else -> {image=R.drawable.sunandcloud}
    }
    Box(modifier = modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(10.dp))
        .background(color = Color(0x40FFFFFF))){
        when(state){
            "standard" ->{
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier= Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .align(Alignment.Center), verticalArrangement = Arrangement.Center){
                Image(painterResource(id = image),contentDescription = null,modifier= Modifier
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
                    colorFilter = ColorFilter.tint(Color.White))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(modifier=Modifier.align(Alignment.CenterHorizontally),
                        text = day+"",
                        fontSize = 25.sp,
                        color= Color.White,
                        fontFamily = latoFamily,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(modifier=Modifier.align(Alignment.CenterHorizontally),
                        text = weather.daily[i].temp.day.roundToInt().toString()+"°C",
                        fontSize = 30.sp,
                        color= Color.White,
                        fontFamily = latoFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            "elders" -> {Row(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .align(Alignment.Center)){
                Image(painterResource(id = image),contentDescription = null,modifier= Modifier
                    .height(60.dp)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    colorFilter = ColorFilter.tint(Color.White))

                Column(modifier= Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterVertically)
                    .padding(10.dp)){
                    Text(modifier=Modifier.align(Alignment.CenterHorizontally),
                        text = day+"",
                        fontSize = 35.sp,
                        color= Color.White,
                        fontFamily = latoFamily,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(modifier=Modifier.align(Alignment.CenterHorizontally),
                        text = weather.daily[i].temp.day.roundToInt().toString()+"°C",
                        fontSize = 40.sp,
                        color= Color.White,
                        fontFamily = latoFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }}
        }


    }
}
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun StandardView(weather:WeatherResponse) {

    var imageId=0
    var color = Color.Black

    when (weather.current.weather[0].main){
        "Thunderstorm" -> {imageId=R.drawable.thunder;color=Thunder}
        "Drizzle" -> {imageId=R.drawable.rainy; color= Rainy}
        "Rain" -> {imageId=R.drawable.rainy; color= Rainy}
        "Snow" -> {imageId = R.drawable.snowy; color= Snowy}
        "Mist" -> {imageId = R.drawable.misty; color= Misty}
        "Fog" -> {imageId = R.drawable.misty; color= Misty}
        "Smoke" -> {imageId = R.drawable.misty; color= Misty}
        "Haze" -> {imageId = R.drawable.misty; color= Misty}
        "Clear" -> {imageId = R.drawable.sunny; color= Sunny}
        "Clouds" -> {
            when(weather.current.weather[0].id ) {
                801 -> {imageId = R.drawable.sunandclouds; color= SunAndClouds}
                802 -> {imageId = R.drawable.cloudy; color= Cloudy}
                else -> {imageId = R.drawable.darkclouds; color= DarkClouds}
            }
        }
        else -> {imageId=R.drawable.sunandclouds; color= SunAndClouds}
    }

    Box(modifier = Modifier
        .padding(0.dp)
        .fillMaxSize()
        .verticalScroll(state = rememberScrollState())
        .background(color = color)
    ) {


            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top, unbounded = true)

            )
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Last sync: " + SimpleDateFormat("dd.MM, HH:mm", Locale.GERMAN).format(
                        Date((weather.current.dt) * 1000)
                    ),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(300.dp))
                Text(
                    text = weather.current.temp.roundToInt().toString() + "°C",
                    fontSize = 50.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "humidity: " + weather.current.humidity.toString() + "%",
                    fontSize = 30.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "air pressure: " + weather.current.pressure.toString() + "hPa",
                    fontSize = 30.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "sunrise: " + SimpleDateFormat(
                        "HH:mm",
                        Locale.GERMAN
                    ).format(Date((weather.current.sunrise) * 1000)),
                    fontSize = 30.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "sunset: " + SimpleDateFormat(
                        "HH:mm",
                        Locale.GERMAN
                    ).format(Date((weather.current.sunset) * 1000)),
                    fontSize = 30.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "swipe down\nto see 7 day forecast",
                    fontSize = 30.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(30.dp))


            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)) {
                Row(modifier = Modifier
                    .fillMaxSize(),
                     horizontalArrangement = Arrangement.SpaceAround) {
                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[0].dt*1000)),
                        i = 0
                    )

                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[1].dt*1000)),
                        i = 1
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceAround) {
                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[2].dt*1000)),
                        i = 2
                    )

                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[3].dt*1000)),
                        i = 3
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceAround) {
                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[4].dt*1000)),
                        i = 4
                    )
                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[5].dt*1000)),
                        i = 5
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier
                    .fillMaxHeight()
                    .width(170.dp)
                    .align(Alignment.CenterHorizontally)) {
                    Forecast(modifier= Modifier
                        .width(170.dp)
                        .height(220.dp),
                        state = "standard",
                        weather = weather,
                        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(weather.daily[6].dt*1000)),
                        i = 6
                    )

                }

            }

        }
    }
}

@Composable
fun EldersView(weather: WeatherResponse) {
    var imageId = 0

    when (weather.current.weather[0].main) {
        "Thunderstorm" -> imageId = R.drawable.thunder2
        "Drizzle" -> imageId = R.drawable.rainy2
        "Rain" -> imageId = R.drawable.rainy2
        "Snow" -> imageId = R.drawable.snowy2
        "Mist" -> imageId = R.drawable.misty2
        "Fog" -> imageId = R.drawable.misty2
        "Smoke" -> imageId = R.drawable.misty2
        "Haze" -> imageId = R.drawable.misty2
        "Clear" -> imageId = R.drawable.sunny2
        "Clouds" -> {
            when (weather.current.weather[0].id) {
                801 -> imageId = R.drawable.sunandclouds2
                802 -> imageId = R.drawable.cloudy2
                else -> imageId = R.drawable.darkclouds2
            }
        }
        else -> imageId = R.drawable.sunandclouds2
    }

    Box(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .background(color = Elders)
    ) {


        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top, unbounded = true)

        )

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Last sync: " + SimpleDateFormat(
                        "dd.MM, HH:mm",
                        Locale.GERMAN
                    ).format(Date((weather.current.dt) * 1000)),
                    fontSize = 25.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(250.dp))
                Text(
                    text = weather.current.temp.roundToInt().toString() + "°C",
                    fontSize = 55.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "humidity: " + weather.current.humidity.toString() + "%",
                    fontSize = 35.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "air pressure: " + weather.current.pressure.toString() + "hPa",
                    fontSize = 35.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "sunrise: " + SimpleDateFormat(
                        "HH:mm",
                        Locale.GERMAN
                    ).format(Date((weather.current.sunrise) * 1000)),
                    fontSize = 35.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "sunset: " + SimpleDateFormat(
                        "HH:mm",
                        Locale.GERMAN
                    ).format(Date((weather.current.sunset) * 1000)),
                    fontSize = 35.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "swipe down\nto see 7 day forecast",
                    fontSize = 35.sp,
                    color = Color.White,
                    fontFamily = latoFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(30.dp))


            }


            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[0].dt * 1000)),
                    i = 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[1].dt * 1000)),
                    i = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[2].dt * 1000)),
                    i = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[3].dt * 1000)),
                    i = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[4].dt * 1000)),
                    i = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[5].dt * 1000)),
                    i = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Forecast(
                    state = "elders",
                    weather = weather,
                    day = SimpleDateFormat(
                        "EEEE",
                        Locale.ENGLISH
                    ).format(Date(weather.daily[6].dt * 1000)),
                    i = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )


            }
        }
    }
}
