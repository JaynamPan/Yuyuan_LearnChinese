package com.chotix.yuyuan.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun YearMonth.atStartOfMonth(): LocalDate = this.atDay(1)
val YearMonth.previousMonth: YearMonth
    get() = this.minusMonths(1)
val YearMonth.nextMonth: YearMonth
    get() = this.plusMonths(1)
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)
fun firstDayOfWeekFromLocale(): DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
fun daysOfWeek(firstDayOfWeek: DayOfWeek= firstDayOfWeekFromLocale()):List<DayOfWeek>{
    val pivot=7-firstDayOfWeek.ordinal
    val daysOfWeek=DayOfWeek.values()
    return (daysOfWeek.takeLast(pivot)+daysOfWeek.dropLast(pivot))
}
fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun DayOfWeek.daysUntil(other: DayOfWeek) = (7 + (other.value - value)) % 7
const val NO_INDEX = -1