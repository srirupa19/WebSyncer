package com.example.work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.textView);


        // CREATE SHARED PREFERENCES
        sharedPreferences = getSharedPreferences("MyIp" , 0);
        final String string = sharedPreferences.getString("Key", "Weird");


        // CREATE DATA OBJECT FOR PASSING DATA TO WORK MANAGER (IF NEEDED)

        // Create the Data object:
        Data myData = new Data.Builder()
                // We need to pass three integers: X, Y, and Z
                .putInt("X", 42)
                .putInt("Y", 421)
                .putInt("Z", 8675309)
                .build();

        // BUILD WORK REQUEST FOR WORK MANAGER

        PeriodicWorkRequest mathWork =
                new PeriodicWorkRequest.Builder(MathWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(myData)
                        .setInitialDelay(20 , TimeUnit.SECONDS)
                        // Constraints
                        .build();




        // GET INSTANCE OF WORK MANAGER
        //WorkManager.getInstance(this).enqueue(mathWork);

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("jobTag", ExistingPeriodicWorkPolicy.KEEP, mathWork);



        // CHANGE UI AFTER WORK IS COMPLETED
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(mathWork.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished())
                        {
                            // UPDATE UI
                            sharedPreferences = getSharedPreferences("MyIp" , 0);
                            int number = sharedPreferences.getInt("Test" , -1);
                            String actual = sharedPreferences.getString("Key" , "Default String");

                            // textView.setText(Integer.toString(number));
                            textView.setText(actual);


                        }
                    }
                });





    }



    @Override
    protected void onResume() {
        super.onResume();


    }
}