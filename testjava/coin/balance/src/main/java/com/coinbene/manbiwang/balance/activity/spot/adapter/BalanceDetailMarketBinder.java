package com.coinbene.manbiwang.balance.activity.spot.adapter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 我的资产页面
 *
 * @author mxd on 2018/6/14.
 */

public class BalanceDetailMarketBinder extends ItemViewBinder<WsMarketData, BalanceDetailMarketBinder.ViewHolder> {


	private final Activity mActivity;
	private boolean isRedRise;
	private int redRes;
	private int greenRes;
	private final GradientDrawable shape;

	public BalanceDetailMarketBinder(Activity activity) {
		this.mActivity = activity;
		shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
		shape.setCornerRadius(QMUIDisplayHelper.dp2px(activity, 3));
		shape.setColor(activity.getResources().getColor(R.color.res_divider_tile));
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_balance_detail_market, parent, false);
		isRedRise = SwitchUtils.isRedRise();
		redRes = root.getContext().getResources().getColor(R.color.res_red);
		greenRes = root.getContext().getResources().getColor(R.color.res_green);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull WsMarketData item) {
		holder.itemView.setBackground(shape);
		holder.tvTradePair.setText(item.getTradePairName());
		holder.tvRiseAndFall.setText(item.getUpsAndDowns());

		float aFloat = Tools.parseFloat(item.getChangeValue());
		if (aFloat > 0) {
			holder.tvRiseAndFall.setTextColor(isRedRise ? redRes : greenRes);
		} else if (aFloat == 0) {
			holder.tvRiseAndFall.setTextColor(isRedRise ? redRes : greenRes);
		} else {
			holder.tvRiseAndFall.setTextColor(isRedRise ? greenRes : redRes);
		}


		if (!TextUtils.isEmpty(item.getLastPrice())) {
			holder.tvNewPrice.setText(item.getLastPrice());
		}
		holder.tvNewLocalPrice.setText(item.getLocalPrice());

		holder.llRoot.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString("tab", "spot");
			bundle.putString("subTab", "coin");
			bundle.putString("symbol", item.getSymbol());
			UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
		});

	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_trade_pair)
		TextView tvTradePair;
		@BindView(R2.id.tv_rise_and_fall)
		TextView tvRiseAndFall;
		@BindView(R2.id.tv_new_price)
		TextView tvNewPrice;
		@BindView(R2.id.tv_new_local_price)
		TextView tvNewLocalPrice;
		@BindView(R2.id.ll_root)
		View llRoot;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
