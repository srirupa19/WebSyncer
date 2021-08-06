package com.example.work;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MathWorker extends Worker {
    
    Context context;
    Notification notification;

    public SharedPreferences sharedPreferences ;

    public MathWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        // Set context
        this.context = context;

        // Create a notification
        notification = new Notification(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        String s = "Test String";

        // Setup network request
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url ="https://api.weatherapi.com/v1/current.json?key=71fcd39df6a84daba6471105203010&q=Kolkata";

        // Create a new JSON Object
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", "1");
            obj.put("name", "Weather");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Web request using volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET , url , obj ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String text = response.toString();

                        // Set default values
                        JSONObject location;
                        String city = "Kolkata";
                        String country = "India";
                        String localtime = "00 00 00";
                        JSONObject current;
                        int temp_c = 25 ;
                        JSONObject condition;
                        String weather = "Sunny";
                        String show = "No connection";
                        String title = "Sorry, no data";
                        int wind_kph = 0;
                        int precip_mm = 0;
                        int humidity = 0;
                        int feelslike_c = 25;

                        // Update responses
                        try {
                            location = response.getJSONObject("location");
                            city = location.getString("name");
                            country = location.getString("country");
                            localtime = location.getString("localtime");

                            current = response.getJSONObject("current");
                            temp_c = current.getInt("temp_c");
                            condition = current.getJSONObject("condition");
                            weather = condition.getString("text");
                            wind_kph = current.getInt("wind_kph");
                            precip_mm = current.getInt("precip_mm");
                            humidity = current.getInt("humidity");
                            feelslike_c = current.getInt("feelslike_c");

                            show =  "Feels like " + feelslike_c + "°C • " + weather;
                            title = temp_c + "°C " + "in " + city;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG" , show);
                        }

                        // Sending to UI thread via shared preferences
                        sharedPreferences =getApplicationContext().getSharedPreferences("MyIp",0);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Key" , text);
                        editor.putString("city" , city);
                        editor.putString("country" , country);
                        editor.putString("localtime" , localtime);
                        editor.putInt("temp_c" , temp_c);
                        editor.putString("weather" , weather);
                        editor.putInt("wind_kph" , wind_kph);
                        editor.putInt("precip_mm" , precip_mm);
                        editor.putInt("humidity" , humidity);
                        editor.putInt("feelslike_c" , feelslike_c);
                        editor.apply();

                        // Update Notification
                        notification.createNotification(show , title);
                    }
                } , new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                String TAG = "ERROR";

                if (error instanceof TimeoutError) {
                    Log.e(TAG, "onErrorResponse: " + "Timeout");
                } else if (error instanceof ServerError) {
                    Log.e(TAG, "onErrorResponse: " + "Server Error");
                } else if (error instanceof NetworkError) {
                    Log.e(TAG , "onErrorResponse: " + "Network");
                } else if (error instanceof ParseError) {
                    Log.e(TAG, "onErrorResponse: " + "Parse Error");
                } else {
                    Log.e(TAG, "onErrorResponse: Something went wrong ");
                }
            }
        });

        // Add JSON object request to queue
        queue.add(jsonObjectRequest);

        // Return data from Work if needed
        Data output = new Data.Builder()
                .putString("S" , s)
                .build();
        return Result.success(output);
    }
}