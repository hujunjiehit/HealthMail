package com.coinbene.manbiwang.kline.spotkline.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.NumberUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.bean.KLineBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-11-22
 */
public class TopViewLand extends RelativeLayout {

	@BindView(R2.id.trade_pair_name)
	TextView mTradePairName;
	@BindView(R2.id.current_price_tv)
	TextView mCurrentPriceTv;
	@BindView(R2.id.percent_tv)
	TextView mPercentTv;
	@BindView(R2.id.current_price_local_tv)
	TextView mCurrentPriceLocalTv;
	@BindView(R2.id.price_layout)
	ConstraintLayout mPriceLayout;
	@BindView(R2.id.tv_open)
	TextView mTvOpen;
	@BindView(R2.id.tv_open_value)
	TextView mTvOpenValue;
	@BindView(R2.id.tv_high)
	TextView mTvHigh;
	@BindView(R2.id.tv_high_value)
	TextView mTvHighValue;
	@BindView(R2.id.tv_low)
	TextView mTvLow;
	@BindView(R2.id.tv_low_value)
	TextView mTvLowValue;
	@BindView(R2.id.tv_close)
	TextView mTvClose;
	@BindView(R2.id.tv_close_value)
	TextView mTvCloseValue;
	@BindView(R2.id.tv_change)
	TextView mTvChange;
	@BindView(R2.id.tv_change_value)
	TextView mTvChangeValue;
	@BindView(R2.id.tv_volume)
	TextView mTvVolume;
	@BindView(R2.id.tv_volume_value)
	TextView mTvVolumeValue;
	@BindView(R2.id.tv_time_value)
	TextView mTvTimeValue;
	@BindView(R2.id.top_press_layout_value)
	LinearLayout mTopPressLayoutValue;
	@BindView(R2.id.iv_rise_type)
	ImageView mIvRiseType;

	private Context mContext;
	private boolean isRedRise;

	private int mRiseType = Constants.RISE_DEFAULT;
	private WsMarketData mQuote;
	private int contractType;
	private String symbol;
	private ContractUsdtInfoTable table;
	private boolean isContract;

	public TopViewLand(Context context) {
		super(context);
		initView(context);
	}

