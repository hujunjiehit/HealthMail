package com.coinbene.manbiwang.record.orderrecord.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.CurrentLoanModel;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.manbiwang.record.R;

/**
 * ding
 * 2019-08-15
 * com.coinbene.manbiwang.modules.balance.record.leverage.adapter
 */
public class LoanCurrentAdapter extends BaseQuickAdapter<CurrentLoanModel.DataBean.ListBean, BaseViewHolder> {
	public LoanCurrentAdapter() {
		super(R.layout.record_item_loan_current);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CurrentLoanModel.DataBean.ListBean item) {

		Long time = Long.valueOf(item.getCreateTime());
		helper.setText(R.id.tv_currency_category, item.getAsset());
		helper.setText(R.id.tv_time, TimeUtils.getDateTimeFromMillisecond(time));

		//未还利息数量
		helper.setText(R.id.tv_unpaid_interest, item.getUnRepayInterest());

		//借币数量
		helper.setText(R.id.tv_loan_amount, item.getBorrowQuantity());

		//未还本金数量
		helper.setText(R.id.tv_unpaid_quantity, item.getUnRepayQuantity());

		//剩余应还
		helper.setText(R.id.tv_over_repay, item.getActualQuantity());

		helper.setText(R.id.tv_loan_id, item.getOrderId());

	}
}
