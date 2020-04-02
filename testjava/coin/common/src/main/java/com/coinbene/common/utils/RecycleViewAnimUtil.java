package com.coinbene.common.utils;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

/**
 * Created by june
 * on 2019-07-28
 */
public class RecycleViewAnimUtil {

	/**
	 * 关闭默认局部刷新动画
	 */
	public static void closeDefaultAnimator(RecyclerView recyclerView) {
		if(recyclerView == null) return;

		recyclerView.getItemAnimator().setAddDuration(0);
		recyclerView.getItemAnimator().setChangeDuration(0);
		recyclerView.getItemAnimator().setMoveDuration(0);
		recyclerView.getItemAnimator().setRemoveDuration(0);

		((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
	}
}
