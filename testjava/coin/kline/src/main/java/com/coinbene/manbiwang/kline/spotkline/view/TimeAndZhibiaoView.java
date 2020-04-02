package com.coinbene.manbiwang.kline.spotkline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.listener.SelectTimeListener;
import com.coinbene.manbiwang.kline.spotkline.listener.TimeListener;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.widget.MoreTimePopWindow;
import com.coinbene.manbiwang.kline.widget.ZhibiaoPopWindow;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-11-24
 */
public class TimeAndZhibiaoView extends LinearLayout {

	@BindView(R2.id.line_min_tv)
	TextView mLineMinTv;
	@BindView(R2.id.line_min_view1)
	View mLineMinView1;
	@BindView(R2.id.layout_line_min)
	RelativeLayout mLayoutLineMin;
	@BindView(R2.id.fifteen_min_tv)
	TextView mFifteenMinTv;
	@BindView(R2.id.line_view1)
	View mLineView1;
	@BindView(R2.id.layout_fifteen_min)
	RelativeLayout mLayoutFifteenMin;
	@BindView(R2.id.one_hour_tv)
	TextView mOneHourTv;
	@BindView(R2.id.line_view2)
	View mLineView2;
	@BindView(R2.id.layout_one_hour)
	RelativeLayout mLayoutOneHour;
	@BindView(R2.id.four_hour_tv)
	TextView mFourHourTv;
	@BindView(R2.id.line_view3)
	View mLineView3;
	@BindView(R2.id.layout_four_hour)
	RelativeLayout mLayoutFourHour;
	@BindView(R2.id.one_day_tv)
	TextView mOneDayTv;
	@BindView(R2.id.line_view4)
	View mLineView4;
	@BindView(R2.id.layout_one_day)
	RelativeLayout mLayoutOneDay;
	@BindView(R2.id.more_tv)
	TextView mMoreTv;
	@BindView(R2.id.iv_more_top)
	ImageView mIvMoreTop;
	@BindView(R2.id.iv_more_bottom)
	ImageView mIvMoreBottom;
	@BindView(R2.id.line_view5)
	View mLineView5;
	@BindView(R2.id.layout_more)
	RelativeLayout mLayoutMore;
	@BindView(R2.id.zhibiao_tv)
	TextView mZhibiaoTv;
	@BindView(R2.id.iv_zhib_top)
	ImageView mIvZhibTop;
	@BindView(R2.id.iv_zhib_bottom)
	ImageView mIvZhibBottom;
	@BindView(R2.id.line_view6)
	View mLineView6;
	@BindView(R2.id.layout_zhib)
	RelativeLayout mLayoutZhib;
	@BindView(R2.id.layout_type)
	LinearLayout mLayoutType;

	private Context mContext;

	private TimeListener mTimeListener;
	private ZhibiaoListener mZhibiaoListener;

	private MoreTimePopWindow moreTimePopWindow;
	private ZhibiaoPopWindow zhibiaoPopWindow;
	private SelectTimeListener selectTimeListener;

	private TextView lastTxtView;
	private int[] lineArr = {R.id.line_view1, R.id.line_view2, R.id.line_view3, R.id.line_view4, R.id.line_view5, R.id.line_view6, R.id.line_min_view1};

	public TimeAndZhibiaoView(Context context) {
		super(context);
		initView(context);
	}

