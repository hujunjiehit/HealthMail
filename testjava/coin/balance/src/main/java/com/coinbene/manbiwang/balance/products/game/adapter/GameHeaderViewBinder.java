package com.coinbene.manbiwang.balance.products.game.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.GameTotalInfoModel;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

public class GameHeaderViewBinder extends ItemViewBinder<GameTotalInfoModel.DataBean, GameHeaderViewBinder.ViewHolder> {

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.layout_game_account_header, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull GameTotalInfoModel.DataBean accountData) {
		boolean hideValue =	AssetManager.getInstance().isHideValue();
		if (hideValue) {
			holder.tvAccountBalance.setText("*****");
			holder.tvAccountBalanceLocal.setText("*****");
		} else {
			holder.tvAccountBalance.setText(String.format("%s BTC",accountData.getBtcTotalPreestimate()));
			holder.tvAccountBalanceLocal.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(accountData.getCurrencySymbol()), accountData.getLocalTotalPreestimate()));
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_account_balance)
		TextView tvAccountBalance;
		@BindView(R2.id.tv_account_balance_local)
		TextView tvAccountBalanceLocal;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
