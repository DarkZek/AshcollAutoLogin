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
        AshcollAutoLogin.getInstance().sendNotification("0", "0", context);

        //Get the data from the website
        String data = "";

        try {
            data = new CaptivePortalLoginPageLocater().execute().get();
        } catch (Exception e){
            return LoginResponse.ALREADY_LOGGED_IN;
        }
        AshcollAutoLogin.getInstance().sendNotification("1", "1", context);

        //That means an error was thrown - or there is no captive portal
        if (data == null) {
            return LoginResponse.ALREADY_LOGGED_IN;
        }
        AshcollAutoLogin.getInstance().sendNotification("2", "2", context);

        //Find the ID using regex
        String id = "";

        Pattern pattern = Pattern.compile("(?<=name=\"magic\" value=\")(.*)(?>\"><input type=\"hidden\")");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            id = matcher.group(1);
        }
        AshcollAutoLogin.getInstance().sendNotification("3", "3", context);

        //Get response
        String response = "";

        //Send the login request
        try {
            response = new CaptivePortalLoginSystem().execute(id, username, password).get();
        } catch (Exception e) {
            Log.i("error sending login", "Could not send the login request to http://172.16.160.1:1000/");
            return LoginResponse.COULDNT_SEND_LOGIN_REQUEST;
        }

        AshcollAutoLogin.getInstance().sendNotification("4", "4", context);
        if (response.equals("Success")) {
            CaptivePortalFunctions.IncreaseLoginCount(context);
            return LoginResponse.SUCCESS;
        }

        AshcollAutoLogin.getInstance().sendNotification("5", "5", context);
        //An error occurred :(
        Log.i("An error has occurred", response);
        return LoginResponse.INCORRECT_USER_INFO;
    }
}
