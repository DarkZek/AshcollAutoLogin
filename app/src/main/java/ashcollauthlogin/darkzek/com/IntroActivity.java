package ashcollauthlogin.darkzek.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        mViewPager = findViewById(R.id.viewpager);

        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new IntroPageTransformer());

        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(findViewById(R.id.viewpager), true);

        findViewById(R.id.skip).setOnClickListener(view -> mViewPager.setCurrentItem(3));
        findViewById(R.id.next).setOnClickListener(view -> NextButtonClicked(view));


    }

    public void NextButtonClicked(View v) {
        int page = mViewPager.getCurrentItem();

        if (page != 2) {
            //Increase page
            mViewPager.setCurrentItem(page += 1);
            Log.i("SETTING", page + "");
            return;
        }

        onContinueClick(v);
    }

    public void onContinueClick(View v)
    {
        String usernameText = ((EditText)findViewById(R.id.usernameField)).getText().toString();
        String passwordText = ((EditText)findViewById(R.id.passwordField)).getText().toString();

        //Check if a username was put in
        if (usernameText.equalsIgnoreCase("")) {
            //Invalid!
            ((EditText)findViewById(R.id.usernameField)).setError("This field can not be blank!");
            return;
        }

        if (passwordText.equalsIgnoreCase("")) {
            //Invalid!
            ((EditText)findViewById(R.id.passwordField)).setError("This field can not be blank!");
            return;
        }

        SaveCredentials(usernameText, passwordText);

        Log.i("Started", "Started");

        String user = getSharedPreferences("credentials", MODE_PRIVATE).getString("username", "DEFAULT");

        //Show toast
        Toast.makeText(this, "Logging in as " + user,
                Toast.LENGTH_LONG).show();

        LoadSettingsActivity();
    }

    public void SaveCredentials(String username, String password) {
        SharedPreferences.Editor editor = getSharedPreferences("credentials", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putInt("timesLoggedIn", 0);
        editor.commit();
    }


    public void LoadSettingsActivity() {
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);

        finish();
    }
}
