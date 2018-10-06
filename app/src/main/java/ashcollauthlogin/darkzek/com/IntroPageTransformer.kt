package ashcollauthlogin.darkzek.com

import android.app.Activity
import android.support.v4.view.ViewPager
import android.view.View

class IntroPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {


        if (page.tag == null) {
            return
        }

        val pagePosition = page.tag as Int

        val pageWidth = page.width
        val pageWidthTimesPosition = pageWidth * position
        val absPosition = Math.abs(position)

        // Now it's time for the effects
        if (position <= -1.0f || position >= 1.0f) {

            // The page is not visible. This is a good place to stop
            // any potential work / animations you may have running.

        } else if (position == 0.0f) {

        } else {

            val title = page.findViewById<View>(R.id.title)
            title.alpha = 1.0f - absPosition

            val description = page.findViewById<View>(R.id.description)
            if (description != null) {
                description.translationY = -pageWidthTimesPosition / 2f
                description.alpha = 1.0f - absPosition
            }

            val computer = page.findViewById<View>(R.id.computer)

            if (pagePosition == 0 && computer != null) {
                computer.alpha = 1.0f - absPosition
                computer.translationX = -pageWidthTimesPosition * 1.5f
            }
        }
    }

}