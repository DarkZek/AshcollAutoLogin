package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import ashcollauthlogin.darkzek.com.R;

public class CaptivePortalCheckerService extends Service{

    CaptivePortalManager portalManager = new CaptivePortalManager();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        portalManager = new CaptivePortalManager();
        portalManager.Setup(this);

        Log.i("Service", "Service started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);


        String usernameText = getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT");
        String passwordText = getSharedPreferences("credentials", MODE_PRIVATE).getString("password", "DEFAULT");

        //Login
        portalManager.tryLogin(usernameText, passwordText, this);

        return START_STICKY;
    }

    protected void onFinished() {

    }
}