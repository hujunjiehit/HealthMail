package com.coinbene.manbiwang.contract.newcontract.bottom.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.layout.SubteactPositionLayout;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;

/**
 * 当前持仓adapter
 */
public class CurrentPositionAdapter extends BaseQuickAdapter<ContractPositionListModel.DataBean, BaseViewHolder> {

	private ClickHoldPostionListener clickHoldPostionListener;

	private String symbleStr, longStr, shortStr, avlTotalUnit;

	private boolean isRedRaise;
	private int greenDrawable, redDrawable;
	private int greeColor, redColor;
	private Context mContext;

	private SpannableString spanString;
	private ForegroundColorSpan colorSpan;


	public CurrentPositionAdapter() {
		super(R.layout.item_current_position);
	}

	public void setClickHoldPostionListener(ClickHoldPostionListener clickHoldPostionListener) {
		this.clickHoldPostionListener = clickHoldPostionListener;
	}

	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		mContext = parent.getContext();
		colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.res_blue));
		avlTotalUnit = parent.getResources().getString(R.string.avl_total_unit);
		symbleStr = parent.getResources().getString(R.string.forever_no_delivery);
		longStr = parent.getResources().getString(R.string.side_long);
		shortStr = parent.getResources().getString(R.string.side_short);
		greenDrawable = R.drawable.bg_green_sharp;
		redDrawable = R.drawable.bg_red_sharp;
		greeColor = parent.getResources().getColor(R.color.res_green);
		redColor = parent.getResources().getColor(R.color.res_red);
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder hd, ContractPositionListModel.DataBean item) {
		isRedRaise = SwitchUtils.isRedRise();

		if (TradeUtils.isUsdtContract(item.getSymbol())) {
			//usdt合约
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(item.getSymbol());
			if (table == null) {
				table = new ContractUsdtInfoTable();
			}
			hd.setText(R.id.tv_total_hold, String.format(avlTotalUnit, TradeUtils.getContractUsdtUnit(table)));
			hd.setText(R.id.tv_total_hold_value, String.format("%s/%s", TradeUtils.getContractUsdtUnitValue(item.getAvailableQuantity(), table), TradeUtils.getContractUsdtUnitValue(item.getQuantity(), table)));
			hd.setText(R.id.tv_contrack_name, String.format(symbleStr, table.baseAsset));

			hd.setText(R.id.tv_unrealized_profit_loss, mContext.getString(R.string.unrealized_profit_loss_usdt));
			hd.setText(R.id.tv_realized_profit_loss, mContext.getString(R.string.realized_profit_loss_usdt));
			hd.setText(R.id.tv_usage_bond, mContext.getString(R.string.usage_bond_usdt));
		} else {
			//btc合约
			hd.setText(R.id.tv_total_hold, String.format(avlTotalUnit, mContext.getString(R.string.number)));
			hd.setText(R.id.tv_total_hold_value, String.format("%s/%s", item.getAvailableQuantity(), item.getQuantity()));
			hd.setText(R.id.tv_contrack_name, String.format(symbleStr, item.getSymbol()));

			hd.setText(R.id.tv_unrealized_profit_loss, mContext.getString(R.string.unrealized_profit_loss_btc));
			hd.setText(R.id.tv_realized_profit_loss, mContext.getString(R.string.realized_profit_loss_btc));
			hd.setText(R.id.tv_usage_bond, mContext.getString(R.string.usage_bond_btc));
		}


		if (item.getSide().equals("long")) {//多
			hd.setText(R.id.current_delegation_tag, String.format(longStr, item.getLeverage()));
			hd.setBackgroundRes(R.id.current_delegation_tag, isRedRaise ? redDrawable : greenDrawable);
		} else {//空
			hd.setText(R.id.current_delegation_tag, String.format(shortStr, item.getLeverage()));
			hd.setBackgroundRes(R.id.current_delegation_tag, isRedRaise ? greenDrawable : redDrawable);
		}
		if (BigDecimalUtils.lessThanToZero(item.getRoe())) {
			hd.setTextColor(R.id.tv_unrealized_profit_loss_value, isRedRaise ? greeColor : redColor);
		} else {
			hd.setTextColor(R.id.tv_unrealized_profit_loss_value, isRedRaise ? redColor : greeColor);
		}
		if (BigDecimalUtils.lessThanToZero(item.getRealisedPnl())) {
			hd.setTextColor(R.id.tv_realized_profit_loss_value, isRedRaise ? greeColor : redColor);
		} else {
			hd.setTextColor(R.id.tv_realized_profit_loss_value, isRedRaise ? redColor : greeColor);
		}

		//回报率
//		if (BigDecimalUtils.lessThanToZero(item.getRoe())) {
//			hd.setTextColor(R.id.tv_return_percent_value, isRedRaise ? greeColor : redColor);
//		} else {
//			hd.setTextColor(R.id.tv_return_percent_value, isRedRaise ? redColor : greeColor);
//		}
//		hd.setText(R.id.tv_return_percent_value, BigDecimalUtils.toPercentage(item.getRoe()));


		hd.setText(R.id.tv_entrust_price_value, item.getAvgPrice());
		hd.setText(R.id.tv_force_price_value, item.getLiquidationPrice());
		hd.setText(R.id.tv_unrealized_profit_loss_value, item.getUnrealisedPnl() + "(" + BigDecimalUtils.toPercentage(item.getRoe()) + ")");
		hd.setText(R.id.tv_realized_profit_loss_value, item.getRealisedPnl());
		hd.setText(R.id.tv_usage_bond_value, item.getPositionMargin());
		((SubteactPositionLayout) hd.getView(R.id.tv_substrack_queue_value)).setLever(item.getWeight());


		if (item.getMarginMode().equals(Constants.MODE_FIXED)) {
			spanString = new SpannableString((TradeUtils.isUsdtContract(item.getSymbol()) ? "±₮ " : "±฿ ") + item.getPositionMargin());
			spanString.setSpan(colorSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			hd.setText(R.id.tv_usage_bond_value, spanString);
			hd.getView(R.id.tv_usage_bond_value).setOnClickListener(v -> clickHoldPostionListener.updateMargin(item));
		} else {
			hd.setText(R.id.tv_usage_bond_value, item.getPositionMargin());
			hd.getView(R.id.tv_usage_bond_value).setOnClickListener(null);
		}

		hd.getView(R.id.iv_share).setOnClickListener(v -> {
			if (clickHoldPostionListener != null) {
				clickHoldPostionListener.clickShare(item);
			}
		});
		hd.getView(R.id.tv_close_hold).setOnClickListener(v -> {
			if (clickHoldPostionListener != null) {
				clickHoldPostionListener.clickClosePosition(item);
			}
		});
		hd.getView(R.id.tv_target_profit).setOnClickListener(v -> {
			if (clickHoldPostionListener != null) {
				clickHoldPostionListener.clickTargetProfit(item, 0);
			}
		});
		hd.getView(R.id.tv_stop_loss).setOnClickListener(v -> {
			if (clickHoldPostionListener != null) {
				clickHoldPostionListener.clickStopLoss(item, 1);
			}
		});
		hd.getView(R.id.tv_quick_close).setOnClickListener(v -> {
			if (clickHoldPostionListener != null) {
				clickHoldPostionListener.clickQuickClosePosition(item);
			}
		});

		hd.getView(R.id.iv_substrack_queue_tips).setOnClickListener(v -> {
			DialogManager.getMessageDialogBuilder(v.getContext())
					.setMessage(v.getContext().getString(R.string.res_substract_tips))
					.showDialog();
		});
	}


	public interface ClickHoldPostionListener {
		void clickShare(ContractPositionListModel.DataBean item);

		void clickTargetProfit(ContractPositionListModel.DataBean item, int planType);

		void clickStopLoss(ContractPositionListModel.DataBean item, int planType);

		void clickClosePosition(ContractPositionListModel.DataBean item);

		void clickQuickClosePosition(ContractPositionListModel.DataBean item);

		/**
		 * 修改保证金
		 */
		void updateMargin(ContractPositionListModel.DataBean data);
	}
}
