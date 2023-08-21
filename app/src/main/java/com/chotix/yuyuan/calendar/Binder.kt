package com.chotix.yuyuan.calendar

import android.view.View

open class ViewContainer(val view: View)

interface Binder<Data, Container : ViewContainer> {
    fun create(view: View): Container
    fun bind(container: Container, data: Data)
}

interface MonthHeaderFooterBinder<Container:ViewContainer>:Binder<CalendarMonth,Container>
interface MonthDayBinder<Container : ViewContainer> : Binder<CalendarDay, Container>
typealias MonthScrollListener = (CalendarMonth) -> Unit