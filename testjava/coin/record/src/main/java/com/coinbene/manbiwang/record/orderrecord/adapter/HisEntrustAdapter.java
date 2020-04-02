package com.coinbene.manbiwang.record.orderrecord.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.coinbene.common.base.BaseAdapter;
import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.orderrecord.HistoryDetailRecordActivity;

/**
 * @author huyong
 * 现货杠杆历史委托
 */
public class HisEntrustAdapter extends BaseAdapter<CurOrderListModel.DataBean.ListBean> {


	private boolean isRedRise;
	private int greenColor, redColor;
	private int bgGreen, bgRed;
	private int clickEnableBg;
	private int clickEnableFlaseBg;
	private String percentStr;
	private String fixedPrice, planOrder, oco, maketPrice;

	public HisEntrustAdapter() {
		super(R.layout.item_history_entrust);
	}

	@Override
	protected void convert(BaseViewHolder holder, int position, CurOrderListModel.DataBean.ListBean model) {

		isRedRise = SwitchUtils.isRedRise();
		holder.setText(R.id.pair_tv, TextUtils.isEmpty(model.baseAsset) || TextUtils.isEmpty(model.quoteAsset) ? "" : model.baseAsset + "/" + model.quoteAsset);
		holder.setText(R.id.time_tv, model.orderTime);
		if (model.getOrderType() == 1) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, fixedPrice);
		} else if (model.getOrderType() == 2) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, maketPrice);
		} else if (model.getOrderType() == 4) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, R.string.fixed_price_stop_loss_and_stop_loss);
		} else if (model.getOrderType() == 5) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, R.string.market_price_stop_loss_and_sop_loss);
		} else if (model.getOrderType() == 6) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, R.string.fixed_price_oco);
		} else if (model.getOrderType() == 7) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, R.string.market_price_oco);
		} else {
			holder.getView(R.id.tv_order_type).setVisibility(View.GONE);
		}

		//2是卖  1是买
		if (model.orderDirection == 2) {
			holder.setText(R.id.left_top_tv, R.string.trade_sell_new);
			holder.setBackGroundResource(R.id.left_top_tv, isRedRise ? bgGreen : bgRed);
			holder.setTextColor(R.id.left_top_tv, isRedRise ? greenColor : redColor);
			holder.setBackGroundResource(R.id.tv_order_type, isRedRise ? bgGreen : bgRed);
			holder.setTextColor(R.id.tv_order_type, isRedRise ? greenColor : redColor);
		} else {
			holder.setText(R.id.left_top_tv, R.string.trade_buy_new);
			holder.setBackGroundResource(R.id.left_top_tv, isRedRise ? bgRed : bgGreen);
			holder.setTextColor(R.id.left_top_tv, isRedRise ? redColor : greenColor);
			holder.setBackGroundResource(R.id.tv_order_type, isRedRise ? bgRed : bgGreen);
			holder.setTextColor(R.id.tv_order_type, isRedRise ? redColor : greenColor);

		}
		TradePairInfoTable tradePairInfoTable = TradePairInfoController.getInstance().queryDataById(model.tradePair);
		if (tradePairInfoTable == null) {
			tradePairInfoTable = new TradePairInfoTable();
		}

		//限价  oco限价   止盈止损限价  和  市价  oco市价   止盈止损市价 的区别
		if (model.getOrderType() == 1 || model.getOrderType() == 4 || model.getOrderType() == 6) {
			holder.setText(R.id.entrust_price, BigDecimalUtils.setScaleHalfUp(model.orderPrice, tradePairInfoTable.pricePrecision));
			holder.setText(R.id.entrust_volume, BigDecimalUtils.setScaleHalfUp(model.quantity, tradePairInfoTable.volumePrecision));
			holder.setText(R.id.historyRec_tv2, R.string.entrust_volume);
		} else if (model.getOrderType() == 2 || model.getOrderType() == 5 || model.getOrderType() == 7) {
			holder.setText(R.id.entrust_price, R.string.market_price);

			//市价oco 有可能委托价是市价 也可能是限价
			if (model.getOrderType() == 7 && !BigDecimalUtils.isEmptyOrZero(model.getOrderPrice())) {
				holder.setText(R.id.entrust_price, model.getOrderPrice());
			}

			holder.setText(R.id.historyRec_tv2, R.string.entrust_volume);
			if (model.orderDirection == 1) {
				holder.setText(R.id.historyRec_tv2, R.string.entruast_total_price);
				holder.setText(R.id.entrust_volume, BigDecimalUtils.setScaleHalfUp(model.amount, tradePairInfoTable.pricePrecision));
			} else {
				holder.setText(R.id.historyRec_tv2, R.string.entrust_volume);
				holder.setText(R.id.entrust_volume, BigDecimalUtils.setScaleHalfUp(model.quantity, tradePairInfoTable.volumePrecision));
			}
		}

		if (model.orderDirection == 1) {//买入向上取整    卖出向下取整
			holder.setText(R.id.avg_tv, BigDecimalUtils.setScaleUp(model.avgPrice, tradePairInfoTable.pricePrecision));
		} else {
			holder.setText(R.id.avg_tv, BigDecimalUtils.setScaleDown(model.avgPrice, tradePairInfoTable.pricePrecision));
		}
		holder.setText(R.id.entrust_amount, BigDecimalUtils.setScaleHalfUp(model.filledQuantity, tradePairInfoTable.volumePrecision));
		holder.setText(R.id.entrust_amount_vol, BigDecimalUtils.setScaleHalfUp(model.filledAmount, tradePairInfoTable.pricePrecision));


		//1 已成交 2 已撤单 3 部分撤单
		if (model.getOrderStatus() == 1) {
			holder.setText(R.id.percent_tv, R.string.all_deal);
			holder.setBackGroundResource(R.id.root_layout, clickEnableBg);
			holder.setEnabled(R.id.root_layout, true);
			setOtherTextColor(holder);
		} else if (model.getOrderStatus() == 2) {
			holder.setText(R.id.percent_tv, R.string.order_cancel_label);
			holder.setBackGroundColor(R.id.root_layout, clickEnableFlaseBg);
			holder.setEnabled(R.id.root_layout, false);
			setAllCancelTextColor(holder);
		} else if (model.getOrderStatus() == 3) {
			holder.setText(R.id.percent_tv, percentStr);
			holder.setEnabled(R.id.root_layout, true);
			holder.setBackGroundResource(R.id.root_layout, clickEnableBg);
			setOtherTextColor(holder);
		}
		holder.getView(R.id.root_layout).setOnClickListener(v -> {
			HistoryDetailRecordActivity.startMe(v.getContext(), model.getOrderId());
		});
