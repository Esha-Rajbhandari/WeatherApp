package com.example.esha.weatherapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class CustomizedFontText extends android.support.v7.widget.AppCompatTextView {

    private Context context;
    public CustomizedFontText(Context context) {
        super(context);
        this.context = context;
        createTextView();
    }

    public CustomizedFontText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createTextView();
    }

    public CustomizedFontText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        createTextView();
    }

    private void createTextView(){
        setGravity(Gravity.CENTER);
        String fontPath = "font/taile_0.ttf";
        Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        setTypeface(tf);
    }
}
