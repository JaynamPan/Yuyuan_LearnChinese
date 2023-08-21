package com.chotix.yuyuan.calendar

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.YearMonth

class CalendarLayoutManager(
    private val calView: CalendarView,
) :
    LinearLayoutManager(calView.context, calView.orientation, false) {
    private val adapter: MonthCalendarAdapter
        get() = calView.adapter as MonthCalendarAdapter

    fun getItemAdapterPosition(data: YearMonth): Int =
        adapter.getAdapterPosition(data)

    fun getDayAdapterPosition(data: CalendarDay): Int =
        adapter.getAdapterPosition(data)

    fun getDayTag(data: CalendarDay): Int {
        return dayTag(data.date)
    }

    fun getItemMargins(): MarginValues =
        calView.monthMargins

    fun notifyScrollListenerIfNeeded() = adapter.notifyMonthScrollListenerIfNeeded()
    fun scrollToIndex(indexData: YearMonth) {
        val position = getItemAdapterPosition(indexData)
        if (position == NO_INDEX) return
        scrollToPositionWithOffset(position, 0)
        calView.post { notifyScrollListenerIfNeeded() }
    }

    private fun calculateDayViewOffsetInParent(day: CalendarDay, itemView: View): Int {
        val dayView = itemView.findViewWithTag<View>(getDayTag(day)) ?: return 0
        val rect = Rect()
        dayView.getDrawingRect(rect)
        (itemView as ViewGroup).offsetDescendantRectToMyCoords(dayView, rect)
        val isVertical = orientation == VERTICAL
        val margins = getItemMargins()
        return if (isVertical) rect.top + margins.top else rect.left + margins.start
    }

}