//		holder.setText(R.id.fee_tv, BigDecimalUtils.setScaleHalfUp(model.totalFee, 6));

	}


	private void setAllCancelTextColor(BaseViewHolder holder) {
		holder.setTextColor(R.id.pair_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.time_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.entrust_price, R.color.res_textColor_2);
		holder.setTextColor(R.id.entrust_volume, R.color.res_textColor_2);
		holder.setTextColor(R.id.entrust_amount, R.color.res_textColor_2);
		holder.setTextColor(R.id.avg_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.fee_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.deal_detail_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.entrust_amount_vol, R.color.res_textColor_2);
		holder.setTextColor(R.id.percent_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.historyRec_tv1, R.color.res_textColor_2);
		holder.setTextColor(R.id.historyRec_tv2, R.color.res_textColor_2);
		holder.setTextColor(R.id.historyRec_tv3, R.color.res_textColor_2);
		holder.setTextColor(R.id.left_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.historyRec_tv4, R.color.res_textColor_2);
		holder.setTextColor(R.id.fee_label, R.color.res_textColor_2);
	}


	private void setOtherTextColor(BaseViewHolder holder) {
		holder.setTextColor(R.id.pair_tv, R.color.res_textColor_1);
		holder.setTextColor(R.id.time_tv, R.color.res_textColor_2);
		holder.setTextColor(R.id.entrust_price, R.color.res_textColor_1);
		holder.setTextColor(R.id.entrust_volume, R.color.res_textColor_1);
		holder.setTextColor(R.id.entrust_amount, R.color.res_textColor_1);
		holder.setTextColor(R.id.avg_tv, R.color.res_textColor_1);
		holder.setTextColor(R.id.fee_tv, R.color.res_textColor_1);
		holder.setTextColor(R.id.deal_detail_tv, R.color.res_textColor_1);
		holder.setTextColor(R.id.entrust_amount_vol, R.color.res_textColor_1);
		holder.setTextColor(R.id.percent_tv, R.color.res_textColor_1);
	}


	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		greenColor = R.color.res_green;
		redColor = R.color.res_red;
		bgGreen = R.drawable.res_green_1e;
		bgRed = R.drawable.res_red_1e;
		clickEnableBg = R.drawable.res_selector_item_press_status;
		clickEnableFlaseBg = parent.getResources().getColor(R.color.res_ItemPress);
		percentStr = parent.getResources().getString(R.string.part_cancel);
		fixedPrice = parent.getResources().getString(R.string.fixed_price);
		planOrder = parent.getResources().getString(R.string.contract_plan);
		oco = "OCO";
		maketPrice = parent.getResources().getString(R.string.market_price);
		return super.onCreateViewHolder(parent, viewType);


	}
//
//	public interface OnItemClickLisenter {
//		void cancelOrder(String orderId);
//	}
}