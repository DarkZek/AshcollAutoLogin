package ashcollauthlogin.darkzek.com.CaptivePortalSystem

import android.os.AsyncTask
import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

//Returns the login page HTML code - or null if there is no middle man attack or any errors
internal class CaptivePortalLoginPageLocater : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg urls: Void): String? {
        return run("http://detectportal.firefox.com/success.txt")
    }

    protected fun run(url: String): String? {
        try {
            val site = URL(url)

            val con = site.openConnection()
            con.connect()

            val reader = BufferedReader(InputStreamReader(con.getInputStream()))

            var message = ""
            var line = reader.readLine()

            //Get the data
            while (line != null) {
                message += line + "\n"
                line = reader.readLine()
            }

            message = message.trim { it <= ' ' }

            return if (message.equals("success", ignoreCase = true)) {
                //Already logged in
                null
            } else {
                message.trim { it <= ' ' }
            }

        } catch (e1: MalformedURLException) {
            Log.i("MalformedURLException", e1.fillInStackTrace().toString() + "")
        } catch (e: IOException) {
            Log.i("IOException", e.fillInStackTrace().toString() + "")
        }

        return null
    }
}

