package ashcollauthlogin.darkzek.com.CaptivePortalSystem

import android.os.AsyncTask

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap

internal class CaptivePortalLoginSystem : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg urls: String): String {
        return run(urls[0], urls[1], urls[2])
    }

    protected fun run(id: String, username: String, password: String): String {
        try {
            //Setup the POST request variables
            val arguments = HashMap<String, String>()
            arguments["magic"] = id
            arguments["4Tredir"] = "https://github.com/darkzek/AshcollAutoLogin"
            arguments["username"] = username
            arguments["password"] = password

            val url = URL("http://172.16.160.1:1000/")
            val con = url.openConnection()
            val http = con as HttpURLConnection
            http.doOutput = true
            http.requestMethod = "POST"

            var m = ""
            for ((key, value) in arguments)
                m += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8") + "&"
            val out = m.toByteArray()
            val length = out.size

            http.setFixedLengthStreamingMode(length)
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            http.connect()

            http.outputStream.use { os -> os.write(out) }

            val br = BufferedReader(InputStreamReader(http.inputStream))

        } catch (e1: MalformedURLException) {
            return "An error occured. Please try again later."
        } catch (e: IOException) {
            return "An error occured. Please try again later."
        }

        return "Success"
    }
}

