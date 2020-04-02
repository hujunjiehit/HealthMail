package com.coinbene.common.widget;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by june
 * on 2019-08-09
 */
public class WrapperLinearLayoutManager extends LinearLayoutManager {
	public WrapperLinearLayoutManager(Context context) {
		super(context);
	}

	public WrapperLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	public WrapperLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			//try catch一下
			super.onLayoutChildren( recycler, state );
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
}
