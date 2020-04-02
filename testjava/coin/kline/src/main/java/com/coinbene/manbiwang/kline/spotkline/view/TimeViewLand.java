package com.coinbene.manbiwang.kline.spotkline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.spotkline.listener.TimeListener;

/**
 * Created by june
 * on 2019-11-22
 */
public class TimeViewLand extends RadioGroup {

	private Context mContext;

	private TimeListener mTimeListener;

	private RadioGroup mRadioGroup;

	private static SparseIntArray statusMap;

	static {
		statusMap = new SparseIntArray(10);
		statusMap.put(0, R.id.radio_fifteen_min);
		statusMap.put(1, R.id.radio_one_hour);
		statusMap.put(2, R.id.radio_four_hour);
		statusMap.put(3, R.id.radio_one_day);
		statusMap.put(4, R.id.radio_one_min);
		statusMap.put(5, R.id.radio_five_min);
		statusMap.put(6, R.id.radio_thirty_min);
		statusMap.put(7, R.id.radio_one_week);
		statusMap.put(8, R.id.radio_one_month);
		statusMap.put(9, R.id.radio_time_line);
	}

	public TimeViewLand(Context context) {
		super(context);
		initView(context);
	}

	public TimeViewLand(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}


	public void setTimeListener(TimeListener mTimeListener) {
		this.mTimeListener = mTimeListener;
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.kline_time_view_land, this, true);

		if (isInEditMode()) {
			return;
		}

		mRadioGroup = findViewById(R.id.radio_group);


		int timeStatus = SpUtil.get(mContext, SpUtil.K_LINE_TIME_STATUS, 1);
		mRadioGroup.check(statusMap.get(timeStatus));

		mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			for(int i = 0; i < statusMap.size(); i++) {
				int key = statusMap.keyAt(i);
				if (checkedId == statusMap.get(key)) {
					if (mTimeListener != null) {
						//保存sp
						SpUtil.put(mContext, SpUtil.K_LINE_TIME_STATUS, key);

						mTimeListener.onTimeStatusChanged(key);
						break;
					}
				}
			}
		});
	}
}
