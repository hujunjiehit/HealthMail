package com.coinbene.common.widget.wrapper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.BaseAdapter;


/**
 * LoadMoreListener
 *
 * @author ding
 * RecyclerView 加载监听
 */
public abstract class LoadMoreListener extends RecyclerView.OnScrollListener {

	private boolean isUp;

	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
			linearSettings(recyclerView, newState);
		}


	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		// 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
		isUp = dy > 0;
	}


	/**
	 * @param recyclerView
	 * @param newState     线性布局管理器设置
	 */
	private void linearSettings(RecyclerView recyclerView, int newState) {
		LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
		// 当不滑动时
		if (newState == RecyclerView.SCROLL_STATE_IDLE) {
			// 获取最后一个完全显示的itemPosition
			int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
			int itemCount = manager.getItemCount();

			// 判断是否滑动到了最后一个item，并且是向上滑动
			if (lastItemPosition == (itemCount - 1) && isUp) {
				BaseAdapter adapter = (BaseAdapter) recyclerView.getAdapter();
				if (adapter != null && adapter.getState() == BaseAdapter.LOADING_END) {
					return;
				}
				// 加载更多
				loadMore();
			}
		}
	}

	/**
	 * 加载更多
	 */
	public abstract void loadMore();
}
