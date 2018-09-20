package ashcollauthlogin.darkzek.com.CaptivePortalSystem;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

class CaptivePortalLoginSystem extends AsyncTask<String, Void, String> {

    public CaptivePortalLoginSystem() {
        super();
    }

    protected String doInBackground(String... urls) {
        return run(urls[0], urls[1], urls[2]);
    }

    protected String run(String id, String username, String password) {
        try {
            //Setup the POST request variables
            Map<String,String> arguments = new HashMap<>();
            arguments.put("magic", id);
            arguments.put("4Tredir", "https://github.com/darkzek/AshcollAutoLogin");
            arguments.put("username", username);
            arguments.put("password", password);

            URL url = new URL("http://172.16.160.1:1000/");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            String m = "";
            for(Map.Entry<String,String> entry : arguments.entrySet())
                m += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
            byte[] out = m.getBytes();
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();

            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));

            String line = "";
            while ((line = br.readLine()) != null) {
                Log.i("INFO", line);
            }

        } catch (MalformedURLException e1) {
            return "An error occured. Please try again later.";
        } catch (IOException e) {
            return "An error occured. Please try again later.";
        }
        return "Success";
    }
}

