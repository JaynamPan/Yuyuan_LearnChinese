package com.chotix.yuyuan.init

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.shared.StatusBarHelper
import com.chotix.yuyuan.ui.HomeActivity

class InitActivity : AppCompatActivity() {
    private val initTime: Long = 2000
    private val initHandler: Handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusBarHelper=StatusBarHelper()
        statusBarHelper.setStatusBarDark(this)
        setContentView(R.layout.activity_init)
        load()
    }

    private fun load() {
        initHandler.postDelayed({
            val goHomeIntent = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(goHomeIntent)
        }, initTime)
    }
}