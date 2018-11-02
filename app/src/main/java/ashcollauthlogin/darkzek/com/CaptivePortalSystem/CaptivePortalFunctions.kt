package ashcollauthlogin.darkzek.com.CaptivePortalSystem

import android.content.Context

object CaptivePortalFunctions {
    fun IncreaseLoginCount(context: Context) {
        //Get times logged in
        val timesLoggedIn = context.getSharedPreferences("credentials", 0).getInt("timesLoggedIn", 0)
        //Increase it by 1
        context.getSharedPreferences("credentials", 0).edit().putInt("timesLoggedIn", timesLoggedIn + 1).commit()
    }
}
