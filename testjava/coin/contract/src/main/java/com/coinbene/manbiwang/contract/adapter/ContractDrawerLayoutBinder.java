package com.coinbene.manbiwang.contract.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.service.contract.ContractChangePopWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class ContractDrawerLayoutBinder extends ItemViewBinder<WsMarketData, ContractDrawerLayoutBinder.ViewHolder> {

	private ContractChangePopWindow.OnItemClickContractListener onItemClickContrackListener;
	private int redColor, greenColor;
	private String strForever;


	public void setOnItemClickContractListener(ContractChangePopWindow.OnItemClickContractListener onItemClickContrackListener) {
		this.onItemClickContrackListener = onItemClickContrackListener;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.contract_change_item, parent, false);
		redColor = parent.getResources().getColor(R.color.res_red);
		greenColor = parent.getResources().getColor(R.color.res_green);
		strForever = parent.getResources().getString(R.string.forever_no_delivery);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull WsMarketData item) {
		if (item.getContractType() == ContractType.USDT) {
			holder.tvContractName.setText(TextUtils.isEmpty(item.getBaseAsset()) ? "--" :
					String.format(strForever, item.getBaseAsset()));
		} else {
			holder.tvContractName.setText(TextUtils.isEmpty(item.getSymbol()) ? "--" :
					String.format(strForever, item.getSymbol()));
		}

		String upsAndDowns = TextUtils.isEmpty(item.getUpsAndDowns()) ? "0.00%" : item.getUpsAndDowns();
		if (upsAndDowns.equals("0.00%")) {
			holder.tvPrice.setTextColor(SwitchUtils.isRedRise() ? redColor : greenColor);
			holder.tvPercent.setTextColor(SwitchUtils.isRedRise() ? redColor : greenColor);
			holder.tvPercent.setText(!TextUtils.isEmpty(item.getUpsAndDowns()) ? item.getUpsAndDowns() : "0.00%");
		} else if (upsAndDowns.contains("-")) {
			holder.tvPrice.setTextColor(SwitchUtils.isRedRise() ? greenColor : redColor);
			holder.tvPercent.setTextColor(SwitchUtils.isRedRise() ? greenColor : redColor);
			holder.tvPercent.setText(!TextUtils.isEmpty(item.getUpsAndDowns()) ? item.getUpsAndDowns() : "0.00%");
		} else {
			holder.tvPrice.setTextColor(SwitchUtils.isRedRise() ? redColor : greenColor);
			holder.tvPercent.setTextColor(SwitchUtils.isRedRise() ? redColor : greenColor);
			holder.tvPercent.setText(!TextUtils.isEmpty(item.getUpsAndDowns()) ? "+" + item.getUpsAndDowns() : "0.00%");
		}

		holder.tvPrice.setText(TextUtils.isEmpty(item.getLastPrice()) ? "--" : item.getLastPrice());

		holder.rlRoot.setOnClickListener(v -> {
			if (onItemClickContrackListener != null) {
				onItemClickContrackListener.onItemClickContract(item);
			}
		});

	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_contract_name)
		TextView tvContractName;
		@BindView(R2.id.tv_price)
		TextView tvPrice;
		@BindView(R2.id.tv_percent)
		TextView tvPercent;

		@BindView(R2.id.view_line)
		View viewLine;
		@BindView(R2.id.rl_root)
		View rlRoot;


		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
