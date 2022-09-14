package alangsatinantongga.md14.kulitku.activity

import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.adapter.SliderAdapter
import alangsatinantongga.md14.kulitku.fragment.Intro1Fragment
import alangsatinantongga.md14.kulitku.fragment.Intro2Fragment
import alangsatinantongga.md14.kulitku.fragment.Intro3Fragment
import alangsatinantongga.md14.kulitku.fragment.Intro4Fragment
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class SliderActivity : AppCompatActivity() {
    private val fragmentList = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // making the status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_slider)

        val adapter = SliderAdapter(this)
        val vpIntroSlider : ViewPager2 = findViewById(R.id.vpIntroSlider)
        val indicatorLayout : IndicatorLayout = findViewById(R.id.indicatorLayout)

        vpIntroSlider.adapter = adapter
        fragmentList.addAll(listOf(
            Intro1Fragment(), Intro2Fragment(), Intro3Fragment(), Intro4Fragment()
        ))
        adapter.setFragmentList(fragmentList)
        indicatorLayout.setIndicatorCount(adapter.itemCount)
        indicatorLayout.selectCurrentPosition(0)
        registerListeners()

        supportActionBar?.hide()
    }
    private fun registerListeners() {
        val vpIntroSlider : ViewPager2 = findViewById(R.id.vpIntroSlider)
        val indicatorLayout : IndicatorLayout = findViewById(R.id.indicatorLayout)
        val tvSkip : TextView = findViewById(R.id.tvSkip)
        val tvNext : ImageView = findViewById(R.id.tvNext)

        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicatorLayout.selectCurrentPosition(position)
                if (position < fragmentList.lastIndex) {
                    tvSkip.visibility = View.VISIBLE
                } else {
                    tvSkip.visibility = View.GONE
                }
            }
        })
        tvSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        tvNext.setOnClickListener {
            val position = vpIntroSlider.currentItem
            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}