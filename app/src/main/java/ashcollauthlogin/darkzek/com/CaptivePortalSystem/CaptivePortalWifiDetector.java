package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ashcollauthlogin.darkzek.com.AshcollAutoLogin;
import ashcollauthlogin.darkzek.com.LoginResponse;

import static android.content.Context.MODE_PRIVATE;

//Detects when the user connects to WiFi
public class CaptivePortalWifiDetector extends BroadcastReceiver {

    private static CaptivePortalWifiDetector instance = new CaptivePortalWifiDetector();

    public static CaptivePortalWifiDetector getInstance() {
        return instance;
    }

    public boolean initialized = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        AshcollAutoLogin.instance.sendNotification("Title", "Detected", context);

        ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            DetectedWifi(context);
            return;
        }
    }

    public void DetectedWifi(Context context) {
        String usernameText = context.getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT");
        String passwordText = context.getSharedPreferences("credentials", MODE_PRIVATE).getString("password", "DEFAULT");

        LoginResponse login = CaptivePortalManager.getInstance().Login(usernameText, passwordText, context);

        AshcollAutoLogin.getInstance().sendNotification(login.toString(), "this", context);

        if (login == LoginResponse.SUCCESS) {
            AshcollAutoLogin.getInstance().sendTimedNotification("Successfully logged you into the WiFi ", "", context, 60000);
        } else if (login == LoginResponse.INCORRECT_USER_INFO) {
            AshcollAutoLogin.getInstance().sendNotification("Your WiFi account information was incorrect!", "Please change it", context);
        }
    }
}
