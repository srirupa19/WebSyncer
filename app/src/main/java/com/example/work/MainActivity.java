package com.example.work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;


import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView city;
    TextView country;
    TextView humidity;
    TextView feelslike_c;
    TextView precip_mm;
    TextView wind_kph;
    TextView date;
    TextView weather;
    TextView temp_c;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get data from text fields
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        feelslike_c = findViewById(R.id.feelslike_c);
        humidity = findViewById(R.id.humidity);
        precip_mm = findViewById(R.id.precip_mm);
        wind_kph = findViewById(R.id.wind_kph);
        date = findViewById(R.id.date);
        weather = findViewById(R.id.weather);
        temp_c = findViewById(R.id.temp_c);

        // Crate shared preferences to store data
        sharedPreferences = getSharedPreferences("MyIp" , 0);

        // Create the Data object to pass data to Work Manager if needed
        Data myData = new Data.Builder()
                .build();

        // Setup a periodic Work Request
        PeriodicWorkRequest mathWork =
                new PeriodicWorkRequest.Builder(MathWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(myData)
                        .setInitialDelay(20 , TimeUnit.SECONDS)
                        // Constraints
                        .build();

        // Get instance of Work Manager to queue the Work
        WorkManager.getInstance(this).enqueue(mathWork);

        // Update UI after Worker Data changes/after data is fetched
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(mathWork.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished())
                        {
                            // Update UI
                            sharedPreferences = getSharedPreferences("MyIp" , 0);

                            country.setText(sharedPreferences.getString("country" , "India"));
                            city.setText(sharedPreferences.getString("city" , "Kolkata"));
                            feelslike_c.setText("Feels like " + sharedPreferences.getInt("feelslike_c" , 25) +"°");
                            humidity.setText("Humidity • " + sharedPreferences.getInt("humidity" , 0));
                            precip_mm.setText("Precipitation • " + sharedPreferences.getInt("precip_mm" , 0) + " mm");
                            wind_kph.setText("Wind Speed • " + sharedPreferences.getInt("wind_kph" , 0) + " Kmph");

                            date.setText(sharedPreferences.getString("localtime" , "00:00:00"));
                            weather.setText(sharedPreferences.getString("weather" , "Sunny"));
                            temp_c.setText(sharedPreferences.getInt("temp_c", 25) + "°");
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        // Update UI after resuming
        sharedPreferences = getSharedPreferences("MyIp" , 0);

        country.setText(sharedPreferences.getString("country" , "India"));
        city.setText(sharedPreferences.getString("city" , "Kolkata"));
        feelslike_c.setText("Feels like " + sharedPreferences.getInt("feelslike_c" , 25) +"°");
        humidity.setText("Humidity • " + sharedPreferences.getInt("humidity" , 0));
        precip_mm.setText("Precipitation • " + sharedPreferences.getInt("precip_mm" , 0) + " mm");
        wind_kph.setText("Wind Speed • " + sharedPreferences.getInt("wind_kph" , 0) + " Kmph");

        date.setText(sharedPreferences.getString("localtime" , "00:00:00"));
        weather.setText(sharedPreferences.getString("weather" , "Sunny"));
        temp_c.setText(sharedPreferences.getInt("temp_c", 25) + "°");
    }
}