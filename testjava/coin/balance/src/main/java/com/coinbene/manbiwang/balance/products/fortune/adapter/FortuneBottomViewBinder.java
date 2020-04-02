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
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-10-15
 */
public class FortuneBottomViewBinder extends ItemViewBinder<FortuneTotalInfoModel.DataBean.CurrentPreestimateBean, FortuneBottomViewBinder.ViewHolder> {

	private String symbol;
	private boolean hideValue;

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_fortune_bottom, parent, false);
		return new ViewHolder(root);
	}

	public void setCurrencySymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FortuneTotalInfoModel.DataBean.CurrentPreestimateBean item) {
		hideValue = AssetManager.getInstance().isHideValue();

		if (hideValue) {
			holder.mTvAccountBalance.setText("*****");
			holder.mTvLocalBalance.setText("*****");
		} else {
			holder.mTvAccountBalance.setText(String.format("%s BTC", item.getBtcTotalPreestimate()));
			holder.mTvLocalBalance.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(symbol), item.getLocalTotalPreestimate()));
		}

		holder.mTvTransferIn.setOnClickListener(v ->
				ARouter.getInstance().build(RouteHub.Fortune.ybbTransferActivity)
				.withInt("type",RouteHub.Fortune.TRANSFER_TYPE_IN)
				.navigation(v.getContext())
		);

		holder.mTvTransferOut.setOnClickListener(v ->
			ARouter.getInstance().build(RouteHub.Fortune.ybbTransferActivity)
					.withInt("type", RouteHub.Fortune.TRANSFER_TYPE_OUT)
					.navigation(v.getContext())
		);

		holder.mContainerView.setOnClickListener(v ->
			ARouter.getInstance().build(RouteHub.Fortune.ybbDetailActivity)
					.navigation(v.getContext())
		);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.container_view)
		View mContainerView;
		@BindView(R2.id.tv_account_balance)
		TextView mTvAccountBalance;
		@BindView(R2.id.tv_local_balance)
		TextView mTvLocalBalance;
		@BindView(R2.id.tv_transfer_in)
		QMUIRoundButton mTvTransferIn;
		@BindView(R2.id.tv_transfer_out)
		QMUIRoundButton mTvTransferOut;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
