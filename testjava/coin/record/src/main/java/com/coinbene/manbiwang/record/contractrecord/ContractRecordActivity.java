package com.coinbene.manbiwang.record.contractrecord;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-12
 * <p>
 * 合约记录列表页面
 */
public class ContractRecordActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.tv_current_order)
	TextView mTvCurrentOrder;
	@BindView(R2.id.tv_history_order)
	TextView mTvHistoryOrder;
	@BindView(R2.id.tv_fund_fee)
	TextView mTvFundFee;
	private int contractType;

	public static void startMe(Context context, int contractType) {
		Intent intent = new Intent(context, ContractRecordActivity.class);
		intent.putExtra("contractType", contractType);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_contract_record;
	}

	@Override
	public void initView() {
		if (getIntent() != null) {
			contractType = getIntent().getIntExtra("contractType", Constants.CONTRACT_TYPE_BTC);
		}
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			mTopBar.setTitle(R.string.res_btc_contract_record);
		} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
			mTopBar.setTitle(R.string.res_usdt_contract_record);
		}
	}

	@Override
	public void setListener() {
		mTvCurrentOrder.setOnClickListener(v -> {
			//当前委托
			if(contractType == Constants.CONTRACT_TYPE_BTC) {
				CurrentDelegationBtcActivity.startMe(this);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				CurrentDelegationUsdtActivity.startMe(this);
			}
		});

		mTvHistoryOrder.setOnClickListener(v -> {
			//历史委托
			if(contractType == Constants.CONTRACT_TYPE_BTC) {
				HistoryDelegationBtcActivity.startMe(this);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				HistoryDelegationUsdtActivity.startMe(this);
			}
		});

		mTvFundFee.setOnClickListener(v -> {
			//资金费用
			if(contractType == Constants.CONTRACT_TYPE_BTC) {
				FundCostBtcActivity.startMe(this);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				FundCostUsdtActivity.startMe(this);
			}
		});
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return true;
	}
}
