package com.coinbene.common.activities.fragment.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.R;
import com.coinbene.common.database.TradePairInfoTable;

/**
 * ding
 * 2019-08-08
 * com.coinbene.common.activitys.adapters
 */
public class SelectPairAdapter extends BaseQuickAdapter<TradePairInfoTable, BaseViewHolder> {
	public SelectPairAdapter() {
		super(R.layout.common_item_select_pair);
	}

	@Override
	protected void convert(BaseViewHolder helper, TradePairInfoTable item) {

		if (item == null){
			return;
		}

		helper.setText(R.id.coin_name_tv, TextUtils.isEmpty(item.localBaseAsset) ? "" : item.localBaseAsset);
		helper.setText(R.id.coin_pair_tv, TextUtils.isEmpty(item.tradePairName) ? "" : item.tradePairName.toUpperCase());
	}
}
