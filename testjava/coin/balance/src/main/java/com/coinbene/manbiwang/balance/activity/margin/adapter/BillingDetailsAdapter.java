package com.coinbene.manbiwang.balance.activity.margin.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.BillingDetailsModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.manbiwang.balance.R;

import java.util.HashMap;
import java.util.Map;

/**
 * ding
 * 2019-08-19
 * com.coinbene.manbiwang.modules.balance.record.leverage.adapter
 */
public class BillingDetailsAdapter extends BaseQuickAdapter<BillingDetailsModel.DataBean.ListBean, BaseViewHolder> {

	private Map<String, String> typeMap;
	private boolean redRise;

	public BillingDetailsAdapter() {
		super(R.layout.item_billing_detail);
	}


	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		redRise = SwitchUtils.isRedRise();

		if (typeMap == null) {
			initType(parent.getContext());
		}

		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, BillingDetailsModel.DataBean.ListBean item) {
		String time = item.getCreateTime();
		helper.setText(R.id.tv_over, item.getBalance());
		helper.setText(R.id.tv_currency, item.getAsset());
		helper.setText(R.id.tv_amount, item.getChange());
		helper.setText(R.id.tv_currency_pair, item.getSymbol());
		helper.setText(R.id.tv_detail_time, TimeUtils.getDateTimeFromMillisecond(Long.valueOf(time)));

		//数量为负数
		if (BigDecimalUtils.isLessThan(item.getChange(), "0")) {
			helper.setTextColor(R.id.tv_amount, helper.itemView.getResources().getColor(R.color.res_red));
		}

		//数量为正数
		if (BigDecimalUtils.isLessThan("0", item.getChange())) {
			helper.setTextColor(R.id.tv_amount, helper.itemView.getResources().getColor(R.color.res_green));
		}

		if (BigDecimalUtils.isEmptyOrZero(item.getChange())) {
			helper.setTextColor(R.id.tv_amount, helper.itemView.getResources().getColor(R.color.res_textColor_1));
		}

		if (typeMap == null || typeMap.size() == 0) {
			helper.setText(R.id.tv_category, "");
			return;
		}

		for (String key : typeMap.keySet()) {
			if (key.equals(item.getBizType())) {
				helper.setText(R.id.tv_category, typeMap.get(key));
			}
		}
	}

	private void initType(Context context) {
		typeMap = new HashMap<>();
		typeMap.put("3", context.getResources().getString(R.string.res_Loan));
		typeMap.put("4", context.getResources().getString(R.string.res_repay));
		typeMap.put("5", context.getResources().getString(R.string.res_interest_incurred));
		typeMap.put("21", context.getResources().getString(R.string.res_liquidation_fee));
		typeMap.put("9", context.getResources().getString(R.string.res_margin_buy));
		typeMap.put("12", context.getResources().getString(R.string.res_margin_sell));
		typeMap.put("32", context.getResources().getString(R.string.res_transferred_from_Spot_account));
		typeMap.put("33", context.getResources().getString(R.string.res_transfer_to_Spot_account));
		typeMap.put("22", context.getResources().getString(R.string.res_wear_warehouse));
	}
}
