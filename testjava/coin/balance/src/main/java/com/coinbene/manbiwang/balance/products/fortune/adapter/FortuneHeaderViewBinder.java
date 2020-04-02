package com.coinbene.manbiwang.balance.products.fortune.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.FortuneTotalInfoModel;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.service.RouteHub;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

public class FortuneHeaderViewBinder extends ItemViewBinder<FortuneTotalInfoModel.DataBean, FortuneHeaderViewBinder.ViewHolder> {


	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.layout_fortune_account_header, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FortuneTotalInfoModel.DataBean accountData) {
		boolean hideValue = AssetManager.getInstance().isHideValue();
		if (hideValue) {
			holder.tvAccountBalance.setText("*****");
			holder.tvAccountBalanceLocal.setText("*****");
		} else {
			holder.tvAccountBalance.setText(String.format("%s BTC", accountData.getBtcTotalPreestimate()));
			holder.tvAccountBalanceLocal.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(accountData.getCurrencySymbol()), accountData.getLocalTotalPreestimate()));
		}

		holder.mTvTransferRecord.setOnClickListener(v ->
				ARouter.getInstance().build(RouteHub.Fortune.ybbRecordActivity).navigation(v.getContext())
		);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_account_balance)
		TextView tvAccountBalance;
		@BindView(R2.id.tv_account_balance_local)
		TextView tvAccountBalanceLocal;
		@BindView(R2.id.tv_transfer_record)
		TextView mTvTransferRecord;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
