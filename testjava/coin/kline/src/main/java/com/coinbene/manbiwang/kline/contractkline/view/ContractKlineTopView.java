package com.coinbene.manbiwang.kline.contractkline.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
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
 * on 2019-11-23
 */
public class ContractKlineTopView extends ConstraintLayout {

	@BindView(R2.id.tv_price_new)
	TextView mTvPriceNew;
	@BindView(R2.id.iv_rise_type)
	ImageView mIvRiseType;
	@BindView(R2.id.tv_rise_value)
	TextView mTvRiseValue;
	@BindView(R2.id.tv_percent_value)
	TextView mTvPercentValue;
	@BindView(R2.id.tv_volume24_key)
	TextView mTvVolume24Key;
	@BindView(R2.id.tv_volume24_value)
	TextView mTvVolume24Value;
	@BindView(R2.id.tv_fund_fee_key)
	TextView mTvFundFeeKey;
	@BindView(R2.id.tv_high_price_key)
	TextView mTvHighPriceKey;
	@BindView(R2.id.tv_low_price_key)
	TextView mTvLowPriceKey;
	@BindView(R2.id.tv_fund_fee_value)
	TextView mTvFundFeeValue;
	@BindView(R2.id.tv_high_price_value)
	TextView mTvHighPriceValue;
	@BindView(R2.id.tv_low_price_value)
	TextView mTvLowPriceValue;
	@BindView(R2.id.view_container)
	View mViewContainer;
	@BindView(R2.id.tv_open_value)
	TextView mTvOpenValue;
	@BindView(R2.id.tv_high_value)
	TextView mTvHighValue;
	@BindView(R2.id.tv_low_value)
	TextView mTvLowValue;
	@BindView(R2.id.tv_close_value)
	TextView mTvCloseValue;
	@BindView(R2.id.tv_change_value)
	TextView mTvChangeValue;
	@BindView(R2.id.tv_volume_value)
	TextView mTvVolumeValue;
	@BindView(R2.id.tv_time_value)
	TextView mTvTimeValue;
	@BindView(R2.id.group_value)
	Group mGroupValue;

	private Context mContext;
	private boolean isRedRise;

	private int mRiseType = Constants.RISE_DEFAULT;
	private WsMarketData mQuote;
	private int contractType;
	private String symbol;
	private ContractUsdtInfoTable table;

	public ContractKlineTopView(Context context) {
		super(context);
		initView(context);
	}

