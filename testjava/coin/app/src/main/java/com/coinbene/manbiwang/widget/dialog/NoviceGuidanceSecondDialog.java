package com.coinbene.manbiwang.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.R;
import com.coinbene.common.router.UIBusService;
import com.coinbene.manbiwang.modules.common.parser.SchemaParser;

/**
 * 新手引导弹窗
 */
public class NoviceGuidanceSecondDialog extends Dialog implements View.OnClickListener {


	private View tvToTrade, ivClose;
	private TextView tvBuyTips2, tvBuyTips3;

	public NoviceGuidanceSecondDialog(@NonNull Context context) {
		super(context);
		init();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog_novice_guidance_second);
		initView();
		initListener();
	}

	private void initView() {
		tvToTrade = findViewById(R.id.tv_to_trade);
		ivClose = findViewById(R.id.iv_close);
		tvBuyTips2 = findViewById(R.id.tv_buy_digital_currency_tips2);
		tvBuyTips3 = findViewById(R.id.tv_buy_digital_currency_tips3);


		String format = String.format(getContext().getString(R.string.buy_digital_currency_tips2), getContext().getString(R.string.otc_trading));
		int i = format.indexOf(getContext().getString(R.string.otc_trading));
		SpannableString spannableString = new SpannableString(format);
		spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_blue)), i, getContext().getString(R.string.otc_trading).length() + i, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		tvBuyTips2.setText(spannableString);

		String format1 = String.format(getContext().getString(R.string.buy_digital_currency_tips3), getContext().getString(R.string.otc_trading), getContext().getString(R.string.spot_trading));
		int i1 = format1.indexOf(getContext().getString(R.string.otc_trading));
		int i2 = format1.indexOf(getContext().getString(R.string.spot_trading));
		SpannableString spannableString1 = new SpannableString(format1);
		spannableString1.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_blue)), i1, getContext().getString(R.string.otc_trading).length() + i1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spannableString1.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_blue)), i2, getContext().getString(R.string.spot_trading).length() + i2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		tvBuyTips3.setText(spannableString1);


	}

	public void initListener() {
		tvToTrade.setOnClickListener(this);
		ivClose.setOnClickListener(this);
	}


	/**
	 * 初始化
	 */
	private void init() {
		// 在构造方法里, 传入主题
		Window window = getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		window.setBackgroundDrawable(new BitmapDrawable());
		// 获取Window的LayoutParams
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
		attributes.gravity = Gravity.CENTER;
		// 一定要重新设置, 才能生效
		window.setAttributes(attributes);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_to_trade) {
			dismiss();
			UIBusService.getInstance().openUri(getContext(), "coinbene://" + SchemaParser.HOST_OTC, null);
		} else if (id == R.id.iv_close) {
			dismiss();
		}

	}
}
