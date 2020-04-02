package com.coinbene.manbiwang.contract.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class UsdtOrderDetailItemBinder extends ItemViewBinder<WsTradeList, UsdtOrderDetailItemBinder.ViewHolder> {


	private boolean isRedRise;
	private int redColor, greenColor;
	private String symbol;


	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.contrack_trade_deal_item, parent, false);
		isRedRise = SwitchUtils.isRedRise();
		redColor = parent.getResources().getColor(R.color.res_red);
		greenColor = parent.getResources().getColor(R.color.res_green);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull WsTradeList item) {

		try {
			holder.tv_time.setText(TextUtils.isEmpty(TimeUtils.transferFormatMMSS(item.getTime())) ? "--" : TimeUtils.transferFormatMMSS(item.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		holder.tv_price.setText(TextUtils.isEmpty(item.getPrice()) ? "--" : item.getPrice());
		holder.tv_num.setText(TextUtils.isEmpty(item.getAmount()) ? "--" : PrecisionUtils.getQuantityContractRule(TradeUtils.getContractUsdtUnitValue(item.getAmount(),
				ContractUsdtInfoController.getInstance().queryContrackByName(symbol))));
		if (!TextUtils.isEmpty(item.getType())) {
			if (item.getType().equals("buy")) {//中文：买入字体红色，卖出字体绿色  英文：买入字体绿色，卖出字体红色
				holder.tv_price.setTextColor(isRedRise ? redColor : greenColor);
			} else {
				holder.tv_price.setTextColor(isRedRise ? greenColor : redColor);
			}
		}


	}

	public void setSymbol(String contractName) {
		this.symbol = contractName;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_time)
		TextView tv_time;
		@BindView(R2.id.tv_price)
		TextView tv_price;
		@BindView(R2.id.tv_num)
		TextView tv_num;
//        @BindView(R.id.tv_direction)
//        TextView tv_direction;


		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}