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
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.manbiwang.spot.R;

/**
 * Created by june
 * on 2019-11-28
 */
public class HistoryOrderFiveAdapter extends BaseQuickAdapter<CurOrderListModel.DataBean.ListBean, BaseViewHolder> {

	private boolean isRedRise;
	private int greenColor, redColor;
	private int bgGreen, bgRed;
	private int clickEnableBg;
	private int clickEnableFlaseBg = 0;
	private String orderType;
	private String percentStr;
	private String fixedPrice, planOrder, oco, maketPrice;

	public HistoryOrderFiveAdapter() {
		super(R.layout.item_history_entrust);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CurOrderListModel.DataBean.ListBean model) {

		initResours();


		if (model.getOrderType() == 1) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, fixedPrice);
		} else if (model.getOrderType() == 2) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, maketPrice);
		} else if (model.getOrderType() == 4) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, R.string.fixed_price_stop_loss_and_stop_loss);
		} else if (model.getOrderType() == 5) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, R.string.market_price_stop_loss_and_sop_loss);
		} else if (model.getOrderType() == 6) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, R.string.fixed_price_oco);
		} else if (model.getOrderType() == 7) {
			helper.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			helper.setText(R.id.tv_order_type, R.string.market_price_oco);
		} else {
			helper.getView(R.id.tv_order_type).setVisibility(View.GONE);
		}


		helper.setText(R.id.pair_tv, TextUtils.isEmpty(model.baseAsset) || TextUtils.isEmpty(model.quoteAsset) ? "" : model.baseAsset + "/" + model.quoteAsset);
		helper.setText(R.id.time_tv, model.orderTime);
		TradePairInfoTable tradePairInfoTable = TradePairInfoController.getInstance().queryDataById(model.tradePair);
		if (tradePairInfoTable == null) {
			tradePairInfoTable = new TradePairInfoTable();
		}
		if (model.orderDirection == 2) {
			helper.setText(R.id.left_top_tv, R.string.trade_sell_new);
			helper.setBackgroundRes(R.id.left_top_tv, isRedRise ? bgGreen : bgRed);
			helper.setTextColor(R.id.left_top_tv, isRedRise ? greenColor : redColor);
			helper.setBackgroundRes(R.id.tv_order_type, isRedRise ? bgGreen : bgRed);
			helper.setTextColor(R.id.tv_order_type, isRedRise ? greenColor : redColor);

		} else {
			helper.setText(R.id.left_top_tv, R.string.trade_buy_new);
			helper.setBackgroundRes(R.id.left_top_tv, isRedRise ? bgRed : bgGreen);
			helper.setTextColor(R.id.left_top_tv, isRedRise ? redColor : greenColor);
			helper.setBackgroundRes(R.id.tv_order_type, isRedRise ? bgRed : bgGreen);
			helper.setTextColor(R.id.tv_order_type, isRedRise ? redColor : greenColor);
		}
		//限价  oco限价   止盈止损限价  和  市价  oco市价   止盈止损市价 的区别
		if (model.getOrderType() == 1 || model.getOrderType() == 4 || model.getOrderType() == 6) {
			helper.setText(R.id.entrust_price, BigDecimalUtils.setScaleHalfUp(model.orderPrice, tradePairInfoTable.pricePrecision));
			helper.setText(R.id.entrust_volume, BigDecimalUtils.setScaleHalfUp(model.quantity, tradePairInfoTable.volumePrecision));
			helper.setText(R.id.historyRec_tv2, R.string.entrust_volume);
		} else if (model.getOrderType() == 2 || model.getOrderType() == 5 || model.getOrderType() == 7) {
			helper.setText(R.id.entrust_price, R.string.market_price);
			//市价oco 有可能委托价是市价 也可能是限价
			if (model.getOrderType() == 7 && !BigDecimalUtils.isEmptyOrZero(model.getOrderPrice())) {
				helper.setText(R.id.entrust_price, model.getOrderPrice());
			}

			helper.setText(R.id.historyRec_tv2, R.string.entrust_volume);
			if (model.orderDirection == 1) {
				helper.setText(R.id.historyRec_tv2, R.string.entruast_total_price);
				helper.setText(R.id.entrust_volume, BigDecimalUtils.setScaleHalfUp(model.amount, tradePairInfoTable.pricePrecision));
			} else {
				helper.setText(R.id.historyRec_tv2, R.string.entrust_volume);
				helper.setText(R.id.entrust_volume, BigDecimalUtils.setScaleHalfUp(model.quantity, tradePairInfoTable.volumePrecision));
			}
		}


		if (model.orderDirection == 1) {//买入向上取整    卖出向下取整
			helper.setText(R.id.avg_tv, BigDecimalUtils.setScaleUp(model.avgPrice, tradePairInfoTable.pricePrecision));
		} else {
			helper.setText(R.id.avg_tv, BigDecimalUtils.setScaleDown(model.avgPrice, tradePairInfoTable.pricePrecision));
		}

		helper.setText(R.id.entrust_amount, BigDecimalUtils.setScaleHalfUp(model.filledQuantity, tradePairInfoTable.volumePrecision));
		helper.setText(R.id.entrust_amount_vol, BigDecimalUtils.setScaleHalfUp(model.filledAmount, tradePairInfoTable.pricePrecision));

		//1 已成交 2 已撤单 3 部分撤单
		if (model.getOrderStatus() == 1) {
			helper.setText(R.id.percent_tv, R.string.all_deal);
			helper.setBackgroundRes(R.id.root_layout, clickEnableBg);
			helper.setEnabled(R.id.root_layout, true);
			setOtherTextColor(helper);
			helper.addOnClickListener(R.id.root_layout);
		} else if (model.getOrderStatus() == 2) {
			helper.setText(R.id.percent_tv, R.string.order_cancel_label);
			setBackGroundColor(helper, R.id.root_layout, clickEnableFlaseBg);
			helper.setEnabled(R.id.root_layout, false);
			setAllCancelTextColor(helper);
			helper.getView(R.id.root_layout).setOnClickListener(null);
		} else if (model.getOrderStatus() == 3) {
			helper.setText(R.id.percent_tv, percentStr);
			helper.setEnabled(R.id.root_layout, true);
			helper.setBackgroundRes(R.id.root_layout, clickEnableBg);
			setOtherTextColor(helper);
			helper.addOnClickListener(R.id.root_layout);
		}

