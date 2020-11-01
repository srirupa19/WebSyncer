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

    TextView location;
    TextView date;
    TextView weather;
    TextView temp_c;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        weather = findViewById(R.id.weather);
        temp_c = findViewById(R.id.temp_c);


        // CREATE SHARED PREFERENCES
        sharedPreferences = getSharedPreferences("MyIp" , 0);


        // CREATE DATA OBJECT FOR PASSING DATA TO WORK MANAGER (IF NEEDED)

        // Create the Data object:
        Data myData = new Data.Builder()
                .build();

        // BUILD WORK REQUEST FOR WORK MANAGER


        PeriodicWorkRequest mathWork =
                new PeriodicWorkRequest.Builder(MathWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(myData)
                        .setInitialDelay(20 , TimeUnit.SECONDS)
                        // Constraints
                        .build();




        // GET INSTANCE OF WORK MANAGER
        WorkManager.getInstance(this).enqueue(mathWork);

//        WorkManager.getInstance(this)
//                .enqueueUniquePeriodicWork("jobTag", ExistingPeriodicWorkPolicy.KEEP, mathWork);



        // CHANGE UI AFTER WORK IS COMPLETED
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(mathWork.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished())
                        {
                            // UPDATE UI
                            sharedPreferences = getSharedPreferences("MyIp" , 0);

                            String city = sharedPreferences.getString("city" , "Kolkata");
                            String country = sharedPreferences.getString("country" , "India");

                            String loc = city + ", " + country;
                            location.setText(loc);

                            date.setText(sharedPreferences.getString("localtime" , "00 : 00 : 00"));
                            weather.setText(sharedPreferences.getString("weather" , "Sunny"));
                            temp_c.setText(sharedPreferences.getInt("temp_c", 25) + "°C");



                        }
                    }
                });





    }



    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = getSharedPreferences("MyIp" , 0);

        String city = sharedPreferences.getString("city" , "London");
        String country = sharedPreferences.getString("country" , "United Kingdom");

        String loc = city + ", " + country;
        location.setText(loc);

        date.setText(sharedPreferences.getString("localtime" , "2020-01-01 00:00"));
        weather.setText(sharedPreferences.getString("weather" , "Sunny"));
        temp_c.setText(sharedPreferences.getInt("temp_c", 25) + "°C");


    }
}