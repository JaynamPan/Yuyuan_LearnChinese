package com.chotix.yuyuan.calendar

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.YearMonth

class MonthCalendarAdapter(
    private val calView: CalendarView,
    private var outDateStyle: OutDateStyle,
    private var startMonth: YearMonth,
    private var endMonth: YearMonth,
    private var firstDayOfWeek: DayOfWeek
) : RecyclerView.Adapter<MonthViewHolder>() {
    private var itemCount = getMonthIndicesCount(startMonth, endMonth)
    private val dataStore = DataStore { offset ->
        getCalendarMonthData(startMonth, offset, firstDayOfWeek, outDateStyle).calendarMonth
    }
    private var visibleMonth: CalendarMonth? = null

    init {
        setHasStableIds(true)
    }

    private val isAttached: Boolean
        get() = calView.adapter === this

    private fun getItem(position: Int): CalendarMonth = dataStore[position]
    override fun getItemId(position: Int): Long = getItem(position).yearMonth.hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val content = setupItemRoot(
            itemMargins = calView.monthMargins,
            daySize = calView.daySize,
            context = calView.context,
            dayViewResource = calView.dayViewResource,
            itemHeaderResource = calView.monthHeaderResource,
            itemFooterResource = calView.monthFooterResource,
            weekSize = 6,
            itemViewClass = calView.monthViewClass,
            dayBinder = calView.dayBinder as MonthDayBinder
        )
        @Suppress("UNCHECKED_CAST")
        return MonthViewHolder(
            rootLayout = content.itemView,
            headerView = content.headerView,
            footerView = content.footerView,
            weekHolders = content.weekHolders,
            monthHeaderBinder = calView.monthHeaderBinder as MonthHeaderFooterBinder<ViewContainer>?,
            monthFooterBinder = calView.monthFooterBinder as MonthHeaderFooterBinder<ViewContainer>?
        )
    }

    override fun getItemCount(): Int = itemCount

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bindMonth(getItem(position))
    }

    override fun onBindViewHolder(
        holder: MonthViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                holder.reloadDay(it as CalendarDay)
            }
        }
    }

    fun reloadDay(vararg day: CalendarDay) {
        day.forEach { dayEach ->
            val position = getAdapterPosition(dayEach)
            if (position != NO_INDEX) {
                notifyItemChanged(position, dayEach)
            }
        }
    }

    fun getAdapterPosition(month: YearMonth): Int {
        return getMonthIndex(startMonth, month)
    }

    fun getAdapterPosition(day: CalendarDay): Int {
        return getAdapterPosition(day.positionYearMonth)
    }

    private val layoutManager: CalendarLayoutManager
        get() = calView.layoutManager as CalendarLayoutManager

    fun findFirstVisibleMonth(): CalendarMonth? {
        val index = findFirstVisibleMonthPosition()
        return if (index == NO_INDEX) null else dataStore[index]
    }

    fun findLastVisibleMonth(): CalendarMonth? {
        val index = findLastVisibleMonthPosition()
        return if (index == NO_INDEX) null else dataStore[index]
    }

    fun findFirstVisibleDay(): CalendarDay? = findVisibleDay(true)

    fun findLastVisibleDay(): CalendarDay? = findVisibleDay(false)

    private fun findFirstVisibleMonthPosition(): Int = layoutManager.findFirstVisibleItemPosition()

    private fun findLastVisibleMonthPosition(): Int = layoutManager.findLastVisibleItemPosition()

    private fun findVisibleDay(isFirst: Boolean): CalendarDay? {
        val visibleIndex =
            if (isFirst) findFirstVisibleMonthPosition() else findLastVisibleMonthPosition()
        if (visibleIndex == NO_INDEX) return null
        val visibleItemView = layoutManager.findViewByPosition(visibleIndex) ?: return null
        val monthRect = Rect()
        visibleItemView.getGlobalVisibleRect(monthRect)

        val dayRect = Rect()
        return dataStore[visibleIndex].weekDays.flatten()
            .run { if (isFirst) this else reversed() }
            .firstOrNull {
                val dayView = visibleItemView.findViewWithTag<View>(dayTag(it.date))
                    ?: return@firstOrNull false
                dayView.getGlobalVisibleRect(dayRect)
                dayRect.intersect(monthRect)
            }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(
        startMonth: YearMonth,
        endMonth: YearMonth,
        outDateStyle: OutDateStyle,
        firstDayOfWeek: DayOfWeek
    ){
        this.startMonth=startMonth
        this.endMonth=endMonth
        this.outDateStyle=outDateStyle
        this.firstDayOfWeek=firstDayOfWeek
        this.itemCount= getMonthIndicesCount(startMonth, endMonth)
        dataStore.clear()
        notifyDataSetChanged()
    }
    fun notifyMonthScrollListenerIfNeeded() {
        if (!isAttached) return

        if (calView.isAnimating) {
            calView.itemAnimator?.isRunning {
                notifyMonthScrollListenerIfNeeded()
            }
            return
        }
        val visibleItemPos = findFirstVisibleMonthPosition()
        if (visibleItemPos != RecyclerView.NO_POSITION) {
            val visibleMonth = dataStore[visibleItemPos]

            if (visibleMonth != this.visibleMonth) {
                this.visibleMonth = visibleMonth
                calView.monthScrollListener?.invoke(visibleMonth)
                if (calView.scrollPaged && calView.layoutParams.height == WRAP_CONTENT) {
                    val visibleVH =
                        calView.findViewHolderForAdapterPosition(visibleItemPos) ?: return

                    visibleVH.itemView.requestLayout()
                }
            }
        }
    }


}

val CalendarDay.positionYearMonth: YearMonth
    get() = when (position) {
        DayPosition.InDate -> date.yearMonth.nextMonth
        DayPosition.MonthDate -> date.yearMonth
        DayPosition.OutDate -> date.yearMonth.previousMonth
    }