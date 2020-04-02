package com.coinbene.manbiwang.balance.products.contract.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 合约资产--->下面的每个项目
 *
 * @author mxd on 2019/3/18.
 */

public class BalanceContractItemBinder extends ItemViewBinder<ContractPositionListModel.DataBean, BalanceContractItemBinder.ViewHolder> {
	private String longStr, shortStr, number, holdUnit;
	private boolean isRedRise;
	private Drawable greenDrawable, redDrawable;
	private int redColor, greenColor, normalColor;
	private int contractType;
	private Context context;

	public void setContractType(int contractType) {
		this.contractType = contractType;

	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.balance_contract_item_position, parent, false);
		longStr = root.getContext().getResources().getString(R.string.side_long);
		shortStr = root.getContext().getResources().getString(R.string.side_short);
		greenDrawable = parent.getResources().getDrawable(R.drawable.bg_green_sharp);
		redDrawable = parent.getResources().getDrawable(R.drawable.bg_red_sharp);
		redColor = parent.getResources().getColor(R.color.res_red);
		greenColor = parent.getResources().getColor(R.color.res_green);

		normalColor = parent.getResources().getColor(R.color.res_textColor_3);
		number = parent.getResources().getString(R.string.number);
		holdUnit = parent.getResources().getString(R.string.future_item_num);

		context = parent.getContext();

		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull ContractPositionListModel.DataBean balanceModel) {
		ContractPositionListModel.DataBean item = balanceModel;
		isRedRise = SpUtil.isRedRise();

		viewHolder.tv_contrack_name.setText(TextUtils.isEmpty(item.getSymbol()) ? "--" : item.getSymbol());//合约名字


		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(item.getSymbol());
			viewHolder.tvTotalHold.setText(String.format("%s(%s)", holdUnit, TradeUtils.getContractUsdtUnit(table)));
			viewHolder.tvTotalHoldValue.setText(TradeUtils.getContractUsdtUnitValue(item.getQuantity(), table));//持仓数量
		} else {
			viewHolder.tvTotalHold.setText(String.format("%s(%s)", holdUnit, number));
			viewHolder.tvTotalHoldValue.setText(item.getQuantity());//持仓数量
		}


		viewHolder.tvForcePriceValue.setText(TextUtils.isEmpty(item.getRealisedPnl()) ? "0" : item.getRealisedPnl());//已实现盈亏
		viewHolder.tvUnrealizedProfitLossValue.setText(BigDecimalUtils.toPercentage(TextUtils.isEmpty(item.getRoe()) ? "0" : item.getRoe()));//回报率
		viewHolder.tv_not_win_value.setText(TextUtils.isEmpty(item.getUnrealisedPnl()) ? "0" : item.getUnrealisedPnl());//未实现盈亏

		if (item.getSide().equals("long")) {//long 多，short空
			viewHolder.current_delegation_tag.setText(String.format(longStr, item.getLeverage()));
			viewHolder.current_delegation_tag.setBackgroundResource(isRedRise ? R.drawable.bg_red_sharp : R.drawable.bg_green_sharp);
//                    (isRedRise ? redDrawable : greenDrawable);
		} else if (item.getSide().equals("short")) {
			viewHolder.current_delegation_tag.setText(String.format(shortStr, item.getLeverage()));
			viewHolder.current_delegation_tag.setBackgroundResource(isRedRise ? R.drawable.bg_green_sharp : R.drawable.bg_red_sharp);
//            viewHolder.current_delegation_tag.setBackground(isRedRise ? greenDrawable : redDrawable);
		}

		String pnlStr = TextUtils.isEmpty(item.getRealisedPnl()) ? "0" : item.getRealisedPnl();
		float pnFloat = Float.valueOf(pnlStr);

		if (isRedRise) {
			viewHolder.tvForcePriceValue.setTextColor(pnFloat >= 0 ? redColor : greenColor);
		} else {
			viewHolder.tvForcePriceValue.setTextColor(pnFloat >= 0 ? greenColor : redColor);
		}

		String roeStr = TextUtils.isEmpty(item.getRoe()) ? "0" : item.getRoe();
		float roeFloat = Float.valueOf(roeStr);
		if (isRedRise) {
			viewHolder.tvUnrealizedProfitLossValue.setTextColor(roeFloat >= 0 ? redColor : greenColor);
		} else {
			viewHolder.tvUnrealizedProfitLossValue.setTextColor(roeFloat >= 0 ? greenColor : redColor);
		}


		String unrealStr = TextUtils.isEmpty(item.getUnrealisedPnl()) ? "0" : item.getUnrealisedPnl();
		float unRealFloat = Float.valueOf(unrealStr);
		if (isRedRise) {
			viewHolder.tv_not_win_value.setTextColor(unRealFloat >= 0 ? redColor : greenColor);
		} else {
			viewHolder.tv_not_win_value.setTextColor(unRealFloat >= 0 ? greenColor : redColor);
		}

		viewHolder.tvForcePrice.setText(String.format("%s(%s)", context.getResources().getString(R.string.realized_profit_loss),
				contractType == Constants.CONTRACT_TYPE_BTC ? "BTC" : "USDT"));

		viewHolder.tvNotWin.setText(String.format("%s(%s)", context.getResources().getString(R.string.unrealized_profit_loss),
				contractType == Constants.CONTRACT_TYPE_BTC ? "BTC" : "USDT"));
	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_contrack_name)
		TextView tv_contrack_name;
		@BindView(R2.id.current_delegation_tag)
		TextView current_delegation_tag;
		@BindView(R2.id.tv_total_hold)
		TextView tvTotalHold;

		@BindView(R2.id.tv_total_hold_value)
		TextView tvTotalHoldValue;
		@BindView(R2.id.tv_force_price_value)
		TextView tvForcePriceValue;
		@BindView(R2.id.tv_unrealized_profit_loss_value)
		TextView tvUnrealizedProfitLossValue;
		@BindView(R2.id.tv_not_win_value)
		TextView tv_not_win_value;

		@BindView(R2.id.tv_force_price)
		TextView tvForcePrice;
		@BindView(R2.id.tv_not_win)
		TextView tvNotWin;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
