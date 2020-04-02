package com.coinbene.manbiwang.record.serviceimpl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.manbiwang.record.coinrecord.TransferRecordActivity;
import com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity;
import com.coinbene.manbiwang.record.contractrecord.ContractRecordActivity;
import com.coinbene.manbiwang.record.contractrecord.fragment.CurrentOrderBtcFragment;
import com.coinbene.manbiwang.record.contractrecord.fragment.CurrentOrderUsdtFragment;
import com.coinbene.manbiwang.record.contractrecord.fragment.HistoryOrderBtcFragment;
import com.coinbene.manbiwang.record.contractrecord.fragment.HistoryOrderUsdtFragment;
import com.coinbene.manbiwang.record.miningrecord.MiningRecordActivity;
import com.coinbene.manbiwang.record.optionrecord.OptionRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.CurrentEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.HighLeverEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.HistoryEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.LoanCurrentActivity;
import com.coinbene.manbiwang.record.orderrecord.LoanHistoryActivity;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.record.RecordService;
import com.coinbene.manbiwang.service.record.RecordType;

/**
 * Created by june
 * on 2019-11-09
 */
@Route(path = RouteHub.Record.recordService)
public class RecordServiceImpl implements RecordService {

	@Override
	public void gotoRecord(Context context, RecordType recordType) {
		switch (recordType)  {
			case RECHARGE:
				//充值
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_RECHARGE_TYPE);
				break;
			case WITHDRAW:
				//提现
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_WITHDRAW_TYPE);
				break;
			case PLATFORM_TRANSFER:
				//平台内转账
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_TRANSFER_TYPE);
				break;
			case TRANSFER:
				//划转记录
				TransferRecordActivity.startMe(context);
				break;
			case OTHER:
				//其它记录
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_DISPATCH_TYPE);
				break;
			case SPOT_CURRENT_ORDER:
				//币币当前委托
				CurrentEntrustRecordActivity.startMe(context);
				break;
			case SPOT_HISTORY_ORDER:
				//币币历史委托
				HistoryEntrustRecordActivity.startMe(context);
				break;
			case SPOT_HIGH_LEVER_ORDER:
				//币币高级委托
				HighLeverEntrustRecordActivity.startMe(context);
				break;
			case MARGIN_CURRENT_ORDER:
				//杠杆当前委托
				CurrentEntrustRecordActivity.startMe(context, "margin");
				break;
			case MARGIN_HISTORY_ORDER:
				//杠杆历史委托
				HistoryEntrustRecordActivity.startMe(context, "margin");
				break;
			case MARGIN_CURRENT_BORROW:
				//杠杆当前借币
				LoanCurrentActivity.startActivity(context);
				break;
			case MARGIN_HISTORY_BORROM:
				//杠杆历史借币
				LoanHistoryActivity.startActivity(context);
				break;
			case USDT_CONTRACT:
				//usdt合约记录
				ContractRecordActivity.startMe(context, Constants.CONTRACT_TYPE_USDT);
				break;
			case BTC_CONTRACT:
				//BTC合约记录
				ContractRecordActivity.startMe(context, Constants.CONTRACT_TYPE_BTC);
				break;
			case MINING:
				//挖矿明细
				MiningRecordActivity.startMe(context);
				break;
			case OPTIONS:
				//猜涨跌记录
				OptionRecordActivity.startMe(context);
				break;
		}
	}

	@Override
	public Fragment getContractHistoryOrderFragment(int contractType) {
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			return HistoryOrderBtcFragment.newInstance();
		} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
			return HistoryOrderUsdtFragment.newInstance();
		}
		return new Fragment();
	}

	@Override
	public Fragment getContractCurrentOrderFragment(int contractType) {
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			return CurrentOrderBtcFragment.newInstance();
		} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
			return CurrentOrderUsdtFragment.newInstance();
		}
		return new Fragment();
	}




	@Override
	public void init(Context context) {

	}
}
