package com.chotix.yuyuan.calendar

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import java.time.LocalDate
import java.time.YearMonth

data class ItemContent<Day>(
    val itemView: ViewGroup,
    val headerView: View?,
    val footerView: View?,
    val weekHolders: List<WeekHolder<Day>>
)

fun <Day, Container : ViewContainer> setupItemRoot(
    itemMargins: MarginValues,
    daySize: DaySize,
    context: Context,
    dayViewResource: Int,
    itemHeaderResource: Int,
    itemFooterResource: Int,
    weekSize: Int,
    itemViewClass: String?,
    dayBinder: Binder<Day, Container>
): ItemContent<Day> {
    val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }
    val itemHeaderView = if (itemHeaderResource != 0) {
        rootLayout.inflate(itemHeaderResource).also { headerView ->
            rootLayout.addView(headerView)
        }
    } else null

    @Suppress("UNCHECKED_CAST")
    val dayConfig = DayConfig(
        daySize = daySize,
        dayViewRes = dayViewResource,
        dayBinder = dayBinder as Binder<Day, ViewContainer>
    )
    val weekHolders = List(weekSize) {
        WeekHolder(dayConfig.daySize, List(7) { DayHolder(dayConfig) })
    }.onEach { weekHolder ->
        rootLayout.addView(weekHolder.inflateWeekView(rootLayout))
    }
    val itemFooterView = if (itemFooterResource != 0) {
        rootLayout.inflate(itemFooterResource).also { footerView ->
            rootLayout.addView(footerView)
        }
    } else null

    fun setupRoot(root: ViewGroup) {
        val width = if (daySize.parentDecidesWidth) MATCH_PARENT else WRAP_CONTENT
        val height = if (daySize.parentDecidesHeight) MATCH_PARENT else WRAP_CONTENT
        root.layoutParams = ViewGroup.MarginLayoutParams(width, height).apply {
            bottomMargin = itemMargins.bottom
            topMargin = itemMargins.top
            marginStart = itemMargins.start
            marginEnd = itemMargins.end

        }
    }

    val itemView = itemViewClass?.let {
        val customLayout = runCatching {
            Class.forName(it)
                .getDeclaredConstructor(Context::class.java)
                .newInstance(rootLayout.context) as ViewGroup
        }.onFailure {
            Log.e(
                "MyTest CalendarView ",
                "Failure loading custom class $itemViewClass, \" +\n" +
                        "                    \"check that $itemViewClass is a ViewGroup and the \" +\n" +
                        "                    \"single argument context constructor is available. \" +\n" +
                        "                    \"For an example on how to use a custom class, see: $EXAMPLE_CUSTOM_CLASS_URL",
                it
            )
        }.getOrNull()
        customLayout?.apply {
            setupRoot(this)
            addView(rootLayout)
        }
    }?:rootLayout.apply { setupRoot(this) }
    return ItemContent(
        itemView=itemView,
        headerView = itemHeaderView,
        footerView = itemFooterView,
        weekHolders=weekHolders
    )
}
fun checkDateRange(startMonth: YearMonth, endMonth: YearMonth) {
    check(endMonth >= startMonth) {
        "startMonth: $startMonth is greater than endMonth: $endMonth"
    }
}

fun checkDateRange(startDate: LocalDate, endDate: LocalDate) {
    check(endDate >= startDate) {
        "startDate: $startDate is greater than endDate: $endDate"
    }
}

fun dayTag(date: LocalDate): Int = date.hashCode()
private const val EXAMPLE_CUSTOM_CLASS_URL =
    "https://github.com/kizitonwose/Calendar/blob/3dfb2d2e91d5e443b540ff411113a05268e4b8d2/sample/src/main/java/com/kizitonwose/calendar/sample/view/Example6Fragment.kt#L29"