package com.coinbene.manbiwang.record.orderrecord.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.model.http.HisOrderModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author mxd on 2018/7/30.
 */

public class HisDetailHeaderBinder extends ItemViewBinder<HisOrderModel.DataBean, HisDetailHeaderBinder.HeaderViewHolder> {


	private boolean isRedRise;

	@NonNull
	@Override
	protected HeaderViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.record_his_detail_header, parent, false);
		isRedRise = SwitchUtils.isRedRise();
		return new HeaderViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull HeaderViewHolder holder, @NonNull HisOrderModel.DataBean model) {
//		TradePairInfoTable tradePairInfoTable = TradePairInfoController.getInstance().queryDataById(model.getTradePair());
//		if (tradePairInfoTable == null) {
//			tradePairInfoTable = new TradePairInfoTable();
//		}


		holder.tvSymbol.setText(String.format("%s/%s", model.getBaseAsset(), model.getQuoteAsset()));
		if (model.getOrderStatus() == 1) {
			holder.tvPercent.setText(holder.tvPercent.getResources().getString(R.string.all_deal));
			holder.tvPercent.setBackground(ResourceProvider.getRectShape(30, holder.tvPercent.getResources().getColor(R.color.res_blue_28)));
			holder.tvPercent.setTextColor(holder.tvPercent.getResources().getColor(R.color.res_blue));
		} else if (model.getOrderStatus() == 2) {
			holder.tvPercent.setText(holder.tvPercent.getResources().getString(R.string.order_cancel_label));
		} else if (model.getOrderStatus() == 3) {
			holder.tvPercent.setText(holder.tvPercent.getResources().getString(R.string.part_cancel));
			holder.tvPercent.setBackground(ResourceProvider.getRectShape(30, holder.tvPercent.getResources().getColor(R.color.res_lable_background_28)));
			holder.tvPercent.setTextColor(holder.tvPercent.getResources().getColor(R.color.res_assistColor_22));
		}
		if (model.getDirection() == 1) {
			holder.tvOrderDirection.setText(holder.tvOrderDirection.getResources().getString(R.string.trade_buy));
			holder.tvOrderDirection.setTextColor(holder.tvOrderDirection.getResources().getColor(isRedRise ? R.color.res_red : R.color.res_green));
		} else {
			holder.tvOrderDirection.setText(holder.tvOrderDirection.getResources().getString(R.string.trade_sell));
			holder.tvOrderDirection.setTextColor(holder.tvOrderDirection.getResources().getColor(isRedRise ? R.color.res_green : R.color.res_red));
		}

		if (BigDecimalUtils.isEmptyOrZero(model.getFee())) {
			holder.tvFeeValue.setVisibility(View.GONE);
		} else {
			holder.tvFeeValue.setVisibility(View.VISIBLE);
			holder.tvFeeValue.setText(String.format("%s %s", model.getFee(), model.getQuoteAsset()));
		}

		if (BigDecimalUtils.isEmptyOrZero(model.getFeeByConi())) {
			holder.tvFeeCoin.setVisibility(View.GONE);
		} else {
			holder.tvFeeCoin.setVisibility(View.VISIBLE);
			holder.tvFeeCoin.setText(String.format("%s%s", model.getFeeByConi(), " CONI"));
		}


		switch (model.getOrderType()) {
			case 1://限价
				holder.tvOrderTypeValue.setText(R.string.fixed_price);

				holder.tvQuantityValue.setText(String.format("%s/%s", model.getFilledQuantity(), model.getQuantity()));
				holder.tvPriceValue.setText(String.format("%s/%s", model.getAvgPrice(), model.getOrderPrice()));
				holder.tvTotalAmountValue.setText(String.format("%s %s", model.getAmount(), model.getQuoteAsset()));
				holder.tvFilledAmountValue.setText(String.format("%s %s", model.getFilledAmount(), model.getQuoteAsset()));

				holder.rlTouchPrice.setVisibility(View.GONE);
				holder.rlTouchTime.setVisibility(View.GONE);
				holder.llHighLever.setVisibility(View.GONE);
				break;
			case 2://市价
				holder.tvOrderTypeValue.setText(R.string.market_price);
				holder.tvQuantityValue.setText(model.getDirection() == 1 ?
						String.format("%s/%s", model.getFilledQuantity(), "--") :
						String.format("%s/%s", model.getFilledQuantity(), model.getQuantity()));
				holder.tvPriceValue.setText(String.format("%s/%s",
						model.getAvgPrice(),
						BigDecimalUtils.isEmptyOrZero(model.getOrderPrice()) ? holder.tvPriceValue.getResources().getString(R.string.market_price) : model.getOrderPrice()));

				holder.tvTotalAmountValue.setText(model.getDirection() == 1 ? String.format("%s %s", model.getAmount(), model.getQuoteAsset()) : "--");
				holder.tvFilledAmountValue.setText(String.format("%s %s", model.getFilledAmount(), model.getQuoteAsset()));

				holder.rlTouchPrice.setVisibility(View.GONE);
				holder.rlTouchTime.setVisibility(View.GONE);
				holder.llHighLever.setVisibility(View.GONE);
				break;
			case 4://限价止盈止损
				holder.tvOrderTypeValue.setText(R.string.fixed_price_stop_loss_and_stop_loss);
				holder.tvTouchPriceValue.setText(String.format("%s %s", model.getPlanTriggeredCondition(), model.getQuoteAsset()));
				holder.tvTouchTimeValue.setText(model.getTriggeredTime());
				holder.tvQuantityValue.setText(String.format("%s/%s", model.getFilledQuantity(), model.getQuantity()));
				holder.tvPriceValue.setText(String.format("%s/%s", model.getAvgPrice(), model.getOrderPrice()));
				holder.tvTotalAmountValue.setText(String.format("%s %s", model.getAmount(), model.getQuoteAsset()));
				holder.tvFilledAmountValue.setText(String.format("%s %s", model.getFilledAmount(), model.getQuoteAsset()));
				holder.rlTouchPrice.setVisibility(View.VISIBLE);
				holder.rlTouchTime.setVisibility(View.VISIBLE);
				holder.llHighLever.setVisibility(View.GONE);
				break;
			case 5://市价止盈止损
				holder.tvTouchPriceValue.setText(String.format("%s %s", model.getPlanTriggeredCondition(), model.getQuoteAsset()));
				holder.tvTouchTimeValue.setText(model.getTriggeredTime());
				holder.tvOrderTypeValue.setText(R.string.market_price_stop_loss_and_sop_loss);
				holder.tvQuantityValue.setText(model.getDirection() == 1 ?
						String.format("%s/%s", model.getFilledQuantity(), "--") :
						String.format("%s/%s", model.getFilledQuantity(), model.getQuantity()));
				holder.tvPriceValue.setText(String.format("%s/%s",
						model.getAvgPrice(),
						BigDecimalUtils.isEmptyOrZero(model.getOrderPrice()) ? holder.tvPriceValue.getResources().getString(R.string.market_price) : model.getOrderPrice()));

				holder.tvTotalAmountValue.setText(model.getDirection() == 1 ? String.format("%s %s", model.getAmount(), model.getQuoteAsset()) : "--");
				holder.tvFilledAmountValue.setText(String.format("%s %s", model.getFilledAmount(), model.getQuoteAsset()));


				holder.rlTouchPrice.setVisibility(View.VISIBLE);
				holder.rlTouchTime.setVisibility(View.VISIBLE);
				holder.llHighLever.setVisibility(View.GONE);
				break;
			case 6://限价oco
				holder.tvTouchPriceValue.setText(String.format("%s | %s %s", model.getOcoLimitTriggerCondition(), model.getOcoTriggerCondition(), model.getQuoteAsset()));
				holder.tvTouchTimeValue.setText(model.getTriggeredTime());
				holder.tvOrderTypeValue.setText(R.string.fixed_price_oco);
				holder.tvQuantityValue.setText(String.format("%s/%s", model.getFilledQuantity(), model.getQuantity()));
				holder.tvPriceValue.setText(String.format("%s/%s", model.getAvgPrice(), model.getOrderPrice()));
				holder.tvTotalAmountValue.setText(String.format("%s %s", model.getAmount(), model.getQuoteAsset()));
				holder.tvFilledAmountValue.setText(String.format("%s %s", model.getFilledAmount(), model.getQuoteAsset()));

				holder.tvRealTouchPriceValue.setText(model.getOcoTriggeredCondition());
				holder.tvOcoTouchedOrderValue.setText(R.string.fixed_order);
				holder.tvOcoTouchedPriceValue.setText(model.getOcoTriggeredOrderPrice());
				holder.tvOcoTouchedQuantityValue.setText(model.getOcoTriggeredQuantity());
				holder.tvOcoTouchedAmountValue.setText(model.getOcoTriggeredAmount());

				holder.tvOcoCanceledOrderValue.setText(R.string.fixed_order);
				holder.tvOcoCanceledPriceValue.setText(model.getOcoCancelledOrderPrice());
				holder.tvOcoCanceledQuantityValue.setText(model.getOcoCancelledQuantity());
				holder.tvOcoCanceledAmountValue.setText(model.getOcoCancelledAmount());

				holder.llTouchedTitle.setOnClickListener(v -> showTouchedOrder(holder));
				holder.llCanceledTitle.setOnClickListener(v -> showCanceledOrder(holder));
				holder.rlOrderType.setVisibility(View.VISIBLE);
				holder.rlTouchTime.setVisibility(View.VISIBLE);
				holder.llHighLever.setVisibility(View.VISIBLE);
				break;
			case 7://市价oco
				holder.tvOrderTypeValue.setText(R.string.market_price_oco);
				holder.tvTouchPriceValue.setText(String.format("%s | %s %s",
						model.getOcoLimitTriggerCondition(),
						model.getOcoTriggerCondition(),
						model.getQuoteAsset()));

				holder.tvTouchTimeValue.setText(model.getTriggeredTime());

				holder.tvQuantityValue.setText(BigDecimalUtils.isEmptyOrZero(model.getQuantity()) ?
						String.format("%s/%s", model.getFilledQuantity(), "--") :
						String.format("%s/%s", model.getFilledQuantity(), model.getQuantity()));
				holder.tvPriceValue.setText(String.format("%s/%s",
						model.getAvgPrice(),
						BigDecimalUtils.isEmptyOrZero(model.getOrderPrice()) ? holder.tvPriceValue.getResources().getString(R.string.market_price) : model.getOrderPrice()));

				holder.tvTotalAmountValue.setText(!BigDecimalUtils.isEmptyOrZero(model.getAmount()) ? String.format("%s %s", model.getAmount(), model.getQuoteAsset()) : "--");
				holder.tvFilledAmountValue.setText(String.format("%s %s", model.getFilledAmount(), model.getQuoteAsset()));

				holder.tvRealTouchPriceValue.setText(model.getOcoTriggeredCondition());
				holder.tvOcoTouchedOrderValue.setText(BigDecimalUtils.isEmptyOrZero(model.getOcoTriggeredOrderPrice()) ? R.string.market_order : R.string.fixed_order);
				holder.tvOcoTouchedPriceValue.setText(BigDecimalUtils.isEmptyOrZero(model.getOcoTriggeredOrderPrice()) ?
						holder.tvOcoTouchedPriceValue.getResources().getString(R.string.market_price) :
						model.getOcoTriggeredOrderPrice());

				if (BigDecimalUtils.isEmptyOrZero(model.getOcoTriggeredOrderPrice()) && model.getDirection() == 1) {//触发的是市价单  并且是买  则委托数量显示 --
					holder.tvOcoTouchedQuantityValue.setText("--");
				} else {
					holder.tvOcoTouchedQuantityValue.setText(model.getOcoTriggeredQuantity());
				}

				holder.tvOcoTouchedAmountValue.setText(model.getOcoTriggeredAmount());

				holder.tvOcoCanceledOrderValue.setText(BigDecimalUtils.isEmptyOrZero(model.getOcoCancelledOrderPrice()) ? R.string.market_order : R.string.fixed_order);
				holder.tvOcoCanceledPriceValue.setText(BigDecimalUtils.isEmptyOrZero(model.getOcoCancelledOrderPrice()) ?
						holder.tvOcoCanceledPriceValue.getResources().getString(R.string.market_price) :
						model.getOcoCancelledOrderPrice());

				if (BigDecimalUtils.isEmptyOrZero(model.getOcoCancelledOrderPrice()) && model.getDirection() == 1) {//取消的是市价单  并且是买  则委托数量显示 --
					holder.tvOcoCanceledQuantityValue.setText("--");
				} else {
					holder.tvOcoCanceledQuantityValue.setText(model.getOcoCancelledQuantity());
				}
				holder.tvOcoCanceledAmountValue.setText(BigDecimalUtils.isEmptyOrZero(model.getOcoCancelledAmount()) ? "--" : model.getOcoCancelledAmount());

				holder.llTouchedTitle.setOnClickListener(v -> showTouchedOrder(holder));
				holder.llCanceledTitle.setOnClickListener(v -> showCanceledOrder(holder));
				holder.rlOrderType.setVisibility(View.VISIBLE);
				holder.rlTouchTime.setVisibility(View.VISIBLE);
				holder.llHighLever.setVisibility(View.VISIBLE);
				break;
		}


	}

	private void showCanceledOrder(HeaderViewHolder holder) {
		holder.tvTouched.setTextColor(holder.tvTouched.getResources().getColor(R.color.res_textColor_2));
		holder.tvCanceled.setTextColor(holder.tvTouched.getResources().getColor(R.color.res_blue));
		holder.vTouchedOrder.setVisibility(View.GONE);
		holder.llTouchedOrder.setVisibility(View.GONE);
		holder.vCanceledOrder.setVisibility(View.VISIBLE);
		holder.llCanceledOrder.setVisibility(View.VISIBLE);
	}

	private void showTouchedOrder(HeaderViewHolder holder) {
		holder.tvCanceled.setTextColor(holder.tvTouched.getResources().getColor(R.color.res_textColor_2));
		holder.tvTouched.setTextColor(holder.tvTouched.getResources().getColor(R.color.res_blue));
		holder.vTouchedOrder.setVisibility(View.VISIBLE);
		holder.llTouchedOrder.setVisibility(View.VISIBLE);
		holder.vCanceledOrder.setVisibility(View.GONE);
		holder.llCanceledOrder.setVisibility(View.GONE);
	}

	static class HeaderViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_order_direction)
		TextView tvOrderDirection;
		@BindView(R2.id.tv_symbol)
		TextView tvSymbol;
		@BindView(R2.id.ll_top)
		LinearLayout llTop;
		@BindView(R2.id.tv_percent)
		TextView tvPercent;
		@BindView(R2.id.tv_order_type)
		TextView tvOrderType;
		@BindView(R2.id.tv_order_type_value)
		TextView tvOrderTypeValue;
		@BindView(R2.id.rl_order_type)
		RelativeLayout rlOrderType;
		@BindView(R2.id.tv_touch_price)
		TextView tvTouchPrice;
		@BindView(R2.id.tv_touch_price_value)
		TextView tvTouchPriceValue;
		@BindView(R2.id.rl_touch_price)
		RelativeLayout rlTouchPrice;
		@BindView(R2.id.tv_touch_time)
		TextView tvTouchTime;
		@BindView(R2.id.tv_touch_time_value)
		TextView tvTouchTimeValue;
		@BindView(R2.id.rl_touch_time)
		RelativeLayout rlTouchTime;
		@BindView(R2.id.tv_quantity)
		TextView tvQuantity;
		@BindView(R2.id.tv_quantity_value)
		TextView tvQuantityValue;
		@BindView(R2.id.rl_quantity)
		RelativeLayout rlQuantity;
		@BindView(R2.id.tv_price)
		TextView tvPrice;
		@BindView(R2.id.tv_price_value)
		TextView tvPriceValue;
		@BindView(R2.id.rl_price)
		RelativeLayout rlPrice;
		@BindView(R2.id.tv_total_amount)
		TextView tvTotalAmount;
		@BindView(R2.id.tv_total_amount_value)
		TextView tvTotalAmountValue;
		@BindView(R2.id.rl_total_amount)
		RelativeLayout rlTotalAmount;
		@BindView(R2.id.tv_filled_amount)
		TextView tvFilledAmount;
		@BindView(R2.id.tv_filled_amount_value)
		TextView tvFilledAmountValue;
		@BindView(R2.id.rl_filled_amount)
		RelativeLayout rlFilledAmount;
		@BindView(R2.id.tv_fee)
		TextView tvFee;
		@BindView(R2.id.tv_fee_value)
		TextView tvFeeValue;
		@BindView(R2.id.rl_fee)
		LinearLayout rlFee;
		@BindView(R2.id.tv_fee_coini)
		TextView tvFeeCoin;
		@BindView(R2.id.v_touched_order)
		View vTouchedOrder;
		@BindView(R2.id.v_canceled_order)
		View vCanceledOrder;
		@BindView(R2.id.ll_high_lever)
		LinearLayout llHighLever;
		@BindView(R2.id.tv_real_touch_price)
		TextView tvRealTouchPrice;
		@BindView(R2.id.tv_real_touch_price_value)
		TextView tvRealTouchPriceValue;
		@BindView(R2.id.rl_real_touch_price)
		RelativeLayout rlRealTouchPrice;
		@BindView(R2.id.tv_oco_touched_order)
		TextView tvOcoTouchedOrder;
		@BindView(R2.id.tv_oco_touched_order_value)
		TextView tvOcoTouchedOrderValue;
		@BindView(R2.id.rl_oco_touched_order)
		RelativeLayout rlOcoTouchedOrder;
		@BindView(R2.id.tv_oco_touched_price)
		TextView tvOcoTouchedPrice;
		@BindView(R2.id.tv_oco_touched_price_value)
		TextView tvOcoTouchedPriceValue;
		@BindView(R2.id.rl_oco_touched_price)
		RelativeLayout rlOcoTouchedPrice;
		@BindView(R2.id.tv_oco_touched_quantity)
		TextView tvOcoTouchedQuantity;
		@BindView(R2.id.tv_oco_touched_quantity_value)
		TextView tvOcoTouchedQuantityValue;
		@BindView(R2.id.rl_oco_touched_quantity)
		RelativeLayout rlOcoTouchedQuantity;
		@BindView(R2.id.tv_oco_touched_amount)
		TextView tvOcoTouchedAmount;
		@BindView(R2.id.tv_oco_touched_amount_value)
		TextView tvOcoTouchedAmountValue;
		@BindView(R2.id.rl_oco_touched_amount)
		RelativeLayout rlOcoTouchedAmount;
		@BindView(R2.id.ll_touched_order)
		LinearLayout llTouchedOrder;
		@BindView(R2.id.tv_oco_canceled_order)
		TextView tvOcoCanceledOrder;
		@BindView(R2.id.tv_oco_canceled_order_value)
		TextView tvOcoCanceledOrderValue;
		@BindView(R2.id.rl_oco_canceled_order)
		RelativeLayout rlOcoCanceledOrder;
		@BindView(R2.id.tv_oco_canceled_price)
		TextView tvOcoCanceledPrice;
		@BindView(R2.id.tv_oco_canceled_price_value)
		TextView tvOcoCanceledPriceValue;
		@BindView(R2.id.rl_oco_canceled_price)
		RelativeLayout rlOcoCanceledPrice;
		@BindView(R2.id.tv_oco_canceled_quantity)
		TextView tvOcoCanceledQuantity;
		@BindView(R2.id.tv_oco_canceled_quantity_value)
		TextView tvOcoCanceledQuantityValue;
		@BindView(R2.id.rl_oco_canceled_quantity)
		RelativeLayout rlOcoCanceledQuantity;
		@BindView(R2.id.tv_oco_canceled_amount)
		TextView tvOcoCanceledAmount;
		@BindView(R2.id.tv_oco_canceled_amount_value)
		TextView tvOcoCanceledAmountValue;
		@BindView(R2.id.rl_oco_canceled_amount)
		RelativeLayout rlOcoCanceledAmount;
		@BindView(R2.id.ll_canceled_order)
		LinearLayout llCanceledOrder;
		@BindView(R2.id.ll_touched_title)
		LinearLayout llTouchedTitle;
		@BindView(R2.id.ll_canceled_title)
		LinearLayout llCanceledTitle;
		@BindView(R2.id.tv_touched)
		TextView tvTouched;
		@BindView(R2.id.tv_canceled)
		TextView tvCanceled;

		HeaderViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

}

