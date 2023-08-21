package com.chotix.yuyuan.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import com.chotix.yuyuan.MainActivity
import com.chotix.yuyuan.R
import com.chotix.yuyuan.db.DBHelper
import com.chotix.yuyuan.web.MyJSInterface

class QuizCharacterActivity : AppCompatActivity(), View.OnClickListener {
    private var charAudio: String? = ""
    private var webView: WebView? = null
    private var ivBack: ImageView? = null
    private var ivTopTick: ImageView? = null
    private var ivAudio: ImageView? = null
    private var tvNew: TextView? = null
    private var tvReview: TextView? = null
    private var tvTopMeaning: TextView? = null
    private var tvPinyin: TextView? = null
    private var tvChar: TextView? = null
    private var tvMeaning: TextView? = null
    private var tvSkip: TextView? = null
    private var btnPractise: Button? = null
    private var sharedPreferences: SharedPreferences? = null
    private val LAST_PLAN_REVIEW_COUNT = "LAST_PLAN_REVIEW_COUNT"
    private val PLAN_COUNT = "PLAN_COUNT"
    private val CURRENT_EVENT = "CURRENT_EVENT"
    private var word: String? = ""
    private var player: MediaPlayer? = null


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_character)
        initView()
        webView?.settings?.javaScriptEnabled = true
        webView?.addJavascriptInterface(MyJSInterface(this, btnPractise), "AndroidInterface")
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loadData()
        if (word != null) {
            showOrQuiz(word!!, true)
        } else {
            Log.e("MyTest", "word is null!")
        }

    }

    private fun initView() {
        webView = this.findViewById(R.id.webview_quiz)
        ivBack = this.findViewById(R.id.iv_quiz_char_back)
        ivBack?.setOnClickListener(this)
        ivTopTick = this.findViewById(R.id.iv_quiz_char_top_tick)
        ivAudio = this.findViewById(R.id.iv_quiz_char_audio)
        ivAudio?.setOnClickListener(this)
        tvNew = this.findViewById(R.id.tv_quiz_char_new)
        tvReview = this.findViewById(R.id.tv_quiz_char_review)
        tvTopMeaning = this.findViewById(R.id.tv_quiz_char_top_meaning)
        tvPinyin = this.findViewById(R.id.tv_quiz_character_pinyin)
        tvChar = this.findViewById(R.id.tv_quiz_character_char)
        tvMeaning = this.findViewById(R.id.tv_quiz_character_meaning)
        tvSkip = this.findViewById(R.id.tv_quiz_char_skip)
        tvSkip?.setOnClickListener(this)
        btnPractise = this.findViewById(R.id.btn_quiz_char_practice)
        btnPractise?.setOnClickListener(this)

    }

    private fun showOrQuiz(char: String, showMode: Boolean) {
        Log.e("MyTest", "showQuiz() invoked")
        val jsonFile = "char_json/$char.json"
        val jsonContent =
            applicationContext.assets.open(jsonFile).bufferedReader().use { it.readText() }
        webView?.loadUrl("file:///android_asset/animate.html")
        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val jsCode = "javascript: animate(\"$char\",$jsonContent,$showMode);"
                webView?.evaluateJavascript(jsCode, null)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
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
            val currentWord =
                eventList.getCurrentWordOrder(currentEvent!!)
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
            var pinyin: String? = ""
            while (cursorCurrent.moveToNext()) {
                char = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("character"))
                charAudio =
                    cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("char_audio_file"))
                meaning = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("meaning"))
                pinyin = cursorCurrent.getStringOrNull(cursorCurrent.getColumnIndex("pinyin"))
            }
            cursorCurrent.close()
            word = char
            tvMeaning?.text = meaning
            tvChar?.text = char
            tvPinyin?.text = pinyin
        }

        //close db
        db.close()
    }

    private fun playAudio() {
        player = MediaPlayer()
        try {
            val afd: AssetFileDescriptor = assets.openFd("word_audio/$charAudio")
            player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player?.prepareAsync()
            player?.setOnPreparedListener { mp -> mp.start() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_quiz_char_back -> run {
                val goMainIntent = Intent(this, MainActivity::class.java)
                goMainIntent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                this.finish()
                startActivity(goMainIntent)
            }

            R.id.iv_quiz_char_audio -> run {
                player?.stop()
                player?.release()
                player = null
                playAudio()

            }

            R.id.tv_quiz_char_skip -> run {
                val goAnswerIntent=Intent(this,ShowAnswerActivity::class.java)
                this.finish()
                startActivity(goAnswerIntent)
            }

            R.id.btn_quiz_char_practice -> run {
                if (btnPractise != null && btnPractise!!.text.equals("PRACTISE")) {
                    if (word != null) {
                        showOrQuiz(word!!, false)
                    } else {
                        Log.e("MyTest", "word is null!")
                    }

                } else if (btnPractise != null && btnPractise!!.text.equals("CONTINUE")) {
                    val goAnswerIntent=Intent(this,ShowAnswerActivity::class.java)
                    this.finish()
                    startActivity(goAnswerIntent)
                } else {
                    Log.e("MyTest", "btnPractise is null!")
                }

            }
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}