//		helper.setText(R.id.fee_tv, BigDecimalUtils.setScaleHalfUp(model.totalFee, 6));


	}

	private void initResours() {
		isRedRise = SwitchUtils.isRedRise();
		if (bgGreen == 0) {
			bgGreen = R.drawable.res_green_1e;

		}
		if (redColor == 0) {
			redColor = getRecyclerView().getResources().getColor(R.color.res_red);
		}
		if (greenColor == 0) {
			greenColor = getRecyclerView().getResources().getColor(R.color.res_green);
		}
		if (bgRed == 0) {
			bgRed = R.drawable.res_red_1e;
		}
		if (clickEnableBg == 0) {
			clickEnableBg = R.drawable.res_selector_item_press_status;
		}
		if (clickEnableFlaseBg == 0) {
			clickEnableFlaseBg = getRecyclerView().getResources().getColor(R.color.res_ItemPress);
		}
		if (percentStr == null) {
			percentStr = getRecyclerView().getResources().getString(R.string.part_cancel);
		}
		if (TextUtils.isEmpty(fixedPrice)) {
			fixedPrice = getRecyclerView().getResources().getString(R.string.fixed_price);
		}
		if (TextUtils.isEmpty(planOrder)) {
			planOrder = getRecyclerView().getResources().getString(R.string.contract_plan);
		}
		if (TextUtils.isEmpty(oco)) {
			oco = "OCO";
		}
		if (TextUtils.isEmpty(maketPrice)) {
			maketPrice = getRecyclerView().getResources().getString(R.string.market_price);
		}
	}

	private void setAllCancelTextColor(BaseViewHolder holder) {
		holder.setTextColor(R.id.pair_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.time_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.entrust_price, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.entrust_volume, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.entrust_amount, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.avg_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.fee_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.deal_detail_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.entrust_amount_vol, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.percent_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.historyRec_tv1, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.historyRec_tv2, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.historyRec_tv3, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.left_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.historyRec_tv4, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.fee_label, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
	}


	private void setOtherTextColor(BaseViewHolder holder) {
		holder.setTextColor(R.id.pair_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.time_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		holder.setTextColor(R.id.entrust_price, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.entrust_volume, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.entrust_amount, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.avg_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.fee_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.deal_detail_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.entrust_amount_vol, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		holder.setTextColor(R.id.percent_tv, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
	}

	private void setBackGroundDrable(BaseViewHolder helper, int id, Drawable drawable) {
		View view = helper.getView(id);
		view.setBackground(drawable);
	}

	public void setBackGroundColor(BaseViewHolder helper, int id, int color) {
		View view = helper.getView(id);
		view.setBackgroundColor(color);
	}
}
