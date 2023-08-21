package com.chotix.yuyuan.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.shared.StatusBarHelper
import com.chotix.yuyuan.ui.HomeActivity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarActivity : AppCompatActivity(), View.OnClickListener {

    private var ivBack: ImageView? = null
    private var ivNext: ImageView? = null
    private var ivPrev: ImageView? = null
    private var ivPlan: ImageView? = null
    private var tvMonth: TextView? = null
    private var tvToday: TextView? = null
    private var tvPlan: TextView? = null
    private var calendarView: CalendarView? = null
    private var currentDate: LocalDate? = null
    private var currentSelectedMonth: YearMonth? = null
    private var startMonth: YearMonth? = null
    private var currentMonth: YearMonth? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //status bar
        val statusBarHelper = StatusBarHelper()
        statusBarHelper.setStatusBarDark(this)

        setContentView(R.layout.activity_calendar)
        initView()
        currentDate = LocalDate.now()
        calendarView?.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer =
                DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.tvDay.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.tvDay.makeVisible()
                    container.ivDay.makeVisible()
                    //set bg
                    if (data.date.isBefore(currentDate)) {
                        container.ivDay.setImageResource(R.drawable.calendar_day_gray_bg)
                    } else if (data.date == currentDate) {
                        container.ivDay.setImageResource(R.drawable.calendar_day_gray_selected)
                    }

                } else {
                    container.tvDay.makeInVisible()
                    container.ivDay.makeInVisible()
                }
            }

        }
        currentMonth = YearMonth.now()
        startMonth = currentMonth?.minusMonths(10)
        val firstDayOfWeek = DayOfWeek.SUNDAY
        calendarView?.setup(startMonth!!, currentMonth!!, firstDayOfWeek)
        calendarView?.scrollToMonth(currentMonth!!)
        currentSelectedMonth = currentMonth
        calendarView?.setOnTouchListener { _, _ -> true }
        checkAndUpdateArrow()

    }

    private fun initView() {
        ivBack = this.findViewById(R.id.iv_calendar_back)
        ivBack?.setOnClickListener(this)
        ivNext = this.findViewById(R.id.iv_calendar_next)
        ivNext?.setOnClickListener(this)
        ivPrev = this.findViewById(R.id.iv_calendar_prev)
        ivPrev?.setOnClickListener(this)
        ivPlan = this.findViewById(R.id.iv_calendar_plan)
        tvMonth = this.findViewById(R.id.tv_calendar_month)
        tvToday = this.findViewById(R.id.tv_calendar_today)
        tvPlan = this.findViewById(R.id.tv_calendar_plan)
        calendarView = this.findViewById(R.id.calendar_view)

    }

    private fun goPrevOrNextMonth(isToPrev: Boolean) {
        if (isToPrev) {
            if (currentSelectedMonth?.previousMonth!! >= startMonth!! && currentSelectedMonth != startMonth) {
                calendarView?.scrollToMonth(currentSelectedMonth?.previousMonth!!)
                currentSelectedMonth = currentSelectedMonth?.previousMonth
                checkAndUpdateArrow()
            }
        } else {
            if (currentSelectedMonth?.nextMonth!! <= currentMonth && currentSelectedMonth != currentMonth) {
                calendarView?.scrollToMonth(currentSelectedMonth?.nextMonth!!)
                currentSelectedMonth = currentSelectedMonth?.nextMonth
                checkAndUpdateArrow()
            }
        }
    }

    private fun checkAndUpdateArrow() {
        //prev arrow
        if (currentSelectedMonth == startMonth) {
            ivPrev?.setImageResource(R.drawable.layerlist_left_calendar_alpha)
        } else if (currentSelectedMonth!! > startMonth && currentSelectedMonth!! <= currentMonth) {
            ivPrev?.setImageResource(R.drawable.layerlist_left_calendar_white)
        } else {
            Log.e("MyTest Calendar", "Wrong currentSelectedMonth!")
        }
        //next arrow
        if (currentSelectedMonth == currentMonth) {
            ivNext?.setImageResource(R.drawable.layerlist_right_calendar_alpha)
        } else if (currentSelectedMonth!! < currentMonth && currentSelectedMonth!! >= startMonth) {
            ivNext?.setImageResource(R.drawable.layerlist_right_calendar_white)
        } else {
            Log.e("MyTest Calendar", "Wrong currentSelectedMonth!")
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_calendar_back -> run {
                this.finish()
            }

            R.id.iv_calendar_next -> run {
                if (currentSelectedMonth!! < currentMonth) {
                    goPrevOrNextMonth(false)
                } else {
                    Log.e("MyTest Calendar", "no need to go next")
                }
            }

            R.id.iv_calendar_prev -> run {
                if (currentSelectedMonth!! > startMonth) {
                    goPrevOrNextMonth(true)
                } else {
                    Log.e("MyTest Calendar", "no need to go prev")
                }
            }
        }
    }

    private class DayViewContainer(view: View) : ViewContainer(view) {
        val tvDay: TextView = view.findViewById(R.id.tv_calendar_day)
        val ivDay: ImageView = view.findViewById(R.id.iv_calendar_day)
    }

}