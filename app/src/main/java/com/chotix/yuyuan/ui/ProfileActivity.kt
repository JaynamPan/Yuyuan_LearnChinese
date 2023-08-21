package com.chotix.yuyuan.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.shared.StatusBarHelper

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var ivBack:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusBarHelper=StatusBarHelper()
        statusBarHelper.setStatusBarDark(this)
        setContentView(R.layout.activity_profile)
        initView()
    }
    private fun initView(){
        ivBack=this.findViewById(R.id.iv_profile_back)
        ivBack?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_profile_back->run{
                this.finish()
            }
        }
    }
}