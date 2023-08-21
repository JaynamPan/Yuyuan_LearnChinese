package com.chotix.yuyuan.shared

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager

class StatusBarHelper {
   fun setStatusBarDark(activity: Activity) {

       val window = activity.window
       window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


    }
}