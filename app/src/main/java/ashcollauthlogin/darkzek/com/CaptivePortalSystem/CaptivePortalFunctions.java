package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class CaptivePortalFunctions {

    public static String GetSSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        if (wifiInfo.getLinkSpeed() == -1) {
            return "";
        }

        return wifiInfo.getSSID();
    }

    public static void IncreaseLoginCount(Context context) {
        //Get times logged in
        int timesLoggedIn = context.getSharedPreferences("credentials", 0).getInt("timesLoggedIn", 0);
        //Increase it by 1
        context.getSharedPreferences("credentials", 0).edit().putInt("timesLoggedIn", timesLoggedIn + 1).commit();
    }
}
