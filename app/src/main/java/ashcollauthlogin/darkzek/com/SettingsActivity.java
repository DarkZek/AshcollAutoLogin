package ashcollauthlogin.darkzek.com;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalWifiDetector;
import ashcollauthlogin.darkzek.com.R;
import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalManager;

public class SettingsActivity extends AppCompatActivity {

    private AdView mAdView;

    private AshcollAutoLogin main = AshcollAutoLogin.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AshcollAutoLogin.getInstance().createNotificationChannel(this);

        //Check if its their first time
        if (getSharedPreferences("credentials", MODE_PRIVATE).contains("username")) {
            //Already setup!

            //Setup wifi listener
            AshcollAutoLogin.SetupWifiListener(getApplicationContext());

            //For some reason android throws a bunch of random errors here
            try {
                setContentView(R.layout.activity_settings);
            } catch (Exception e) {

            }

            //Set the scroll to the top
            ScrollView v = findViewById(R.id.settingsScroll);
            v.requestFocus();

        } else {
            LoadLoginActivity();
            return;
        }

        loadAd();
        //Load the credentials
        LoadCredentials();
        DetectTextChange();

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] activeNetworks=connectivityManager.getAllNetworks();
        for(Network network:activeNetworks){
            if(connectivityManager.getNetworkInfo(network).isConnected()){
                NetworkCapabilities networkCapabilities=connectivityManager.getNetworkCapabilities(network);
                if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)){
                    ManuallyRun(this);
                }
                break;
            }
        }
    }

    public void LoadCredentials() {

        String usernameText = getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT");
        String passwordText = getSharedPreferences("credentials", MODE_PRIVATE).getString("password", "DEFAULT");
        int timesLoggedIn = getSharedPreferences("credentials", MODE_PRIVATE).getInt("timesLoggedIn", 0);

        ((EditText)findViewById(R.id.usernameField)).setText(usernameText);
        ((EditText)findViewById(R.id.passwordField)).setText(passwordText);
        ((TextView)findViewById(R.id.timesLoggedIn)).setText(timesLoggedIn + " times");
    }

    public void loadAd() {
        MobileAds.initialize(this,
                "ca-app-pub-5689777634096933~5341567652");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }

    public void DetectTextChange() {

        EditText username = findViewById(R.id.usernameField);
        EditText password = findViewById(R.id.passwordField);

        password.setInputType(129);

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (username.getText().toString().equalsIgnoreCase("")) {
                    //Invalid!
                    ((EditText)findViewById(R.id.usernameField)).setError("This field can not be blank!");
                    return;
                }

                getSharedPreferences("credentials", MODE_PRIVATE).edit().putString("username", username.getText().toString()).apply();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int two, int three) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (password.getText().toString().equalsIgnoreCase("")) {
                    //Invalid!
                    ((EditText)findViewById(R.id.passwordField)).setError("This field can not be blank!");
                    return;
                }

                getSharedPreferences("credentials", MODE_PRIVATE).edit().putString("password", password.getText().toString()).apply();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int two, int three) {

            }
        });

    }

    //Button pressed
    public void DetectCaptivePortal(View view) {
        ManuallyRun(view.getContext());
    }


    public void LoadLoginActivity() {
        Intent myIntent = new Intent(this, IntroActivity.class);
        startActivity(myIntent);

        finish();
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

        //Afterwards load ad (when we have internet)
        loadAd();
    }
}
