package com.coinbene.common.widget;


import com.coinbene.common.R;

/**
 * Created by june
 * on 2019-08-12
 */
public class LoadMoreView extends com.chad.library.adapter.base.loadmore.LoadMoreView {

	@Override
	public int getLayoutId() {
		return R.layout.common_load_more_layout;
	}

	@Override
	protected int getLoadingViewId() {
		return R.id.load_more_loading_view;
	}

	@Override
	protected int getLoadFailViewId() {
		return R.id.load_more_load_fail_view;
	}

	@Override
	protected int getLoadEndViewId() {
		return R.id.load_more_load_end_view;
	}
}
