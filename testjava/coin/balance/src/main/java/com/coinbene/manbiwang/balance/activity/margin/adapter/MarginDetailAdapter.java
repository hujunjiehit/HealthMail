package com.coinbene.manbiwang.balance.activity.margin.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.SingleAccountModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.balance.R;

import java.util.List;

/**
 * Created by june
 * on 2019-08-20
 */
public class MarginDetailAdapter extends BaseMultiItemQuickAdapter<MarginDetailItem, BaseViewHolder> {

	private Group group;

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with
	 * some initialization data.
	 *
	 * @param data A new list is created out of this one to avoid mutable list
	 */
	public MarginDetailAdapter(List<MarginDetailItem> data) {
		super(data);
		addItemType(MarginDetailItem.TYPE_HEADER, R.layout.item_margin_detail_header);
		addItemType(MarginDetailItem.TYPE_DETAIL, R.layout.item_margin_detail_item);
		addItemType(MarginDetailItem.TYPE_DETAIL_EMPTY, R.layout.item_detail_empty);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, MarginDetailItem item) {
		switch (item.getItemType()) {
			case MarginDetailItem.TYPE_HEADER:
				//头部item
				if (item.getSingleAccountModel() == null || item.getSingleAccountModel().getData() == null) {
					return;
				}

				//爆仓价
				if (BigDecimalUtils.isEmptyOrZero(item.getSingleAccountModel().getData().getForceClosePrice())) {
					helper.setText(R.id.tv_force_close_price, "--");
				} else {
					helper.setText(R.id.tv_force_close_price, item.getSingleAccountModel().getData().getForceClosePrice());
				}

				//风险率
				helper.setText(R.id.tv_risk_rate, TradeUtils.getDisplayRiskRate(item.getSingleAccountModel().getData().getRiskRate()));

				if (item.getSingleAccountModel().getData().getBalanceList() == null || item.getSingleAccountModel().getData().getBalanceList().size() < 2) {
					return;
				}

				//分子资产详情
				SingleAccountModel.DataBean.BalanceListBean balanceFenzi = item.getSingleAccountModel().getData().getBalanceList().get(0);
				helper.setText(R.id.tv_asset_fenzi, balanceFenzi.getAsset());
				helper.setText(R.id.tv_fenzi_available, balanceFenzi.getAvailable());
				helper.setText(R.id.tv_fenzi_frozen, balanceFenzi.getFrozen());
				helper.setText(R.id.tv_fenzi_borrow, balanceFenzi.getBorrow());
				helper.setText(R.id.tv_fenzi_interest, balanceFenzi.getInterest());

				//分母资产详情
				SingleAccountModel.DataBean.BalanceListBean balanceFenmu = item.getSingleAccountModel().getData().getBalanceList().get(1);
				helper.setText(R.id.tv_asset_fenmu, balanceFenmu.getAsset());
				helper.setText(R.id.tv_fenmu_available, balanceFenmu.getAvailable());
				helper.setText(R.id.tv_fenmu_frozen, balanceFenmu.getFrozen());
				helper.setText(R.id.tv_fenmu_borrow, balanceFenmu.getBorrow());
				helper.setText(R.id.tv_fenmu_interest, balanceFenmu.getInterest());

				helper.addOnClickListener(R.id.tv_history_borrow);
				break;

			case MarginDetailItem.TYPE_DETAIL:

				helper.setText(R.id.tv_borrow_asset, item.getOrderListItem().getAsset());
				helper.setText(R.id.tv_borrow_time, TimeUtils.getDateTimeFromMillisecond(Tools.parseLong(item.getOrderListItem().getCreateTime())));
				helper.setText(R.id.tv_ammount_borrow, item.getOrderListItem().getBorrowQuantity());
				helper.setText(R.id.tv_amount_unpaid, item.getOrderListItem().getUnRepayQuantity());
				helper.setText(R.id.tv_interest_unpaid, item.getOrderListItem().getUnRepayInterest());
				break;

			case MarginDetailItem.TYPE_DETAIL_EMPTY:
				//no item
				break;
			default:
				break;
		}
	}
}
