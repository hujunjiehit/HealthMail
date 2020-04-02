package com.coinbene.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by june
 * on 2019-09-06
 *
 * 修复viewpager滑动的时候，触发下拉刷新问题
 */
public class FixSwipeRefreshLayout extends SwipeRefreshLayout {

	private float startY;
	private float startX;

	// 记录viewPager是否拖拽的标记
	private boolean mIsVpDragger;

	private int mTouchSlop;

	private boolean mAppbarExpanded;

	private ViewPager mViewPager;
	private Map<Integer, RecyclerView> mRecyclerViewMap;
	private List<RecyclerView> mRecyclerViewList;

	public FixSwipeRefreshLayout(@NonNull Context context) {
		super(context);
		init(context);
	}

	public FixSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		int action = ev.getAction();

		switch (action) {

			case MotionEvent.ACTION_DOWN:
				// 记录手指按下的位置
				startY = ev.getY();
				startX = ev.getX();
				// 初始化标记
				mIsVpDragger = false;
				break;
			case MotionEvent.ACTION_MOVE:
				// 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
				if(mIsVpDragger) {
					return false;
				}

				// 获取当前手指位置
				float endY = ev.getY();
				float endX = ev.getX();
				float distanceX = Math.abs(endX - startX);
				float distanceY = Math.abs(endY - startY);
				// 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
				if(distanceX > mTouchSlop && distanceX > distanceY) {
					mIsVpDragger = true;
					return false;
				}

				if (mViewPager == null) {
					//拿到ViewPager
					findViewPager(this);
				}

				if ((mViewPager != null && mRecyclerViewMap == null) || (mRecyclerViewList != null && mRecyclerViewList.size() == 0)) {
					//拿到ViewPager里面的每个RecyclerView
					mRecyclerViewMap = new TreeMap<>();
					mRecyclerViewList = new ArrayList<>();
					for(int i = 0; i < mViewPager.getChildCount(); i++) {
						getRecyclerView(mViewPager.getChildAt(i), mViewPager.getChildAt(i).getLeft());
					}
					Iterator<Map.Entry<Integer, RecyclerView>> iterator = mRecyclerViewMap.entrySet().iterator();
					while (iterator.hasNext()) {
						mRecyclerViewList.add(iterator.next().getValue());
					}
				}

				if (mViewPager != null && mRecyclerViewMap != null) {
					if (mRecyclerViewList == null || mRecyclerViewList.size() == 0){
						return super.onInterceptTouchEvent(ev);
					}

					if (mRecyclerViewList.get(mViewPager.getCurrentItem()).getLayoutManager() instanceof LinearLayoutManager) {
						if (((LinearLayoutManager)mRecyclerViewList.get(mViewPager.getCurrentItem()).getLayoutManager()).findFirstCompletelyVisibleItemPosition() <= 0) {
							//自己处理
							return super.onInterceptTouchEvent(ev);
						}
					}
				}

				if (mAppbarExpanded) {
					//return false 交给viewpager处理
					return false;
				}

				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				// 初始化标记
				mIsVpDragger = false;
				break;
		}
		// 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mViewPager = null;
		if (mRecyclerViewMap != null) {
			mRecyclerViewMap.clear();
		}
		mRecyclerViewMap = null;
		if (mRecyclerViewList != null) {
			mRecyclerViewList.clear();
		}
		mRecyclerViewList = null;
	}

	public void setAppbarExpanded(boolean isExpanded) {
		this.mAppbarExpanded = isExpanded;
	}

	private void findViewPager(View view) {
		if (mViewPager != null) {
			return;
		}
		if (view instanceof ViewGroup) {
			if (view instanceof ViewPager) {
				mViewPager = (ViewPager) view;
			}
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				findViewPager(vp.getChildAt(i));
			}
		}
	}

	private void getRecyclerView(View view, int leftPosition) {
		if (view instanceof ViewGroup) {
			if (view instanceof RecyclerView) {
				if (mRecyclerViewMap != null) {
					//mRecyclerViewList内部按照 view.getLeft 排序
					mRecyclerViewMap.put(leftPosition, (RecyclerView) view);
				}
			}
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				getRecyclerView(vp.getChildAt(i), leftPosition);
			}
		}
	}

}
