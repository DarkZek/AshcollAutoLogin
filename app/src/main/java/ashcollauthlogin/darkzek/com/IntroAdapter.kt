package ashcollauthlogin.darkzek.com

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class IntroAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return IntroFragment.newInstance(Color.parseColor("#03A9F4"), position) // blue
            1 -> return IntroFragment.newInstance(Color.parseColor("#4CAF50"), position) // green
            2 -> return IntroFragment.newInstance(Color.parseColor("#df6142"), position) // orange
        }

        return IntroFragment.newInstance(Color.parseColor("#df6142"), position) // orange
    }

    override fun getCount(): Int {
        return 3
    }

}