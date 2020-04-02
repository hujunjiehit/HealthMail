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

import java.math.BigDecimal;

/**
 * @author ding
 * 现货、杠杆当前委托
 */
public class CurEntrustAdapter extends BaseAdapter<CurOrderListModel.DataBean.ListBean> {


	private boolean isRedRise;
	private int greenColor, redColor;
	private int bgGreen, bgRed;
	private CancelOrderListener cancelOrderListener;
	private String fixedPrice, planOrder, oco,marketPrice;

	public void setCancelOrderListener(CancelOrderListener cancelOrderListener) {
		this.cancelOrderListener = cancelOrderListener;
	}

	public CurEntrustAdapter() {
		super(R.layout.item_cur_entrust);
	}

	@Override
	protected void convert(BaseViewHolder holder, int position, CurOrderListModel.DataBean.ListBean item) {
		isRedRise = SwitchUtils.isRedRise();
		if (TextUtils.isEmpty(item.quantity))
			holder.setProgress(R.id.progress, 0);
		else if (!TextUtils.isEmpty(item.filledQuantity) && item.quantity.equals(item.filledQuantity)) {
			holder.setProgress(R.id.progress, 100);
		} else if (TextUtils.isEmpty(item.filledQuantity) || Double.parseDouble(item.filledQuantity) == 0) {
			holder.setProgress(R.id.progress, 0);
		} else {
			BigDecimal resultDivide = BigDecimalUtils.divideBigDmal(item.filledQuantity, item.quantity, 2);
			int resultInt = resultDivide.multiply(new BigDecimal(100)).intValue();
			holder.setProgress(R.id.progress, resultInt);
		}

		holder.setText(R.id.tv_entrust_volume, R.string.Filled_amount);
		holder.setText(R.id.pair_tv, TextUtils.isEmpty(item.baseAsset) || TextUtils.isEmpty(item.quoteAsset) ? "" : item.baseAsset + "/" + item.quoteAsset);
		holder.setText(R.id.time_tv, item.orderTime);

		if (item.getOrderType() == 1) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, fixedPrice);
		} else if (item.getOrderType() == 4) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, String.format("%s-%s", planOrder, fixedPrice));
		} else if (item.getOrderType() == 6) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, String.format("%s-%s", oco, fixedPrice));
		}else if (item.getOrderType() == 7) {
			holder.getView(R.id.tv_order_type).setVisibility(View.VISIBLE);
			holder.setText(R.id.tv_order_type, String.format("%s-%s", oco, marketPrice));
		}  else {
			holder.getView(R.id.tv_order_type).setVisibility(View.GONE);
		}


		if (item.orderDirection == 2) {
			holder.setText(R.id.left_top_tv, R.string.trade_sell_new);
			holder.setProgressStartColor(R.id.progress, isRedRise ? greenColor : redColor);
			holder.setProgressEndColor(R.id.progress, isRedRise ? greenColor : redColor);
			holder.setProgressTextColor(R.id.progress, isRedRise ? greenColor : redColor);
			holder.setBackGroundResource(R.id.left_top_tv, isRedRise ? bgGreen : bgRed);
			holder.setTextColor(R.id.left_top_tv, isRedRise ? greenColor : redColor);

			holder.setBackGroundResource(R.id.tv_order_type, isRedRise ? bgGreen : bgRed);
			holder.setTextColor(R.id.tv_order_type, isRedRise ? greenColor : redColor);


		} else if (item.orderDirection == 1) {
			holder.setText(R.id.left_top_tv, R.string.trade_buy_new);
			holder.setProgressStartColor(R.id.progress, isRedRise ? redColor : greenColor);
			holder.setProgressEndColor(R.id.progress, isRedRise ? redColor : greenColor);
			holder.setProgressTextColor(R.id.progress, isRedRise ? redColor : greenColor);
			holder.setBackGroundResource(R.id.left_top_tv, isRedRise ? bgRed : bgGreen);
			holder.setTextColor(R.id.left_top_tv, isRedRise ? redColor : greenColor);
			holder.setBackGroundResource(R.id.tv_order_type, isRedRise ? bgRed : bgGreen);
			holder.setTextColor(R.id.tv_order_type, isRedRise ? redColor : greenColor);
		}
		TradePairInfoTable tradePairInfoTable = TradePairInfoController.getInstance().queryDataById(item.tradePair);
		if (tradePairInfoTable == null) {
			tradePairInfoTable = new TradePairInfoTable();
		}

		holder.setText(R.id.entrust_price, BigDecimalUtils.setScaleHalfUp(item.orderPrice, tradePairInfoTable.pricePrecision));

		holder.setText(R.id.entrust_volume, new StringBuilder().append(BigDecimalUtils.setScaleHalfUp(item.filledQuantity, tradePairInfoTable.volumePrecision))
				.append("/")
				.append(BigDecimalUtils.setScaleHalfUp(item.quantity, tradePairInfoTable.volumePrecision)).toString());
		if (Float.valueOf(item.quantity) > Float.valueOf(item.filledQuantity) && Float.valueOf(item.filledQuantity) > 0) {
			holder.setText(R.id.filled_tv, R.string.part_deal);
		} else {
			holder.setText(R.id.filled_tv, R.string.not_deal);
		}
		holder.getView(R.id.cancel_tv).setOnClickListener(v -> {
			if (cancelOrderListener != null) {
				cancelOrderListener.cancelOrder(item.orderId);
			}
		});
	}


	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		greenColor = R.color.res_green;
		redColor = R.color.res_red;
		bgGreen =R.drawable.res_green_1e;
		bgRed = R.drawable.res_red_1e;
		fixedPrice = parent.getResources().getString(R.string.fixed_price);
		planOrder = parent.getResources().getString(R.string.contract_plan);
		marketPrice = parent.getResources().getString(R.string.market_price);
		oco = "OCO";
		return super.onCreateViewHolder(parent, viewType);
	}

	public void removeTradeById(String tradeId) {
		if (getList() == null || getList().size() == 0) {
			return;
		}
		for (int i = 0; i < getList().size(); i++) {
			CurOrderListModel.DataBean.ListBean model = getList().get(i);
			if (model != null && model.orderId != null && model.orderId.equals(tradeId)) {
				getList().remove(i);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public interface CancelOrderListener {
		void cancelOrder(String orderId);
	}
}
