package com.coinbene.manbiwang.contract.contractusdt.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class UsdtOrderListItemAdapter extends ItemViewBinder<OrderModel, UsdtOrderListItemAdapter.ViewHolder> {

	private int greenColor, redColor, depthGreenColor, depthRedColor, backgroundColor;
	private boolean isRedRise;
	private OnItemClickListener onItemClickListener;
	private String symbol;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.fr_contract_order_item, parent, false);
		greenColor = root.getContext().getResources().getColor(R.color.res_green);
		redColor = root.getContext().getResources().getColor(R.color.res_red);
		depthGreenColor = root.getContext().getResources().getColor(R.color.res_deepth_green);
		depthRedColor = root.getContext().getResources().getColor(R.color.res_deepth_red);
		backgroundColor = root.getContext().getResources().getColor(R.color.transparent);
		isRedRise = SwitchUtils.isRedRise();
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderModel item) {
		holder.amount_tv.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getCntNumContractEn(item.cnt));
		holder.priceTv.setText(TextUtils.isEmpty(item.price) ? "--" : item.price);
		if (item.isSell) {
			holder.priceTv.setTextColor(isRedRise ? greenColor : redColor);
			holder.progress.setBarColor(backgroundColor, isRedRise ? depthGreenColor : depthRedColor);
		} else {
			holder.priceTv.setTextColor(isRedRise ? redColor : greenColor);
			holder.progress.setBarColor(backgroundColor, isRedRise ? depthRedColor : depthGreenColor);
		}

		holder.amount_tv.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ?
				"--" :
				PrecisionUtils.getQuantityContractRule(
						TradeUtils.getContractUsdtUnitValue(item.cnt, ContractUsdtInfoController.getInstance().queryContrackByName(symbol))));

		//深度图
		if (BigDecimalUtils.isEmptyOrZero(item.cnt)) {
			holder.progress.setProgress(0, false);
		} else {
			holder.progress.setProgress((int) (item.percent * 100), false);
		}

		holder.rlRoot.setOnClickListener(v -> {
			if (onItemClickListener != null && !item.isFalse) {
				onItemClickListener.itemOnClick(item.price);
			}
		});
	}

	public void setSymbol(String contractName) {
		this.symbol = contractName;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.price_tv)
		TextView priceTv;
		@BindView(R2.id.amount_tv)
		TextView amount_tv;
		@BindView(R2.id.rl_root)
		RelativeLayout rlRoot;
		@BindView(R2.id.qmui_progress)
		QMUIProgressBar progress;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
//            amount_tv.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_regular));
//            priceTv.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_regular));
		}
	}


	public interface OnItemClickListener {
		void itemOnClick(String price);
	}
}
