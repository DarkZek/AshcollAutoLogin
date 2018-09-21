package ashcollauthlogin.darkzek.com;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalManager;

public class AutomaticLogin extends AppCompatActivity {

    private AshcollAutoLogin main = AshcollAutoLogin.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] activeNetworks = connectivityManager.getAllNetworks();
        for (Network network : activeNetworks) {
            if (connectivityManager.getNetworkInfo(network).isConnected()) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)) {
                    ManuallyRun(this);
                }
                break;
            }
        }

        this.finish();
    }

    private void ManuallyRun(Context context) {
        String usernameText = getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT");
        String passwordText = getSharedPreferences("credentials", MODE_PRIVATE).getString("password", "DEFAULT");

        //Login
        LoginResponse response = CaptivePortalManager.getInstance().Login(usernameText, passwordText, this);

        switch (response) {
            case SUCCESS: {
                main.sendNotification("Success!", "Successfully logged you into the schools WiFi!", context);
                break;
            }
            case NO_INTERNET: {
                main.sendNotification("Error!", "You must be connected to the schools WiFi for this app to work!", context);
                break;
            }
            case LOGIN_PAGE_UNACCESSABLE: {
                main.sendNotification("Error!", "Couldnt access the login page! Try toggling airplane mode on then off again", context);
                break;
            }
            case UNKNOWN_ERROR: {
                main.sendNotification("Error!", "An unknown error occured! Please try again later", context);
                break;
            }
            case ALREADY_LOGGED_IN: {
                main.sendNotification("Error!", "You're already logged into the WiFi! Try again when you're not", context);
                break;
            }
            default: {
                main.sendNotification("Error!", "Something unknown happened to your login request! Try double checking your username and password", context);
            }
        }
    }

}
