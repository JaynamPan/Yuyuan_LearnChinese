package com.chotix.yuyuan.pinyin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.chotix.yuyuan.R

class PinyinTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var fontSize: Int
    private var pinyin: Array<String>? = null
    private var hanzi: Array<String>? = null
    private var color: Int
    private var keyWordColor = Color.rgb(0, 0, 255)
    private var keyWord: String? = null
    private var keyWordIndex = -1
    private val textPaint: TextPaint? = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var fontMetrics: Paint.FontMetrics? = null
    private val paddingTop = 0
    private val minHeight = 144
    private val indexList = ArrayList<Int>() // 存储每行首个String位置
    var line = 1
    var density = 0f
    fun initTextPaint() {
        textPaint!!.color = color
        density = resources.displayMetrics.density //返回dpi单位
        textPaint.strokeWidth = density * 2
        textPaint.textSize = fontSize.toFloat()
        fontMetrics = textPaint.fontMetrics

    }

    fun setPinyin(pinyin: Array<String>?) {
        this.pinyin = pinyin
    }

    fun setHanzi(hanzi: Array<String>?) {
        this.hanzi = hanzi
    }

    fun setColor(color: Int) {
        this.color = color
        if (textPaint != null) {
            textPaint.color = color
        }
    }

    fun setFontSize(size: Int) {
        fontSize = size
        if (textPaint != null) {
            textPaint.textSize = size.toFloat()
        }
    }

    fun setKeyWordColor(color: Int) {
        keyWordColor = color
    }

    fun setKeyWord(keyWord: String?) {
        this.keyWord = keyWord
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 需要根据文本测量高度
        var width = 0
        var height = 0
        indexList.clear()
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)

        //计算宽度
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = 700
        } else {
            width = 700
        }
        //计算高度
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            if (textPaint != null) {
                if (pinyin != null && pinyin!!.size != 0) {
                    height =
                        ((pinyin!!.size / 11 + 1) * 1.6 * (fontMetrics!!.bottom - fontMetrics!!.top) + paddingTop).toInt()
                    //设置height为行数*行高+paddingTop
                } else if (hanzi != null) {
                    height = (fontMetrics!!.bottom - fontMetrics!!.top + paddingTop).toInt()
                    //设置height为行高+paddingTop
                }
            }
        } else {
            //如果为 MeasureSpec.UNSPECIFIED
            if (textPaint != null) {
                if (pinyin != null && pinyin!!.size != 0) {
                    var pinyinWidth = 0f
                    var lineCount = 1
                    for (index in pinyin!!.indices) {
                        pinyinWidth = if (TextUtils.equals(pinyin!![index], "null")) {
                            pinyinWidth + textPaint.measureText(hanzi!![index])
                        } else {
                            pinyinWidth + textPaint.measureText(pinyin!![index])
                        }
                        if (pinyinWidth > width) {
                            indexList.add(index)
                            lineCount++
                            pinyinWidth = textPaint.measureText(pinyin!![index])
                        }
                    }
                    height =
                        Math.ceil((lineCount * 2 * (textPaint.fontSpacing + density * 1)).toDouble())
                            .toInt()
                    //向上取整 getFontSpacing() 返回以像素为单位的行间距
                } else if (hanzi != null) {
                    height = textPaint.fontSpacing.toInt()
                }
            }
        }
        height = Math.max(height, minHeight)
        setMeasuredDimension(width, height)
    }

    private var pinyinWidth = 0f
    private val isCenterHorizontal = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinyinTextView)
        color = typedArray.getColor(R.styleable.PinyinTextView_customColor, currentTextColor)
        fontSize =
            typedArray.getDimension(R.styleable.PinyinTextView_customTextSize, textSize).toInt()
        //回收资源
        typedArray.recycle()
        initTextPaint()
    }

    override fun onDraw(canvas: Canvas) {
        //keyword
        if (keyWord != null && hanzi != null) {
            keyWordIndex = getKeyWordIndex(keyWord, hanzi)
        }
        var widthMeasure = 0f
        if (indexList.isEmpty()) {
            // 单行数据处理
            if (isCenterHorizontal) {
                if (pinyin != null && pinyin!!.size > 0) {
                    widthMeasure =
                        (width - textPaint!!.measureText(combinePinyinEnd(0, pinyin!!.size))) / 2
                    //得到剩余宽度的一半
                } else if (hanzi != null && hanzi!!.size > 0) {
                    widthMeasure =
                        (width - textPaint!!.measureText(combineHanziEnd(0, hanzi!!.size))) / 2
                }
            }
        }
        pinyinWidth = 0f
        line = 1
        var trimIndex = 0
        if (pinyin != null && pinyin!!.size > 0) {
            for (index in pinyin!!.indices) {
                if (!TextUtils.equals(pinyin!![index], "null") && !TextUtils.equals(
                        pinyin!![index], " "
                    )
                ) {
                    pinyinWidth = widthMeasure + textPaint!!.measureText(pinyin!![index])
                    if (pinyinWidth > width) {
                        line++
                        widthMeasure = 0f
                        trimIndex = index
                    }
                    if (keyWordIndex != -1 && keyWord != null) {
                        if (keyWordIndex <= index && index <= keyWordIndex + keyWord!!.length - 1) {
                            textPaint.color = keyWordColor
                        } else {
                            textPaint.color = color
                        }
                    }
                    widthMeasure = if (index == trimIndex) {
                        val trimmed = pinyin!![index].replaceFirst("^\\s+".toRegex(), "")
                        canvas.drawText(
                            trimmed,
                            widthMeasure,
                            (line * 2 - 1) * textPaint.fontSpacing,
                            textPaint
                        )
                        canvas.drawText(
                            hanzi!![index],
                            widthMeasure + (textPaint.measureText(trimmed) - textPaint.measureText(
                                hanzi!![index]
                            )) / 2 - moveHalfIfNeed(trimmed, textPaint),
                            line * 2 * textPaint.fontSpacing,
                            textPaint
                        )
                        widthMeasure + textPaint.measureText(trimmed)
                    } else {
                        canvas.drawText(
                            pinyin!![index],
                            widthMeasure,
                            (line * 2 - 1) * textPaint.fontSpacing,
                            textPaint
                        )
                        canvas.drawText(
                            hanzi!![index], widthMeasure + (textPaint.measureText(
                                pinyin!![index]
                            ) - textPaint.measureText(hanzi!![index])) / 2 - moveHalfIfNeed(
                                pinyin!![index], textPaint
                            ), line * 2 * textPaint.fontSpacing, textPaint
                        )
                        widthMeasure + textPaint.measureText(pinyin!![index])
                    }
                } else if (TextUtils.equals(pinyin!![index], "null")) {
                    val hanziWidth = widthMeasure + textPaint!!.measureText(hanzi!![index])
                    if (hanziWidth > width) {
                        line++
                        widthMeasure = 0f
                    }
                    canvas.drawText(
                        hanzi!![index],
                        widthMeasure,
                        line * 2 * textPaint.fontSpacing,
                        textPaint
                    )
                    widthMeasure += textPaint.measureText(hanzi!![index])
                }
            }
        }
        super.onDraw(canvas)
    }

    private fun moveHalfIfNeed(pinyinUnit: String, paint: TextPaint?): Float {
        return if (pinyinUnit.trim { it <= ' ' }.length % 2 == 0) {
            paint!!.measureText(" ") / 2
        } else {
            0f
        }
    }

    private fun combinePinyinEnd(index: Int, length: Int): String {
        val sb = StringBuilder()
        for (subIndex in index until length) {
            val pendString = pinyin!![subIndex]
            sb.append(pendString)
        }
        return sb.toString()
    }

    private fun combineHanziEnd(index: Int, length: Int): String {
        val sb = StringBuilder()
        for (subIndex in index until length) {
            sb.append(hanzi!![subIndex])
        }
        return sb.toString()
    }


    fun splitHanziString(str: String): Array<String> {
        val arrayList = ArrayList<String>()
        var i = 0
        while (i < str.length) {
            val c = str[i]
            if (Character.isDigit(c) || Character.isLetter(c) && Character.UnicodeBlock.of(c) === Character.UnicodeBlock.BASIC_LATIN) {
                val sb = StringBuilder()
                sb.append(c)
                while (i + 1 < str.length) {
                    val nextChar = str[i + 1]
                    if (Character.isDigit(c) && Character.isDigit(nextChar) || Character.isLetter(
                            c
                        ) && Character.UnicodeBlock.of(c) === Character.UnicodeBlock.BASIC_LATIN && Character.isLetter(
                            nextChar
                        ) && Character.UnicodeBlock.of(nextChar) === Character.UnicodeBlock.BASIC_LATIN
                    ) {
                        sb.append(nextChar)
                        i++
                    } else {
                        break
                    }
                }
                arrayList.add(sb.toString())
            } else {
                arrayList.add(c.toString())
            }
            i++
        }
        return arrayList.toTypedArray()
    }

    fun splitPinyin(str: String): Array<String> {
        val s = str.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val arrayList = ArrayList<String>()
        for (s1 in s) {
            if (s1 != " " && s1 != "") {
                arrayList.add(s1)
            }
        }
        return arrayList.toTypedArray()
    }

    fun getFormattedPinyin(pinyin: String, hanzi: Array<String>): Array<String> {
        val splitPinyin = splitPinyin(pinyin)
        val newPinyin = ArrayList<String>()
        val formattedPinyin = ArrayList<String>()
        var i = 0
        for (item in hanzi) {
            if (isChineseCharacter(item)) {
                newPinyin.add(splitPinyin[i])
                i++
            } else {
                newPinyin.add("null")
            }
        }
        for (s in newPinyin) {
            if (s != "null") {
                formattedPinyin.add(formatUnit(s))
            } else {
                formattedPinyin.add("null")
            }
        }
        return formattedPinyin.toTypedArray()
    }

    private fun isChineseCharacter(str: String): Boolean {
        if (str.length == 1) {
            val c = str[0]
            return isChineseChar(c)
        }
        return false
    }

    private fun isChineseChar(c: Char): Boolean {
        val block = Character.UnicodeBlock.of(c)
        return block === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || block === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || block === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || block === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || block === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
    }

    private fun formatUnit(unit: String): String {
        var result = unit
        when (unit.length) {
            1 -> result = "  $result   "
            2 -> result = "  $result  "
            3 -> result = " $result  "
            4 -> result = " $result "
            5 -> result = "$result "
        }
        return result
    }

    private fun getKeyWordIndex(keyword: String?, hanzi: Array<String>?): Int {
        for (i in hanzi!!.indices) {
            var flag = 0
            for (j in 0 until keyword!!.length) {
                if (i + j < hanzi.size) {
                    if (hanzi[i + j] == keyword[j].toString()) {
                        flag++
                    }
                }
            }
            if (flag == keyword.length) {
                return i
            }
        }
        return -1
    }

}