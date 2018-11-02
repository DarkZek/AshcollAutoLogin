package ashcollauthlogin.darkzek.com

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

import java.util.Random

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalWifiDetector

class AshcollAutoLogin private constructor() {

    //TODO: Pretty sure it's broken in Android Pie - needs more testing

    @JvmOverloads
    fun sendNotification(title: String, content: String, context: Context, id: Int = Random().nextInt(1000)): Notification {

        //Build notification
        val mBuilder = NotificationCompat.Builder(context, "CaptivePortalAlerts")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle())
        mBuilder.build()

        //Send it off
        val notificationManager = NotificationManagerCompat.from(context)

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build())

        return mBuilder.build()
    }

    //Time in ms
    fun sendTimedNotification(title: String, content: String, context: Context, time: Int) {
        sendNotification(title, content, context, 404)

        val h = Handler()
        h.postDelayed({
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(404)
        }, time.toLong() * 1000)
    }

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CaptivePortalAlerts"
            val description = "Alerts for connecting to your captive portal"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CaptivePortalAlerts", name, importance)
            channel.description = description

            //Register
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {

        //TODO: Make the captive portal detection work on all android versions
        var _instance = AshcollAutoLogin()

        fun getInstance(): AshcollAutoLogin {
            return _instance
        }

        fun setupWifiListener(context: Context) {

            val intentFilter = IntentFilter()

            intentFilter.addAction(ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN)

            intentFilter.priority = 999

            val wifiDetector = CaptivePortalWifiDetector.getInstance()
            if (!wifiDetector.initialized) {
                context.applicationContext.registerReceiver(wifiDetector, intentFilter)
                wifiDetector.initialized = true
            }

        }
    }
}
