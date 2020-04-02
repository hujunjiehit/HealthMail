package com.coinbene.manbiwang.fortune.activity.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.YbbAccountListModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.fortune.R;

/**
 * Created by june
 * on 2019-10-18
 */
public class YbbAccountListAdapter extends BaseQuickAdapter<YbbAccountListModel.DataBean.CurrentPreestimateBean.CurrentAccountListBean, BaseViewHolder> {

	boolean isRedRise;

	public YbbAccountListAdapter() {
		super(R.layout.fortune_account_list_item);
		isRedRise = SwitchUtils.isRedRise();
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, YbbAccountListModel.DataBean.CurrentPreestimateBean.CurrentAccountListBean item) {

		helper.setText(R.id.tv_asset_name, String.format("%s%s", item.getAsset(), mContext.getString(R.string.res_total_value)));
		helper.setText(R.id.tv_asset_value, item.getAvailableBalance());


		//昨日收益
		if (BigDecimalUtils.isEmptyOrZero(item.getYesterdayProfit())){
			helper.setText(R.id.tv_yesterday_interest, item.getYesterdayProfit());
			helper.setTextColor(R.id.tv_yesterday_interest, mContext.getResources().getColor(R.color.res_textColor_1));
		} else {
			helper.setText(R.id.tv_yesterday_interest, "+" + item.getYesterdayProfit());
			helper.setTextColor(R.id.tv_yesterday_interest,
					isRedRise ? mContext.getResources().getColor(R.color.res_red) : mContext.getResources().getColor(R.color.res_green));
		}

		//累计收益
		if (BigDecimalUtils.isEmptyOrZero(item.getTotalProfit())){
			helper.setText(R.id.tv_history_interest, item.getTotalProfit());
			helper.setTextColor(R.id.tv_history_interest, mContext.getResources().getColor(R.color.res_textColor_1));
		} else {
			helper.setText(R.id.tv_history_interest, "+" + item.getTotalProfit());
			helper.setTextColor(R.id.tv_history_interest,
					isRedRise ? mContext.getResources().getColor(R.color.res_red) : mContext.getResources().getColor(R.color.res_green));
		}

		//七日年化
		helper.setText(R.id.tv_seven_day_annual_key, String.format("%s(%%)", mContext.getString(R.string.res_seven_day_annual)));
		helper.setText(R.id.tv_seven_day_annual, TradeUtils.getLastSevenDayAnnual(item.getLastSevenDayAnnual()));

		helper.addOnClickListener(R.id.btn_transfer_in, R.id.btn_transfer_out);
	}
}
