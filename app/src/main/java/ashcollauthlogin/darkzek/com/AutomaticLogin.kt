package ashcollauthlogin.darkzek.com


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalManager

class AutomaticLogin : AppCompatActivity() {
    private val main = AshcollAutoLogin.getInstance()

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworks = connectivityManager.allNetworks
        for (network in activeNetworks) {
            if (connectivityManager.getNetworkInfo(network).isConnected) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)) {
                    ManuallyRun(this)
                }
                break
            }
        }
        this.finish()
    }

    private fun ManuallyRun(context: Context) {
        val usernameText = getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("username", "DEFAULT")
        val passwordText = getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("password", "DEFAULT")
        //Login
        val response = CaptivePortalManager.getInstance().Login(usernameText, passwordText, this)
        when (response) {
            LoginResponse.SUCCESS -> {
                main.sendNotification("Success!", "Successfully logged you into the schools WiFi!", context)
            }
            LoginResponse.NO_INTERNET -> {
                main.sendNotification("Error!", "You must be connected to the schools WiFi for this app to work!", context)
            }
            LoginResponse.LOGIN_PAGE_UNACCESSABLE -> {
                main.sendNotification("Error!", "Couldnt access the login page! Try toggling airplane mode on then off again", context)
            }
            LoginResponse.UNKNOWN_ERROR -> {
                main.sendNotification("Error!", "An unknown error occured! Please try again later", context)
            }
            LoginResponse.ALREADY_LOGGED_IN -> {
                main.sendNotification("Error!", "You're already logged into the WiFi! Try again when you're not", context)
            }
            else -> {
                main.sendNotification("Error!", "Something unknown happened to your login request! Try double checking your username and password", context)
            }
        }
    }
}