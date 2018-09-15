package ashcollauthlogin.darkzek.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Setup app
        AshcollAutoLogin login = AshcollAutoLogin.getInstance();
        login.createNotificationChannel(context);

        AshcollAutoLogin.SetupWifiListener(context);
    }

}