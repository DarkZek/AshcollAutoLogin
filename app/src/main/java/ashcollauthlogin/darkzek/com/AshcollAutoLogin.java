package ashcollauthlogin.darkzek.com;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Random;

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalWifiDetector;

public class AshcollAutoLogin {

    //TODO: Make the captive portal detection work on all android versions

    public static AshcollAutoLogin instance = new AshcollAutoLogin();

    private AshcollAutoLogin() {}

    public static AshcollAutoLogin getInstance() {
        return instance;
    }

    public Notification sendNotification(String title, String content, Context context, int id) {

        //Build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "CaptivePortalAlerts")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle());
        mBuilder.build();

        //Send it off
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());

        return mBuilder.build();
    }

    public Notification sendNotification(String title, String content, Context context) {
        return sendNotification(title, content, context, new Random().nextInt(1000));
    }

    //Time in ms
    public void sendTimedNotification(String title, String content, Context context, int time) {
        sendNotification(title, content, context, 404);

        Handler h = new Handler();
        h.postDelayed(() -> {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(404);
        }, time);
    }

    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CaptivePortalAlerts";
            String description = "Alerts for connecting to your captive portal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CaptivePortalAlerts", name, importance);
            channel.setDescription(description);

            //Register
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void SetupWifiListener(Context context) {

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN);

        intentFilter.setPriority(999);

        CaptivePortalWifiDetector wifiDetector = CaptivePortalWifiDetector.getInstance();
        if (!wifiDetector.initialized) {
            context.getApplicationContext().registerReceiver(wifiDetector, intentFilter);
            wifiDetector.initialized = true;
        }

    }
}
