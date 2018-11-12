package com.example.esha.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class SettingsActivity extends Activity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RadioButton rdbtn;
    private  int selectedId;
    static int i=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        radioGroup=findViewById(R.id.radio_group_temp);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectedId=radioGroup.getCheckedRadioButtonId();
                radioButton=findViewById(selectedId);
                save(selectedId);
                load();
                addListenerOnButton(rdbtn.getText().toString().trim());
                Log.i(SettingsActivity.class.getSimpleName(), "onCreate: "+rdbtn.getText().toString());

            }
        });
        if(i==1){
            RadioButton rb=findViewById(R.id.celsius_radio_btn);
            rb.setSelected(true);
            rb.setChecked(true);
            save(radioGroup.getCheckedRadioButtonId());
            i++;
        }
        load();

    }

    public void addListenerOnButton(String result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    private void save(int radioid) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("check", radioid);
        rdbtn=radioGroup.findViewById(radioid);
        editor.putString("unit",rdbtn.getText().toString().trim());
        editor.commit();
    }

    public void load() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        int radioId=sharedPreferences.getInt("check", 0);
        if(radioId>0){
            rdbtn=radioGroup.findViewById(radioId);
            rdbtn.setChecked(true);
            rdbtn.setSelected(true);
            Log.i(SettingsActivity.class.getSimpleName(), "load: "+radioButton.getText().toString());
        }

    }

}