	public TopViewLand(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TopViewLand(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.kline_top_view_land, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		isRedRise = SwitchUtils.isRedRise();
	}


	/**
	 * 设置合约顶部数据
	 *
	 * @param symbol
	 * @param quote
	 */
	public void setContractData(String symbol, WsMarketData quote) {
		this.mQuote = quote;
		this.mRiseType = quote.getRiseType();
		if (TradeUtils.isUsdtContract(symbol)) {
			mTradePairName.setText(String.format(getContext().getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(symbol)));
		} else {
			mTradePairName.setText(symbol);
		}

		if (mRiseType == Constants.RISE_UP) {
			mIvRiseType.setVisibility(View.VISIBLE);
			mIvRiseType.setImageResource(isRedRise ? R.drawable.res_red_up : R.drawable.res_green_up);
			mCurrentPriceTv.setTextColor(isRedRise ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		} else if (mRiseType == Constants.RISE_DOWN) {
			mIvRiseType.setVisibility(View.VISIBLE);
			mIvRiseType.setImageResource(isRedRise ? R.drawable.res_green_down : R.drawable.res_red_down);
			mCurrentPriceTv.setTextColor(isRedRise ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		} else {
			mIvRiseType.setVisibility(View.GONE);
			mCurrentPriceTv.setTextColor(getResources().getColor(R.color.white));
		}
		mCurrentPriceTv.setText(TextUtils.isEmpty(quote.getLastPrice()) ? "0" : quote.getLastPrice());

		if (!TextUtils.isEmpty(quote.getChangeValue())) {
			float aFloat = Tools.parseFloat(quote.getChangeValue());
			if (aFloat > 0) {
				mCurrentPriceLocalTv.setText(String.format("+%s", quote.getUpsAndDowns()));
				mPercentTv.setText(String.format("+%s", quote.getChangeValue()));
			} else {
				mCurrentPriceLocalTv.setText(quote.getUpsAndDowns());
				mPercentTv.setText(quote.getChangeValue());
			}
			if (isRedRise) {
				if (aFloat > 0) {
					mCurrentPriceLocalTv.setTextColor(getResources().getColor(R.color.res_red));
					mPercentTv.setTextColor(getResources().getColor(R.color.res_red));
				} else if (aFloat < 0) {
					mCurrentPriceLocalTv.setTextColor(getResources().getColor(R.color.res_green));
					mPercentTv.setTextColor(getResources().getColor(R.color.res_green));
				} else {
					mCurrentPriceLocalTv.setTextColor(getResources().getColor(R.color.Kline_White));
					mPercentTv.setTextColor(getResources().getColor(R.color.Kline_White));
				}
			} else {
				if (aFloat > 0) {
					mCurrentPriceLocalTv.setTextColor(getResources().getColor(R.color.res_green));
					mPercentTv.setTextColor(getResources().getColor(R.color.res_green));
				} else if (aFloat < 0) {
					mCurrentPriceLocalTv.setTextColor(getResources().getColor(R.color.res_red));
					mPercentTv.setTextColor(getResources().getColor(R.color.res_red));
				} else {
					mCurrentPriceLocalTv.setTextColor(getResources().getColor(R.color.Kline_White));
					mPercentTv.setTextColor(getResources().getColor(R.color.Kline_White));
				}
			}
		}
	}

	public void setRiseType(String symbol, int riseType) {
		this.mRiseType = riseType;
		if (mQuote != null) {
			setContractData(symbol, mQuote);
		}
	}

	public void setData(String tradePairName, WsMarketData quote) {
		mIvRiseType.setVisibility(View.GONE);
		mTradePairName.setText(tradePairName);
		mCurrentPriceTv.setText(quote.getLastPrice());

		String nl = quote.getLocalPrice();
		mCurrentPriceLocalTv.setText(new StringBuilder().append("≈").append(StringUtils.getCnyReplace(nl)).toString());

		//涨跌幅
		String upsAndDowns = TextUtils.isEmpty(quote.getUpsAndDowns()) ? "0.00%" : quote.getUpsAndDowns();
		if (!upsAndDowns.equals("0.00%") && !upsAndDowns.contains("-")) {
			upsAndDowns = String.format("+%s", upsAndDowns);
		}
		mPercentTv.setText(upsAndDowns);

		if (!TextUtils.isEmpty(upsAndDowns)) {
			mCurrentPriceTv.setTextColor(getTextColor(upsAndDowns));
			mPercentTv.setTextColor(getTextColor(upsAndDowns));
		}
	}

	private @ColorInt int getTextColor(String value){
		if (value.contains("+")) {
			return isRedRise ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green);
		} else if (value.contains("-")) {
			return isRedRise ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red);
		} else {
			return getResources().getColor(R.color.Kline_White);
		}
	}

	public void hideHighValueSelectedValue() {
		mPriceLayout.setVisibility(VISIBLE);
		mTopPressLayoutValue.setVisibility(GONE);
	}


	public void highLightPress(KLineBean entry) {
		mPriceLayout.setVisibility(GONE);
		mTopPressLayoutValue.setVisibility(VISIBLE);

		mTvCloseValue.setText(BigDecimalUtils.parseENum(entry.close));
		mTvOpenValue.setText(BigDecimalUtils.parseENum(entry.open));
		mTvLowValue.setText(BigDecimalUtils.parseENum(entry.low));
		mTvHighValue.setText(BigDecimalUtils.parseENum(entry.high));
		if (!isContract)
			mTvVolumeValue.setText(NumberUtils.floatToString(entry.vol));
		else {
			mTvVolumeValue.setText(contractType == Constants.CONTRACT_TYPE_USDT ?
					String.format("%s%s", NumberUtils.floatToString(entry.vol, 1), TradeUtils.getContractUsdtUnit(table)) :
					String.format("%s%s", NumberUtils.floatToString(entry.vol, 1), getResources().getString(R.string.number)));
		}


		//设置涨跌幅文字和颜色
		if ("0.00%".equals(entry.change)) {
			//百分比为0
			mTvChangeValue.setText(entry.change);
			mTvChangeValue.setTextColor(getResources().getColor(R.color.res_textColor_2));
		} else if (entry.change.contains("-")) {
			//百分比为负数,跌
			mTvChangeValue.setText(entry.change);
			mTvChangeValue.setTextColor(isRedRise ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		} else {
			//百分比为正数,涨
			mTvChangeValue.setText("+" + entry.change);
			mTvChangeValue.setTextColor(isRedRise ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		}

		mTvTimeValue.setText(TimeUtils.getYMDHMFromMillisecond(Long.valueOf(entry.time)));
	}


	public void setContractType(int contractType) {
		this.contractType = contractType;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setContractTable(ContractUsdtInfoTable table) {
		this.table = table;
	}

	public void setIsContract(boolean isContract) {
		this.isContract = isContract;
	}
}
