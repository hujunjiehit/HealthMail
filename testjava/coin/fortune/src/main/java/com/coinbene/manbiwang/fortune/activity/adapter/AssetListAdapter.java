package com.coinbene.manbiwang.fortune.activity.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.fortune.R;
import com.coinbene.manbiwang.model.http.YbbAssetConfigListModel;

/**
 * Created by june
 * on 2019-10-17
 */
public class AssetListAdapter extends BaseQuickAdapter<YbbAssetConfigListModel.DataBean, BaseViewHolder> {

	//是否显示昨日分配和累计分配
	boolean showDividendInfo;

	public AssetListAdapter() {
		super(R.layout.fortune_item_asset_list);
		showDividendInfo = SpUtil.getFortuneSwitch() > 1;
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, YbbAssetConfigListModel.DataBean item) {
		helper.setText(R.id.tv_asset_name, item.getAsset());

		helper.setText(R.id.tv_seven_day_annual, TradeUtils.getLastSevenDayAnnual(item.getLastSevenDayAnnual()));

		if (showDividendInfo) {
			//昨日分配
			helper.setGone(R.id.tv_yesterday_dividend, true);
			helper.setText(R.id.tv_yesterday_dividend,
					String.format("%s: %s", mContext.getString(R.string.res_yestoday_dividend), item.getYesterdayDividend()));

			helper.setGone(R.id.tv_history_dividend, true);
			//累计分配
			helper.setText(R.id.tv_history_dividend,
					String.format("%s: %s", mContext.getString(R.string.res_total_dividend), item.getHistoryDividend()));
		} else {
			helper.setGone(R.id.tv_history_dividend, false);
			helper.setGone(R.id.tv_yesterday_dividend, false);
		}

		if ("0".equals(item.getStatus())) {
			helper.setEnabled(R.id.btn_transfer_now, false);
			helper.setVisible(R.id.tv_status, true);
		} else {
			helper.setEnabled(R.id.btn_transfer_now, true);
			helper.setVisible(R.id.tv_status, false);
		}

		helper.addOnClickListener(R.id.btn_transfer_now);
	}
}
