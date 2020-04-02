package com.coinbene.manbiwang.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.coinbene.manbiwang.home.R;
import com.coinbene.manbiwang.home.R2;
import com.coinbene.manbiwang.home.adapter.NavigationAdapter;
import com.coinbene.manbiwang.home.util.PagerGridSnapHelper;
import com.coinbene.manbiwang.home.util.PagerLayoutManager;
import com.coinbene.manbiwang.model.http.AppConfigModel;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ding
 * 2019-12-26
 * com.coinbene.manbiwang.home.view
 */
public class NavigationLayout extends LinearLayout {
	static final String TAG = "NavigationLayout";

	@BindView(R2.id.transformer_RecyclerView)
	RecyclerView mRecyclerView;

	@BindView(R2.id.transformer_RadioGroup)
	RadioGroup mRadioGroup;

	private PagerLayoutManager layoutManager;
	private PagerGridSnapHelper snapHelper;
	private NavigationAdapter adapter;

	private OnPageChangeListener onPageChangeListener;

	public NavigationLayout(Context context) {
		this(context, null);
	}

	public NavigationLayout(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NavigationLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		listener();
	}

	public void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_navigation, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		layoutManager = new PagerLayoutManager(1, 4);
		mRecyclerView.setLayoutManager(layoutManager);

		snapHelper = new PagerGridSnapHelper();
		snapHelper.attachToRecyclerView(mRecyclerView);

		adapter = new NavigationAdapter();
		adapter.bindToRecyclerView(mRecyclerView);
	}

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}


	public void scroolToPage(int page) {
		if (layoutManager != null)
			layoutManager.scrollToPage(page);
	}

	public void listener() {
		layoutManager.setPageListener(new PagerLayoutManager.PageListener() {
			@Override
			public void onPageSizeChanged(int pageSize) {
				//没有页面，或者只有1页不显示指示器
				if (pageSize == 0 || pageSize == 1) {
					mRadioGroup.setVisibility(GONE);
				} else {
					mRadioGroup.setVisibility(VISIBLE);
					addIndicator(pageSize);
				}
			}

			@Override
			public void onPageSelect(int pageIndex) {
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageChange(pageIndex);
				}
				if (mRadioGroup.getChildCount() > 0) {
					mRadioGroup.check(mRadioGroup.getChildAt(pageIndex).getId());
				}
			}
		});


		mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			for (int i = 0; i < group.getChildCount(); i++) {
				if (group.getChildAt(i).getId() == checkedId) {
					RadioButton btn = (RadioButton) group.getChildAt(i);
					btn.setBackgroundResource(R.drawable.shape_indicator_selected);
				} else {
					RadioButton btn = (RadioButton) group.getChildAt(i);
					btn.setBackgroundResource(R.drawable.shape_indicator_normal);
				}
			}
		});
	}


	public interface OnPageChangeListener {
		void onPageChange(int page);
	}

	/**
	 * 添加指示器
	 *
	 * @param quantity 指示器总数
	 */
	public void addIndicator(int quantity) {
		int height = QMUIDisplayHelper.dp2px(getContext(), 2);
		int margin = QMUIDisplayHelper.dp2px(getContext(), 7);
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


		//清空上次指示器
		if (mRadioGroup.getChildCount() > 0) {
			mRadioGroup.removeAllViews();
		}

		//建立此次指示器
		for (int index = 0; index < quantity; index++) {
			RadioButton child = new RadioButton(getContext());
			child.setHeight(height);
			child.setButtonDrawable(null);
			child.setBackgroundResource(R.drawable.shape_indicator_normal);

			layoutParams.leftMargin = margin;
			child.setLayoutParams(layoutParams);
			mRadioGroup.addView(child, index);
		}


		mRadioGroup.check(mRadioGroup.getChildAt(0).getId());

		notifyData();
	}

	public void notifyData() {
		postDelayed(() -> {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}, 500);
	}


	public void setOnItemClickListener(BaseQuickAdapter.OnItemClickListener itemClickListener) {
		adapter.setOnItemClickListener(itemClickListener);
	}

	public void setData(List<AppConfigModel.MainNavigationBean> data) {
		adapter.setNewData(data);
//		invalidate();
	}

}
