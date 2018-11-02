package ashcollauthlogin.darkzek.com

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class IntroFragment : Fragment() {

    private var mBackgroundColor: Int = 0
    private var mPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!arguments!!.containsKey(BACKGROUND_COLOR))
            throw RuntimeException("Fragment must contain a \"$BACKGROUND_COLOR\" argument!")
        mBackgroundColor = arguments!!.getInt(BACKGROUND_COLOR)

        if (!arguments!!.containsKey(PAGE))
            throw RuntimeException("Fragment must contain a \"$PAGE\" argument!")
        mPage = arguments!!.getInt(PAGE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Select a layout based on the current page
        val layoutResId: Int
        when (mPage) {
            0 -> layoutResId = R.layout.intro_fragment_layout_1
            1 -> layoutResId = R.layout.intro_fragment_layout_2
            else -> layoutResId = R.layout.intro_fragment_layout_3
        }

        // Inflate the layout resource file
        val view = activity!!.layoutInflater.inflate(layoutResId, container, false)

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.tag = mPage

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the background color of the root view to the color specified in newInstance()
        val background = view.findViewById<View>(R.id.intro_background)
        background.setBackgroundColor(mBackgroundColor)
    }

    companion object {

        private val BACKGROUND_COLOR = "backgroundColor"
        private val PAGE = "page"

        fun newInstance(backgroundColor: Int, page: Int): IntroFragment {
            val frag = IntroFragment()
            val b = Bundle()
            b.putInt(BACKGROUND_COLOR, backgroundColor)
            b.putInt(PAGE, page)
            frag.arguments = b
            return frag
        }
    }

}