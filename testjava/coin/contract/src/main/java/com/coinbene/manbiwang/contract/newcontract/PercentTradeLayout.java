package com.coinbene.manbiwang.contract.newcontract;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PercentTradeLayout extends ConstraintLayout {


	@BindView(R2.id.seek_bar)
	BubbleSeekBar seekBar;
	@BindView(R2.id.tv_position)
	TextView tvPosition;
	private Context mContext;

	public PercentTradeLayout(Context context) {
		super(context);
		init(context);
	}

	public PercentTradeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	private void init(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_percent_trade, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}
		initLisenter();
	}

	private void initLisenter() {

	}


	public BubbleSeekBar getSeekBar() {
		return seekBar;
	}

	public void setSeekBarStatus(int tradeType) {
		if (tradeType == Constants.INDEX_OPEN) {
			seekBar.setThumbColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
			seekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		} else {
			seekBar.setThumbColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
			seekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		}
		seekBar.setProgress(0);
	}

	public TextView getTvPosition() {
		return tvPosition;
	}

	public void setSeekBarValue(int value) {
		seekBar.setProgress(value);
		tvPosition.setText(String.valueOf(value));

	}
}
