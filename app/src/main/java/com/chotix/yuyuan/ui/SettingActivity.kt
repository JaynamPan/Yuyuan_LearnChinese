package com.chotix.yuyuan.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.shared.StatusBarHelper

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private var rlSettingProfile: RelativeLayout? = null
    private var rlSettingAbout: RelativeLayout? = null
    private var rlSettingAudio: RelativeLayout? = null
    private var rlSettingReset: RelativeLayout? = null
    private var rlSettingFeedback: RelativeLayout? = null
    private var ivBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusBarHelper = StatusBarHelper()
        statusBarHelper.setStatusBarDark(this)
        setContentView(R.layout.activity_setting)
        //init views
        initView()
    }

    private fun initView() {
        rlSettingProfile = this.findViewById(R.id.rl_setting_profile)
        rlSettingProfile?.setOnClickListener(this)
        rlSettingAbout = this.findViewById(R.id.rl_setting_about)
        rlSettingAbout?.setOnClickListener(this)
        rlSettingAudio = this.findViewById(R.id.rl_setting_audio)
        rlSettingAudio?.setOnClickListener(this)
        rlSettingReset = this.findViewById(R.id.rl_setting_reset)
        rlSettingReset?.setOnClickListener(this)
        rlSettingFeedback = this.findViewById(R.id.rl_setting_feedback)
        rlSettingFeedback?.setOnClickListener(this)
        ivBack = this.findViewById(R.id.iv_setting_back)
        ivBack?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_setting_profile -> run {
                val goProfileIntent = Intent(this, ProfileActivity::class.java)
                startActivity(goProfileIntent)
            }

            R.id.rl_setting_about -> run {
                val goAboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(goAboutIntent)
            }

            R.id.rl_setting_audio -> run {
                Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show()
            }

            R.id.rl_setting_reset -> run {
                Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show()
            }

            R.id.rl_setting_feedback -> run {
                Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show()
            }

            R.id.iv_setting_back -> run {
                this.finish()
            }
        }
    }


}