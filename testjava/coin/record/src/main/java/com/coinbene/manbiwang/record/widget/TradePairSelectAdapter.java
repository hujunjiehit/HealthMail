package com.coinbene.manbiwang.record.widget;

import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.SelectTradepairModel;
import com.coinbene.manbiwang.record.R;

/**
 * Created by june
 * on 2019-09-15
 */
public class TradePairSelectAdapter extends BaseQuickAdapter<SelectTradepairModel, BaseViewHolder> {

	public TradePairSelectAdapter() {
		super(R.layout.record_item_tradepair_select);
	}

	public TradePairSelectAdapter(int layoutResId) {
		super(layoutResId);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, SelectTradepairModel item) {
		RadioButton radioButton = helper.getView(R.id.rb_trade_pair);
		radioButton.setText(item.getTradePairName());
		radioButton.setChecked(item.isChecked());

		helper.addOnClickListener(R.id.rb_trade_pair);
	}
}
