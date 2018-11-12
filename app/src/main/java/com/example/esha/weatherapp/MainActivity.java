package com.example.esha.weatherapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mDegreeCelsius;
    private TextView mWeather;
    private TextView mPlaceText;
    private ImageView mWeatherIcon;
    private String searchValue;
    private Typeface weatherFont;
    private CoordinatorLayout coordinatorLayout;
    final int RESULT_CODE = 1;
    private String result;
    private boolean isCelsius = true;
    WeatherController weatherController = WeatherController.getInstance();
    private ArrayList weatherList=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        mDegreeCelsius = findViewById(R.id.degree_celsius);
        mWeather = findViewById(R.id.weather);
        mPlaceText = findViewById(R.id.place_text);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        mWeatherIcon = findViewById(R.id.weather_icon);

        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.hamburger);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = preferences.getString("unit", "unit");
        if (unit == null) {
            isCelsius = true;
        } else {
            if (unit.equalsIgnoreCase("°C"))
                isCelsius = true;
            else
                isCelsius = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem actionSearch = menu.findItem(R.id.menu_search);
        final SearchView searchViewEditText = (SearchView) actionSearch.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchViewEditText.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);

        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countries);
        searchAutoComplete.setAdapter(newsAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String queryString = (String) adapterView.getItemAtPosition(i);
                searchAutoComplete.setText("" + queryString);
            }
        });

        searchViewEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchValue = searchViewEditText.getQuery().toString().trim();
                getLatLng(searchValue);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void getLatLng(String place){
        if(Geocoder.isPresent()){
            try {
                String location = place;
                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(location, 5);

                List<LatLng> ll = new ArrayList<LatLng>(addresses.size());
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                if(ll.size()>0)
                loadWeatherData(ll.get(ll.size()-1).getLatitude(),ll.get(ll.size()-1).getLongitude(), isCelsius);
                else
                    Toast.makeText(weatherController, "Cannot find the city", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // handle the exception
            }
        }
    }

    private boolean getUnit() {
        Log.i(TAG, "getUnit: " + result);
        if (result.equalsIgnoreCase("°C")) {
            return true;
        } else {
            return false;
        }
    }

    private void loadWeatherData(double lat, double lng, final boolean isUnit) {
        Log.i(TAG, "loadWeatherData: " + isUnit);
        new WeatherApiActivity(lat, lng);
        CustomJsonRequest customJsonRequest = new CustomJsonRequest(Request.Method.GET, WeatherApiActivity.WEATHER_API_KEY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String currTemperature;
                    String weatherSummary;
                    String icon;
                    String city;
                    double temperature;
                    double convertedTemp;


                    JSONObject object = response.getJSONObject("currently");
                    city = response.getString("timezone");
                    temperature = object.getDouble("temperature");
                    weatherSummary = object.getString("summary");
                    icon = object.getString("icon");
                    int iconId= IconActivity.getIcon(icon);

                    mWeatherIcon.setImageResource(iconId);

                    if (isUnit) {
                        mDegreeCelsius.setText(temperature + "°C");

                    } else {
                        convertedTemp = (temperature * 9 / 5) +32;
                        currTemperature = String.format("%.2f", convertedTemp);
                        mDegreeCelsius.setText(currTemperature + "°F");
                    }
                    JSONObject obj1=response.getJSONObject("daily");
                    JSONArray array=obj1.getJSONArray("data");

                    for(int i=0;i<array.length();i++){
                        JSONObject obj2=array.getJSONObject(i);
                        Weather weather=new Weather();
                        weather.setSummary(obj2.getString("icon"));
                        weather.setTemperature(obj2.getDouble("temperatureHigh"));
                        Log.i(TAG, "onResponse: "+obj2.getDouble("temperatureHigh"));
                        weatherList.add(weather);
                    }

                    RecyclerView recyclerView=findViewById(R.id.recycler_view);
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setAdapter(new RecyclerViewAdapter(weatherList));
                    Log.i(TAG, "onResponse: "+weatherList.get(0).toString());
                    recyclerView.setLayoutManager(linearLayoutManager);



                    mPlaceText.setText(city);
                    mWeather.setText(weatherSummary);
                    //mWeatherIcon.setText(Html.fromHtml(weatherCode));

                    if (weatherSummary.equalsIgnoreCase("Mostly Cloudy")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            coordinatorLayout.setBackground((ContextCompat.getDrawable(getApplicationContext(), R.drawable.background)));
                        }
                    } else if (weatherSummary.equalsIgnoreCase("rain")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            coordinatorLayout.setBackground((ContextCompat.getDrawable(getApplicationContext(), R.drawable.rainy_background)));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            coordinatorLayout.setBackground((ContextCompat.getDrawable(getApplicationContext(), R.drawable.sunny_background)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error.getLocalizedMessage());
            }
        });

        customJsonRequest.setPriority(Request.Priority.HIGH);
        weatherController.add(customJsonRequest);

    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, RESULT_CODE);

        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                result = data.getStringExtra("result");
                isCelsius = getUnit();
                Log.i(TAG, "onActivityResult: " + isCelsius);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }
    }
}
