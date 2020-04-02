package com.coinbene.common.widget;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by june
 * on 2020-03-17
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

	private static final int TIME_PERIOD = 100;

	private State mCurrentState = State.IDLE;

	private int lastOffset = Integer.MAX_VALUE;
	private long lastTime = System.currentTimeMillis();

	private ScrollState currentScrollState = ScrollState.IDLE;

	public enum State {
		EXPANDED,
		COLLAPSED,
		IDLE
	}

	public enum ScrollState {
		IDLE,
		SCROLLING
	}


	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
		if (lastOffset == Integer.MAX_VALUE ) {
			lastOffset = i;
		}
		int diffY = Math.abs(i - lastOffset);

		if (diffY != 0) {
			lastTime = System.currentTimeMillis();
		}

		if (diffY == 0) {
			if ((System.currentTimeMillis() - lastTime) > TIME_PERIOD) {
				if (currentScrollState != ScrollState.IDLE) {
					onScrollStateChanged(ScrollState.IDLE);
					currentScrollState = ScrollState.IDLE;
				}
			} else {
				if (currentScrollState != ScrollState.SCROLLING) {
					onScrollStateChanged(ScrollState.SCROLLING);
					currentScrollState = ScrollState.SCROLLING;
				}
			}
		} else {
			if (currentScrollState != ScrollState.SCROLLING) {
				onScrollStateChanged(ScrollState.SCROLLING);
				currentScrollState = ScrollState.SCROLLING;
			}
		}

		onOffsetValueChanged(appBarLayout, i);

		lastOffset = i;

		if (i == 0) {
			if (mCurrentState != State.EXPANDED) {
				onStateChanged(appBarLayout, State.EXPANDED);
			}
			mCurrentState = State.EXPANDED;
		} else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
			if (mCurrentState != State.COLLAPSED) {
				onStateChanged(appBarLayout, State.COLLAPSED);
			}
			mCurrentState = State.COLLAPSED;
		} else {
			if (mCurrentState != State.IDLE) {
				onStateChanged(appBarLayout, State.IDLE);
			}
			mCurrentState = State.IDLE;
		}
	}

	//状态发生了改变
	public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

	//发生了偏移
	public abstract void onOffsetValueChanged(AppBarLayout appBarLayout, int offset);

	public abstract void onScrollStateChanged(ScrollState scrollState);
}
