package com.chotix.yuyuan.calendar

import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.abs

class CalendarPageSnapHelper : PagerSnapHelper() {
    private var recyclerView: RecyclerView? = null
    private var flingTargetPosition: Int? = null
    private var scrollAction: ScrollAction? = null
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            scrollAction = when {
                dx > 0 || dy > 0 -> ScrollAction.Forward
                dx < 0 || dy < 0 -> ScrollAction.Backward
                else -> ScrollAction.Layout
            }
        }
    }

    override fun calculateDistanceToFinalSnap(lm: LayoutManager, targetView: View): IntArray {
        return IntArray(2).apply {
            this[0] = if (lm.canScrollHorizontally()) {
                distanceToStart(targetView, getHorizontalHelper(lm))
            } else 0

            this[1] = if (lm.canScrollVertically()) {
                distanceToStart(targetView, getVerticalHelper(lm))
            } else 0
        }
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView?.removeOnScrollListener(scrollListener)
        this.recyclerView = recyclerView
        this.recyclerView?.addOnScrollListener(scrollListener)
    }

    override fun findSnapView(lm: LayoutManager): View? {
        val flingTargetPosition = flingTargetPosition
        return if (flingTargetPosition != null) {
            this.flingTargetPosition = null
            lm.findViewByPosition(flingTargetPosition)
        } else {
            val scrollAction = scrollAction
            this.scrollAction = null
            val helper = getOrientationHelper(lm)
            val increment = getPositionIncrement(lm)
            val firstVisibleItemPosition = findFirstVisibleItemPositionInLayout(lm)
            val firstVisibleItem = lm.findViewByPosition(firstVisibleItemPosition) ?: return null
            val firstVisibleItemDistanceToStart = distanceToStart(firstVisibleItem, helper)
            if (firstVisibleItemDistanceToStart == 0) null else {
                when (scrollAction) {
                    ScrollAction.Forward -> {
                        if (
                            abs(firstVisibleItemDistanceToStart) >=
                            helper.getDecoratedMeasurement(firstVisibleItem) * VISIBILITY_THRESHOLD
                        ) {
                            val nextPos =
                                (firstVisibleItemPosition + increment).coerceIn(lm.indices)
                            lm.findViewByPosition(nextPos)
                        } else {
                            firstVisibleItem
                        }
                    }

                    ScrollAction.Backward -> {
                        val nextPos = (firstVisibleItemPosition + increment).coerceIn(lm.indices)
                        val nextItem = lm.findViewByPosition(nextPos) ?: return firstVisibleItem
                        if (
                            abs(distanceToStart(nextItem, helper)) <=
                            helper.getDecoratedMeasurement(nextItem) * VISIBILITY_THRESHOLD
                        ) {
                            nextItem
                        } else {
                            firstVisibleItem
                        }
                    }

                    ScrollAction.Layout -> firstVisibleItem
                    null -> firstVisibleItem
                }
            }
        }
    }

    override fun findTargetSnapPosition(lm: LayoutManager, velocityX: Int, velocityY: Int): Int {
        val targetPosition = getFlingTarget(lm as LinearLayoutManager, velocityX, velocityY)
        return targetPosition.also { flingTargetPosition = it }
    }

    private fun getFlingTarget(lm: LinearLayoutManager, velocityX: Int, velocityY: Int): Int {
        val position = if (isForwardFling(lm, velocityX, velocityY)) {
            findFirstVisibleItemPositionInLayout(lm) + getPositionIncrement(lm)
        } else {
            findFirstVisibleItemPositionInLayout(lm)
        }
        return position.coerceIn(lm.indices)
    }

    private fun isForwardFling(lm: LayoutManager, velocityX: Int, velocityY: Int): Boolean {
        return if (lm.canScrollHorizontally()) {
            velocityX > 0
        } else {
            velocityY > 0
        }
    }

    private fun findFirstVisibleItemPositionInLayout(lm: LinearLayoutManager): Int {
        return if (lm.reverseLayout) {
            lm.findLastVisibleItemPosition()
        } else {
            lm.findFirstVisibleItemPosition()
        }
    }

    private fun getPositionIncrement(lm: LinearLayoutManager): Int =
        if (lm.reverseLayout) -1 else 1

    private fun distanceToStart(targetView: View, helper: OrientationHelper): Int {
        val childStart = helper.getDecoratedStart(targetView)
        val containerStart = helper.startAfterPadding
        return childStart - containerStart
    }

    @OptIn(ExperimentalContracts::class)
    private fun getOrientationHelper(lm: LayoutManager): OrientationHelper {
        contract {
            returns() implies (lm is LinearLayoutManager)
        }
        return when ((lm as LinearLayoutManager).orientation) {
            RecyclerView.HORIZONTAL -> getHorizontalHelper(lm)
            RecyclerView.VERTICAL -> getVerticalHelper(lm)
            else -> throw IllegalStateException()
        }
    }

    private lateinit var verticalHelper: OrientationHelper
    private lateinit var horizontalHelper: OrientationHelper

    private fun getVerticalHelper(lm: LayoutManager): OrientationHelper {
        if (!::verticalHelper.isInitialized || verticalHelper.layoutManager != lm) {
            verticalHelper = OrientationHelper.createVerticalHelper(lm)
        }
        return verticalHelper
    }

    private fun getHorizontalHelper(lm: LayoutManager): OrientationHelper {
        if (!::horizontalHelper.isInitialized || horizontalHelper.layoutManager != lm) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(lm)
        }
        return horizontalHelper
    }

}
private enum class ScrollAction {
    Forward, Backward, Layout
}

private val LayoutManager.indices: IntRange get() = 0 until itemCount
private const val VISIBILITY_THRESHOLD = 0.1f