package ashcollauthlogin.darkzek.com;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Random;

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalWifiDetector;

public class AshcollAutoLogin {

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
        h.postDelayed(new Runnable() {
            public void run() {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(404);
            }
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

    public static void SetupWifiListener(Context context) {AshcollAutoLogin.getInstance().sendNotification("started wifi!", "hhhhh", context);

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        intentFilter.setPriority(999);

        CaptivePortalWifiDetector wifiDetector = CaptivePortalWifiDetector.getInstance();
        if (!wifiDetector.initilized) {
            context.getApplicationContext().registerReceiver(wifiDetector, intentFilter);
            wifiDetector.initilized = true;
        }

    }

    public static void checkBatterySaver(Context context) {

        //Make sure it only runs on android devices that have power saver features
        if (android.os.Build.VERSION.SDK_INT < 23) {
            return;
        }

        //Get system power manager
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        //If
        if (!pm.isIgnoringBatteryOptimizations("ashcollauthlogin.darkzek.com")) {
            ShowBatteryExplination(context);
        }

    }

    public static String getSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static void checkLocationServices(Context context, Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //Does not require location
            return;
        }

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Denied
            ShowLocationExplination(context, activity);
        } else {
            checkBatterySaver(context);
        }
    }

    private static void ShowLocationExplination(Context context, Activity activity) {
        String content = "Ashcoll Auto Login requires the location permission to find the name of the WiFi you are connected to, and make sure you're connected to the Student_BYOD before logging you in! This permission is required.";

        new MaterialDialog.Builder(context)
                .title("Location Services Alert")
                .content(content)
                .positiveText("AGREE")
                .onPositive((dialog, which) -> {
                    Intent batterySaverIntent=new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    context.startActivity(batterySaverIntent);
                }).show();
    }

    private static void ShowBatteryExplination(Context context) {
        String content = "Please allow Ashcoll Auto Login to bypass the Battery Saver feature of Android. This feature kills the WiFi checker and stops the app from automatically logging you in. If you experience glitches this is why";

        new MaterialDialog.Builder(context)
                .title("Battery Saver Alert")
                .content(content)
                .positiveText("AGREE")
                .onPositive((dialog, which) -> {
                    Intent batterySaverIntent=new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    context.startActivity(batterySaverIntent);
                }).show();
    }
}
