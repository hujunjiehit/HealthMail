package com.coinbene.manbiwang.record.contractrecord.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.coinbene.manbiwang.record.R;


/**
 * Created by june
 * on 2019-09-09
 */
public class CurrentOrderUsdtAdapter extends BaseQuickAdapter<CurrentDelegationModel.DataBean.ListBean, BaseViewHolder> {

	private String symbleStr;
	private String longStr, shortStr, closeLongStr, closeShortStr, entrustUnitCoin, dealUnitCoin;
	private boolean isRedRise;
	private int greenDrawable, redDrawable;

	public CurrentOrderUsdtAdapter() {
		super(R.layout.record_item_contract_current_order);
	}

	@Override
	protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
		symbleStr = parent.getResources().getString(R.string.forever_no_delivery);
		longStr = parent.getResources().getString(R.string.open_long_v2);
		shortStr = parent.getResources().getString(R.string.open_short_v2);
		closeLongStr = parent.getResources().getString(R.string.side_close_long);
		closeShortStr = parent.getResources().getString(R.string.side_close_short);
		greenDrawable = R.drawable.bg_green_sharp;
		redDrawable = R.drawable.bg_red_sharp;
		entrustUnitCoin = parent.getResources().getString(R.string.entrust_volume_unit_coin);
		dealUnitCoin = parent.getResources().getString(R.string.deal_unit_coin);

		return super.onCreateDefViewHolder(parent, viewType);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CurrentDelegationModel.DataBean.ListBean item) {
		isRedRise = SwitchUtils.isRedRise();

		ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(item.getSymbol());
		if(table==null){
			table = new ContractUsdtInfoTable();
		}
		helper.setText(R.id.textView2, String.format(entrustUnitCoin, TradeUtils.getContractUsdtUnit(table)));
		helper.setText(R.id.textView5, String.format(dealUnitCoin, TradeUtils.getContractUsdtUnit(table)));


		//合约类型
		helper.setText(R.id.current_delegation_contracts, String.format(symbleStr,table.baseAsset));

		//时间
		helper.setText(R.id.tv_type, TimeUtils.getMonthDayHMSFromMillisecond(item.getOrderTime()));

		//委托量
		helper.setText(R.id.current_delegation_vol, TradeUtils.getContractUsdtUnitValue(item.getQuantity(), table));

		//委托价
		helper.setText(R.id.current_delegation_price, item.getOrderPrice());

		//成交量
		helper.setText(R.id.current_clinch_vol, TradeUtils.getContractUsdtUnitValue(item.getFilledQuantity(), table));

		//成交价
		helper.setText(R.id.current_clinch_price, item.getAveragePrice());

		//成交比例
		helper.setText(R.id.current_clinch_scale, BigDecimalUtils.divideToPercentage(item.getFilledQuantity(), item.getQuantity()));

		//类型
		setOrderType(helper, item.getOrderType());

		//杠杆标签
		if (item.getDirection().equals("openLong")) {//开多
			helper.setText(R.id.current_delegation_tag, String.format(longStr, item.getLeverage()));
			helper.setBackgroundRes(R.id.current_delegation_tag, isRedRise ? redDrawable : greenDrawable);
		} else if (item.getDirection().equals("openShort")) {//开空
			helper.setText(R.id.current_delegation_tag, String.format(shortStr, item.getLeverage()));
			helper.setBackgroundRes(R.id.current_delegation_tag, isRedRise ? greenDrawable : redDrawable);
		} else if (item.getDirection().equals("closeLong")) {//平多
			helper.setText(R.id.current_delegation_tag, closeLongStr);
			helper.setBackgroundRes(R.id.current_delegation_tag, isRedRise ? greenDrawable : redDrawable);
		} else if (item.getDirection().equals("closeShort")) {//平空
			helper.setText(R.id.current_delegation_tag, closeShortStr);
			helper.setBackgroundRes(R.id.current_delegation_tag, isRedRise ? redDrawable : greenDrawable);
		}


		//撤单设置点击
//		helper.getView(R.id.current_delegation_cancel).setOnClickListener(v -> {
//			if (listener != null) {
//				listener.cancelOrder(item.getOrderId(), position);
//			}
//		});

		helper.addOnClickListener(R.id.current_delegation_cancel);
	}

	/**
	 * @param holder
	 * @param type   设置类型
	 */
	private void setOrderType(BaseViewHolder holder, String type) {
		if (Constants.TYPE_LIMIT.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.fixed_price);
		} else if (Constants.TYPE_MARKET.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.market_price);
		} else if (Constants.TYPE_LIQUIDATION.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.delegation_type_liquidation);
		} else if (Constants.TYPE_PLAN_LIMIT.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.delegation_plan);
		} else if (Constants.TYPE_ADL.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.delegation_type_adi);
		} else if (Constants.TYPE_POST_ONLY.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.only_marker);
		} else if (Constants.TYPE_FOK.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.all_deal_or_cancel);
		} else if (Constants.TYPE_IOC.equals(type)) {
			holder.setText(R.id.current_delegation_type, R.string.deal_cancel_surplus);
		} else {
			holder.setText(R.id.current_delegation_type, "--");
		}
	}
}
