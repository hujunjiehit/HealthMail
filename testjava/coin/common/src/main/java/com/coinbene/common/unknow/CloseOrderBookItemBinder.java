package com.coinbene.common.unknow;

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
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class CloseOrderBookItemBinder extends ItemViewBinder<OrderModel, CloseOrderBookItemBinder.ViewHolder> {

	private int greenColor, redColor, depthGreenColor, depthRedColor, backgroundColor;
	private boolean isRedRise;
	private boolean isBuy;//是不是买  买盘  text位置不一样
	private int amountTextColor;
	private ClosePositionOrderBookBinder.OnItemClick onItemClick;
	private int contractType = Constants.CONTRACT_TYPE_BTC;
	private String symbol;

	public CloseOrderBookItemBinder(boolean isBuy, int greenColor, int redColor, boolean isRedRise, int amountTextColor, int contractType) {
		this.isBuy = isBuy;
		this.greenColor = greenColor;
		this.redColor = redColor;
		this.isRedRise = isRedRise;
		this.amountTextColor = amountTextColor;
		this.contractType = contractType;
	}

	public CloseOrderBookItemBinder(boolean isBuy, int greenColor, int redColor, boolean isRedRise, int amountTextColor) {
		this.isBuy = isBuy;
		this.greenColor = greenColor;
		this.redColor = redColor;
		this.isRedRise = isRedRise;
		this.amountTextColor = amountTextColor;
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
		View root = inflater.inflate(R.layout.commom_item_close_order_book, parent, false);
		depthGreenColor = root.getContext().getResources().getColor(R.color.res_deepth_green);
		depthRedColor = root.getContext().getResources().getColor(R.color.res_deepth_red);
		backgroundColor = root.getContext().getResources().getColor(R.color.transparent);
		return new ViewHolder(root, this);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderModel item) {
		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			if (isBuy)
				holder.priceTv.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getCntNumContractEn(TradeUtils.getContractUsdtUnitValue(item.cnt, table)));
			else
				holder.amount_tv.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getCntNumContractEn(TradeUtils.getContractUsdtUnitValue(item.cnt, table)));
		} else {
			if (isBuy) {
				holder.priceTv.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getCntNumContractEn(item.cnt));
			} else {
				holder.amount_tv.setText((TextUtils.isEmpty(item.cnt) || "0".equals(item.cnt)) ? "--" : PrecisionUtils.getCntNumContractEn(item.cnt));
			}
		}


		if (isBuy) {
			holder.amount_tv.setText(TextUtils.isEmpty(item.price) ? "--" : item.price);
			holder.priceTv.setTextColor(amountTextColor);
			holder.amount_tv.setTextColor(isRedRise ? redColor : greenColor);
			holder.progress.setBarColor(backgroundColor, isRedRise ? depthRedColor : depthGreenColor);
			holder.progress.setRotation(180);
		} else {

			holder.priceTv.setText(TextUtils.isEmpty(item.price) ? "--" : item.price);
			holder.amount_tv.setTextColor(amountTextColor);
			holder.priceTv.setTextColor(isRedRise ? greenColor : redColor);
			holder.progress.setBarColor(backgroundColor, isRedRise ? depthGreenColor : depthRedColor);
			holder.progress.setRotation(0);
		}

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
		@BindView(R2.id.price_tv)
		TextView priceTv;
		@BindView(R2.id.amount_tv)
		TextView amount_tv;
		@BindView(R2.id.qmui_progress)
		QMUIProgressBar progress;
		@BindView(R2.id.rl_root)
		RelativeLayout relativeLayout;

		ViewHolder(View view, CloseOrderBookItemBinder binder) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	public interface OnItemClick {
		void onItemClick(String price);
	}
}
