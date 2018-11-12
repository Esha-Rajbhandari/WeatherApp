package com.example.esha.weatherapp;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class WeatherController extends Application{

    private RequestQueue mRequestQueue;
    private static WeatherController mInstance;
    private static String TAG = WeatherController.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized WeatherController getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel(){
        mRequestQueue.cancelAll(TAG);
    }

}
