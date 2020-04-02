package com.coinbene.manbiwang.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.home.R;

/**
 * ding
 * 2019-12-26
 * com.coinbene.manbiwang.home.adapter
 */
public class TransformerAdapter extends BaseQuickAdapter<WsMarketData, BaseViewHolder> {
	private boolean isRedUp;
	private  Context context;


	public TransformerAdapter(Context context) {
		super(R.layout.item_big_coin);
		this.context =context;
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, WsMarketData item) {
		isRedUp = SwitchUtils.isRedRise();

		((TextView)helper.getView(R.id.tv_Name)).setTypeface(ResourcesCompat.getFont(CBRepository.getContext(), R.font.roboto_medium));
		((TextView)helper.getView(R.id.tv_LastPrice)).setTypeface(ResourcesCompat.getFont(CBRepository.getContext(), R.font.roboto_medium));
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
			if (item.getUpsAndDowns().contains("-")) {
				helper.setTextColor(R.id.tv_QuoteChange, ResourceProvider.getColor(context,R.color.res_green));
				helper.setTextColor(R.id.tv_LastPrice, ResourceProvider.getColor(context,R.color.res_green));
			} else {
				helper.setTextColor(R.id.tv_QuoteChange, ResourceProvider.getColor(context,R.color.res_red));
				helper.setTextColor(R.id.tv_LastPrice, ResourceProvider.getColor(context,R.color.res_red));
			}
		} else {
			if (item.getUpsAndDowns().contains("-")) {
				helper.setTextColor(R.id.tv_QuoteChange, ResourceProvider.getColor(context,R.color.res_red));
				helper.setTextColor(R.id.tv_LastPrice, ResourceProvider.getColor(context,R.color.res_red));
			} else {
				helper.setTextColor(R.id.tv_QuoteChange, ResourceProvider.getColor(context,R.color.res_green));
				helper.setTextColor(R.id.tv_LastPrice, ResourceProvider.getColor(context,R.color.res_green));
			}
		}
	}
}
