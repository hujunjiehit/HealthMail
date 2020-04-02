//package com.coinbene.manbiwang.record.orderrecord.adapter;
//
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.coinbene.common.context.CBRepository;
//import com.coinbene.common.database.TradePairInfoController;
//import com.coinbene.common.database.TradePairInfoTable;
//import com.coinbene.manbiwang.model.http.CurOrderListModel;
//import com.coinbene.common.utils.BigDecimalUtils;
//import com.coinbene.common.utils.PrecisionUtils;
//import com.coinbene.common.utils.SwitchUtils;
//import com.coinbene.manbiwang.record.R;
//import com.coinbene.manbiwang.record.R2;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import me.drakeet.multitype.ItemViewBinder;
//
///**
// * @author mxd on 2018/7/30.
// */
//
//public class HistoryDetailHeaderBinder extends ItemViewBinder<CurOrderListModel.DataBean.ListBean, HistoryDetailHeaderBinder.HeaderViewHolder> {
//
//
//	private boolean isRedRise;
//
//	@NonNull
//	@Override
//	protected HeaderViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
//		View root = inflater.inflate(R.layout.record_history_detail_header, parent, false);
//		isRedRise = SwitchUtils.isRedRise();
//		return new HeaderViewHolder(root);
//	}
//
//	@Override
//	protected void onBindViewHolder(@NonNull HeaderViewHolder holder, @NonNull CurOrderListModel.DataBean.ListBean model) {
//		TradePairInfoTable tradePairInfoTable = TradePairInfoController.getInstance().queryDataById(model.tradePair);
//		if (tradePairInfoTable == null) {
//			tradePairInfoTable = new TradePairInfoTable();
//		}
//		if (model.getOrderType() == 1) {//限价
//			holder.priceValueTv.setText(String.format("/%s", model.orderPrice));
//			holder.countTv.setText(holder.typeTv.getResources().getString(R.string.history_detail_header_count));
//			holder.countValueTv.setText(String.format("/%s", model.quantity));
//			holder.dealPriceTv.setText(R.string.history_detail_header_total);
//			holder.dealPriceValue.setText(String.format("%s %s", model.filledAmount, model.quoteAsset));
//			holder.filledCountTv.setText(model.filledQuantity);
//		} else if (model.getOrderType() == 2) {//
//			if (model.orderDirection == 1) {//市价买入
//				holder.countTv.setText(holder.typeTv.getResources().getString(R.string.deal_price_from_total));
//				holder.priceValueTv.setText(String.format("/%s", holder.typeTv.getResources().getString(R.string.market_price)));
//				holder.countValueTv.setText(String.format("/%s", model.amount));
//				holder.dealPriceTv.setText(R.string.history_item_detail_count);
//				holder.dealPriceValue.setText(String.format("%s %s", model.filledQuantity, model.baseAsset));
//				holder.filledCountTv.setText(model.filledAmount);
//			} else {//市价卖出
//				holder.priceValueTv.setText(String.format("/%s", CBRepository.getContext().getResources().getString(R.string.market_price)));
//				holder.countTv.setText(holder.typeTv.getResources().getString(R.string.history_detail_header_count));
//				holder.countValueTv.setText(String.format("/%s", model.quantity));
//				holder.dealPriceTv.setText(R.string.history_detail_header_total);
//				holder.dealPriceValue.setText(String.format("%s %s", model.filledAmount, model.quoteAsset));
//				holder.filledCountTv.setText(model.filledQuantity);
//			}
//		}
//
//		if (model.orderDirection == 1) {//买入向上取整    卖出向下取整
//			holder.avgPriceValueTv.setText(BigDecimalUtils.setScaleUp(model.avgPrice, tradePairInfoTable.pricePrecision));
//		} else {
//			holder.avgPriceValueTv.setText(BigDecimalUtils.setScaleDown(model.avgPrice, tradePairInfoTable.pricePrecision));
//		}
//		if (model.orderDirection == 2) {
//			holder.typeTv.setText(holder.typeTv.getResources().getString(R.string.history_detail_header_sell));
//			if (isRedRise) {
//				holder.typeTv.setTextColor(holder.typeTv.getResources().getColor(R.color.res_green));
//				holder.typeTv.setBackgroundResource(R.drawable.res_green_1e);
//			} else {
//				holder.typeTv.setTextColor(holder.typeTv.getResources().getColor(R.color.res_red));
//				holder.typeTv.setBackgroundResource(R.drawable.res_red_1e);
//			}
//		} else if (model.orderDirection == 1) {
//			holder.typeTv.setText(holder.typeTv.getResources().getString(R.string.history_detail_header_buy));
//			if (isRedRise) {
//				holder.typeTv.setTextColor(holder.typeTv.getResources().getColor(R.color.res_red));
//				holder.typeTv.setBackgroundResource(R.drawable.res_red_1e);
//			} else {
//				holder.typeTv.setTextColor(holder.typeTv.getResources().getColor(R.color.res_green));
//				holder.typeTv.setBackgroundResource(R.drawable.res_green_1e);
//			}
//		}
//		holder.tradePairTv.setText(String.format("%s/%s", model.baseAsset, model.quoteAsset));
//
//
//		if (model.getOrderStatus() == 1) {
//			holder.percent_tv.setText(holder.percent_tv.getResources().getString(R.string.all_deal));
//		} else if (model.getOrderStatus() == 2) {
//			holder.percent_tv.setText(holder.percent_tv.getResources().getString(R.string.order_cancel_label));
//		} else if (model.getOrderStatus() == 3) {
//			holder.percent_tv.setText(holder.percent_tv.getResources().getString(R.string.part_cancel));
//		}
//
//
////		if (TextUtils.isEmpty(model.quantity)) {
////			holder.percent_tv.setText("");
////		} else {
////			if (!TextUtils.isEmpty(model.filledQuantity) && model.quantity.equals(model.filledQuantity)) {
////				holder.percent_tv.setText(holder.percent_tv.getResources().getString(R.string.history_detail_header_percent_all));
////			} else {
////				String percentStr = holder.percent_tv.getResources().getString(R.string.history_detail_header_percent);
////				if (TextUtils.isEmpty(model.filledQuantity) || Double.parseDouble(model.filledQuantity) == 0) {
////					String str = String.format(percentStr, 0) + "%";
////					holder.percent_tv.setText(str);
////				} else {
////					BigDecimal resultDivide = BigDecimalUtils.divideBigDmal(model.filledQuantity, model.quantity, 2);
////					int resultInt = resultDivide.multiply(new BigDecimal(100)).intValue();
////					String str = String.format(percentStr, resultInt) + "%";
////					holder.percent_tv.setText(str);
////				}
////			}
////		}
//
//
//
//
//
//
//		if (!TextUtils.isEmpty(model.totalFee_usdt) && !model.totalFee_usdt.equals("0") && !TextUtils.isEmpty(model.totalFee_coni) && !model.totalFee_coni.equals("0")) {
//			holder.fee_valueTv.setText(String.format("%s %s", PrecisionUtils.getRoundUp(model.totalFee_usdt, "0.000001"), model.quoteAsset));//两种情况
//			holder.fee_valueTv2.setText(String.format("%s CONI", PrecisionUtils.getRoundUp(model.totalFee_coni, "0.000001")));
//
//			holder.fee_valueTv2.setVisibility(View.VISIBLE);
//			holder.fee_valueTv.setVisibility(View.VISIBLE);
//		} else if (TextUtils.isEmpty(model.totalFee_coni) || model.totalFee_coni.equals("0")) {//coni为0
//			holder.fee_valueTv.setText(String.format("%s %s", PrecisionUtils.getRoundUp(model.totalFee_usdt, "0.000001"), model.quoteAsset));
//
//			holder.fee_valueTv2.setVisibility(View.GONE);
//			holder.fee_valueTv.setVisibility(View.VISIBLE);
//		} else {
//			holder.fee_valueTv.setText(String.format("%s CONI", PrecisionUtils.getRoundUp(model.totalFee_coni, "0.000001")));
//			holder.fee_valueTv2.setVisibility(View.GONE);
//			holder.fee_valueTv.setVisibility(View.VISIBLE);
//		}
//	}
//
//	static class HeaderViewHolder extends RecyclerView.ViewHolder {
//		@BindView(R2.id.type_tv)
//		TextView typeTv;
//
//		@BindView(R2.id.pair_tv)
//		TextView tradePairTv;
//
//		@BindView(R2.id.percent_tv)
//		TextView percent_tv;
//		@BindView(R2.id.filled_count_value)
//		TextView filledCountTv;
//		@BindView(R2.id.count_value)
//		TextView countValueTv;
//
//		@BindView(R2.id.avg_price_value)
//		TextView avgPriceValueTv;
//		@BindView(R2.id.price_value)
//		TextView priceValueTv;
//		@BindView(R2.id.deal_price_value)
//		TextView dealPriceValue;
//		@BindView(R2.id.fee_value)
//		TextView fee_valueTv;
//		@BindView(R2.id.fee_value2)
//		TextView fee_valueTv2;
//		@BindView(R2.id.count_tv)
//		TextView countTv;
//		@BindView(R2.id.deal_price_tv)
//		TextView dealPriceTv;
//
//
//
//		HeaderViewHolder(View view) {
//			super(view);
//			ButterKnife.bind(this, view);
//		}
//	}
//
//}
//
