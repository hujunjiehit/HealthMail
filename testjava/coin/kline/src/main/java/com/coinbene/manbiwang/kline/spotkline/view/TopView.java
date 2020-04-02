package com.coinbene.manbiwang.kline.spotkline.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.NumberUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
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
public class TopView extends ConstraintLayout {

	@BindView(R2.id.tv_price_new)
	TextView mTvPriceNew;
	@BindView(R2.id.tv_price_local)
	TextView mTvPriceLocal;
	@BindView(R2.id.tv_rise_value)
	TextView mTvRiseValue;
	@BindView(R2.id.tv_percent_value)
	TextView mTvPercentValue;
	@BindView(R2.id.tv_24h_high_value)
	TextView mTv24hHighValue;
	@BindView(R2.id.tv_24h_low_value)
	TextView mTv24hLowValue;
	@BindView(R2.id.tv_24h_volume_value)
	TextView mTv24hVolumeValue;
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

	public TopView(Context context) {
		super(context);
		initView(context);
	}

	public TopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.kline_top_view, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		isRedRise = SwitchUtils.isRedRise();
	}

	/**
	 * 设置顶部数据
	 * @param tradePairName
	 * @param quote
	 */
	public void setData(String tradePairName, WsMarketData quote) {

		mTvPriceNew.setText(TextUtils.isEmpty(quote.getLastPrice()) ? "0" : quote.getLastPrice());

		String nl = quote.getLocalPrice();
		mTvPriceLocal.setText(new StringBuilder().append("≈").append(StringUtils.getCnyReplace(nl)).toString());


		//涨跌幅
		String upsAndDowns = TextUtils.isEmpty(quote.getUpsAndDowns()) ? "0.00%" : quote.getUpsAndDowns();
		if (upsAndDowns.equals("0.00%") || upsAndDowns.contains("-")) {
			mTvPercentValue.setText(upsAndDowns);
		} else {
			mTvPercentValue.setText(String.format("+%s", upsAndDowns));
		}

		//涨跌值
		String changeValue = quote.getChangeValue();
		if (BigDecimalUtils.isGreaterThan(changeValue, "0")) {
			changeValue = "+" + changeValue;
		}
		mTvRiseValue.setText(changeValue);

		if (!TextUtils.isEmpty(changeValue)) {
			mTvPriceNew.setTextColor(getTextColor(changeValue));
			mTvRiseValue.setTextColor(getTextColor(changeValue));
			mTvPercentValue.setTextColor(getTextColor(changeValue));
		}

		String volume = PrecisionUtils.getVolumeEn(quote.getVolume24h(), Constants.PRECISION);
		String unitStr = "";
		if (tradePairName.contains("/")) {
			unitStr = tradePairName.split("/")[1];
		}
		mTv24hVolumeValue.setText(volume + " " + unitStr);

		//24小时最高价
		mTv24hHighValue.setText(TextUtils.isEmpty(quote.getHigh24h()) ? "--" : quote.getHigh24h());

		//24小时最低价
		mTv24hLowValue.setText(TextUtils.isEmpty(quote.getLow24h()) ? "--" : quote.getLow24h());

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

	public void highLightPress(KLineBean entry) {
		mGroupValue.setVisibility(VISIBLE);

		setSpannableText(mTvOpenValue, getResources().getString(R.string.k_chart_open), BigDecimalUtils.parseENum(entry.open));
		setSpannableText(mTvHighValue, getResources().getString(R.string.k_chart_high), BigDecimalUtils.parseENum(entry.high));
		setSpannableText(mTvLowValue, getResources().getString(R.string.k_chart_low), BigDecimalUtils.parseENum(entry.low));
		setSpannableText(mTvCloseValue, getResources().getString(R.string.k_chart_close), BigDecimalUtils.parseENum(entry.close));
		setSpannableText(mTvVolumeValue, getResources().getString(R.string.k_chart_vol), NumberUtils.floatToString(entry.vol,1));

		//设置涨跌幅文字和颜色
		SpannableString spannableString;
		String key, value;
		key = getResources().getString(R.string.k_chart_change);
		if ("0.00%".equals(entry.change)) {
			//百分比为0
			value = entry.change;
			spannableString = new SpannableString(key + " " + value);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0 ,key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_white)), key.length() + 1 ,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

		} else if (entry.change.contains("-")) {
			//百分比为负数,跌
			value = entry.change;
			spannableString = new SpannableString(key + " " + value);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0 ,key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(isRedRise ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red)),
					key.length() + 1 ,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		} else {
			//百分比为正数,涨
			value = "+" + entry.change;
			spannableString = new SpannableString(key + " " + value);
			spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0 ,key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(isRedRise ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green)),
					key.length() + 1 ,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		mTvChangeValue.setText(spannableString);

		mTvTimeValue.setText(TimeUtils.getYMDHMFromMillisecond(Long.valueOf(entry.time)));
	}

	private void setSpannableText(TextView textView, String key, String value) {
		SpannableString spannableString = new SpannableString(key + " " + value);
		spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_grey)), 0 ,key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kline_text_color_white)), key.length() + 1 ,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		textView.setText(spannableString);
	}

	public void hideHighValueSelectedValue() {
		mGroupValue.setVisibility(GONE);
	}
}
