package com.coinbene.manbiwang.record.orderrecord.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.model.http.HighLeverOrderModel;
import com.coinbene.manbiwang.record.R;

/**
 * Created by june
 * on 2019-11-28
 */
public class HighLeverOrderAdapter extends BaseQuickAdapter<HighLeverOrderModel.DataBean.ListBean, BaseViewHolder> {

	private boolean isRedRise;
	private int greenColor, redColor;
	private int bgGreen, bgRed;
	private int colorBlue, colorTextEnable;
	private String fixedPrice, planOrder, oco, maketPrice;

	public HighLeverOrderAdapter() {
		super(R.layout.record_item_high_lever_entrust);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, HighLeverOrderModel.DataBean.ListBean model) {
		initResours();
		if (model.getOrderDirection() == 1) {
			helper.setText(R.id.tv_trade_direction, R.string.trade_buy_new);
			helper.setBackgroundRes(R.id.tv_trade_direction, isRedRise ? bgRed : bgGreen);
			helper.setTextColor(R.id.tv_trade_direction, isRedRise ? redColor : greenColor);
			helper.setBackgroundRes(R.id.tv_order_type, isRedRise ? bgRed : bgGreen);
			helper.setTextColor(R.id.tv_order_type, isRedRise ? redColor : greenColor);
		} else {
			helper.setText(R.id.tv_trade_direction, R.string.trade_sell_new);
			helper.setBackgroundRes(R.id.tv_trade_direction, isRedRise ? bgGreen : bgRed);
			helper.setTextColor(R.id.tv_trade_direction, isRedRise ? greenColor : redColor);
			helper.setBackgroundRes(R.id.tv_order_type, isRedRise ? bgGreen : bgRed);
			helper.setTextColor(R.id.tv_order_type, isRedRise ? greenColor : redColor);
		}
		helper.setText(R.id.tv_trade_pair, model.getBaseAsset() + "/" + model.getQuoteAsset());
		helper.setText(R.id.tv_time, model.getCreateTime());

		switch (model.getStatus()) {
			case 0://未触发
				helper.setText(R.id.tv_order_status, R.string.plan_status_not_touch);
				helper.setTextColor(R.id.tv_cancel_order, colorBlue);
				helper.addOnClickListener(R.id.tv_cancel_order);
				helper.getView(R.id.tv_cancel_order).setEnabled(true);
				helper.getView(R.id.tv_cancel_order).setVisibility(View.VISIBLE);
				helper.getView(R.id.v_line).setVisibility(View.VISIBLE);
				break;
			case 1://已触发
				helper.setText(R.id.tv_order_status, R.string.plan_status_touched);
				helper.setTextColor(R.id.tv_cancel_order, colorTextEnable);
				helper.getView(R.id.tv_cancel_order).setEnabled(false);
				helper.getView(R.id.tv_cancel_order).setVisibility(View.GONE);
				helper.getView(R.id.v_line).setVisibility(View.GONE);
				break;
			case 2://已撤销
				helper.setText(R.id.tv_order_status, R.string.orderstatus_5);
				helper.setTextColor(R.id.tv_cancel_order, colorTextEnable);
				helper.getView(R.id.tv_cancel_order).setEnabled(false);
				helper.getView(R.id.tv_cancel_order).setVisibility(View.GONE);
				helper.getView(R.id.v_line).setVisibility(View.GONE);
				break;
			case 3://已失败
				helper.setText(R.id.tv_order_status, R.string.touched_failed);
				helper.setTextColor(R.id.tv_cancel_order, colorTextEnable);
				helper.getView(R.id.tv_cancel_order).setEnabled(false);
				helper.getView(R.id.tv_cancel_order).setVisibility(View.GONE);
				helper.getView(R.id.v_line).setVisibility(View.GONE);
				break;
		}


		switch (model.getOrderType()) {
			case 4://限价止盈止损
				helper.setText(R.id.tv_order_type, R.string.fixed_price_stop_loss_and_stop_loss);
				helper.setText(R.id.tv_trigger_condition_value, model.getPlanTriggerCondition());
				helper.setText(R.id.tv_entrust_price_value, model.getPlanOrderPrice());
				helper.setText(R.id.tv_entrust_volume_value, model.getQuantity());
				helper.setText(R.id.tv_entrust_total_amout_value, model.getAmount());

				break;
			case 5:    //市价止盈止损
				helper.setText(R.id.tv_order_type, R.string.market_price_stop_loss_and_sop_loss);
				helper.setText(R.id.tv_trigger_condition_value, model.getPlanTriggerCondition());
				helper.setText(R.id.tv_entrust_price_value, R.string.market_price);
				if (model.getOrderDirection() == 1) {
					helper.setText(R.id.tv_entrust_volume_value, "--");
					helper.setText(R.id.tv_entrust_total_amout_value, model.getAmount());
				} else {
					helper.setText(R.id.tv_entrust_volume_value, model.getQuantity());
					helper.setText(R.id.tv_entrust_total_amout_value, "--");
				}
				break;
			case 6://限价oco
				helper.setText(R.id.tv_order_type, R.string.fixed_price_oco);
				if (model.getStatus() == 1) {// 0未触发,1已触发,2已撤消,3已失败
					helper.setText(R.id.tv_trigger_condition_value, model.getOcoTriggeredCondition());
					helper.setText(R.id.tv_entrust_price_value, BigDecimalUtils.isEmptyOrZero(model.getOcoTriggeredOrderPrice()) ?
							getRecyclerView().getResources().getString(R.string.market_price) :
							model.getOcoTriggeredOrderPrice());
				} else {
					helper.setText(R.id.tv_trigger_condition_value, model.getOcoLimitTriggerCondition() + " | " + model.getOcoTriggerCondition());
					helper.setText(R.id.tv_entrust_price_value, model.getOcoLimitPrice() + " | " + model.getOcoOrderPrice());
				}
				helper.setText(R.id.tv_entrust_volume_value, model.getQuantity());
				helper.setText(R.id.tv_entrust_total_amout_value, model.getAmount());
				break;
			case 7://市价oco
				helper.setText(R.id.tv_order_type, R.string.market_price_oco);
				if (model.getStatus() == 1) {// 0未触发,1已触发,2已撤消,3已失败
					helper.setText(R.id.tv_trigger_condition_value, model.getOcoTriggeredCondition());
					helper.setText(R.id.tv_entrust_price_value, BigDecimalUtils.isEmptyOrZero(model.getOcoTriggeredOrderPrice()) ?
							getRecyclerView().getResources().getString(R.string.market_price) :
							model.getOcoTriggeredOrderPrice());
				} else {
					helper.setText(R.id.tv_trigger_condition_value, model.getOcoLimitTriggerCondition() + " | " + model.getOcoTriggerCondition());
					helper.setText(R.id.tv_entrust_price_value, model.getOcoLimitPrice() + " | " + maketPrice);
				}

				if (model.getOrderDirection() == 1) {
					helper.setText(R.id.tv_entrust_volume_value, "--");
					helper.setText(R.id.tv_entrust_total_amout_value, model.getAmount());
				} else {
					helper.setText(R.id.tv_entrust_volume_value, model.getQuantity());
					helper.setText(R.id.tv_entrust_total_amout_value, "--");
				}
				break;
		}
	}

	private void initResours() {
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
		if (colorBlue == 0) {
			colorBlue = getRecyclerView().getResources().getColor(R.color.res_blue);
		}
		if (colorTextEnable == 0) {
			colorTextEnable = getRecyclerView().getResources().getColor(R.color.res_textColor_2);
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
		isRedRise = SwitchUtils.isRedRise();
	}

//	private void setBackGroundDrable(BaseViewHolder helper, int id, int drawable) {
//		View view = helper.getView(id);
//		view.setBackgroundR(drawable);
//	}
//
//	public void setBackGroundColor(BaseViewHolder helper, int id, int color) {
//		View view = helper.getView(id);
//		view.setBackgroundColor(color);
//	}

}
