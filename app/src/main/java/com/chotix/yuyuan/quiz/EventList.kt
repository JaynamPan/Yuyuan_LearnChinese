package com.chotix.yuyuan.quiz

import android.content.SharedPreferences
import android.util.Log

class EventList {
    object MyEventList {
        private const val EVENT_LEARN = 111
        private const val EVENT_REVIEW = 222
        private const val EVENT_SKIP = 333
        private const val TODO_STRING = "TODO_STRING"
        private const val LAST_PLAN_REVIEW_COUNT = "LAST_PLAN_REVIEW_COUNT"
        private const val PLAN_COUNT = "PLAN_COUNT"
        private var todoList = mutableListOf<Int>()
        var lastPlanReviewCount = 0
        var planCount = 0

        fun getEvent(currentEvent: Int): Int {
            return todoList[currentEvent - 1]
        }

        fun loadData(sharedPreferences: SharedPreferences?) {
            val todoString = sharedPreferences?.getString(TODO_STRING, "") ?: ""
            if (todoString != "") {
                todoList = todoString.split(",").map { it.toInt() }.toMutableList()
            } else {
                Log.e("MyTest", "EventList todoString is blank")
            }
            val reviewCount = sharedPreferences?.getInt(LAST_PLAN_REVIEW_COUNT, -1) ?: -1
            if (reviewCount != -1) {
                lastPlanReviewCount = reviewCount
            } else {
                Log.e("MyTest", "EventList reviewCount is -1")
            }
            val planWordsCount = sharedPreferences?.getInt(PLAN_COUNT, -1) ?: -1
            if (planWordsCount != -1) {
                planCount = planWordsCount
            } else {
                Log.e("MyTest", "EventList planWordsCount is -1")
            }

        }

        fun updateSkip(currentEvent: Int, sharedPreferences: SharedPreferences?) {
            val planIndex = currentEvent - lastPlanReviewCount
            val beforeTailIndex = planCount.div(5) * 10 + planCount.rem(5)
            if (planIndex <= beforeTailIndex && planIndex.rem(10) in 1..5) {
                val tailStartIndex = planCount.div(5) * 10 + 1 + lastPlanReviewCount
                if (currentEvent < tailStartIndex) {
                    todoList[currentEvent - 1] = EVENT_SKIP
                    todoList[currentEvent + 4] = EVENT_SKIP
                } else {
                    todoList[currentEvent - 1] = EVENT_SKIP
                    todoList[currentEvent - 1 + planCount.rem(5)] = EVENT_SKIP
                }
                val todoString = todoList.joinToString(separator = ",")
                val editor = sharedPreferences?.edit()
                editor?.putString(TODO_STRING, todoString)
                editor?.commit()
            } else {
                Log.e("MyTest", "EventList updateSkip wrong currentEvent")
            }
        }

        fun getToReviewWordCount(currentEvent: Int): Int {
            if (currentEvent <= lastPlanReviewCount) {
                return lastPlanReviewCount + 1 - currentEvent
            } else {
                var learntCount = 0
                var reviewedCount = 0
                return if (lastPlanReviewCount + 1 == currentEvent) {
                    0
                } else {
                    for (i in lastPlanReviewCount + 1 until currentEvent) {
                        if (todoList[i - 1] == EVENT_LEARN) {
                            learntCount++
                        } else if (todoList[i - 1] == EVENT_REVIEW) {
                            reviewedCount++
                        }
                    }
                    learntCount - reviewedCount
                }

            }
        }

        fun init(lastPlanReviewCount: Int, planCount: Int, sharedPreferences: SharedPreferences?) {
            clearAll()
            this.lastPlanReviewCount = lastPlanReviewCount
            this.planCount = planCount
            val initialLength = lastPlanReviewCount + planCount * 2
            for (i in 1..initialLength) {
                val event = getEventOld(i)
                todoList.add(event)
            }
            val todoString = todoList.joinToString(separator = ",")
            val editor = sharedPreferences?.edit()
            editor?.putInt(LAST_PLAN_REVIEW_COUNT,lastPlanReviewCount)
            editor?.putInt(PLAN_COUNT,planCount)
            editor?.putString(TODO_STRING, todoString)
            editor?.commit()
            Log.e("MyTest","eventList review "+this.lastPlanReviewCount)
            Log.e("MyTest","eventList plan "+this.planCount)
            Log.e("MyTest", "eventList todo $todoString")


        }

        private fun clearAll() {
            this.planCount = 0
            this.lastPlanReviewCount = 0
            this.todoList.clear()
        }

        //currentEvent and lastPlanReviewCount start from 1
        private fun getEventOld(currentEvent: Int): Int {
            return if (currentEvent <= lastPlanReviewCount) {
                EVENT_REVIEW
            } else {
                val resultPlanIndex = currentEvent - lastPlanReviewCount
                val resultBeforeTailIndex = planCount.div(5) * 10 + planCount.rem(5)
                if (resultPlanIndex <= resultBeforeTailIndex) {
                    val resultRem = resultPlanIndex.rem(10)
                    if (resultRem in 1..5) {
                        EVENT_LEARN
                    } else {
                        EVENT_REVIEW
                    }
                } else {
                    EVENT_REVIEW
                }

            }
        }

        fun getCurrentWordOrder(
            currentEvent: Int

        ): Int {
            if (currentEvent <= lastPlanReviewCount) {
                return currentEvent
            } else {
                val planIndex = currentEvent - lastPlanReviewCount
                val beforeTailIndex = planCount.div(5) * 10 + planCount.rem(5)
                return if (planIndex <= beforeTailIndex) {
                    val resultRem = planIndex.rem(10)
                    if (resultRem in 1..5) {
                        planIndex - 5 * planIndex.div(10) + lastPlanReviewCount
                    } else {
                        val indexCal = planIndex - 5
                        indexCal - 5 * indexCal.div(10) + lastPlanReviewCount
                    }

                } else {
                    val tailIndexCal = planIndex - planCount.rem(5)
                    tailIndexCal - 5 * tailIndexCal.div(10) + lastPlanReviewCount
                }
            }
        }

        fun getToLearnWordCount(
            currentEvent: Int
        ): Int {
            if (currentEvent <= lastPlanReviewCount) {
                return planCount
            } else {
                val planIndex = currentEvent - lastPlanReviewCount
                val beforeTailIndex = planCount.div(5) * 10 + planCount.rem(5)
                return if (planIndex <= beforeTailIndex) {
                    val resultRem = planIndex.rem(10)
                    if (resultRem in 1..5) {
                        planCount - planIndex.div(10) * 5 - planIndex.rem(10) + 1
                    } else {
                        val calIndex = ((planIndex - 5).div(10) * 2 + 1) * 5
                        planCount - calIndex.div(10) * 5 - calIndex.rem(10)
                    }
                } else {
                    0
                }
            }
        }


    }


}