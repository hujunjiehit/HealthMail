package com.coinbene.manbiwang.kline.fragment.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class DealItemViewBinder extends ItemViewBinder<WsTradeList, DealItemViewBinder.ViewHolder> {


	private boolean isRedRise;
	private boolean isFuture;
	private int contractType = -1;
	private ContractUsdtInfoTable table;

	public DealItemViewBinder(boolean isRedRise) {
		this.isRedRise = isRedRise;
	}

	public void setFromFuture(boolean isFuture) {
		this.isFuture = isFuture;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.fr_deal_item, parent, false);
		return new ViewHolder(root, this);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull WsTradeList item) {
		try {
			holder.tv_time.setText(TextUtils.isEmpty(TimeUtils.transferToHHmmssFormatMMSS(item.getTime())) ? "--" : TimeUtils.transferToHHmmssFormatMMSS(item.getTime()));
		} catch (Exception e) {

			e.printStackTrace();
		}
		holder.tv_price.setText(TextUtils.isEmpty(item.getPrice()) ? "--" : item.getPrice());

		if (contractType == -1) {
			holder.tv_num.setText(TextUtils.isEmpty(item.getAmount()) ? "--" : PrecisionUtils.getCntNumContractEn(item.getAmount()));
		} else {
			if (contractType == Constants.CONTRACT_TYPE_USDT) {
				holder.tv_num.setText(TextUtils.isEmpty(item.getAmount()) ? "--" : PrecisionUtils.getQuantityContractRule(TradeUtils.getContractUsdtUnitValue(item.getAmount(), table)));
			} else {
				holder.tv_num.setText(TextUtils.isEmpty(item.getAmount()) ? "--" : PrecisionUtils.getQuantityContractRule(item.getAmount()));
			}
		}

		if (!TextUtils.isEmpty(item.getType())) {
			if (item.getType().equals("buy")) {//中文：买入字体红色，卖出字体绿色  英文：买入字体绿色，卖出字体红色
				holder.tv_direction.setText(R.string.trade_buy);
				if (isRedRise) {
					holder.tv_price.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_red));
					holder.tv_direction.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_red));
				} else {
					holder.tv_price.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_green));
					holder.tv_direction.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_green));
				}
			} else {
				holder.tv_direction.setText(R.string.trade_sell);
				if (isRedRise) {
					holder.tv_price.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_green));
					holder.tv_direction.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_green));
				} else {
					holder.tv_price.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_red));
					holder.tv_direction.setTextColor(holder.tv_direction.getResources().getColor(R.color.res_red));
				}
			}
		} else {
			holder.tv_direction.setText("--");

		}

	}

	public void setContractType(int contractType) {
		this.contractType = contractType;
	}

	public void setContractTable(ContractUsdtInfoTable table) {
		this.table = table;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_time)
		TextView tv_time;
		@BindView(R2.id.tv_price)
		TextView tv_price;
		@BindView(R2.id.tv_num)
		TextView tv_num;
		@BindView(R2.id.tv_direction)
		TextView tv_direction;


		ViewHolder(View view, DealItemViewBinder binder) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
