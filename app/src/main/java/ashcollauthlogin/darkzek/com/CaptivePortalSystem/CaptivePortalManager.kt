package ashcollauthlogin.darkzek.com.CaptivePortalSystem


import android.content.Context
import android.util.Log

import java.util.regex.Matcher
import java.util.regex.Pattern

import ashcollauthlogin.darkzek.com.AshcollAutoLogin
import ashcollauthlogin.darkzek.com.LoginResponse

class CaptivePortalManager private constructor() {

    /*
        The actual login code
     */

    fun Login(username: String, password: String, context: Context): LoginResponse {
        try {
            return tryLogin(username, password, context)
        } catch (e: Exception) {
        }

        return LoginResponse.UNKNOWN_ERROR
    }


    private fun tryLogin(username: String, password: String, context: Context): LoginResponse {
        //Get the data from the website
        val data: String?

        try {
            data = CaptivePortalLoginPageLocater().execute().get()
        } catch (e: Exception) {
            return LoginResponse.ALREADY_LOGGED_IN
        }

        //That means an error was thrown - or there is no captive portal
        if (data == null) {
            return LoginResponse.ALREADY_LOGGED_IN
        }

        //Find the ID using regex
        var id = ""

        val pattern = Pattern.compile("(?<=name=\"magic\" value=\")(.*)(?>\"><input type=\"hidden\")")
        val matcher = pattern.matcher(data)
        while (matcher.find()) {
            id = matcher.group(1)
        }

        //Get response
        val response: String

        //Send the login request
        try {
            response = CaptivePortalLoginSystem().execute(id, username, password).get()
        } catch (e: Exception) {
            Log.i("Error sending login", "Could not send the login request")
            return LoginResponse.COULDNT_SEND_LOGIN_REQUEST
        }

        if (response == "Success") {
            CaptivePortalFunctions.IncreaseLoginCount(context)
            return LoginResponse.SUCCESS
        }

        //An error occurred :(
        Log.i("An error has occurred", response)
        return LoginResponse.INCORRECT_USER_INFO
    }

    companion object {

        val _instance = CaptivePortalManager()

        fun getInstance(): CaptivePortalManager {
            return _instance
        }
    }
}
