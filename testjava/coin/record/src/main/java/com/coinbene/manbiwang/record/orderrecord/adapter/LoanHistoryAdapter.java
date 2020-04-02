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
public class LoanHistoryAdapter extends BaseQuickAdapter<CurrentLoanModel.DataBean.ListBean, BaseViewHolder> {
	public LoanHistoryAdapter() {
		super(R.layout.record_item_loan_history);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CurrentLoanModel.DataBean.ListBean item) {
		Long time = Long.valueOf(item.getCreateTime());
		helper.setText(R.id.tv_currency, item.getAsset());
		helper.setText(R.id.tv_time, TimeUtils.getDateTimeFromMillisecond(time));
		helper.setText(R.id.tv_loan_amount, item.getBorrowQuantity());
		helper.setText(R.id.tv_Loan_Interest, item.getInterest());
		helper.setText(R.id.tv_loan_id, item.getOrderId());
	}
}
