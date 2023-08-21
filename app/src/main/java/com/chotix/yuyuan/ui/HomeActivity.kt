package com.chotix.yuyuan.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.chotix.yuyuan.MainActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.home.HomeFragment
import com.chotix.yuyuan.home.LearnFragment
import com.chotix.yuyuan.home.UserFragment
import com.chotix.yuyuan.shared.StatusBarHelper
import com.chotix.yuyuan.views.MyPagerAdapter
import com.chotix.yuyuan.views.NoSlideViewPager
import com.chotix.yuyuan.views.TabIconAdapter
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private var tabLayout: TabLayout? = null
    private var viewPager: NoSlideViewPager? = null
    private val homeFragment: HomeFragment = HomeFragment()
    private val learnFragment: LearnFragment = LearnFragment()
    private val userFragment: UserFragment = UserFragment()
    private val fragments: List<Fragment> = listOf(homeFragment, learnFragment, userFragment)
    private val myPagerAdapter: MyPagerAdapter = MyPagerAdapter(supportFragmentManager, fragments)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //status bar
        val statusBarHelper = StatusBarHelper()
        statusBarHelper.setStatusBarDark(this)

        setContentView(R.layout.activity_home)
        //init views
        tabLayout = this.findViewById(R.id.tab_layout_home)
        viewPager = this.findViewById(R.id.vp_home)
        //init fragments
        viewPager?.adapter = myPagerAdapter
        tabLayout?.setupWithViewPager(viewPager)
        //init tabs
        initTabs()

    }

    @SuppressLint("InflateParams")
    private fun initTabs() {
        tabLayout?.removeAllTabs()
        val tabTitles = arrayOf("Word", "Learn", "Me")
        val tabIcons = arrayOf(R.drawable.word_on, R.drawable.learn_off, R.drawable.user_off)
        for (i in tabTitles.indices) {
            val tab: TabLayout.Tab? = tabLayout?.newTab()
            val myTabView: View = LayoutInflater.from(this).inflate(R.layout.tab_item_layout, null)
            val ivTab: ImageView = myTabView.findViewById(R.id.iv_tab_item)
            val tvTab: TextView = myTabView.findViewById(R.id.tv_tab_item)
            tvTab.text = tabTitles[i]
            ivTab.setImageResource(tabIcons[i])
            tab?.customView = myTabView
            if (tab != null) {
                tabLayout?.addTab(tab)
            }
        }
        val iconRes = listOf(
            R.drawable.word_on,
            R.drawable.word_off,
            R.drawable.learn_on,
            R.drawable.learn_off,
            R.drawable.user_on,
            R.drawable.user_off
        )
        val tabIconAdapter = TabIconAdapter(this, tabLayout, iconRes)
        viewPager?.addOnPageChangeListener(tabIconAdapter)
    }


}