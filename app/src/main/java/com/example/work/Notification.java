package com.example.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification extends ContextWrapper {

    Context context;

    public Notification(Context context)
    {
        super(context);
        this.context = context;

        // CHANNEL MUST BE CREATED IN THE CONSTRUCTOR
        createNotificationChannel();

    }

    public void createNotificationChannel() {

        String channel_name = "channel_name";
        String channel_description = "channel_description";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = channel_name;
            String description = channel_description;

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NOTF", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createnotf(String content)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "NOTF")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Work Reminder")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);



        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }

}
