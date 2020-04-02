package com.coinbene.manbiwang.contract.newcontract.bottom.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.model.http.ContractPlanModel;

/**
 * @author by june
 * <p>
 * 止盈止损 adapter
 */
public class ContractPlanAdapter extends BaseQuickAdapter<ContractPlanModel.DataBean.ListBean, BaseViewHolder> {

	private String symbleStr;
	private boolean isRedRise;
	private int greenColor, redColor;
	private String targetProfit, stopLoss, fixedPrice, contractUnit, marketPrice;
	private OnCancelClickLisenter onCancelClickLisenter;
	private int redDrawable, greenDrawable;

	public ContractPlanAdapter() {
		super(R.layout.item_plan);
	}


	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		symbleStr = parent.getResources().getString(R.string.forever_no_delivery);
		greenColor = parent.getResources().getColor(R.color.res_green);
		redColor = parent.getResources().getColor(R.color.res_red);
		targetProfit = parent.getResources().getString(R.string.target_profit);
		stopLoss = parent.getResources().getString(R.string.stop_loss);
		fixedPrice = parent.getResources().getString(R.string.fixed_price);
		marketPrice = parent.getResources().getString(R.string.market_price);
		greenDrawable = R.drawable.bg_green_sharp;
		redDrawable = R.drawable.bg_red_sharp;
		contractUnit = String.format("%s/%s", parent.getResources().getString(R.string.touch_vol), parent.getResources().getString(R.string.entrust_volume));
		return super.onCreateViewHolder(parent, viewType);
	}


	public void setRedRaise(boolean isRedRise) {
		this.isRedRise = isRedRise;
	}


	@Override
	protected void convert(@NonNull BaseViewHolder helper, ContractPlanModel.DataBean.ListBean item) {
		isRedRise = SwitchUtils.isRedRise();
		if (TradeUtils.isUsdtContract(item.getSymbol())) {
			//usdt合约
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(item.getSymbol());
			if (table == null) {
				table = new ContractUsdtInfoTable();
			}

			helper.setText(R.id.textView4,
					String.format("%s(%s)",
							contractUnit,
							TradeUtils.getContractUsdtUnit(table)));

			//合约类型
			helper.setText(R.id.current_delegation_contracts, String.format(symbleStr, table.baseAsset));

			//数量
			helper.setText(R.id.current_delegation_type, item.getStatus().equals("trigger") ?
					String.format("%s/%s", item.getTriggerQuantity(), TradeUtils.getContractUsdtUnitValue(item.getQuantity(), table)) :
					String.format("%s/%s", "0", TradeUtils.getContractUsdtUnitValue(item.getQuantity(), table)));

		} else {
			//btc合约
			helper.setText(R.id.textView4,
					String.format("%s(%s)",
							contractUnit,
							mContext.getString(R.string.number)));

			//合约类型
			helper.setText(R.id.current_delegation_contracts, String.format(symbleStr, item.getSymbol()));

			//数量
			helper.setText(R.id.current_delegation_type, item.getStatus().equals("trigger") ? String.format("%s/%s", item.getTriggerQuantity(), item.getQuantity()) :
					String.format("%s/%s", "0", item.getQuantity()));
		}


		helper.setText(R.id.tv_type, item.getPlanType().equals("takeProfit") ? targetProfit : stopLoss);
		helper.setText(R.id.current_delegation_vol, item.getTriggerPrice());
		helper.setText(R.id.tv_setting_time, TimeUtils.getMonthDayHMSFromMillisecond(item.getCreateTime()));

		if (Constants.TYPE_PLAN_LIMIT.equals(item.getOrderType())) {
			helper.setText(R.id.current_clinch_vol, fixedPrice);
			helper.setText(R.id.current_delegation_price, item.getOrderPrice());
		} else if (Constants.TYPE_PLAN_MARKET.equals(item.getOrderType())) {
			helper.setText(R.id.current_clinch_vol, marketPrice);
			helper.setText(R.id.current_delegation_price, "--");
		} else {
			helper.setText(R.id.current_clinch_vol, "--");
			helper.setText(R.id.current_delegation_price, "--");
		}


		if (item.getStatus().equals("waitTrigger")) {
			helper.setVisible(R.id.current_delegation_cancel, true);
			helper.setText(R.id.current_clinch_price, R.string.plan_status_not_touch);
			helper.setText(R.id.textView8, R.string.touch_time);
			helper.setText(R.id.tv_update_time, "--");
		} else if (item.getStatus().equals("trigger")) {
			helper.setVisible(R.id.current_delegation_cancel, false);
			helper.setText(R.id.current_clinch_price, R.string.touch_suc);
			helper.setText(R.id.textView8, mContext.getString(R.string.touch_time));
			helper.setText(R.id.tv_update_time, TimeUtils.getMonthDayHMSFromMillisecond(item.getUpdateTime()));

		} else if (item.getStatus().equals("canceled")) {
			helper.setVisible(R.id.current_delegation_cancel, false);
			helper.setText(R.id.current_clinch_price, R.string.hand_cancel);
			helper.setText(R.id.textView8, R.string.cancel_time);
			helper.setText(R.id.tv_update_time, TimeUtils.getMonthDayHMSFromMillisecond(item.getUpdateTime()));
		} else if (item.getStatus().equals("fail")) {
			helper.setVisible(R.id.current_delegation_cancel, false);
			helper.setText(R.id.current_clinch_price, mContext.getString(R.string.touch_fail));
			helper.setText(R.id.textView8, R.string.touch_time);
			helper.setText(R.id.tv_update_time, TimeUtils.getMonthDayHMSFromMillisecond(item.getUpdateTime()));
		} else if (item.getStatus().equals("canceledSys")) {
			helper.setVisible(R.id.current_delegation_cancel, false);
			helper.setText(R.id.current_clinch_price, mContext.getString(R.string.sys_cancel));
			helper.setText(R.id.textView8, R.string.touch_time);
			helper.setText(R.id.tv_update_time, TimeUtils.getMonthDayHMSFromMillisecond(item.getUpdateTime()));
		}

		if (item.getDirection().equals("closeLong")) {//平多
			helper.setText(R.id.current_delegation_tag, R.string.side_close_long);
			helper.setBackgroundRes(R.id.current_delegation_tag, isRedRise ? greenDrawable : redDrawable);
		} else {
			helper.setText(R.id.current_delegation_tag, R.string.side_close_short);
			helper.setBackgroundRes(R.id.current_delegation_tag, isRedRise ? redDrawable : greenDrawable);
		}

		helper.addOnClickListener(R.id.current_delegation_cancel);
	}


	public void setOnCancelClickLisenter(OnCancelClickLisenter onCancelClickLisenter) {
		this.onCancelClickLisenter = onCancelClickLisenter;
	}

	public interface OnCancelClickLisenter {
		void oncancel(String id);
	}

}
