package com.chotix.yuyuan.views

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NoSlideViewPager:ViewPager {
    private var swipeEnabled=false
    constructor(context: Context):super(context)
    constructor(context: Context,attrs:AttributeSet?):super(context,attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return swipeEnabled
    }
}