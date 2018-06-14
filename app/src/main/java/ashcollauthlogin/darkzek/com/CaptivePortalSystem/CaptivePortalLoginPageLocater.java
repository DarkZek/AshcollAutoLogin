package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

//Returns the login page HTML code - or null if there is no middle man attack or any errors
class CaptivePortalLoginPageLocater extends AsyncTask<Void, Void, String> {

    public CaptivePortalLoginPageLocater() {
        super();
    }

    protected String doInBackground(Void... urls) {
        return run("http://detectportal.firefox.com/success.txt");
    }

    protected String run(String url) {
        try {
            URL site = new URL(url);

            URLConnection con = site.openConnection();
            con.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String message = "";
            String line = "";

            //Get the data
            while ((line = reader.readLine()) != null) {
                message += line + "\n";
            }

            message = message.trim();

            if (message.equalsIgnoreCase("success")) {
                //Already logged in
                return null;
            } else {
                return message.trim();
            }

        } catch (MalformedURLException e1) {
            Log.i("MalformedURLException", e1.fillInStackTrace() + "");
        } catch (IOException e) {
            Log.i("IOException", e.fillInStackTrace() + "");
        }
        return null;
    }
}

