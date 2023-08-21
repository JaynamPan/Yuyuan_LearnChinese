package com.chotix.yuyuan.calendar

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.RecyclerView
import com.chotix.yuyuan.R
import java.time.DayOfWeek
import java.time.YearMonth

class CalendarView : RecyclerView {
    private var startMonth: YearMonth? = null
    private var endMonth: YearMonth? = null
    private var firstDayOfWeek: DayOfWeek? = null
    private val calendarAdapter: MonthCalendarAdapter
        get() = adapter as MonthCalendarAdapter
    private val calendarLayoutManager: CalendarLayoutManager
        get() = layoutManager as CalendarLayoutManager
    private val pagerSnapHelper = CalendarPageSnapHelper()
    var monthScrollListener: MonthScrollListener? = null
    var scrollPaged = false
        set(value) {
            if (field != value) {
                field = value
                pagerSnapHelper.attachToRecyclerView(if (scrollPaged) this else null)
            }
        }

    var outDateStyle = OutDateStyle.EndOfRow
        set(value) {
            if (field != value) {
                field = value
                if (adapter != null) updateAdapter()
            }
        }
    var dayViewResource = 0
        set(value) {
            if (field != value) {
                check(value != 0) { "Invalid 'dayViewResource' value." }
                field = value
                invalidateViewHolders()
            }
        }
    var monthMargins=MarginValues()
        set(value){
            if (field!=value){
                field=value
                invalidateViewHolders()
            }
        }
    var daySize:DaySize=DaySize.Square
        set(value) {
            if (field!=value){
                field=value
                invalidateViewHolders()
            }
        }
    var monthHeaderResource=0
        set(value) {
            if (field != value) {
                field = value
                invalidateViewHolders()
            }
        }
    var monthFooterBinder: MonthHeaderFooterBinder<*>? = null
        set(value) {
            field = value
            invalidateViewHolders()
        }
    var monthFooterResource = 0
        set(value) {
            if (field != value) {
                field = value
                invalidateViewHolders()
            }
        }
    var monthViewClass: String? = null
        set(value) {
            if (field != value) {
                field = value
                invalidateViewHolders()
            }
        }
    var dayBinder: MonthDayBinder<*>? = null
        set(value) {
            field = value
            invalidateViewHolders()
        }
    var monthHeaderBinder: MonthHeaderFooterBinder<*>? = null
        set(value) {
            field = value
            invalidateViewHolders()
        }
    @Orientation
    var orientation: Int = HORIZONTAL
        set(value) {
            if (field != value) {
                field = value
                (layoutManager as? CalendarLayoutManager)?.orientation = value
            }
        }

    private val scrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE) {
                calendarAdapter.notifyMonthScrollListenerIfNeeded()
            }
        }
    }
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr, defStyleAttr)
    }

    private fun init(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        if (isInEditMode) return
        itemAnimator=null
        setHasFixedSize(true)
        context.withStyledAttributes(
            attrs,
            R.styleable.CalendarView,
            defStyleAttr,
            defStyleRes
        ){
            dayViewResource = getResourceId(
                R.styleable.CalendarView_cv_dayViewResource,
                dayViewResource,
            )
        }
        check(dayViewResource != 0) { "No value set for `cv_dayViewResource` attribute." }
    }
    private fun invalidateViewHolders() {
        if (adapter == null || layoutManager == null) return
        val state = layoutManager?.onSaveInstanceState()
        adapter = adapter
        layoutManager?.onRestoreInstanceState(state)
        post { calendarAdapter.notifyMonthScrollListenerIfNeeded() }
    }
    fun setup(startMonth: YearMonth, endMonth: YearMonth, firstDayOfWeek: DayOfWeek) {
        checkDateRange(startMonth = startMonth, endMonth = endMonth)
        this.startMonth = startMonth
        this.endMonth = endMonth
        this.firstDayOfWeek = firstDayOfWeek

        removeOnScrollListener(scrollListener)
        addOnScrollListener(scrollListener)

        layoutManager = CalendarLayoutManager(this)
        adapter = MonthCalendarAdapter(
            calView = this,
            outDateStyle = outDateStyle,
            startMonth = startMonth,
            endMonth = endMonth,
            firstDayOfWeek = firstDayOfWeek,
        )
    }
    fun updateMonthData(
        startMonth: YearMonth = requireStartMonth(),
        endMonth: YearMonth = requireEndMonth(),
        firstDayOfWeek: DayOfWeek = requireFirstDayOfWeek(),
    ) {
        checkDateRange(startMonth = startMonth, endMonth = endMonth)
        this.startMonth = startMonth
        this.endMonth = endMonth
        this.firstDayOfWeek = firstDayOfWeek
        updateAdapter()
    }

    private fun updateAdapter() {
        calendarAdapter.updateData(
            startMonth = requireStartMonth(),
            endMonth = requireEndMonth(),
            outDateStyle = outDateStyle,
            firstDayOfWeek = requireFirstDayOfWeek(),
        )
    }
    fun scrollToMonth(month: YearMonth) {
        calendarLayoutManager.scrollToIndex(month)
    }
    private fun requireStartMonth(): YearMonth = startMonth ?: throw getFieldException("startMonth")

    private fun requireEndMonth(): YearMonth = endMonth ?: throw getFieldException("endMonth")

    private fun requireFirstDayOfWeek(): DayOfWeek =
        firstDayOfWeek ?: throw getFieldException("firstDayOfWeek")
    private fun getFieldException(field: String) =
        IllegalStateException("`$field` is not set. Have you called `setup()`?")
}