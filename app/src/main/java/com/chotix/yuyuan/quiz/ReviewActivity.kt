package com.chotix.yuyuan.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor
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
import androidx.core.database.getStringOrNull
import com.chotix.yuyuan.R
import com.chotix.yuyuan.db.DBHelper
import com.chotix.yuyuan.MainActivity

class ReviewActivity : AppCompatActivity(), View.OnClickListener {
    private var tvWord: TextView? = null
    private var tvNew: TextView? = null
    private var tvReview: TextView? = null
    private var tvTopMeaning: TextView? = null
    private var tvSentence: TextView? = null
    private var tvTrans: TextView? = null
    private var tvChoice1: TextView? = null
    private var tvChoice2: TextView? = null
    private var tvChoice3: TextView? = null
    private var tvChoice4: TextView? = null
    private var tvSkip: TextView? = null
    private var tvTip: TextView? = null
    private var ivBack: ImageView? = null
    private var ivTopTick: ImageView? = null
    private var ivSound: ImageView? = null
    private var ivChoice1: ImageView? = null
    private var ivChoice2: ImageView? = null
    private var ivChoice3: ImageView? = null
    private var ivChoice4: ImageView? = null
    private var cardTip: CardView? = null
    private var cardChoice1: CardView? = null
    private var cardChoice2: CardView? = null
    private var cardChoice3: CardView? = null
    private var cardChoice4: CardView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var player: MediaPlayer? = null
    private val LAST_PLAN_REVIEW_COUNT = "LAST_PLAN_REVIEW_COUNT"
    private val PLAN_COUNT = "PLAN_COUNT"
    private val CURRENT_EVENT = "CURRENT_EVENT"
    private var currentEvent: Int? = null
    private var planCount: Int? = null
    private var lastPlanReviewCount: Int? = null
    private var charAudio: String? = null
    private var sentenceAudio: String? = null
    private var rightAnswerIndex = -1
    private val handler = Handler(Looper.getMainLooper())
    private val EVENT_LEARN = 111
    private val EVENT_REVIEW = 222
    private val EVENT_SKIP = 333
    private val FINISH_CODE = -123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        initView()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loadData()
    }

    private fun initView() {
        tvWord = this.findViewById(R.id.tv_review_word)
        tvNew = this.findViewById(R.id.tv_review_new)
        tvReview = this.findViewById(R.id.tv_review_review)
        tvTopMeaning = this.findViewById(R.id.tv_review_top_meaning)
        tvSentence = this.findViewById(R.id.tv_review_sentence)
        tvTrans = this.findViewById(R.id.tv_review_trans)
        tvChoice1 = this.findViewById(R.id.tv_review_choice1)
        tvChoice1?.setOnClickListener { cardChoice1?.performClick() }
        tvChoice2 = this.findViewById(R.id.tv_review_choice2)
        tvChoice2?.setOnClickListener { cardChoice2?.performClick() }
        tvChoice3 = this.findViewById(R.id.tv_review_choice3)
        tvChoice3?.setOnClickListener { cardChoice3?.performClick() }
        tvChoice4 = this.findViewById(R.id.tv_review_choice4)
        tvChoice4?.setOnClickListener { cardChoice4?.performClick() }
        tvSkip = this.findViewById(R.id.tv_review_skip)
        tvSkip?.setOnClickListener(this)
        tvTip = this.findViewById(R.id.tv_review_tip)
        tvTip?.setOnClickListener(this)
        ivBack = this.findViewById(R.id.iv_review_back)
        ivBack?.setOnClickListener(this)
        ivTopTick = this.findViewById(R.id.iv_review_top_tick)
        ivSound = this.findViewById(R.id.iv_review_sound)
        ivSound?.setOnClickListener(this)
        ivChoice1 = this.findViewById(R.id.iv_review_choice1)
        ivChoice1?.visibility = View.GONE
        ivChoice2 = this.findViewById(R.id.iv_review_choice2)
        ivChoice2?.visibility = View.GONE
        ivChoice3 = this.findViewById(R.id.iv_review_choice3)
        ivChoice3?.visibility = View.GONE
        ivChoice4 = this.findViewById(R.id.iv_review_choice4)
        ivChoice4?.visibility = View.GONE
        cardTip = this.findViewById(R.id.card_review_tip)
        cardTip?.visibility = View.GONE
        cardChoice1 = this.findViewById(R.id.card_review1)
        cardChoice1?.setOnClickListener(this)
        cardChoice2 = this.findViewById(R.id.card_review2)
        cardChoice2?.setOnClickListener(this)
        cardChoice3 = this.findViewById(R.id.card_review3)
        cardChoice3?.setOnClickListener(this)
        cardChoice4 = this.findViewById(R.id.card_review4)
        cardChoice4?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_review_skip -> run {
                startNextEventOrFinish()
            }

            R.id.tv_review_tip -> run {
                player?.stop()
                player?.release()
                player = null
                cardTip?.visibility = View.VISIBLE
                playSentenceAudio()
            }

            R.id.iv_review_back -> run {
                val goMainIntent = Intent(this, MainActivity::class.java)
                goMainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                this.finish()
                startActivity(goMainIntent)
            }

            R.id.iv_review_sound -> run {
                player?.stop()
                player?.release()
                player = null
                playCharAudio()

            }

            R.id.card_review1 -> run {
                player?.stop()
                player?.release()
                player = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "review wrong answer index")
                } else {
                    if (rightAnswerIndex == 0) {
                        ivChoice1?.setImageResource(R.drawable.ic_tick_no_bg)
                        ivChoice1?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice1)
                        playSoundEffect(true) { startNextEventOrFinish() }
                    } else {
                        ivChoice1?.setImageResource(R.drawable.ic_cross_no_bg)
                        ivChoice1?.visibility = View.VISIBLE
                        cardTip?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice1)
                        playSoundEffect(false) {}
                    }
                }

            }

            R.id.card_review2 -> run {
                player?.stop()
                player?.release()
                player = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "review wrong answer index")
                } else {
                    if (rightAnswerIndex == 1) {
                        ivChoice2?.setImageResource(R.drawable.ic_tick_no_bg)
                        ivChoice2?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice2)
                        playSoundEffect(true) { startNextEventOrFinish() }
                    } else {
                        ivChoice2?.setImageResource(R.drawable.ic_cross_no_bg)
                        ivChoice2?.visibility = View.VISIBLE
                        cardTip?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice2)
                        playSoundEffect(false) {}
                    }
                }

            }

            R.id.card_review3 -> run {
                player?.stop()
                player?.release()
                player = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "review wrong answer index")
                } else {
                    if (rightAnswerIndex == 2) {
                        ivChoice3?.setImageResource(R.drawable.ic_tick_no_bg)
                        ivChoice3?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice3)
                        playSoundEffect(true) { startNextEventOrFinish() }
                    } else {
                        ivChoice3?.setImageResource(R.drawable.ic_cross_no_bg)
                        ivChoice3?.visibility = View.VISIBLE
                        cardTip?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice3)
                        playSoundEffect(false) {}
                    }
                }
            }

            R.id.card_review4 -> run {
                player?.stop()
                player?.release()
                player = null
                if (rightAnswerIndex == -1) {
                    Toast.makeText(this, "wrong answer index", Toast.LENGTH_SHORT).show()
                    Log.e("MyTest", "review wrong answer index")
                } else {
                    if (rightAnswerIndex == 3) {
                        ivChoice4?.setImageResource(R.drawable.ic_tick_no_bg)
                        ivChoice4?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice4)
                        playSoundEffect(true) { startNextEventOrFinish() }
                    } else {
                        ivChoice4?.setImageResource(R.drawable.ic_cross_no_bg)
                        ivChoice4?.visibility = View.VISIBLE
                        cardTip?.visibility = View.VISIBLE
                        hideAfterDelay(ivChoice4)
                        playSoundEffect(false) {}
                    }
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        //init db
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        //get data from sharedPreference
        planCount = sharedPreferences?.getInt(PLAN_COUNT, -1)
        currentEvent = sharedPreferences?.getInt(CURRENT_EVENT, -1)
        lastPlanReviewCount = sharedPreferences?.getInt(LAST_PLAN_REVIEW_COUNT, -1)
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
            val currentWord =
                eventList.getCurrentWordOrder(currentEvent!!)
            val newCount =
                eventList.getToLearnWordCount(currentEvent!!)
            val reviewCount =
                eventList.getToReviewWordCount(currentEvent!!)
            //set new and review words count
            tvNew?.text = "new     $newCount"
            tvReview?.text = "review $reviewCount"
            //get current word info
            val queryCurrent = "select * from hsk_all where learn_order=$currentWord"
            val cursorCurrent = db.rawQuery(queryCurrent, null)
            var char: String? = ""
            var sentence: String? = ""
            var trans: String? = ""
            var meaning: String? = ""
            val choices = mutableListOf<String>()
            while (cursorCurrent.moveToNext()) {
                char = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("character"))
                sentence = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence"))
                trans =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence_translation"))
                charAudio =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("char_audio_file"))
                sentenceAudio =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("sentence_audio_file"))
                meaning = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("meaning"))
            }
            cursorCurrent.close()
            //set data
            tvWord?.text = char
            tvSentence?.text = sentence
            tvTrans?.text = trans
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
                tvChoice1?.text = choices[0]
                tvChoice2?.text = choices[1]
                tvChoice3?.text = choices[2]
                tvChoice4?.text = choices[3]
            } else {
                Log.e("MyTest", "Wrong choices size!")
            }
            //get right answer index
            rightAnswerIndex = choices.indexOf(meaning)
        }
        //close db
        db.close()
    }

    private fun startNextEventOrFinish() {
        //update currentEvent
        if (currentEvent == lastPlanReviewCount!! + planCount!! * 2) {
            //today's plan is finished
            currentEvent = FINISH_CODE
            val editor = sharedPreferences?.edit()
            editor?.putInt(CURRENT_EVENT, currentEvent!!)
            editor?.commit()
        } else {
            currentEvent = currentEvent!! + 1
            val editor = sharedPreferences?.edit()
            editor?.putInt(CURRENT_EVENT, currentEvent!!)
            editor?.commit()
        }
        //go to next page
        if (currentEvent == FINISH_CODE) {
            Toast.makeText(this, "today's plan is finished", Toast.LENGTH_SHORT).show()
            val goMainIntent = Intent(this, MainActivity::class.java)
            goMainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            this.finish()
            startActivity(goMainIntent)
        } else {
            if (currentEvent != -1 && currentEvent != null && lastPlanReviewCount != -1 && lastPlanReviewCount != null && planCount != -1 && planCount != null){
                val eventList = EventList.MyEventList
                var event = eventList.getEvent(currentEvent!!)
                while (event == EVENT_SKIP && currentEvent!! < planCount!! * 2 + lastPlanReviewCount!!) {
                    currentEvent = currentEvent!! + 1
                    val editor = sharedPreferences?.edit()
                    editor?.putInt(CURRENT_EVENT, currentEvent!!)
                    editor?.commit()
                    event = eventList.getEvent(currentEvent!!)
                }
                event = eventList.getEvent(currentEvent!!)
                if (event == EVENT_SKIP && currentEvent == lastPlanReviewCount!! + planCount!! * 2) {
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
                when (eventList.getEvent(currentEvent!!)) {
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
            }else{
                Log.e("MyTest","review startNextEventOrFinish Error data")
            }
        }


    }

    private fun playCharAudio() {
        if (charAudio != null) {
            player = MediaPlayer()
            try {
                val afd: AssetFileDescriptor = assets.openFd("word_audio/$charAudio")
                player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                player?.prepareAsync()
                player?.setOnPreparedListener { mp -> mp.start() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.e("MyTest", "review charAudio is null")
        }
    }

    private fun playSentenceAudio() {
        if (sentenceAudio != null) {
            player = MediaPlayer()
            try {
                val afd: AssetFileDescriptor = assets.openFd("sentence_audio/$sentenceAudio")
                player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                player?.prepareAsync()
                player?.setOnPreparedListener { mp -> mp.start() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.e("MyTest", "review sentenceAudio is null")
        }
    }

    private fun playSoundEffect(isRight: Boolean, func: () -> Unit) {
        player = MediaPlayer()
        val afd: AssetFileDescriptor = if (isRight) {
            assets.openFd("correct_short.mp3")
        } else {
            assets.openFd("wrong.mp3")
        }
        player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player?.prepareAsync()
        player?.setOnPreparedListener { mp -> mp.start() }
        player?.setOnCompletionListener { func() }
    }

    private fun hideAfterDelay(iv: ImageView?) {
        handler.postDelayed({
            iv?.visibility = View.GONE
        }, 500)
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}