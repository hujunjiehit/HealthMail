package com.coinbene.common.activities.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.unknow.ClosePositionOrderBookBinder;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class OrderBookBidsItemBinder extends ItemViewBinder<OrderModel, OrderBookBidsItemBinder.ViewHolder> {

	private int greenColor, redColor, depthGreenColor, depthRedColor, backgroundColor;
	private boolean isRedRise;
	private boolean isBuy;//是不是买  买盘  text位置不一样
	private int amountTextColor;
	private ClosePositionOrderBookBinder.OnItemClick onItemClick;
	private int contractType = -1;
	private String symbol;
	private boolean usedKline;
	private int usedKlineColor, closeColor;

	public OrderBookBidsItemBinder(boolean isBuy, int greenColor, int redColor, boolean isRedRise, int amountTextColor, int contractType) {
		this.isBuy = isBuy;
		this.greenColor = greenColor;
		this.redColor = redColor;
		this.isRedRise = isRedRise;
		this.amountTextColor = amountTextColor;
		this.contractType = contractType;
	}

	public OrderBookBidsItemBinder(boolean isBuy, int greenColor, int redColor, boolean isRedRise, int amountTextColor) {
		this.isBuy = isBuy;
		this.greenColor = greenColor;
		this.redColor = redColor;
		this.isRedRise = isRedRise;
		this.amountTextColor = amountTextColor;
	}

	/**
	 * k线页面的颜色不一样
	 *
	 * @param usedKline
	 */
	public void setUsedKline(boolean usedKline) {
		this.usedKline = usedKline;
	}

	public void setContractType(int contractType) {
		this.contractType = contractType;
	}


	public void setOnItemClick(ClosePositionOrderBookBinder.OnItemClick onItemClick) {
		this.onItemClick = onItemClick;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.kline_item_order_book_bids, parent, false);
		depthGreenColor = root.getContext().getResources().getColor(R.color.res_deepth_green);
		depthRedColor = root.getContext().getResources().getColor(R.color.res_deepth_red);
		backgroundColor = root.getContext().getResources().getColor(R.color.transparent);
		usedKlineColor = root.getContext().getResources().getColor(R.color.color_kline_text1);
		closeColor = root.getContext().getResources().getColor(R.color.res_textColor_2);
		return new ViewHolder(root, this);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderModel item) {
		if (contractType == -1) {//现货
			holder.tvQuantity.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getCntNumContractEn(item.cnt));
		} else {
			if (contractType == Constants.CONTRACT_TYPE_USDT) {
				ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
				holder.tvQuantity.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getQuantityContractRule(TradeUtils.getContractUsdtUnitValue(item.cnt, table)));
			} else {
				holder.tvQuantity.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getQuantityContractRule(item.cnt));
			}
		}
		holder.tvPosition.setText(String.valueOf(getPosition(holder) + 1));
		if (usedKline) {
			holder.tvPosition.setTextColor(usedKlineColor);
		} else {
			holder.tvPosition.setTextColor(closeColor);
		}

		holder.tvPrice.setText(TextUtils.isEmpty(item.price) ? "--" : item.price);
		holder.tvQuantity.setTextColor(amountTextColor);
		holder.tvPrice.setTextColor(isRedRise ? greenColor : redColor);
		holder.progress.setBarColor(backgroundColor, isRedRise ? depthGreenColor : depthRedColor);
		holder.progress.setRotation(0);

		//深度图
		if (BigDecimalUtils.isEmptyOrZero(item.cnt)) {
			holder.progress.setProgress(0, false);
		} else {
			holder.progress.setProgress((int) (item.percent * 100), false);
		}

		if (onItemClick != null) {
			holder.relativeLayout.setOnClickListener(v -> {
				if (onItemClick != null) {
					onItemClick.onItemClick(item.price);
				}
			});
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_price)
		TextView tvPrice;
		@BindView(R2.id.tv_quantity)
		TextView tvQuantity;
		@BindView(R2.id.tv_position)
		TextView tvPosition;
		@BindView(R2.id.qmui_progress)
		QMUIProgressBar progress;
		@BindView(R2.id.rl_root)
		RelativeLayout relativeLayout;

		ViewHolder(View view, OrderBookBidsItemBinder binder) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	public interface OnItemClick {
		void onItemClick(String price);
	}
}
