package com.chotix.yuyuan.calendar

enum class DaySize {
    Square,
    Rectangle,
    SeventhWidth,
    FreeForm;

    val parentDecidesWidth: Boolean
        get() = this == Square || this == SeventhWidth || this == Rectangle
    val parentDecidesHeight: Boolean
        get() = this == Rectangle

}