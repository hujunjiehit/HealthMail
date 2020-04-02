package com.coinbene.manbiwang.record.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by june
 * on 2019-09-16
 */
public class CustomSpaceItemDecoration extends RecyclerView.ItemDecoration {

	private int space;  //位移间距
	private Context mContext;

	public CustomSpaceItemDecoration(Context context, int space) {
		this.space = space;
		this.mContext = context;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		if (parent.getChildAdapterPosition(view) % 3 == 0) {
			outRect.left = 0; //第一列左边贴边
		} else {
			if (parent.getChildAdapterPosition(view) % 3 == 1) {
				outRect.left = space;//第二列移动一个位移间距
			} else {
				outRect.left = space * 2;//由于第二列已经移动了一个间距，所以第三列要移动两个位移间距就能右边贴边，且item间距相等
			}
		}
	}
}
