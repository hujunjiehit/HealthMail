package com.coinbene.manbiwang.contract.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class OrderListLocalPriceBinder extends ItemViewBinder<OrderLineItemModel, OrderListLocalPriceBinder.ViewHolder> {


	private boolean isRedRaise;
	private int greenColor, redColor, defaultColor;
	private int orderBookType;
	private boolean showHideContractPlan, showHideContractHighLever;
	private boolean heightIsChange = true;
	private ContractType contractType = ContractType.USDT;

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.order_list_local_item, parent, false);
		redColor = root.getContext().getResources().getColor(R.color.res_red);
		greenColor = root.getContext().getResources().getColor(R.color.res_green);
		defaultColor = root.getContext().getResources().getColor(R.color.res_textColor_1);
		isRedRaise = SwitchUtils.isRedRise();
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderLineItemModel item) {
		isRedRaise = SwitchUtils.isRedRise();
		if (!TextUtils.isEmpty(item.newPrice)) {
			if (item.riseType == Constants.RISE_UP) {
				holder.priceTv.setTextColor(SwitchUtils.isRedRise() ? redColor : greenColor);
				holder.iv_rise_type.setVisibility(View.VISIBLE);
				holder.iv_rise_type.setImageResource(SwitchUtils.isRedRise() ? R.drawable.res_red_up : R.drawable.res_green_up);
			} else if (item.riseType == Constants.RISE_DOWN) {
				holder.priceTv.setTextColor(SwitchUtils.isRedRise() ? greenColor : redColor);
				holder.iv_rise_type.setVisibility(View.VISIBLE);
				holder.iv_rise_type.setImageResource(SwitchUtils.isRedRise() ? R.drawable.res_green_down : R.drawable.res_red_down);
			} else {
				holder.priceTv.setTextColor(defaultColor);
				holder.iv_rise_type.setVisibility(View.GONE);
			}
		} else {
			holder.iv_rise_type.setVisibility(View.GONE);
		}
		holder.priceTv.setText(TextUtils.isEmpty(item.newPrice) ? "--" : item.newPrice);


		String upsAndDowns = TextUtils.isEmpty(item.upsAndDowns) ? "0.00%" : item.upsAndDowns;
		if (upsAndDowns.equals("0.00%")) {
			holder.upsAndDownsTv.setText(upsAndDowns);
			holder.upsAndDownsTv.setTextColor(isRedRaise ? redColor : greenColor);
		} else if (upsAndDowns.contains("-")) {
			holder.upsAndDownsTv.setText(upsAndDowns);
			holder.upsAndDownsTv.setTextColor(isRedRaise ? greenColor : redColor);
		} else {
			holder.upsAndDownsTv.setText(String.format("+%s", upsAndDowns));
			holder.upsAndDownsTv.setTextColor(isRedRaise ? redColor : greenColor);
		}

		if (heightIsChange) {
			ViewGroup.LayoutParams layoutParams = holder.rlRoot.getLayoutParams();
			if (showHideContractHighLever) {
				layoutParams.height = QMUIDisplayHelper.dpToPx(63);
			} else if (showHideContractPlan) {
				layoutParams.height = QMUIDisplayHelper.dpToPx(43);
			} else {
				layoutParams.height = QMUIDisplayHelper.dpToPx(44);
			}

			if (contractType == ContractType.BTC) {
				layoutParams.height = layoutParams.height + QMUIDisplayHelper.dpToPx(24);
			}
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			holder.rlRoot.setLayoutParams(layoutParams);
			heightIsChange = false;
		}

	}


	public void setShowHideContractPlan(boolean show) {
		this.showHideContractPlan = show;
		heightIsChange = true;
	}

	public void setShowHideContractHighLever(Boolean show) {
		this.showHideContractHighLever = show;
		heightIsChange = true;
	}

	public void setContractType(ContractType contractType) {
		if (this.contractType == contractType) {
			return;
		}
		this.contractType = contractType;
		heightIsChange = true;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.current_price_tv)
		TextView priceTv;
		//		@BindView(R2.id.local_price_tv)
//		TextView local_price_tv;
		@BindView(R2.id.iv_rise_type)
		ImageView iv_rise_type;
		@BindView(R2.id.ups_and_downs_tv)
		TextView upsAndDownsTv;
		@BindView(R2.id.rl_root)
		View rlRoot;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			priceTv.setTextColor(SwitchUtils.isRedRise() ? priceTv.getResources().getColor(R.color.res_green) : priceTv.getResources().getColor(R.color.res_red));
//            priceTv.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_medium));
//            local_price_tv.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_regular));
		}
	}
}
