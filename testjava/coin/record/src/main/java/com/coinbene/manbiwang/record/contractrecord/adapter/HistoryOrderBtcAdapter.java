package com.coinbene.manbiwang.record.contractrecord.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.Constants;
import com.coinbene.manbiwang.model.http.HistoryDelegationModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.manbiwang.record.R;


/**
 * @author ding
 * 合约历史委托适配器
 */
public class HistoryOrderBtcAdapter extends BaseQuickAdapter<HistoryDelegationModel.DataBean.ListBean, BaseViewHolder> {

	private String symbleStr;
	private String longStr, shortStr, closeLongStr, closeShortStr;
	private boolean isRedRise;
	private int greenColor, redColor;

	private OnItemClickLisenter onItemClickLisenter;
	private int greenDrawable, redDrawable;


	public void setOnItemClickLisenter(OnItemClickLisenter onItemClickLisenter) {
		this.onItemClickLisenter = onItemClickLisenter;
	}

	/**
	 * 撤单状态
	 */
	private final String STATUS_CANCEL_ORDER = "canceled";

	public HistoryOrderBtcAdapter() {
		super(R.layout.record_item_history_delegation);
	}


	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		symbleStr = parent.getResources().getString(R.string.forever_no_delivery);
		longStr = parent.getResources().getString(R.string.open_long_v2);
		shortStr = parent.getResources().getString(R.string.open_short_v2);
		closeLongStr = parent.getResources().getString(R.string.side_close_long);
		closeShortStr = parent.getResources().getString(R.string.side_close_short);
//        greenColor = parent.getResources().getColor(R.color.res_green);
//        redColor = parent.getResources().getColor(R.color.ma5);
		greenDrawable = R.drawable.bg_green_sharp;
		redDrawable = R.drawable.bg_red_sharp;
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, HistoryDelegationModel.DataBean.ListBean item) {
		isRedRise = SwitchUtils.isRedRise();
//        helper.setText(R.id.delegation_textView18, mContext.getResources().getString(R.string.options_profit) + "(BTC)");

		//合约类型
		helper.setText(R.id.history_delegation_contrancts, String.format(symbleStr, item.getSymbol()));

		//时间
		helper.setText(R.id.history_delegation_time, TimeUtils.getMonthDayHMSFromMillisecond(item.getOrderTime()));

		//委托量
		helper.setText(R.id.history_delegation_vol, item.getQuantity());

		//委托价
		helper.setText(R.id.history_entrust_price, Constants.TYPE_PLAN_MARKET.equals(item.getOrderType()) ? "--" : item.getOrderPrice());

		//成交量
		helper.setText(R.id.history_deal_vol, item.getFilledQuantity());

		//成交价
		helper.setText(R.id.history_deal_price, item.getAveragePrice());

		//委托价值
		helper.setText(R.id.history_entrust_value, item.getOrderValue());

		//手续费
		helper.setText(R.id.history_fee_price, item.getFee());

		//收益
		helper.setText(R.id.history_delegation_profit, item.getProfit());

		//成交比例
		helper.setText(R.id.history_delegation_scale, BigDecimalUtils.divideToPercentage(item.getFilledQuantity(), item.getQuantity()));

		//类型
		setOrderType(helper, item.getOrderType());

		//Tag背景
		setTagBackGround(helper, item);

		if (!STATUS_CANCEL_ORDER.equals(item.getStatus())) {
			helper.itemView.setOnClickListener(v -> {
				if (onItemClickLisenter != null) {
					onItemClickLisenter.onItemClick(item);
				}
			});
		} else {
			helper.itemView.setOnClickListener(null);
		}


		//根据状态设置颜色
		setStatusColor(helper, item.getStatus());
	}

	/**
	 * @param holder
	 * @param item   设置Tag背景
	 */
	private void setTagBackGround(BaseViewHolder holder, HistoryDelegationModel.DataBean.ListBean item) {
		//标签类型设置
		if (item.getDirection().equals("openLong")) {//开多
			holder.setText(R.id.history_delegation_tag, String.format(longStr, item.getLeverage()));
			holder.setBackgroundRes(R.id.history_delegation_tag, isRedRise ? redDrawable : greenDrawable);
		} else if (item.getDirection().equals("openShort")) {//开空
			holder.setText(R.id.history_delegation_tag, String.format(shortStr, item.getLeverage()));
			holder.setBackgroundRes(R.id.history_delegation_tag, isRedRise ? greenDrawable : redDrawable);
		} else if (item.getDirection().equals("closeLong")) {//平多
			holder.setText(R.id.history_delegation_tag, closeLongStr);
			holder.setBackgroundRes(R.id.history_delegation_tag, isRedRise ? greenDrawable : redDrawable);
		} else if (item.getDirection().equals("closeShort")) {//平空
			holder.setText(R.id.history_delegation_tag, closeShortStr);
			holder.setBackgroundRes(R.id.history_delegation_tag, isRedRise ? redDrawable : greenDrawable);
		}

	}

	/**
	 * @param holder
	 * @param type   设置类型
	 */
	private void setOrderType(BaseViewHolder holder, String type) {

		if (Constants.TYPE_LIMIT.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.fixed_price);
		} else if (Constants.TYPE_MARKET.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.market_price);
		} else if (Constants.TYPE_LIQUIDATION.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.delegation_type_liquidation);
		} else if (Constants.TYPE_PLAN_LIMIT.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.delegation_plan);
		} else if (Constants.TYPE_PLAN_MARKET.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.delegation_plan_market);
		} else if (Constants.TYPE_ADL.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.delegation_type_adi);
			holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.res_ItemPress));
		} else if (Constants.TYPE_POST_ONLY.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.only_marker);
		} else if (Constants.TYPE_FOK.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.all_deal_or_cancel);
		} else if (Constants.TYPE_IOC.equals(type)) {
			holder.setText(R.id.history_delegation_type, R.string.deal_cancel_surplus);
		} else {
			holder.setText(R.id.history_delegation_type, "--");
		}

	}


	/**
	 * @param holder
	 * @param status 根据订单状态设置颜色
	 */
	private void setStatusColor(BaseViewHolder holder, String status) {
		if (STATUS_CANCEL_ORDER.equals(status)) {
			holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.res_ItemPress));
			setOrderColor(holder, getRecyclerView().getResources().getColor(R.color.res_textColor_2));
		} else {
			holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.res_background));
			setOrderColor(holder, getRecyclerView().getResources().getColor(R.color.res_textColor_1));
		}
	}


	/**
	 * @param holder
	 * @param colorRes 设置订单颜色
	 */
	private void setOrderColor(BaseViewHolder holder, int colorRes) {

		holder.setTextColor(R.id.history_delegation_contrancts, colorRes);

		holder.setTextColor(R.id.history_delegation_type, colorRes);
		holder.setTextColor(R.id.history_delegation_vol, colorRes);

		holder.setTextColor(R.id.history_deal_vol, colorRes);
		holder.setTextColor(R.id.history_deal_price, colorRes);

		holder.setTextColor(R.id.history_entrust_value, colorRes);
		holder.setTextColor(R.id.history_entrust_price, colorRes);

		holder.setTextColor(R.id.history_delegation_scale, colorRes);
		holder.setTextColor(R.id.history_fee_price, colorRes);

		holder.setTextColor(R.id.history_delegation_profit, colorRes);

	}

	public interface OnItemClickLisenter {
		void onItemClick(HistoryDelegationModel.DataBean.ListBean item);
	}
}
