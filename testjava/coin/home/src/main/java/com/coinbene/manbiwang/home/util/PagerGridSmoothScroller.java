package com.coinbene.manbiwang.home.util;


import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.State;

public class PagerGridSmoothScroller extends LinearSmoothScroller {
	static final String TAG = "PagerGridSmoothScroller";
	private RecyclerView mRecyclerView;

	public PagerGridSmoothScroller(@NonNull RecyclerView recyclerView) {
		super(recyclerView.getContext());
		this.mRecyclerView = recyclerView;
	}

	protected void onTargetFound(View targetView, State state, Action action) {
		LayoutManager manager = this.mRecyclerView.getLayoutManager();
		if (null != manager) {
			if (manager instanceof PagerLayoutManager) {
				PagerLayoutManager layoutManager = (PagerLayoutManager) manager;
				int pos = this.mRecyclerView.getChildAdapterPosition(targetView);
				int[] snapDistances = layoutManager.getSnapOffset(pos);
				int dx = snapDistances[0];
				int dy = snapDistances[1];
				int time = this.calculateTimeForScrolling(Math.max(Math.abs(dx), Math.abs(dy)));
				if (time > 0) {
					action.update(dx, dy, time, this.mDecelerateInterpolator);
				}
			}

		}
	}

	protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
		return PagerGridSnapHelper.PagerConfig.getMillisecondsPreInch() / (float) displayMetrics.densityDpi;
	}
}
