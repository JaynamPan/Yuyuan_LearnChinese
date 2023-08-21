package com.chotix.yuyuan.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chotix.yuyuan.R
import com.chotix.yuyuan.calendar.CalendarActivity

class HomeFragment : Fragment(), View.OnClickListener {
    private var ivCalendar: ImageView? = null
    private var ivBook: ImageView? = null
    private var tvChange: TextView? = null
    private var btnStart: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_home, container, false)
        ivCalendar = rootView.findViewById(R.id.iv_home_calendar)
        ivCalendar?.setOnClickListener(this)
        ivBook = rootView.findViewById(R.id.iv_home_book)
        ivBook?.setOnClickListener(this)
        tvChange = rootView.findViewById(R.id.tv_home_change)
        tvChange?.setOnClickListener(this)
        btnStart = rootView.findViewById(R.id.btn_home_start)
        btnStart?.setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_home_calendar -> run {
                val goCalendarIntent = Intent(this.context, CalendarActivity::class.java)
                startActivity(goCalendarIntent)
            }

            R.id.iv_home_book -> run {

            }

            R.id.tv_home_change -> run {

            }

            R.id.btn_home_start -> run {

            }
        }
    }
}