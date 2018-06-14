package ashcollauthlogin.darkzek.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ashcollauthlogin.darkzek.com.CaptivePortalSystem.CaptivePortalCheckerService;

public class SettingsActivity extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        //Check if its their first time
        if (getSharedPreferences("credentials", MODE_PRIVATE).contains("username")) {
            //Already setup!
            setContentView(R.layout.activity_settings);

            //Set the scroll to the top
            ScrollView v = findViewById(R.id.settingsScroll);
            v.requestFocus();

        } else {
            LoadLoginActivity();
            return;
        }

        //Load the credentials
        LoadCredentials();
        loadAd();
        DetectTextChange();
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

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
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
        DetectCaptivePortal();
    }

    public void DetectCaptivePortal() {
        Intent MyIntentService = new Intent(this, CaptivePortalCheckerService.class);

        startService(MyIntentService);
    }

    public void LoadLoginActivity() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);

        finish();
    }
}
