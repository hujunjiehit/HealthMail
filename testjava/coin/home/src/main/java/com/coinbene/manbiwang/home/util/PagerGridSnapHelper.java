package com.coinbene.manbiwang.home.util;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * 作用：分页居中工具
 * 作者：GcsSloop
 * 摘要：每次只滚动一个页面
 */
public class PagerGridSnapHelper extends SnapHelper {
	static final String TAG = "PagerGridSnapHelper";
	private RecyclerView mRecyclerView;                     // RecyclerView

	/**
	 * 用于将滚动工具和 Recycler 绑定
	 *
	 * @param recyclerView RecyclerView
	 * @throws IllegalStateException 状态异常
	 */
	@Override
	public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws
			IllegalStateException {
		super.attachToRecyclerView(recyclerView);
		mRecyclerView = recyclerView;
	}

	/**
	 * 计算需要滚动的向量，用于页面自动回滚对齐
	 *
	 * @param layoutManager 布局管理器
	 * @param targetView    目标控件
	 * @return 需要滚动的距离
	 */
	@Nullable
	@Override
	public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
	                                          @NonNull View targetView) {
		int pos = layoutManager.getPosition(targetView);
		int[] offset = new int[2];
		if (layoutManager instanceof PagerLayoutManager) {
			PagerLayoutManager manager = (PagerLayoutManager) layoutManager;
			offset = manager.getSnapOffset(pos);
		}
		return offset;
	}

	/**
	 * 获得需要对齐的View，对于分页布局来说，就是页面第一个
	 *
	 * @param layoutManager 布局管理器
	 * @return 目标控件
	 */
	@Nullable
	@Override
	public View findSnapView(RecyclerView.LayoutManager layoutManager) {
		if (layoutManager instanceof PagerLayoutManager) {
			PagerLayoutManager manager = (PagerLayoutManager) layoutManager;
			return manager.findSnapView();
		}
		return null;
	}

	/**
	 * 获取目标控件的位置下标
	 * (获取滚动后第一个View的下标)
	 *
	 * @param layoutManager 布局管理器
	 * @param velocityX     X 轴滚动速率
	 * @param velocityY     Y 轴滚动速率
	 * @return 目标控件的下标
	 */
	@Override
	public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager,
	                                  int velocityX, int velocityY) {
		int target = RecyclerView.NO_POSITION;
		if (null != layoutManager && layoutManager instanceof PagerLayoutManager) {
			PagerLayoutManager manager = (PagerLayoutManager) layoutManager;
			if (manager.canScrollHorizontally()) {
				if (velocityX > PagerConfig.getFlingThreshold()) {
					target = manager.findNextPageFirstPos();
				} else if (velocityX < -PagerConfig.getFlingThreshold()) {
					target = manager.findPrePageFirstPos();
				}
			} else if (manager.canScrollVertically()) {
				if (velocityY > PagerConfig.getFlingThreshold()) {
					target = manager.findNextPageFirstPos();
				} else if (velocityY < -PagerConfig.getFlingThreshold()) {
					target = manager.findPrePageFirstPos();
				}
			}
		}
		return target;
	}

	/**
	 * 一扔(快速滚动)
	 *
	 * @param velocityX X 轴滚动速率
	 * @param velocityY Y 轴滚动速率
	 * @return 是否消费该事件
	 */
	@Override
	public boolean onFling(int velocityX, int velocityY) {
		RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
		if (layoutManager == null) {
			return false;
		}
		RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
		if (adapter == null) {
			return false;
		}
		int minFlingVelocity = PagerConfig.getFlingThreshold();
		return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
				&& snapFromFling(layoutManager, velocityX, velocityY);
	}

	/**
	 * 快速滚动的具体处理方案
	 *
	 * @param layoutManager 布局管理器
	 * @param velocityX     X 轴滚动速率
	 * @param velocityY     Y 轴滚动速率
	 * @return 是否消费该事件
	 */
	private boolean snapFromFling(@NonNull RecyclerView.LayoutManager layoutManager, int velocityX,
	                              int velocityY) {
		if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
			return false;
		}

		RecyclerView.SmoothScroller smoothScroller = createSnapScroller(layoutManager);
		if (smoothScroller == null) {
			return false;
		}

		int targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY);
		if (targetPosition == RecyclerView.NO_POSITION) {
			return false;
		}

		smoothScroller.setTargetPosition(targetPosition);
		layoutManager.startSmoothScroll(smoothScroller);
		return true;
	}

	/**
	 * 通过自定义 LinearSmoothScroller 来控制速度
	 *
	 * @param layoutManager 布局故哪里去
	 * @return 自定义 LinearSmoothScroller
	 */
	protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
		if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
			return null;
		}
		return new PagerGridSmoothScroller(mRecyclerView);
	}

	//--- 公开方法 ----------------------------------------------------------------------------------

	/**
	 * 设置滚动阀值
	 * @param threshold 滚动阀值
	 */
	public void setFlingThreshold(int threshold) {
		PagerConfig.setFlingThreshold(threshold);
	}

	public static class PagerConfig {
		private static final String TAG = "PagerGrid";
		private static boolean sShowLog = false;
		private static int sFlingThreshold = 1000;          // Fling 阀值，滚动速度超过该阀值才会触发滚动
		private static float sMillisecondsPreInch = 60f;    // 每一个英寸滚动需要的微秒数，数值越大，速度越慢

		/**
		 * 判断是否输出日志
		 *
		 * @return true 输出，false 不输出
		 */
		public static boolean isShowLog() {
			return sShowLog;
		}

		/**
		 * 设置是否输出日志
		 *
		 * @param showLog 是否输出
		 */
		public static void setShowLog(boolean showLog) {
			sShowLog = showLog;
		}

		/**
		 * 获取当前滚动速度阀值
		 *
		 * @return 当前滚动速度阀值
		 */
		public static int getFlingThreshold() {
			return sFlingThreshold;
		}

		/**
		 * 设置当前滚动速度阀值
		 *
		 * @param flingThreshold 滚动速度阀值
		 */
		public static void setFlingThreshold(int flingThreshold) {
			sFlingThreshold = flingThreshold;
		}

		/**
		 * 获取滚动速度 英寸/微秒
		 *
		 * @return 英寸滚动速度
		 */
		public static float getMillisecondsPreInch() {
			return sMillisecondsPreInch;
		}

		/**
		 * 设置像素滚动速度 英寸/微秒
		 *
		 * @param millisecondsPreInch 英寸滚动速度
		 */
		public static void setMillisecondsPreInch(float millisecondsPreInch) {
			sMillisecondsPreInch = millisecondsPreInch;
		}
	}
}