package com.chotix.yuyuan.quiz

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.chotix.yuyuan.MainActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.db.DBHelper
import com.chotix.yuyuan.pinyin.PinyinTextView

class ShowAnswerActivity : AppCompatActivity(), View.OnClickListener {
    private var sentenceAudio: String? = ""
    private var charAudio: String? = ""
    private var tvSentence: PinyinTextView? = null
    private var tvChar: TextView? = null
    private var tvPinyin: TextView? = null
    private var tvMeaning: TextView? = null
    private var tvRadical: TextView? = null
    private var tvSkip: TextView? = null
    private var tvTrans: TextView? = null
    private var ivStar: ImageView? = null
    private var ivPinyinSound: ImageView? = null
    private var ivSentenceSound: ImageView? = null
    private var btnContinue: Button? = null
    private var llRadical: LinearLayout? = null
    private var sharedPreferences: SharedPreferences? = null
    private val LAST_PLAN_REVIEW_COUNT = "LAST_PLAN_REVIEW_COUNT"
    private val PLAN_COUNT = "PLAN_COUNT"
    private val CURRENT_EVENT = "CURRENT_EVENT"
    private var player: MediaPlayer? = null
    private var currentWord: Int? = -1
    private var isStarred: Boolean? = null
    private val EVENT_LEARN = 111
    private val EVENT_REVIEW = 222
    private val EVENT_SKIP = 333
    private val FINISH_CODE = -123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_answer)
        initViews()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        updateAndLoadData()


    }

    private fun initViews() {
        tvSentence = this.findViewById(R.id.tv_show_answer_sentence)
        tvChar = this.findViewById(R.id.tv_show_answer_char)
        tvPinyin = this.findViewById(R.id.tv_show_answer_pinyin)
        tvMeaning = this.findViewById(R.id.tv_show_answer_meaning)
        tvRadical = this.findViewById(R.id.tv_show_answer_radical)
        tvSkip = this.findViewById(R.id.tv_show_answer_skip)
        tvSkip?.setOnClickListener(this)
        tvTrans = this.findViewById(R.id.tv_show_answer_sentence_trans)
        ivStar = this.findViewById(R.id.iv_show_answer_star)
        ivStar?.setOnClickListener(this)
        ivPinyinSound = this.findViewById(R.id.iv_show_answer_pinyin_play)
        ivPinyinSound?.setOnClickListener(this)
        ivSentenceSound = this.findViewById(R.id.iv_show_answer_sentence_audio)
        ivSentenceSound?.setOnClickListener(this)
        btnContinue = this.findViewById(R.id.btn_show_answer_continue)
        btnContinue?.setOnClickListener(this)
        llRadical = this.findViewById(R.id.ll_show_answer_radical)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_show_answer_skip -> run {
                startLearnOrReviewOrFinish()
            }

            R.id.iv_show_answer_star -> run {
                if (isStarred != null) {
                    isStarred = if (isStarred!!) {
                        updateStar(false)
                        false
                    } else {
                        updateStar(true)
                        true
                    }
                }
            }

            R.id.iv_show_answer_pinyin_play -> run {
                player?.stop()
                player?.release()
                player = null
                playAudio(true)

            }

            R.id.iv_show_answer_sentence_audio -> run {
                player?.stop()
                player?.release()
                player = null
                playAudio(false)
            }

            R.id.btn_show_answer_continue -> run {
                startLearnOrReviewOrFinish()
            }

        }
    }

    private fun updateAndLoadData() {
        //init db
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase
        //get data from sharedPreference
        val planCount = sharedPreferences?.getInt(PLAN_COUNT, -1)
        val currentEvent = sharedPreferences?.getInt(CURRENT_EVENT, -1)
        val lastPlanReviewCount = sharedPreferences?.getInt(LAST_PLAN_REVIEW_COUNT, -1)
        var flag = true
        if (planCount == -1 || planCount == null) {
            flag = false
            Toast.makeText(this, "planCount -1 or null", Toast.LENGTH_SHORT).show()
            Log.e("MyTest", "planCount -1 or null")
        }
        if (currentEvent == -1 || currentEvent == null) {
            flag = false
            Toast.makeText(this, "currentEvent -1 or null", Toast.LENGTH_SHORT).show()
            Log.e("MyTest", "currentEvent -1 or null")
        }
        if (lastPlanReviewCount == -1 || lastPlanReviewCount == null) {
            flag = false
            Toast.makeText(this, "lastPlanReviewCount -1 or null", Toast.LENGTH_SHORT).show()
            Log.e("MyTest", "lastPlanReviewCount -1 or null")
        }
        if (flag) {
            val eventList = EventList.MyEventList
            currentWord =
                eventList.getCurrentWordOrder(currentEvent!!)
            //update sharedPreference
            if (lastPlanReviewCount != null) {
                if (currentEvent == lastPlanReviewCount + planCount!! * 2) {
                    //today's plan is finished
                    val editor = sharedPreferences?.edit()
                    editor?.putInt(CURRENT_EVENT, FINISH_CODE)
                    editor?.commit()
                } else {
                    val editor = sharedPreferences?.edit()
                    editor?.putInt(CURRENT_EVENT, currentEvent + 1)
                    editor?.commit()
                }
            }

            //update data in database
            val values = ContentValues().apply {
                put("state", 2)
            }
            val result = db.update("hsk_all", values, "learn_order=?", arrayOf("$currentWord"))
            if (result == 1) {
                Log.e("MyTest", "updated the current word")
            } else {
                Log.e("MyTest", "Error updating the current word")
            }
            //get current word data
            val queryCurrent = "select * from hsk_all where learn_order=$currentWord"
            val cursorCurrent = db.rawQuery(queryCurrent, null)
            var meaning: String? = ""
            var char: String? = ""
            var sentence: String? = ""
            var trans: String? = ""
            var sentencePinyin: String? = ""
            var pinyin: String? = ""
            var radical: String? = ""
            var isStarredInt: Int? = -1
            while (cursorCurrent.moveToNext()) {
                char = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("character"))
                sentence = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence"))
                trans =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence_translation"))
                charAudio =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("char_audio_file"))
                sentenceAudio =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence_audio_file"))
                sentencePinyin =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence_pinyin"))
                meaning = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("meaning"))
                pinyin = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("pinyin"))
                radical = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("radical"))
                isStarredInt = cursorCurrent.getIntOrNull(cursorCurrent.getColumnIndex("isStarred"))
            }
            cursorCurrent.close()
            //set data
            val pinyinTV = PinyinTextView(this)
            if (sentence != null && sentencePinyin != null && char != null) {
                val hanzi = pinyinTV.splitHanziString(sentence)
                val pinyinArray = pinyinTV.getFormattedPinyin(sentencePinyin, hanzi)
                tvSentence?.setHanzi(hanzi)
                tvSentence?.setPinyin(pinyinArray)
                tvSentence?.setKeyWord(char)
                tvSentence?.setColor(Color.BLACK)
                tvSentence?.setFontSize(35)
                tvSentence?.setKeyWordColor(Color.BLUE)
            }
            tvChar?.text = char
            tvPinyin?.text = pinyin
            tvMeaning?.text = meaning
            tvTrans?.text = trans
            if (radical == null) {
                llRadical?.visibility = View.GONE
            } else {
                tvRadical?.text = radical
            }


            when (isStarredInt) {
                1 -> run {
                    isStarred = true
                    ivStar?.setImageResource(R.drawable.ic_star_blue)
                }

                0 -> run {
                    isStarred = true
                    ivStar?.setImageResource(R.drawable.ic_star_grey)
                }

                else -> Log.e("MyTest", "wrong isStarredInt value")
            }
        }
        //close db
        db.close()
    }

    private fun updateStar(isStar: Boolean) {
        //init db
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase
        //update data in database
        val values = ContentValues().apply {
            put("isStarred", isStar)
        }
        val result = db.update("hsk_all", values, "learn_order=?", arrayOf("$currentWord"))
        if (result == 1) {
            if (isStar) {
                ivStar?.setImageResource(R.drawable.ic_star_blue)
            } else {
                ivStar?.setImageResource(R.drawable.ic_star_grey)
            }

            Log.e("MyTest", "updated the current word")
        } else {
            Log.e("MyTest", "Error updating the current word")
        }

        //close db
        db.close()
    }

    private fun playAudio(isChar: Boolean) {
        player = MediaPlayer()
        val afd: AssetFileDescriptor = if (isChar) {
            assets.openFd("word_audio/$charAudio")
        } else {
            assets.openFd("sentence_audio/$sentenceAudio")
        }
        player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player?.prepareAsync()
        player?.setOnPreparedListener { mp -> mp.start() }
    }

    private fun startLearnOrReviewOrFinish() {
        var currentEvent = sharedPreferences?.getInt(CURRENT_EVENT, -1) ?: -1
        val planCount = sharedPreferences?.getInt(PLAN_COUNT, -1) ?: -1
        val lastPlanReviewCount = sharedPreferences?.getInt(LAST_PLAN_REVIEW_COUNT, -1) ?: -1
        if (currentEvent == FINISH_CODE) {
            Toast.makeText(this, "today's plan is finished", Toast.LENGTH_SHORT).show()
            val goMainIntent = Intent(this, MainActivity::class.java)
            goMainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            this.finish()
            startActivity(goMainIntent)
        } else {
            if (currentEvent != -1 && planCount != -1 && lastPlanReviewCount != -1) {
                val eventList = EventList.MyEventList
                var event = eventList.getEvent(currentEvent)
                while (event == EVENT_SKIP && currentEvent < planCount * 2 + lastPlanReviewCount) {
                    currentEvent++
                    val editor = sharedPreferences?.edit()
                    editor?.putInt(CURRENT_EVENT, currentEvent)
                    editor?.commit()
                    event = eventList.getEvent(currentEvent)
                }
                event = eventList.getEvent(currentEvent)
                if (event == EVENT_SKIP && currentEvent == lastPlanReviewCount + planCount * 2) {
                    val editor = sharedPreferences?.edit()
                    editor?.putInt(CURRENT_EVENT, FINISH_CODE)
                    editor?.commit()
                    Toast.makeText(this, "today's plan is finished", Toast.LENGTH_SHORT).show()
                    val goMainIntent = Intent(this, MainActivity::class.java)
                    goMainIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    this.finish()
                    startActivity(goMainIntent)
                }
                when (eventList.getEvent(currentEvent)) {
                    EVENT_LEARN -> run {
                        val goShowQuestionIntent = Intent(this, ShowQuestionActivity::class.java)
                        this.finish()
                        startActivity(goShowQuestionIntent)
                    }

                    EVENT_REVIEW -> run {
                        val goReviewIntent = Intent(this, ReviewActivity::class.java)
                        this.finish()
                        startActivity(goReviewIntent)
                    }
                }
            } else {
                Log.e(
                    "MyTest",
                    "showAnswer startLearnOrReview get data from sharedPreference error"
                )
            }
        }

    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }


}