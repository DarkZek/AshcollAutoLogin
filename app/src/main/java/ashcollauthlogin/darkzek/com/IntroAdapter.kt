package ashcollauthlogin.darkzek.com

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class IntroAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return if (position % 2 == 0) {
            IntroFragment.newInstance(Color.parseColor("#03A9F4"), position) // blue
        } else {
            IntroFragment.newInstance(Color.parseColor("#4CAF50"), position) // green
        }
    }

    override fun getCount(): Int {
        return 3
    }

}