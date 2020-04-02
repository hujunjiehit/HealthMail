package com.coinbene.manbiwang.spot.fragment.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.CircleProgressBar;
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.manbiwang.spot.R;

import java.math.BigDecimal;

/**
 * Created by june
 * on 2019-11-27
 */
public class CurrentOrderFiveAdapter extends BaseQuickAdapter<CurOrderListModel.DataBean.ListBean, BaseViewHolder> {

	private boolean isRedRise;
	private int greenColor, redColor;
	private int bgGreen, bgRed;
	private String fixedPrice, planOrder, oco, marketPrice;


	public CurrentOrderFiveAdapter() {
		super(R.layout.item_cur_entrust);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CurOrderListModel.DataBean.ListBean item) {

		initResours();

		if (item.getOrderType() == 1) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, fixedPrice);
		} else if (item.getOrderType() == 4) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, String.format("%s-%s", planOrder, fixedPrice));
		} else if (item.getOrderType() == 6) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, String.format("%s-%s", oco, fixedPrice));
		} else if (item.getOrderType() == 7) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, String.format("%s-%s", oco, marketPrice));
		} else {
			helper.getView(R.id.tv_order_type).setVisibility(View.GONE);
		}


		if (TextUtils.isEmpty(item.quantity)) {
			setProgress(helper, R.id.progress, 0);
		} else if (!TextUtils.isEmpty(item.filledQuantity) && item.quantity.equals(item.filledQuantity)) {
			setProgress(helper, R.id.progress, 100);
		} else if (TextUtils.isEmpty(item.filledQuantity) || Double.parseDouble(item.filledQuantity) == 0) {
			setProgress(helper, R.id.progress, 0);
		} else {
			BigDecimal resultDivide = BigDecimalUtils.divideBigDmal(item.filledQuantity, item.quantity, 2);
			int resultInt = resultDivide.multiply(new BigDecimal(100)).intValue();
			setProgress(helper, R.id.progress, resultInt);
		}

		helper.setText(R.id.tv_entrust_volume, R.string.Filled_amount);
		helper.setText(R.id.pair_tv, TextUtils.isEmpty(item.baseAsset) || TextUtils.isEmpty(item.quoteAsset) ? "" : item.baseAsset + "/" + item.quoteAsset);
		helper.setText(R.id.time_tv, item.orderTime);

		if (item.orderDirection == 2) {
			helper.setText(R.id.left_top_tv, R.string.trade_sell_new);
			setProgressStartColor(helper, R.id.progress, isRedRise ? greenColor : redColor);
			setProgressEndColor(helper, R.id.progress, isRedRise ? greenColor : redColor);
			setProgressTextColor(helper, R.id.progress, isRedRise ? greenColor : redColor);
			helper.setBackgroundRes(R.id.left_top_tv, isRedRise ? bgGreen : bgRed);
			helper.setTextColor(R.id.left_top_tv, isRedRise ? greenColor : redColor);
			helper.setBackgroundRes(R.id.tv_order_type, isRedRise ? bgGreen : bgRed);
			helper.setTextColor(R.id.tv_order_type, isRedRise ? greenColor : redColor);

		} else if (item.orderDirection == 1) {
			helper.setText(R.id.left_top_tv, R.string.trade_buy_new);
			setProgressStartColor(helper, R.id.progress, isRedRise ? redColor : greenColor);
			setProgressEndColor(helper, R.id.progress, isRedRise ? redColor : greenColor);
			setProgressTextColor(helper, R.id.progress, isRedRise ? redColor : greenColor);
			helper.setBackgroundRes(R.id.left_top_tv, isRedRise ? bgRed : bgGreen);
			helper.setTextColor(R.id.left_top_tv, isRedRise ? redColor : greenColor);
			helper.setBackgroundRes(R.id.tv_order_type, isRedRise ? bgRed : bgGreen);
			helper.setTextColor(R.id.tv_order_type, isRedRise ? redColor : greenColor);
		}
		TradePairInfoTable tradePairInfoTable = TradePairInfoController.getInstance().queryDataById(item.tradePair);
		if (tradePairInfoTable == null) {
			tradePairInfoTable = new TradePairInfoTable();
		}


		helper.setText(R.id.entrust_price, BigDecimalUtils.setScaleHalfUp(item.orderPrice, tradePairInfoTable.pricePrecision));

		helper.setText(R.id.entrust_volume, new StringBuilder().append(BigDecimalUtils.setScaleHalfUp(item.filledQuantity, tradePairInfoTable.volumePrecision))
				.append("/")
				.append(BigDecimalUtils.setScaleHalfUp(item.quantity, tradePairInfoTable.volumePrecision)).toString());
		if (Float.valueOf(item.quantity) > Float.valueOf(item.filledQuantity) && Float.valueOf(item.filledQuantity) > 0) {
			helper.setText(R.id.filled_tv, R.string.part_deal);
		} else {
			helper.setText(R.id.filled_tv, R.string.not_deal);
		}

		helper.addOnClickListener(R.id.cancel_tv, R.id.ll_root);
	}

	private void initResours() {
		isRedRise = SwitchUtils.isRedRise();
		if (bgGreen == 0) {
			bgGreen = R.drawable.res_green_1e;

		}
		if (bgRed == 0) {
			bgRed = R.drawable.res_red_1e;
		}
		if (redColor == 0) {
			redColor = getRecyclerView().getResources().getColor(R.color.res_red);
		}
		if (greenColor == 0) {
			greenColor = getRecyclerView().getResources().getColor(R.color.res_green);
		}
		if (TextUtils.isEmpty(fixedPrice)) {
			fixedPrice = getRecyclerView().getResources().getString(R.string.fixed_price);
		}
		if (TextUtils.isEmpty(marketPrice)) {
			marketPrice = getRecyclerView().getResources().getString(R.string.market_price);
		}
		if (TextUtils.isEmpty(planOrder)) {
			planOrder = getRecyclerView().getResources().getString(R.string.contract_plan);
		}
		if (TextUtils.isEmpty(oco)) {
			oco = "OCO";
		}
	}

	public void setProgress(BaseViewHolder helper, int id, int pregress) {
		CircleProgressBar progressBar = helper.getView(id);
		progressBar.setProgress(pregress);
	}

	private void setProgressStartColor(BaseViewHolder helper, int id, int resColor) {
		CircleProgressBar progressBar = helper.getView(id);
		progressBar.setProgressStartColor(resColor);
	}

	private void setProgressEndColor(BaseViewHolder helper, int id, int resColor) {
		CircleProgressBar progressBar = helper.getView(id);
		progressBar.setProgressEndColor(resColor);
	}

	private void setProgressTextColor(BaseViewHolder helper, int id, int resColor) {
		CircleProgressBar progressBar = helper.getView(id);
		progressBar.setProgressTextColor(resColor);
	}

	private void setBackGroundDrable(BaseViewHolder helper, int id, Drawable drawable) {
		View view = helper.getView(id);
		view.setBackground(drawable);
	}
}
