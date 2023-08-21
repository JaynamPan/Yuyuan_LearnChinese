package com.chotix.yuyuan.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.chotix.yuyuan.R
import com.chotix.yuyuan.ui.ProfileActivity
import com.chotix.yuyuan.ui.SettingActivity

class UserFragment : Fragment(),View.OnClickListener {
    private var iv_setting: ImageView? = null
    private var rlProfileBar:RelativeLayout?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView:View=inflater.inflate(R.layout.fragment_user, container, false)
        iv_setting=rootView.findViewById(R.id.iv_user_setting)
        iv_setting?.setOnClickListener(this)
        rlProfileBar=rootView.findViewById(R.id.rl_user_profile_bar)
        rlProfileBar?.setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_user_setting-> run {
                val goSettingIntent = Intent(this.context, SettingActivity::class.java)
                startActivity(goSettingIntent)
            }
            R.id.rl_user_profile_bar->run{
                val goProfileIntent=Intent(this.context,ProfileActivity::class.java)
                startActivity(goProfileIntent)
            }
        }
    }
}