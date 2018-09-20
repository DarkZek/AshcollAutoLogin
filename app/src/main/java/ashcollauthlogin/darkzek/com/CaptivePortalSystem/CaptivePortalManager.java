package ashcollauthlogin.darkzek.com.CaptivePortalSystem;


import android.content.Context;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ashcollauthlogin.darkzek.com.AshcollAutoLogin;
import ashcollauthlogin.darkzek.com.LoginResponse;

public class CaptivePortalManager {

    private static CaptivePortalManager instance = new CaptivePortalManager();

    private CaptivePortalManager() {}

    public static CaptivePortalManager getInstance() {
        return instance;
    }

    /*
        The actual login code
     */

    public LoginResponse Login(String username, String password, Context context) {
        try {
            return tryLogin(username, password, context);
        } catch (Exception e) {}
        return LoginResponse.UNKNOWN_ERROR;
    }


    private LoginResponse tryLogin(String username, String password, Context context) {
        //Get the data from the website
        String data;

        try {
            data = new CaptivePortalLoginPageLocater().execute().get();
        } catch (Exception e){
            return LoginResponse.ALREADY_LOGGED_IN;
        }

        //That means an error was thrown - or there is no captive portal
        if (data == null) {
            return LoginResponse.ALREADY_LOGGED_IN;
        }

        //Find the ID using regex
        String id = "";

        Pattern pattern = Pattern.compile("(?<=name=\"magic\" value=\")(.*)(?>\"><input type=\"hidden\")");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            id = matcher.group(1);
        }

        //Get response
        String response;

        //Send the login request
        try {
            response = new CaptivePortalLoginSystem().execute(id, username, password).get();
        } catch (Exception e) {
            Log.i("Error sending login", "Could not send the login request");
            return LoginResponse.COULDNT_SEND_LOGIN_REQUEST;
        }

        if (response.equals("Success")) {
            CaptivePortalFunctions.IncreaseLoginCount(context);
            return LoginResponse.SUCCESS;
        }

        //An error occurred :(
        Log.i("An error has occurred", response);
        return LoginResponse.INCORRECT_USER_INFO;
    }
}
