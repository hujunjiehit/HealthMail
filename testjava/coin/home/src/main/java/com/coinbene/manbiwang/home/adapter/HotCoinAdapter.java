package com.coinbene.manbiwang.home.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.home.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

/**
 * ding
 * 2020-01-06
 * com.coinbene.manbiwang.home.adapter
 */
public class HotCoinAdapter extends BaseQuickAdapter<WsMarketData, BaseViewHolder> {
	private Drawable greenDrawable;
	private Drawable redDrawable;
	private boolean isRedUp;

	public HotCoinAdapter(Context context) {
		super(R.layout.item_hot_coin);
		int radius = QMUIDisplayHelper.dp2px(CBRepository.getContext(), 3);
		int red = ResourceProvider.getColor(context,R.color.res_red);
		int green = ResourceProvider.getColor(context,R.color.res_green);
		redDrawable = ResourceProvider.getRectShape(radius, red, red);
		greenDrawable = ResourceProvider.getRectShape(radius, green, green);
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, WsMarketData item) {
		isRedUp = SwitchUtils.isRedRise();

		helper.setText(R.id.tv_Name, item.getTradePairName());
		helper.setText(R.id.tv_LastPrice, item.getLastPrice());
		helper.setText(R.id.tv_Valuation, item.getLocalPrice());

		String upsAndDowns = TextUtils.isEmpty(item.getUpsAndDowns()) ? "0.00%" : item.getUpsAndDowns();
		if (upsAndDowns.equals("0.00%")) {
			helper.setText(R.id.tv_QuoteChange, item.getUpsAndDowns());
		} else if (upsAndDowns.contains("-")) {
			helper.setText(R.id.tv_QuoteChange, item.getUpsAndDowns());
		} else {
			helper.setText(R.id.tv_QuoteChange, "+" + item.getUpsAndDowns());
		}

		if (isRedUp) {
			helper.getView(R.id.tv_QuoteChange).setBackground(upsAndDowns.contains("-") ? greenDrawable : redDrawable);
		} else {
			helper.getView(R.id.tv_QuoteChange).setBackground(upsAndDowns.contains("-") ? redDrawable : greenDrawable);
		}

		helper.addOnClickListener(R.id.cl_root);
	}
}
