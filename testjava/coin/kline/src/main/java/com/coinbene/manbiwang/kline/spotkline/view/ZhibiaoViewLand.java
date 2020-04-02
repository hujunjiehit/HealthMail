package com.coinbene.manbiwang.kline.spotkline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-11-22
 */
public class ZhibiaoViewLand extends LinearLayout {

	@BindView(R2.id.main_tv)
	TextView mMainTv;
	@BindView(R2.id.ma_radio)
	RadioButton mMaRadio;
	@BindView(R2.id.boll_radio)
	RadioButton mBollRadio;
	@BindView(R2.id.hide_radio)
	RadioButton mHideRadio;
	@BindView(R2.id.main_radio_group)
	RadioGroup mMainRadioGroup;
	@BindView(R2.id.sub1_tv)
	TextView mSub1Tv;
	@BindView(R2.id.vol_radio)
	RadioButton mVolRadio;
	@BindView(R2.id.sub1_radio_group)
	RadioGroup mSub1RadioGroup;
	@BindView(R2.id.sub2_tv)
	TextView mSub2Tv;
	@BindView(R2.id.macd_radio)
	RadioButton mMacdRadio;
	@BindView(R2.id.kdj_radio)
	RadioButton mKdjRadio;
	@BindView(R2.id.rsi_radio)
	RadioButton mRsiRadio;
	@BindView(R2.id.hide_sub2_radio)
	RadioButton mHideSub2Radio;
	@BindView(R2.id.sub2_radio_group)
	RadioGroup mSub2RadioGroup;

	private ZhibiaoListener mZhibiaoListener;
	private Context mContext;

	public ZhibiaoViewLand(Context context) {
		super(context);
		initView(context);
	}

	public ZhibiaoViewLand(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ZhibiaoViewLand(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.kline_zhibiao_view_land, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		int masterType = SpUtil.get(mContext, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
		switch (masterType) {
			case ZhibiaoListener.MASTER_TYPE_HIDE:
				mMainRadioGroup.check(R.id.hide_radio);
				break;
			case ZhibiaoListener.MASTER_TYPE_MA:
				mMainRadioGroup.check(R.id.ma_radio);
				break;
			case ZhibiaoListener.MASTER_TYPE_BOLL:
				mMainRadioGroup.check(R.id.boll_radio);
				break;
		}

		mSub1RadioGroup.check(R.id.vol_radio);


		String subZhibiao = SpUtil.get(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.MACD);
		switch (subZhibiao) {
			case ZhibiaoListener.KDJ:
				mSub2RadioGroup.check(R.id.kdj_radio);
				break;
			case ZhibiaoListener.MACD:
				mSub2RadioGroup.check(R.id.macd_radio);
				break;
			case ZhibiaoListener.RSI:
				mSub2RadioGroup.check(R.id.rsi_radio);
				break;
			case ZhibiaoListener.SUB2_HIDE:
				mSub2RadioGroup.check(R.id.hide_sub2_radio);
				break;
		}

		setListener();
	}

	private void setListener() {
		mMainRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.ma_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
				mZhibiaoListener.onMasterSelected(ZhibiaoListener.MASTER_TYPE_MA);
			} else if (checkedId == R.id.boll_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_BOLL);
				mZhibiaoListener.onMasterSelected(ZhibiaoListener.MASTER_TYPE_BOLL);
			} else if (checkedId == R.id.hide_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_HIDE);
				mZhibiaoListener.onMasterSelected(ZhibiaoListener.MASTER_TYPE_HIDE);
			}
		});

		mSub2RadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.macd_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.MACD);
				mZhibiaoListener.onSub2Selected(ZhibiaoListener.MACD);
			} else if (checkedId == R.id.kdj_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.KDJ);
				mZhibiaoListener.onSub2Selected(ZhibiaoListener.KDJ);
			} else if (checkedId == R.id.rsi_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.RSI);
				mZhibiaoListener.onSub2Selected(ZhibiaoListener.RSI);
			} else if (checkedId == R.id.hide_sub2_radio) {
				SpUtil.put(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);
				mZhibiaoListener.onSub2Selected(ZhibiaoListener.SUB2_HIDE);
			}
		});
	}


	public void setZhibiaoListener(ZhibiaoListener listener) {
		this.mZhibiaoListener = listener;
	}
}
