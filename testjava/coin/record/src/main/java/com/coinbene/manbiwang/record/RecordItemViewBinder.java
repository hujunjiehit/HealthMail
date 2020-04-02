package com.coinbene.manbiwang.record;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.record.coinrecord.TransferRecordActivity;
import com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity;
import com.coinbene.manbiwang.record.contractrecord.ContractRecordActivity;
import com.coinbene.manbiwang.record.miningrecord.MiningRecordActivity;
import com.coinbene.manbiwang.record.optionrecord.OptionRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.CurrentEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.HighLeverEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.HistoryEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.LoanCurrentActivity;
import com.coinbene.manbiwang.record.orderrecord.LoanHistoryActivity;
import com.coinbene.manbiwang.service.record.RecordItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-09-20
 */
public class RecordItemViewBinder extends ItemViewBinder<RecordItem, RecordItemViewBinder.ViewHolder> {


	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.record_item_record_item, parent, false);
		return new ViewHolder(root);
	}

	public RecordItemViewBinder() {
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull RecordItem recordItem) {
		holder.mTvItemName.setText(recordItem.getRecordName());

		holder.itemView.setOnClickListener(v -> {
			switch (recordItem.getRecordType()) {
				case RECHARGE:
					//充值
					WithDrawRechargeHisActivity.startMe(v.getContext(), Constants.CODE_RECORD_RECHARGE_TYPE);
					break;
				case WITHDRAW:
					//提现
					WithDrawRechargeHisActivity.startMe(v.getContext(), Constants.CODE_RECORD_WITHDRAW_TYPE);
					break;
				case PLATFORM_TRANSFER:
					//平台内转账
					WithDrawRechargeHisActivity.startMe(v.getContext(), Constants.CODE_RECORD_TRANSFER_TYPE);
					break;
				case TRANSFER:
					//划转记录
					TransferRecordActivity.startMe(v.getContext());
					break;
				case OTHER:
					//其它记录
					WithDrawRechargeHisActivity.startMe(v.getContext(), Constants.CODE_RECORD_DISPATCH_TYPE);
					break;
				case SPOT_CURRENT_ORDER:
					//币币当前委托
					CurrentEntrustRecordActivity.startMe(v.getContext());
					break;
				case SPOT_HISTORY_ORDER:
					//币币历史委托
					HistoryEntrustRecordActivity.startMe(v.getContext());
					break;
				case SPOT_HIGH_LEVER_ORDER:
					//币币高级委托
					HighLeverEntrustRecordActivity.startMe(v.getContext());
					break;
				case MARGIN_CURRENT_ORDER:
					//杠杆当前委托
					CurrentEntrustRecordActivity.startMe(v.getContext(), "margin");
					break;
				case MARGIN_HISTORY_ORDER:
					//杠杆历史委托
					HistoryEntrustRecordActivity.startMe(v.getContext(), "margin");
					break;
				case MARGIN_CURRENT_BORROW:
					//杠杆当前借币
					LoanCurrentActivity.startActivity(v.getContext());
					break;
				case MARGIN_HISTORY_BORROM:
					//杠杆历史借币
					LoanHistoryActivity.startActivity(v.getContext());
					break;
				case USDT_CONTRACT:
					//usdt合约记录
					ContractRecordActivity.startMe(v.getContext(), Constants.CONTRACT_TYPE_USDT);
					break;
				case BTC_CONTRACT:
					//BTC合约记录
					ContractRecordActivity.startMe(v.getContext(), Constants.CONTRACT_TYPE_BTC);
					break;
				case MINING:
					//挖矿明细
					MiningRecordActivity.startMe(v.getContext());
					break;
				case OPTIONS:
					//猜涨跌记录
					OptionRecordActivity.startMe(v.getContext());
					break;
			}
		});
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_item_name)
		TextView mTvItemName;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
