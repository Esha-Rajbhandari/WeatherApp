package com.example.esha.weatherapp;

import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class CustomJsonRequest extends JsonObjectRequest {
    private Priority mPriority;

    public CustomJsonRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }


    public void setPriority(Priority priority){
        mPriority = priority;
    }

    public Priority getPriority(){
        return mPriority == null ? Priority.NORMAL : mPriority;
    }

}
