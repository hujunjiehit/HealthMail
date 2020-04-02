package com.coinbene.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.coinbene.common.R;

/**
 * Created by june
 * on 2019-09-21
 *
 * 可以指定最大高度的RecyclerView
 */
public class MaxHeightRecyclerView extends RecyclerView {

	private int mMaxHeight;

	public MaxHeightRecyclerView(Context context) {
		super(context);
	}

	public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView);
		mMaxHeight = arr.getLayoutDimension(R.styleable.MaxHeightRecyclerView_maxHeight, mMaxHeight);
		arr.recycle();
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		if (mMaxHeight > 0) {
			heightSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
		}
		super.onMeasure(widthSpec, heightSpec);
	}
}
