package com.coinbene.manbiwang.balance.products.game.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.GameTotalInfoModel;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

public class GameItemViewBinder extends ItemViewBinder<GameTotalInfoModel.DataBean.ListBean, GameItemViewBinder.ViewHolder> {


	private String symbol;
	private boolean hideValue;

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.layout_game_account_item, parent, false);
		return new ViewHolder(root);
	}

	public void setCurrencySymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull GameTotalInfoModel.DataBean.ListBean balanceModel) {
		hideValue = AssetManager.getInstance().isHideValue();

		holder.mTvCoinName.setText(balanceModel.getAsset());

		if (hideValue) {
			holder.mTvFrozenBalance.setText("*****");
		} else {
			holder.mTvFrozenBalance.setText(balanceModel.getFrozenBalance());
		}

		if (hideValue) {
			holder.mTvPreestimateValue.setText("*****");
		} else {
			String currentSym = TextUtils.isEmpty(symbol) ? "" : symbol;
			holder.mTvPreestimateValue.setText(new StringBuilder().append(StringUtils.getCnyReplace(currentSym)).append(balanceModel.getLocalPreestimate()).toString());
		}

		if (hideValue) {
			holder.mTvTotalBalance.setText("*****");
		} else {
			holder.mTvTotalBalance.setText(balanceModel.getTotalBalance());
		}

		String urlpath = Constants.BASE_IMG_URL + balanceModel.getAsset().trim() + ".png";

		GlideUtils.loadImageViewLoad(holder.mIvCoinIcon.getContext(), urlpath, holder.mIvCoinIcon, R.drawable.coin_default_icon, R.drawable.coin_default_icon);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.iv_coin_icon)
		ImageView mIvCoinIcon;
		@BindView(R2.id.tv_coin_name)
		TextView mTvCoinName;
		@BindView(R2.id.tv_frozen_balance)
		TextView mTvFrozenBalance;
		@BindView(R2.id.tv_preestimate_value)
		TextView mTvPreestimateValue;
		@BindView(R2.id.tv_total_balance)
		TextView mTvTotalBalance;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
