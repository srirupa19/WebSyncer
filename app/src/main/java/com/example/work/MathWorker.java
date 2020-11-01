package com.example.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.core.content.ContextCompat.getSystemService;

public class MathWorker extends Worker {


    Context context;
    Notification notification;
    // Define the parameter keys:
    public static final String KEY_X_ARG = "X";
    public static final String KEY_Y_ARG = "Y";
    public static final String KEY_Z_ARG = "Z";
    public SharedPreferences sharedPreferences ;
    // ...and the result key:
    public static final String KEY_RESULT = "result";

    public MathWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        // SET CONTEXT AND NOTIFICATION
        this.context = context;

        notification = new Notification(getApplicationContext());
    }

    @Override
    public Result doWork() {
        // Fetch the arguments (and specify default values):
        int x = getInputData().getInt(KEY_X_ARG, 0);
        int y = getInputData().getInt(KEY_Y_ARG, 0);
        int z = getInputData().getInt(KEY_Z_ARG, 0);
        String s = "Text";

        final int result = x + y + z;


        // SET UP REQUEST QUEUE FOR WORK

        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url ="https://reqres.in/api/products/3";


        // WEB REQUEST USING VOLLEY
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", "1");
            obj.put("name", "myname");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET , url , obj ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String text = response.toString();
//                        Toast toast=Toast. makeText(getApplicationContext(),text,Toast. LENGTH_LONG);
//                        toast. setMargin(50,50);
//                        toast. show();

                        // SENDING TO UI THREAD VIA SHARED PREFERENCES

                        sharedPreferences =getApplicationContext().getSharedPreferences("MyIp",0);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Key" , text);
                        editor.putInt("Test" , result);
                        editor.commit();

                        // UPDATING NOTIFICATION
                        notification.createnotf(text);





                    }
                } , new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                String text = "OOPS";
                Toast toast=Toast. makeText(getApplicationContext(),text,Toast. LENGTH_LONG);
                toast. setMargin(50,50);
                toast. show();

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

        queue.add(jsonObjectRequest);



        // RETURN DATA FROM WORK IF NEEDED
        Data output = new Data.Builder()
                .putInt(KEY_RESULT, result)
                .putString("S" , s)
                .build();
        return Result.success(output);
    }









}