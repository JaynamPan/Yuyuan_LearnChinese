package com.chotix.yuyuan.views

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chotix.yuyuan.R
import com.google.android.material.tabs.TabLayout

class TabIconAdapter(
    private val context: Context,
    tabLayout: TabLayout?,
    private val iconRes: List<Int>
) : TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
    private val tabHome = tabLayout?.getTabAt(0)
    private val tabLearn = tabLayout?.getTabAt(1)
    private val tabUser = tabLayout?.getTabAt(2)
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        val tvHome: TextView? = tabHome?.customView?.findViewById(R.id.tv_tab_item)
        val ivHome: ImageView? = tabHome?.customView?.findViewById(R.id.iv_tab_item)
        val tvLearn: TextView? = tabLearn?.customView?.findViewById(R.id.tv_tab_item)
        val ivLearn: ImageView? = tabLearn?.customView?.findViewById(R.id.iv_tab_item)
        val tvUser: TextView? = tabUser?.customView?.findViewById(R.id.tv_tab_item)
        val ivUser: ImageView? = tabUser?.customView?.findViewById(R.id.iv_tab_item)
        if (position == 0) {
            tvHome?.setTextColor(Color.BLACK)
            ivHome?.setImageResource(iconRes[0])
        } else {
            tvHome?.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
            ivHome?.setImageResource(iconRes[1])
        }
        if (position == 1) {
            tvLearn?.setTextColor(Color.BLACK)
            ivLearn?.setImageResource(iconRes[2])
        } else {
            tvLearn?.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
            ivLearn?.setImageResource(iconRes[3])
        }
        if (position == 2) {
            tvUser?.setTextColor(Color.BLACK)
            ivUser?.setImageResource(iconRes[4])
        } else {
            tvUser?.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
            ivUser?.setImageResource(iconRes[5])
        }

    }

}