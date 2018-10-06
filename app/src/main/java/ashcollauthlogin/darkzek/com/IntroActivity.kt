package ashcollauthlogin.darkzek.com

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast

class IntroActivity : AppCompatActivity() {

    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_intro)

        mViewPager = findViewById(R.id.viewpager)

        // Set an Adapter on the ViewPager
        mViewPager!!.adapter = IntroAdapter(supportFragmentManager)

        // Set a PageTransformer
        mViewPager!!.setPageTransformer(false, IntroPageTransformer())

        val tabLayout = findViewById<TabLayout>(R.id.tabDots)
        tabLayout.setupWithViewPager(findViewById(R.id.viewpager), true)

        findViewById<View>(R.id.skip).setOnClickListener { view -> mViewPager!!.currentItem = 3 }
        findViewById<View>(R.id.next).setOnClickListener { view -> NextButtonClicked(view) }


    }

    fun NextButtonClicked(v: View) {
        var page = mViewPager!!.currentItem

        if (page != 2) {
            //Increase page
            mViewPager!!.currentItem = page + 1
            return
        }

        onContinueClick(v)
    }

    fun onContinueClick(v: View) {
        val usernameText = (findViewById<View>(R.id.usernameField) as EditText).text.toString()
        val passwordText = (findViewById<View>(R.id.passwordField) as EditText).text.toString()

        //Check if a username was put in
        if (usernameText.equals("", ignoreCase = true)) {
            //Invalid!
            (findViewById<View>(R.id.usernameField) as EditText).error = "This field can not be blank!"
            return
        }

        if (passwordText.equals("", ignoreCase = true)) {
            //Invalid!
            (findViewById<View>(R.id.passwordField) as EditText).error = "This field can not be blank!"
            return
        }

        SaveCredentials(usernameText, passwordText)

        Log.i("Started", "Started")

        val user = getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("username", "DEFAULT")

        //Show toast
        Toast.makeText(this, "Logging in as " + user!!,
                Toast.LENGTH_LONG).show()

        LoadSettingsActivity()
    }

    fun SaveCredentials(username: String, password: String) {
        val editor = getSharedPreferences("credentials", Context.MODE_PRIVATE).edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putInt("timesLoggedIn", 0)
        editor.commit()
    }


    fun LoadSettingsActivity() {
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)

        finish()
    }
}
