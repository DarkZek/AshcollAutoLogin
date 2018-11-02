package ashcollauthlogin.darkzek.com.CaptivePortalSystem

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

import ashcollauthlogin.darkzek.com.AshcollAutoLogin
import ashcollauthlogin.darkzek.com.LoginResponse

import android.content.Context.MODE_PRIVATE

//Detects when the user connects to WiFi
class CaptivePortalWifiDetector : BroadcastReceiver() {

    var initialized = false

    override fun onReceive(context: Context, intent: Intent) {

        AshcollAutoLogin._instance.sendNotification("Title", "Detected", context)

        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = conn.activeNetworkInfo

        if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {

            DetectedWifi(context)
            return
        }
    }

    fun DetectedWifi(context: Context) {
        val usernameText = context.getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT")
        val passwordText = context.getSharedPreferences("credentials", MODE_PRIVATE).getString("password", "DEFAULT")

        val login = CaptivePortalManager.getInstance().Login(usernameText, passwordText, context)

        AshcollAutoLogin.getInstance().sendNotification(login.toString(), "this", context)

        if (login === LoginResponse.SUCCESS) {
            AshcollAutoLogin.getInstance().sendTimedNotification("Successfully logged you into the WiFi ", "", context, 60000)
        } else if (login === LoginResponse.INCORRECT_USER_INFO) {
            AshcollAutoLogin.getInstance().sendNotification("Your WiFi account information was incorrect!", "Please change it", context)
        }
    }

    companion object {

        val _instance = CaptivePortalWifiDetector()

        fun getInstance(): CaptivePortalWifiDetector {
            return _instance
        }
    }
}