	public ContractKlineTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ContractKlineTopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.kline_contract_top_view, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		isRedRise = SwitchUtils.isRedRise();
	}


	public void setRiseType(int riseType) {
		this.mRiseType = riseType;
		if (mQuote != null) {
			setData(mQuote);
		}
	}

	/**
	 * 设置顶部数据
	 *
	 * @param quote
	 */
	public void setData(WsMarketData quote) {
		this.mQuote = quote;
		this.mRiseType = quote.getRiseType();
		if (mRiseType == Constants.RISE_UP) {
			mIvRiseType.setVisibility(View.VISIBLE);
			mIvRiseType.setImageResource(isRedRise ? R.drawable.res_red_up : R.drawable.res_green_up);
			mTvPriceNew.setTextColor(isRedRise ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		} else if (mRiseType == Constants.RISE_DOWN) {
			mIvRiseType.setVisibility(View.VISIBLE);
			mIvRiseType.setImageResource(isRedRise ? R.drawable.res_green_down : R.drawable.res_red_down);
			mTvPriceNew.setTextColor(isRedRise ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		} else {
			mIvRiseType.setVisibility(View.GONE);
			mTvPriceNew.setTextColor(getResources().getColor(R.color.res_white));
		}
		mTvPriceNew.setText(TextUtils.isEmpty(quote.getLastPrice()) ? "0" : quote.getLastPrice());

		if (!TextUtils.isEmpty(quote.getChangeValue())) {
			float aFloat = Tools.parseFloat(quote.getChangeValue());
			if (aFloat > 0) {
				mTvPercentValue.setText(String.format("+%s", quote.getUpsAndDowns()));
				mTvRiseValue.setText(String.format("+%s", quote.getChangeValue()));
			} else {
				mTvPercentValue.setText(quote.getUpsAndDowns());
				mTvRiseValue.setText(quote.getChangeValue());
			}
			if (isRedRise) {
				if (aFloat > 0) {
					mTvPercentValue.setTextColor(getResources().getColor(R.color.res_red));
					mTvRiseValue.setTextColor(getResources().getColor(R.color.res_red));
				} else if (aFloat < 0) {
					mTvPercentValue.setTextColor(getResources().getColor(R.color.res_green));
					mTvRiseValue.setTextColor(getResources().getColor(R.color.res_green));
				} else {
					mTvPercentValue.setTextColor(getResources().getColor(R.color.Kline_White));
					mTvRiseValue.setTextColor(getResources().getColor(R.color.Kline_White));
				}
			} else {
				if (aFloat > 0) {
					mTvPercentValue.setTextColor(getResources().getColor(R.color.res_green));
					mTvRiseValue.setTextColor(getResources().getColor(R.color.res_green));
				} else if (aFloat < 0) {
					mTvPercentValue.setTextColor(getResources().getColor(R.color.res_red));
					mTvRiseValue.setTextColor(getResources().getColor(R.color.res_red));
				} else {
					mTvPercentValue.setTextColor(getResources().getColor(R.color.Kline_White));
					mTvRiseValue.setTextColor(getResources().getColor(R.color.Kline_White));
				}
			}
		} else {
			mTvRiseValue.setText("0");//涨跌量
			mTvPercentValue.setText("0.00%");
		}

		String volumeRes = TextUtils.isEmpty(quote.getVolume24h()) ? "0" : quote.getVolume24h();
		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			String vol = String.format("%s %s", PrecisionUtils.getVolumeEn(TradeUtils.getContractUsdtUnitValue(volumeRes, table), Constants.PRECISION), TradeUtils.getContractUsdtUnit(table));
			mTvVolume24Value.setText(vol);
		} else {
			String vol = String.format(getResources().getString(R.string.future_24vol_unit_label), PrecisionUtils.getVolumeEn(volumeRes, Constants.PRECISION));
			mTvVolume24Value.setText(vol);
		}


		mTvHighPriceValue.setText(TextUtils.isEmpty(quote.getHigh24h()) ? "0" : quote.getHigh24h());
		mTvLowPriceValue.setText(TextUtils.isEmpty(quote.getLow24h()) ? "0" : quote.getLow24h());
		//mTvMarkPriceValue.setText(TextUtils.isEmpty(quote.getMarkPrice()) ? "0" : quote.getMarkPrice());
		String f8Str = TextUtils.isEmpty(quote.getFundingRate()) ? "0" : quote.getFundingRate();
		mTvFundFeeValue.setText(BigDecimalUtils.toPercentage(f8Str, "0.0000%"));
	}

	public void highLightPress(KLineBean entry) {
		mGroupValue.setVisibility(VISIBLE);

		setSpannableText(mTvOpenValue, getResources().getString(R.string.k_chart_open), BigDecimalUtils.parseENum(entry.open));
		setSpannableText(mTvHighValue, getResources().getString(R.string.k_chart_high), BigDecimalUtils.parseENum(entry.high));
		setSpannableText(mTvLowValue, getResources().getString(R.string.k_chart_low), BigDecimalUtils.parseENum(entry.low));
		setSpannableText(mTvCloseValue, getResources().getString(R.string.k_chart_close), BigDecimalUtils.parseENum(entry.close));
		String vol;
		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			vol = String.format("%s %s", BigDecimalUtils.floatToString(entry.vol, 2), TradeUtils.getContractUsdtUnit(table));
		} else {
			vol = String.format("%s %s", BigDecimalUtils.floatToString(entry.vol, 2), getResources().getString(R.string.number));
		}
		setSpannableText(mTvVolumeValue, getResources().getString(R.string.k_chart_vol), vol);

		//设置涨跌幅文字和颜色
		SpannableString spannableString;
		String key, value;
		key = getResources().getString(R.string.k_chart_change);
		if ("0.00%".equals(entry.change)) {
			//百分比为0
			value = entry.change;
			spannableString = new SpannableString(key + " " + value);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0, key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_white)), key.length() + 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

		} else if (entry.change.contains("-")) {
			//百分比为负数,跌
			value = entry.change;
			spannableString = new SpannableString(key + " " + value);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0, key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(isRedRise ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red)),
					key.length() + 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		} else {
			//百分比为正数,涨
			value = "+" + entry.change;
			spannableString = new SpannableString(key + " " + value);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0, key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(isRedRise ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green)),
					key.length() + 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		mTvChangeValue.setText(spannableString);

		mTvTimeValue.setText(TimeUtils.getYMDHMFromMillisecond(Long.valueOf(entry.time)));
	}

	private void setSpannableText(TextView textView, String key, String value) {
		SpannableString spannableString = new SpannableString(key + " " + value);
		spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0, key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_white)), key.length() + 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		textView.setText(spannableString);
	}

	public void hideHighValueSelectedValue() {
		mGroupValue.setVisibility(GONE);
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
}
