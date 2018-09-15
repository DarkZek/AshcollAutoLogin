package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ashcollauthlogin.darkzek.com.AshcollAutoLogin;
import ashcollauthlogin.darkzek.com.AshcollAutoLogin;
import ashcollauthlogin.darkzek.com.LoginResponse;

import static android.content.Context.MODE_PRIVATE;

//Detects when the user connects to WiFi
public class CaptivePortalWifiDetector extends BroadcastReceiver {

    private static CaptivePortalWifiDetector instance = new CaptivePortalWifiDetector();

    public static CaptivePortalWifiDetector getInstance() {
        return instance;
    }

    public boolean initilized = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            //Just connected to WiFi!
            String ssid = AshcollAutoLogin.getSSID(context);

            if (ssid == "<unknown ssid>") {

                SharedPreferences.Editor editor = context.getSharedPreferences("credentials", MODE_PRIVATE).edit();

                editor.putBoolean("location_notification", true);

                editor.commit();

                AshcollAutoLogin.instance.sendNotification("Cannot access SSID",
                        "AshcollAutoLogin cannot access your SSID when Location Services are disabled. Please enable location services or AshcollAutoLogin will try log you into all WiFi connections.", context);
            } else if (!ssid.equalsIgnoreCase("Student_BYOD")) {
                AshcollAutoLogin.getInstance().sendNotification("Not school: " + ssid, "Yay", context);
                //return;
            }

            DetectedWifi(context);
            return;
        }
    }

    public void DetectedWifi(Context context) {

        String usernameText = context.getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT");
        String passwordText = context.getSharedPreferences("credentials", MODE_PRIVATE).getString("password", "DEFAULT");

        AshcollAutoLogin.getInstance().sendNotification("Logging you in!", "doing it", context);

        LoginResponse login = CaptivePortalManager.getInstance().Login(usernameText, passwordText, context);

        AshcollAutoLogin.getInstance().sendNotification(login.toString(), "this", context);

        if (login == LoginResponse.SUCCESS) {
            AshcollAutoLogin.getInstance().sendTimedNotification("Successfully logged you into the WiFi ", "", context, 60000);
        } else if (login == LoginResponse.INCORRECT_USER_INFO) {
            AshcollAutoLogin.getInstance().sendNotification("Your WiFi account information was incorrect!", "Please change it", context);
        }
    }
}