	public TimeAndZhibiaoView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TimeAndZhibiaoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.kline_time_and_zhibiao_view, this, true);
		ButterKnife.bind(this);
		moreTimePopWindow = new MoreTimePopWindow(mContext);
		zhibiaoPopWindow = new ZhibiaoPopWindow(mContext);

		initData();
		setListener();
	}

	public void initData() {
		if (isInEditMode()) {
			return;
		}

		mFifteenMinTv.setTag(1);
		mOneHourTv.setTag(2);
		mFourHourTv.setTag(3);
		mOneDayTv.setTag(4);
		mMoreTv.setTag(5);
		mZhibiaoTv.setTag(6);
		mLineMinTv.setTag(7);

		//初始化状态
		int timeStatus = SpUtil.get(mContext, SpUtil.K_LINE_TIME_STATUS, 1);
		switch (timeStatus) {
			case 9:
				changeStatusView(mLineMinTv);
				break;
			case 0:
				changeStatusView(mFifteenMinTv);
				break;
			case 1:
				changeStatusView(mOneHourTv);
				break;
			case 2:
				changeStatusView(mFourHourTv);
				break;
			case 3:
				changeStatusView(mOneDayTv);
				break;
			case 4:
				mMoreTv.setText(getResources().getString(R.string.k_chart_1min));
				changeStatusView(mMoreTv);
				break;
			case 5:
				mMoreTv.setText(getResources().getString(R.string.k_chart_5min));
				changeStatusView(mMoreTv);
				break;
			case 6:
				mMoreTv.setText(getResources().getString(R.string.k_chart_30min));
				changeStatusView(mMoreTv);
				break;
			case 7:
				mMoreTv.setText(getResources().getString(R.string.k_chart_1week));
				changeStatusView(mMoreTv);
				break;
			case 8:
				mMoreTv.setText(getResources().getString(R.string.k_chart_1mon));
				changeStatusView(mMoreTv);
				break;
		}

	}

	private void setListener() {
//		moreTimePopWindow.getmPopupWindow().setOnDismissListener(() -> {
//			mIvMoreBottom.setVisibility(View.INVISIBLE);
//			mIvMoreTop.setVisibility(View.VISIBLE);
//		});
//
//		zhibiaoPopWindow.getmPopupWindow().setOnDismissListener(() -> {
//			mIvZhibBottom.setVisibility(View.INVISIBLE);
//			mIvZhibTop.setVisibility(View.VISIBLE);
//		});

		mLineMinTv.setOnClickListener(v -> {
			//分时
			mMoreTv.setText(R.string.k_chart_more);
			onTimeStatusChanged(9);
			changeStatusView(mLineMinTv);
		});

		mFifteenMinTv.setOnClickListener(v -> {
			// 15分钟
			mMoreTv.setText(R.string.k_chart_more);
			onTimeStatusChanged(0);
			changeStatusView(mFifteenMinTv);
		});

		mOneHourTv.setOnClickListener(v -> {
			// 1小时
			mMoreTv.setText(R.string.k_chart_more);
			onTimeStatusChanged(1);
			changeStatusView(mOneHourTv);
		});

		mFourHourTv.setOnClickListener(v -> {
			//4小时
			mMoreTv.setText(R.string.k_chart_more);
			onTimeStatusChanged(2);
			changeStatusView(mFourHourTv);
		});

		mOneDayTv.setOnClickListener(v -> {
			//一天
			mMoreTv.setText(R.string.k_chart_more);
			onTimeStatusChanged(3);
			changeStatusView(mOneDayTv);
		});

		selectTimeListener = new SelectTimeListener() {
			@Override
			public void selectTimeType(int clickId, String txt) {
				mMoreTv.setText(txt);
				changeStatusView(mMoreTv);
				if (clickId == R.id.one_min_tv) {
					onTimeStatusChanged(4);
				} else if (clickId == R.id.five_min_tv) {
					onTimeStatusChanged(5);
				} else if (clickId == R.id.thirty_min_tv) {
					onTimeStatusChanged(6);
				} else if (clickId == R.id.one_week_tv) {
					onTimeStatusChanged(7);
				} else if (clickId == R.id.one_month_tv) {
					onTimeStatusChanged(8);
				}
			}

			@Override
			public void selectZhibiaoType(String zhibiao_type, String txt) {
				mZhibiaoListener.onSub2Selected(zhibiao_type);
			}

			@Override
			public void selectMasterType(int zhibiao_type, String txt) {
				mZhibiaoListener.onMasterSelected(zhibiao_type);
			}

			@Override
			public void cancelDimiss() {

			}
		};

		mLayoutMore.setOnClickListener(v -> {
			// 更多
			if (moreTimePopWindow != null) {
				moreTimePopWindow.setTypeChangeListener(selectTimeListener);
//				mIvMoreBottom.setVisibility(View.VISIBLE);
//				mIvMoreTop.setVisibility(View.INVISIBLE);
				moreTimePopWindow.show(mLayoutMore);
			}
		});

		mLayoutZhib.setOnClickListener(v -> {
			//指标
			int masterType = SpUtil.get(mContext, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
			String subZhibiao = SpUtil.get(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);

			zhibiaoPopWindow.setType(subZhibiao);
			zhibiaoPopWindow.setkMasterType(masterType);
			zhibiaoPopWindow.setTypeChangeListener(selectTimeListener);
//			mIvZhibBottom.setVisibility(View.VISIBLE);
//			mIvZhibTop.setVisibility(View.INVISIBLE);
			zhibiaoPopWindow.show(mLayoutZhib);
		});

	}

	private void onTimeStatusChanged(int timeStatus) {
		SpUtil.put(mContext, SpUtil.K_LINE_TIME_STATUS, timeStatus);
		if (mTimeListener != null) {
			mTimeListener.onTimeStatusChanged(timeStatus);
		}
	}

	public void setTimeListener(TimeListener mTimeListener) {
		this.mTimeListener = mTimeListener;
	}

	public void setZhibiaoListener(ZhibiaoListener mZhibiaoListener) {
		this.mZhibiaoListener = mZhibiaoListener;
	}

	private void changeStatusView(TextView currentView) {
		if (lastTxtView == null) {
			lastTxtView = currentView;
		} else {
			if (lastTxtView == currentView) {
				return;
			}
			lastTxtView.setSelected(false);
			ViewGroup group = (ViewGroup) lastTxtView.getParent();
			int tag = (int) lastTxtView.getTag();
			if (tag - 1 < lineArr.length) {
				int id = lineArr[tag - 1];
				View lineView = group.findViewById(id);
				if (lineView != null) {
					lineView.setVisibility(View.INVISIBLE);
				}
			}
			lastTxtView = currentView;
		}
		currentView.setSelected(true);
		ViewGroup group = (ViewGroup) currentView.getParent();
		int tag = (int) currentView.getTag();
		if (tag - 1 < lineArr.length) {
			int id = lineArr[tag - 1];
			View lineView = group.findViewById(id);
			if (lineView != null) {
				lineView.setVisibility(View.VISIBLE);
			}
		}
	}
}
