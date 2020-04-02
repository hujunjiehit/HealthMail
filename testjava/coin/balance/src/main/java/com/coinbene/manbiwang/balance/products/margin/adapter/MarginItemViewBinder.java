package com.coinbene.manbiwang.balance.products.margin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.MarginTotalInfoModel;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.margin.MarginDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-08-17
 */
public class MarginItemViewBinder extends ItemViewBinder<MarginTotalInfoModel.DataBean.AccountListBean, MarginItemViewBinder.ViewHolder> {



	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_margin_item, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MarginTotalInfoModel.DataBean.AccountListBean marginBalance) {
		if (marginBalance == null || marginBalance.getBalanceList() == null || marginBalance.getBalanceList().size() < 2) {
			return;
		}

		holder.mTvAccountSymbol.setText(marginBalance.getSymbol());
		holder.mTvRiskRate.setText(String.format("%s: %s",
				holder.mTvRiskRate.getContext().getString(R.string.risk_rate),
				TradeUtils.getDisplayRiskRate(marginBalance.getRiskRate())));

		boolean hideValue =	AssetManager.getInstance().isHideValue();
		if (marginBalance.getBalanceList().get(0) != null) {
			//设置分子账户
			MarginTotalInfoModel.DataBean.AccountListBean.BalanceListBean balanceListBean = marginBalance.getBalanceList().get(0);
			holder.mTvAccountFenzi.setText(balanceListBean.getAsset());
			holder.mTvFenziAvailable.setText(hideValue ? "*****" : balanceListBean.getAvailable());
			holder.mTvFenziFrozen.setText(hideValue ? "*****" : balanceListBean.getFrozen());
		}

		if (marginBalance.getBalanceList().get(1) != null) {
			//设置分母账户
			MarginTotalInfoModel.DataBean.AccountListBean.BalanceListBean balanceListBean = marginBalance.getBalanceList().get(1);
			holder.mTvAccountFenmu.setText(balanceListBean.getAsset());
			holder.mTvFenmuAvailable.setText(hideValue ? "*****" : balanceListBean.getAvailable());
			holder.mTvFenmuFrozen.setText(hideValue ? "*****" : balanceListBean.getFrozen());
		}

		holder.mLayoutMarginAccount.setOnClickListener(v -> MarginDetailActivity.startMe(v.getContext(), marginBalance.getSymbol()));
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_account_symbol)
		TextView mTvAccountSymbol;
		@BindView(R2.id.tv_risk_rate)
		TextView mTvRiskRate;
		@BindView(R2.id.iv_right_icon)
		ImageView mIvRightIcon;
		@BindView(R2.id.tv_account_fenzi)
		TextView mTvAccountFenzi;
		@BindView(R2.id.tv_fenzi_available)
		TextView mTvFenziAvailable;
		@BindView(R2.id.tv_fenzi_frozen)
		TextView mTvFenziFrozen;
		@BindView(R2.id.tv_account_fenmu)
		TextView mTvAccountFenmu;
		@BindView(R2.id.tv_fenmu_available)
		TextView mTvFenmuAvailable;
		@BindView(R2.id.tv_fenmu_frozen)
		TextView mTvFenmuFrozen;
		@BindView(R2.id.layout_margin_account)
		View mLayoutMarginAccount;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
