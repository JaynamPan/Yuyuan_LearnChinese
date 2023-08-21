package com.chotix.yuyuan.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.chotix.yuyuan.MainActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.db.DBHelper
import com.chotix.yuyuan.pinyin.PinyinTextView

class ShowQuestionActivity : AppCompatActivity(), View.OnClickListener {
    private var tvNew: TextView? = null
    private var tvReview: TextView? = null
    private var tvTopMeaning: TextView? = null
    private var tvSentence: PinyinTextView? = null
    private var tvTrans: TextView? = null
    private var tvLeftTop: TextView? = null
    private var tvRightTop: TextView? = null
    private var tvLeftBottom: TextView? = null
    private var tvRightBottom: TextView? = null
    private var tvSkip: TextView? = null
    private var tvTip: TextView? = null
    private var ivTopTick: ImageView? = null
    private var ivSound: ImageView? = null
    private var ivLeftTop: ImageView? = null
    private var ivLeftBottom: ImageView? = null
    private var ivRightTop: ImageView? = null
    private var ivRightBottom: ImageView? = null
    private var ivBack: ImageView? = null
    private var cardTrans: CardView? = null
    private var cardLeftTop: CardView? = null
    private var cardLeftBottom: CardView? = null
    private var cardRightTop: CardView? = null
    private var cardRightBottom: CardView? = null
    private var sharedPreferences: SharedPreferences? = null
    private val CURRENT_EVENT = "CURRENT_EVENT"
    private var player1: MediaPlayer? = null
    private var player2: MediaPlayer? = null
    private var charAudio: String? = ""
    private var sentenceAudio: String? = ""
    private var rightAnswerIndex = -1
    private val handler = Handler(Looper.getMainLooper())
    private var isKeyChar = false
    private var currentEvent: Int = -1
    private var planCount: Int = 0
    private var lastPlanReviewCount = 0
    private val EVENT_LEARN = 111
    private val EVENT_REVIEW = 222
    private val EVENT_SKIP = 333
    private val FINISH_CODE = -123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_question)
        initViews()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loadData()
        playAudio()
    }

    private fun initViews() {
        tvNew = this.findViewById(R.id.tv_show_question_new)
        tvReview = this.findViewById(R.id.tv_show_question_review)
        tvTopMeaning = this.findViewById(R.id.tv_show_question_top_meaning)
        tvSentence = this.findViewById(R.id.tv_show_question_sentence)
        tvTrans = this.findViewById(R.id.tv_show_question_trans)
        tvLeftTop = this.findViewById(R.id.tv_show_question_left_top)
        tvLeftTop?.setOnClickListener { cardLeftTop?.performClick() }
        tvRightTop = this.findViewById(R.id.tv_show_question_right_top)
        tvRightTop?.setOnClickListener { cardRightTop?.performClick() }
        tvRightBottom = this.findViewById(R.id.tv_show_question_right_bottom)
        tvRightBottom?.setOnClickListener { cardRightBottom?.performClick() }
        tvLeftBottom = this.findViewById(R.id.tv_show_question_left_bottom)
        tvLeftBottom?.setOnClickListener { cardLeftBottom?.performClick() }
        tvSkip = this.findViewById(R.id.tv_show_question_skip)
        tvSkip?.setOnClickListener(this)
        tvTip = this.findViewById(R.id.tv_show_question_tip)
        tvTip?.setOnClickListener(this)
        ivTopTick = this.findViewById(R.id.iv_show_question_top_tick)
        ivSound = this.findViewById(R.id.iv_show_question_sound)
        ivSound?.setOnClickListener(this)
        ivBack = this.findViewById(R.id.iv_show_question_back)
        ivBack?.setOnClickListener(this)
        cardTrans = this.findViewById(R.id.card_show_question_trans)
        cardTrans?.visibility = View.GONE
        ivLeftTop = this.findViewById(R.id.iv_show_question_left_top)
        ivLeftTop?.visibility = View.GONE
        ivLeftBottom = this.findViewById(R.id.iv_show_question_left_bottom)
        ivLeftBottom?.visibility = View.GONE
        ivRightTop = this.findViewById(R.id.iv_show_question_right_top)
        ivRightTop?.visibility = View.GONE
        ivRightBottom = this.findViewById(R.id.iv_show_question_right_bottom)
        ivRightBottom?.visibility = View.GONE
        cardLeftTop = this.findViewById(R.id.card_left_top)
        cardLeftTop?.setOnClickListener(this)
        cardLeftBottom = this.findViewById(R.id.card_left_bottom)
        cardLeftBottom?.setOnClickListener(this)
        cardRightTop = this.findViewById(R.id.card_right_top)
        cardRightTop?.setOnClickListener(this)
        cardRightBottom = this.findViewById(R.id.card_right_bottom)
        cardRightBottom?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_show_question_skip -> run {
                skipCurrentWord()
            }

            R.id.card_left_top -> run {
                player1?.stop()
                player2?.stop()
                player1?.release()
                player2?.release()
                player1 = null
                player2 = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "wrong answer index")
                } else {
                    if (rightAnswerIndex == 0) {
                        ivLeftTop?.setImageResource(R.drawable.right_tick)
                        ivLeftTop?.visibility = View.VISIBLE
                        hideAfterDelay(ivLeftTop)
                        playSoundEffect(true) { quizOrShowAnswer() }

                    } else {
                        ivLeftTop?.setImageResource(R.drawable.wrong_cross)
                        ivLeftTop?.visibility = View.VISIBLE
                        cardTrans?.visibility = View.VISIBLE
                        hideAfterDelay(ivLeftTop)
                        playSoundEffect(false) {}
                    }
                }

            }

            R.id.card_left_bottom -> run {
                player1?.stop()
                player2?.stop()
                player1?.release()
                player2?.release()
                player1 = null
                player2 = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "wrong answer index")
                } else {
                    if (rightAnswerIndex == 2) {
                        ivLeftBottom?.setImageResource(R.drawable.right_tick)
                        ivLeftBottom?.visibility = View.VISIBLE
                        hideAfterDelay(ivLeftBottom)
                        playSoundEffect(true) { quizOrShowAnswer() }
                    } else {
                        ivLeftBottom?.setImageResource(R.drawable.wrong_cross)
                        ivLeftBottom?.visibility = View.VISIBLE
                        cardTrans?.visibility = View.VISIBLE
                        hideAfterDelay(ivLeftBottom)
                        playSoundEffect(false) {}
                    }
                }
            }

            R.id.card_right_top -> run {
                player1?.stop()
                player2?.stop()
                player1?.release()
                player2?.release()
                player1 = null
                player2 = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "wrong answer index")
                } else {
                    if (rightAnswerIndex == 1) {
                        ivRightTop?.setImageResource(R.drawable.right_tick)
                        ivRightTop?.visibility = View.VISIBLE
                        hideAfterDelay(ivRightTop)
                        playSoundEffect(true) { quizOrShowAnswer() }
                    } else {
                        ivRightTop?.setImageResource(R.drawable.wrong_cross)
                        ivRightTop?.visibility = View.VISIBLE
                        cardTrans?.visibility = View.VISIBLE
                        hideAfterDelay(ivRightTop)
                        playSoundEffect(false) {}
                    }
                }
            }

            R.id.card_right_bottom -> run {
                player1?.stop()
                player2?.stop()
                player1?.release()
                player2?.release()
                player1 = null
                player2 = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "wrong answer index")
                } else {
                    if (rightAnswerIndex == 3) {
                        ivRightBottom?.setImageResource(R.drawable.right_tick)
                        ivRightBottom?.visibility = View.VISIBLE
                        hideAfterDelay(ivRightBottom)
                        playSoundEffect(true) { quizOrShowAnswer() }
                    } else {
                        ivRightBottom?.setImageResource(R.drawable.wrong_cross)
                        ivRightBottom?.visibility = View.VISIBLE
                        cardTrans?.visibility = View.VISIBLE
                        hideAfterDelay(ivRightBottom)
                        playSoundEffect(false) {}
                    }
                }
            }

            R.id.tv_show_question_tip -> run {
                player1?.stop()
                player2?.stop()
                player1?.release()
                player2?.release()
                player1 = null
                player2 = null
                cardTrans?.visibility = View.VISIBLE
                playCharAudio()
            }

            R.id.iv_show_question_sound -> run {
                player1?.stop()
                player2?.stop()
                player1?.release()
                player2?.release()
                player1 = null
                player2 = null
                playAudio()

            }

            R.id.iv_show_question_back -> run {
                val goMainIntent = Intent(this, MainActivity::class.java)
                goMainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                this.finish()
                startActivity(goMainIntent)

            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun loadData() {
        //init db
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase
        //get data from sharedPreference
        planCount = EventList.MyEventList.planCount
        currentEvent = sharedPreferences?.getInt(CURRENT_EVENT, -1) ?: -1
        lastPlanReviewCount = EventList.MyEventList.lastPlanReviewCount
        var flag = true
        if (planCount == 0) {
            flag = false
            Toast.makeText(this, "planCount -1 or null", Toast.LENGTH_SHORT).show()
            Log.e("MyTest", "planCount -1 or null")
        }
        if (currentEvent == -1) {
            flag = false
            Toast.makeText(this, "currentEvent -1 or null", Toast.LENGTH_SHORT).show()
            Log.e("MyTest", "currentEvent -1 or null")
        }

        if (flag) {
            val eventList = EventList.MyEventList
            val currentWord =
                eventList.getCurrentWordOrder(currentEvent)
            val newCount =
                eventList.getToLearnWordCount(currentEvent)
            val reviewCount =
                eventList.getToReviewWordCount(currentEvent)
            //set new and review words count
            tvNew?.text = "new     $newCount"
            tvReview?.text = "review $reviewCount"
            //get current word info
            val queryCurrent = "select * from hsk_all where learn_order=$currentWord"
            val cursorCurrent = db.rawQuery(queryCurrent, null)
            var meaning: String? = ""
            var char: String? = ""
            var sentence: String? = ""
            var trans: String? = ""
            var sentencePinyin: String? = ""
            val choices = mutableListOf<String>()
            var isKeyCharInt: Int? = 0
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
                isKeyCharInt =
                    cursorCurrent.getIntOrNull(cursorCurrent.getColumnIndex("is_key_char"))
            }
            cursorCurrent.close()
            if (isKeyCharInt == 1) {
                isKeyChar = true
            }
            val pinyinTV = PinyinTextView(this)
            if (sentence != null && sentencePinyin != null && char != null) {
                val hanzi = pinyinTV.splitHanziString(sentence)
                val pinyin = pinyinTV.getFormattedPinyin(sentencePinyin, hanzi)
                tvSentence?.setHanzi(hanzi)
                tvSentence?.setPinyin(pinyin)
                tvSentence?.setKeyWord(char)
                tvSentence?.setColor(Color.BLACK)
                tvSentence?.setFontSize(35)
                tvSentence?.setKeyWordColor(Color.BLUE)
            }
            tvTrans?.text = "   $trans"

            //get three random meanings
            if (meaning != null) {
                choices.add(meaning)
            }
            val queryMeaning =
                "select meaning from hsk_all where character!=\"$char\" order by random() limit 3;"
            val cursorMeaning = db.rawQuery(queryMeaning, null)
            while (cursorMeaning.moveToNext()) {
                val choice = cursorMeaning.getStringOrNull(cursorMeaning.getColumnIndex("meaning"))
                if (choice != null) {
                    choices.add(choice)
                } else {
                    Log.e("MyTest", "choice is null")
                }
            }
            cursorMeaning.close()
            //shuffle the choices
            if (choices.size == 4) {
                choices.shuffle()
                tvLeftTop?.text = choices[0]
                tvRightTop?.text = choices[1]
                tvLeftBottom?.text = choices[2]
                tvRightBottom?.text = choices[3]
            } else {
                Log.e("MyTest", "Wrong choices size!")
            }
            //get right answer index
            rightAnswerIndex = choices.indexOf(meaning)


        }


        //close db
        db.close()

    }

    private fun playAudio() {
        //prepare the audio
        player1 = MediaPlayer()
        player2 = MediaPlayer()
        try {
            val afd: AssetFileDescriptor = assets.openFd("word_audio/$charAudio")
            player1?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player1?.prepareAsync()
            player1?.setOnPreparedListener { mp -> mp.start() }
            player1?.setOnCompletionListener {
                val afd2: AssetFileDescriptor = assets.openFd("sentence_audio/$sentenceAudio")
                player2?.setDataSource(afd2.fileDescriptor, afd2.startOffset, afd2.length)
                player2?.prepareAsync()
                player2?.setOnPreparedListener { mp -> mp.start() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playCharAudio() {
        player1 = MediaPlayer()
        try {
            val afd: AssetFileDescriptor = assets.openFd("word_audio/$charAudio")
            player1?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player1?.prepareAsync()
            player1?.setOnPreparedListener { mp -> mp.start() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSoundEffect(isRight: Boolean, func: () -> Unit) {
        player1 = MediaPlayer()
        val afd: AssetFileDescriptor = if (isRight) {
            assets.openFd("correct_short.mp3")
        } else {
            assets.openFd("wrong.mp3")
        }
        player1?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player1?.prepareAsync()
        player1?.setOnPreparedListener { mp -> mp.start() }
        player1?.setOnCompletionListener { func() }
    }

    private fun hideAfterDelay(iv: ImageView?) {
        handler.postDelayed({
            iv?.visibility = View.GONE
        }, 500)
    }

    private fun quizOrShowAnswer() {
        when (isKeyChar) {
            true -> run {
                val goQuizIntent = Intent(this, QuizCharacterActivity::class.java)
                this.finish()
                startActivity(goQuizIntent)
            }

            false -> run {
                val goAnswerIntent = Intent(this, ShowAnswerActivity::class.java)
                this.finish()
                startActivity(goAnswerIntent)
            }
        }
    }

    private fun skipCurrentWord() {
        //update eventList
        val eventList = EventList.MyEventList
        eventList.updateSkip(currentEvent, sharedPreferences)
        //update currentEvent
        if (currentEvent == lastPlanReviewCount + planCount * 2) {
            //today's plan is finished
            val editor = sharedPreferences?.edit()
            currentEvent = FINISH_CODE
            editor?.putInt(CURRENT_EVENT, currentEvent)
            editor?.commit()
            Toast.makeText(this, "today's plan is finished", Toast.LENGTH_SHORT).show()
            val goMainIntent = Intent(this, MainActivity::class.java)
            goMainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            this.finish()
            startActivity(goMainIntent)
        } else {
            currentEvent++
            val editor = sharedPreferences?.edit()
            editor?.putInt(CURRENT_EVENT, currentEvent)
            editor?.commit()
        }
        //go to the next page
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
            goMainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            this.finish()
            startActivity(goMainIntent)
        }
        when (event) {
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

    }

    override fun onDestroy() {
        player1?.release()
        player2?.release()
        player1 = null
        player2 = null
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }


}
