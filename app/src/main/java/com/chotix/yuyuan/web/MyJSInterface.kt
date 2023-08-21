package com.chotix.yuyuan.web

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Button
import android.widget.Toast
import com.chotix.yuyuan.MainActivity
import com.chotix.yuyuan.R

class MyJSInterface(
    private val activity: Activity,
    private val btn: Button?
) {
    @JavascriptInterface
    fun goMain() {
        Log.e("Mytest", "js interface invoked")
        Toast.makeText(this.activity, "JS interface invoked", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }

    @JavascriptInterface
    fun goContinue() {
        Log.e("Mytest", "js interface invoked goContinue")
        btn?.text = activity.getString(R.string.btn_continue)
    }

}