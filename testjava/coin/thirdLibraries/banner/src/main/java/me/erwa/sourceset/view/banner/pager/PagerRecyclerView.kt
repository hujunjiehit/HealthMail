package me.erwa.sourceset.view.banner.pager

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import me.erwa.sourceset.view.banner.IPagerViewInstance
import me.erwa.sourceset.view.banner.OnPageChangeListener
import java.util.*
import kotlin.concurrent.timer
import kotlin.properties.Delegates

/**
 * @author: drawf
 * @date: 2019-08-01
 * @see: <a href=""></a>
 * @description: 用RecyclerView实现ViewPager功能，只实现横向功能
 */
class PagerRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener, IPagerViewInstance {

    override fun startAutoScroll(intervalInMillis: Int) {
        this.mFlagStartTimer = true
        this.mSmoothSpeed = intervalInMillis

        if (this.mSmoothMode) {
            if (this.mWidth > 0) {
                this.mPeriodTime = (mSmoothSpeed / (mWidth / DEFAULT_PERIOD_SCROLL_PIXEL)).toLong()
            }
        } else {
            this.mPeriodTime = intervalInMillis.toLong()
        }

        startTimer()
    }

    override fun stopAutoScroll() {
        this.mFlagStartTimer = false
        stopTimer()
    }

    override fun getCurrentPosition(): Int {
        if (mOldPosition < 0) {
            return 0
        }
        return mOldPosition
    }

    override fun getRealCurrentPosition(realCount: Int): Int {
        if (mOldPosition < 0 || realCount <= 0) {
            return 0
        }
        return mOldPosition % realCount
    }

    override fun setSmoothMode(enabled: Boolean) {
        this.mSmoothMode = enabled
    }

    override fun setPageHoldInMillis(pageHoldInMillis: Int) {
        this.mDelayedTime = pageHoldInMillis.toLong()
    }

    override fun setOnPageChangeListener(listener: OnPageChangeListener) {
        this.mPageChangeListener = listener
    }

    override fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    /**
     * smooth模式是否开启，否则为interval模式
     */
    private var mSmoothMode: Boolean = false

    /**
     * 计时器
     */
    private var mTimer: Timer? = null

    /**
     * 当前滚动状态
     */
    private var mScrollState: Int = SCROLL_STATE_IDLE

    /**
     * 滑动到具体位置帮助器
     */
    private var mSnapHelper: androidx.recyclerview.widget.PagerSnapHelper = androidx.recyclerview.widget.PagerSnapHelper()

    /**
     * 页面切换回调
     */
    private var mPageChangeListener: OnPageChangeListener? = null

    /**
     * 防止同一位置多次触发
     */
    private var mOldPosition = -1

    /**
     * 定时器间隔
     */
    private var mPeriodTime = DEFAULT_PERIOD_TIME

    /**
     * 定时器延迟时间
     */
    private var mDelayedTime = DEFAULT_DELAYED_TIME

    /**
     * 匀速滚动速度，按时间来计算
     */
    private var mSmoothSpeed = DEFAULT_SMOOTH_SPEED

    /**
     * 是否开启定时器的标志位
     */
    private var mFlagStartTimer: Boolean = false

    /**
     * View的宽、高
     */
    private var mWidth: Float = -1f
    private var mHeight: Float by Delegates.notNull()

    init {
        mSnapHelper.attachToRecyclerView(this)
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    /**
     * 监听布局完毕
     */
    override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        correctSnapViewPosition()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = (w - paddingLeft - paddingRight).toFloat()
        mHeight = (h - paddingTop - paddingBottom).toFloat()

        if (mSmoothMode) {
            mPeriodTime = (mSmoothSpeed / (mWidth / DEFAULT_PERIOD_SCROLL_PIXEL)).toLong()
        }

        if (mTimer == null) {
            startTimer()
        }
    }

    /**
     * 矫正首次初始化时SnapView的位置
     */
    private fun correctSnapViewPosition() {
        val layoutManager = getLinearLayoutManager()
        val snapView = mSnapHelper.findSnapView(layoutManager)
        if (snapView != null) {
            val snapDistance = mSnapHelper.calculateDistanceToFinalSnap(layoutManager, snapView)
            if (snapDistance != null) {
                if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                    scrollBy(snapDistance[0], snapDistance[1])
                }
                triggerOnPageSelected()
            }
        }
    }

    /**
     * 开始定时器
     */
    private fun startTimer() {
        mTimer?.cancel()
        if (mWidth > 0 && mFlagStartTimer && context != null && context is Activity) {
            mTimer = timer(initialDelay = mDelayedTime, period = mPeriodTime) {
                if (mScrollState == SCROLL_STATE_IDLE) {
                    (context as Activity).runOnUiThread {
                        if (mSmoothMode) {
                            scrollBy(DEFAULT_PERIOD_SCROLL_PIXEL, 0)
                            triggerOnPageSelected()
                        } else {
                            smoothScrollToPosition(++mOldPosition)
                            mPageChangeListener?.onPageSelected(mOldPosition)
                        }
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        mTimer?.cancel()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTimer()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTimer()
    }

    override fun onStartTemporaryDetach() {
        super.onStartTemporaryDetach()
        stopTimer()
    }

    override fun onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach()
        startTimer()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    /**
     * 滚动状态监听
     */
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        mScrollState = state
        if (state == SCROLL_STATE_IDLE) {
            triggerOnPageSelected()
        }
    }

    /**
     * 触发OnPageSelected回调
     */
    private fun triggerOnPageSelected() {
        val layoutManager = getLinearLayoutManager()
        val view = mSnapHelper.findSnapView(layoutManager)
        if (view != null) {
            val position = layoutManager.getPosition(view)
            if (position != mOldPosition) {
                mOldPosition = position
                mPageChangeListener?.onPageSelected(position)
            }
        }
    }

    /**
     * 在分发事件流中控制timer
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                stopTimer()
            }
            MotionEvent.ACTION_UP -> {
                startTimer()
            }
            MotionEvent.ACTION_CANCEL -> {
                startTimer()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 获取LinearLayoutManager
     */
    private fun getLinearLayoutManager(): androidx.recyclerview.widget.LinearLayoutManager {
        if (layoutManager != null && layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
            return layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        }
        throw IllegalStateException("需要且只能设置LinearLayoutManager类型")
    }

    companion object {
        private const val DEFAULT_PERIOD_TIME = 0L
        private const val DEFAULT_DELAYED_TIME = 0L
        private const val DEFAULT_PERIOD_SCROLL_PIXEL = 1
        private const val DEFAULT_SMOOTH_SPEED = 5000
    }
}