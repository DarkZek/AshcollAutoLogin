package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ashcollauthlogin.darkzek.com.R;

public class CaptivePortalManager {

    public String schoolSSID = "Student_BYOD";

    public void Setup(Context context) {
        //Create the notification channel
        createNotificationChannel(context);
    }

    protected void sendNotification(String title, String content, Context context) {
        //Build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "CaptivePortalAlerts")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle());
        mBuilder.build();

        //Send it off
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(1000), mBuilder.build());

    }

    private void createNotificationChannel(Context context) {
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

    /*
        The actual login code
     */

    public void tryLogin(String username, String password, Context context) {
        //Check if we're connected to the schools network
        String ssid = getCurrentSsid(context);

        if (ssid == null || !ssid.equalsIgnoreCase(schoolSSID)) {
            if (ssid == null) {
                ssid = "NULL";
            }
            //Not connected to school wifi!
            sendNotification("An error has occured " + ssid, "You need to be connected to the schools 'Student_BYOD' for this app to function.", context);
        }

        //Get the data from the website
        String data = "";

        try {
            data = new CaptivePortalLoginPageLocater().execute().get();
        } catch (InterruptedException e){return;} catch (ExecutionException e) {return;}

        //That means an error was thrown - or there is no captive portal
        if (data == null) {
            sendNotification("Error", "You're already logged in bro", context);
            return;
        }

        //Find the ID using regex
        String id = "";

        Pattern pattern = Pattern.compile("(?<=name=\"magic\" value=\")(.*)(?=\"><input)");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            id = matcher.group(1);
        }

        //Make sure its a valid id
        if (id == "") {
            return;
        }

        //Get response
        String response = "";

        //Send the login request
        try {
            response = new CaptivePortalLoginSystem().execute(id, username, password).get();
        } catch (InterruptedException e){return;} catch (ExecutionException e) {return;}

        if (response.equals("Success")) {
            //Success!
            IncreaseLoginCount(context);
            return;
        }

        //An error occured :(
        sendNotification("An error has occurred", response, context);
    }

    private void IncreaseLoginCount(Context context) {
        //Get times logged in
        int timesLoggedIn = context.getSharedPreferences("credentials", 0).getInt("timesLoggedIn", 0);
        //Increase it by 1
        context.getSharedPreferences("credentials", 0).edit().putInt("timesLoggedIn", timesLoggedIn + 1);
    }

    public String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }
}
