package com.example.esha.weatherapp;

import android.util.Log;

public class WeatherApiActivity {
    public static String WEATHER_API_KEY;

    public WeatherApiActivity(double lat, double lng) {
        Log.i("WeatherApiActivity", "getLatLng: "+lat+","+lng);

        WEATHER_API_KEY = "https://api.darksky.net/forecast/"+BuildConfig.WEATHERAPIKEY+"/"+lat+","+lng+"?units=si";
    }
}
