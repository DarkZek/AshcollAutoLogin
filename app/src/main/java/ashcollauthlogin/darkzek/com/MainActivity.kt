package ashcollauthlogin.darkzek.com

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalManager

class MainActivity : AppCompatActivity() {

    private var mAdView: AdView? = null

    private val main = AshcollAutoLogin.getInstance()

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        AshcollAutoLogin.getInstance().createNotificationChannel(this)

        //Check if its their first time
        if (getSharedPreferences("credentials", Context.MODE_PRIVATE).contains("username")) {
            //Already setup!

            //Setup wifi listener
            AshcollAutoLogin.SetupWifiListener(applicationContext)

            //For some reason android throws a bunch of random errors here
            try {
                setContentView(R.layout.activity_main)
            } catch (e: Exception) {

            }

            //Set the scroll to the top
            val v = findViewById<ScrollView>(R.id.settingsScroll)
            v.requestFocus()

        } else {
            loadLoginActivity()
            return
        }

        loadAd()
        //Load the credentials
        loadCredentials()
        detectTextChange()

        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworks = connectivityManager.allNetworks
        for (network in activeNetworks) {
            if (connectivityManager.getNetworkInfo(network).isConnected) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)) {
                    manuallyRun(this)
                }
                break
            }
        }
    }

    private fun loadCredentials() {

        val usernameText = getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("username", "DEFAULT")
        val passwordText = getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("password", "DEFAULT")
        val timesLoggedIn = getSharedPreferences("credentials", Context.MODE_PRIVATE).getInt("timesLoggedIn", 0)

        (findViewById<View>(R.id.usernameField) as EditText).setText(usernameText)
        (findViewById<View>(R.id.passwordField) as EditText).setText(passwordText)
        (findViewById<View>(R.id.timesLoggedIn) as TextView).text = timesLoggedIn.toString() + " times"
    }

    private fun loadAd() {
        MobileAds.initialize(this,
                "ca-app-pub-5689777634096933~5341567652")

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        mAdView!!.loadAd(adRequest)

    }

    private fun detectTextChange() {

        val username = findViewById<EditText>(R.id.usernameField)
        val password = findViewById<EditText>(R.id.passwordField)

        //Set the password area to a password area
        password.inputType = 129

        username.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (username.text.toString().equals("", ignoreCase = true)) {
                    //Invalid!
                    (findViewById<View>(R.id.usernameField) as EditText).error = "This field can not be blank!"
                    return
                }

                getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().putString("username", username.text.toString()).apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, two: Int, three: Int) { }
        })

        password.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (password.text.toString().equals("", ignoreCase = true)) {
                    //Invalid!
                    (findViewById<View>(R.id.passwordField) as EditText).error = "This field can not be blank!"
                    return
                }

                getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().putString("password", password.text.toString()).apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, two: Int, three: Int) { }
        })

    }

    //Button pressed
    fun DetectCaptivePortal(view: View) {
        manuallyRun(view.context)
    }


    private fun loadLoginActivity() {
        val myIntent = Intent(this, IntroActivity::class.java)
        startActivity(myIntent)

        finish()
    }

    private fun manuallyRun(context: Context) {
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
                main.sendNotification("Error!", "Couldn't access the login page! Try toggling airplane mode on then off again", context)
            }
            LoginResponse.UNKNOWN_ERROR -> {
                main.sendNotification("Error!", "An unknown error occurred! Please try again later", context)
            }
            LoginResponse.ALREADY_LOGGED_IN -> {
                main.sendNotification("Error!", "You're already logged into the WiFi! Try again when you're not", context)
            }
            else -> {
                main.sendNotification("Error!", "Something unknown happened to your login request! Try double checking your username and password", context)
            }
        }

        //Afterwards load ad (when we have internet)
        loadAd()
    }
}
