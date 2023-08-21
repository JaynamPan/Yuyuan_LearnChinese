package com.chotix.yuyuan.calendar

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.ViewPager.LayoutParams
import java.time.LocalDate


data class DayConfig<Day>(
    val daySize: DaySize,
    @LayoutRes val dayViewRes: Int,
    val dayBinder: Binder<Day, ViewContainer>,
)

class DayHolder<Day>(private val config: DayConfig<Day>) {
    private lateinit var dayView: View
    private lateinit var viewContainer: ViewContainer
    private var day: Day? = null

    fun inflateDayView(parent: LinearLayout): View {
        return parent.inflate(config.dayViewRes).apply {
            dayView = this
            layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
                weight = 1f //parent weight sum set to 7
                when (config.daySize) {
                    DaySize.Square -> {
                        width = MATCH_PARENT
                        height = MATCH_PARENT
                    }

                    DaySize.Rectangle -> {
                        width = MATCH_PARENT
                        height = MATCH_PARENT
                    }

                    DaySize.SeventhWidth -> {
                        width = MATCH_PARENT
                    }

                    DaySize.FreeForm -> {}
                }
            }
        }

    }

    fun bindDayView(currentDay: Day) {
        this.day = currentDay
        if (!::viewContainer.isInitialized) {
            viewContainer = config.dayBinder.create(dayView)
        }
        val dayTag = dayTag(findDate(currentDay))
        if (dayView.tag != dayTag) {
            dayView.tag = dayTag
        }
        config.dayBinder.bind(viewContainer, currentDay)
    }

    fun reloadViewIfNecessary(day: Day): Boolean {
        return if (day == this.day) {
            bindDayView(day)
            true
        } else {
            false
        }
    }


}
private fun findDate(day: Any?): LocalDate {
    return when (day) {
        is CalendarDay -> day.date
        else -> throw IllegalArgumentException("Invalid day type: $day")
    }
}
