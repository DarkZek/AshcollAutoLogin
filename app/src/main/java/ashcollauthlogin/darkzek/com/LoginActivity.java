package ashcollauthlogin.darkzek.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ashcollauthlogin.darkzek.com.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
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
