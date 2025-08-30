package org.taxivyapar.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.onesignal.OneSignal;

public class mynot extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.initWithContext(this);
        OneSignal.setAppId("c6d75888-f3ba-4c5a-8f2c-f61700cdb3f9");
        
        // Add debug logging for OneSignal initialization
        Log.d("OneSignal", "OneSignal initialized with App ID: c6d75888-f3ba-4c5a-8f2c-f61700cdb3f9");

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "vyapam_sound_channel";
            String channelName = "vyapam_sound_channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d("OneSignal", "Notification channel created: " + channelId);
            }
        }
    }
}
