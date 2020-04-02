package com.coinbene.manbiwang.spot.spot.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class OrderBookItemBinder extends ItemViewBinder<OrderModel, OrderBookItemBinder.ViewHolder> {

	private int greenColor, redColor, depthGreenColor, depthRedColor, backgroundColor;
	private boolean isRedRise;

	private OnItemClickLisenter onItemClickLisenter;
	private int parentHeight;

	public void setOnItemClickLisenter(OnItemClickLisenter onItemClickLisenter) {
		this.onItemClickLisenter = onItemClickLisenter;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.spot_fr_spot_order_item, parent, false);
		greenColor = root.getContext().getResources().getColor(R.color.res_green);
		redColor = root.getContext().getResources().getColor(R.color.res_red);
		depthGreenColor = root.getContext().getResources().getColor(R.color.res_deepth_green);
		depthRedColor = root.getContext().getResources().getColor(R.color.res_deepth_red);
		backgroundColor = root.getContext().getResources().getColor(R.color.transparent);
		return new ViewHolder(root, this);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderModel item) {
		isRedRise = SwitchUtils.isRedRise();
		if (BigDecimalUtils.isEmptyOrZero(item.cnt)) {
			holder.amount_tv.setText("--");
		} else {
			holder.amount_tv.setText(PrecisionUtils.getCntNumContractEn(item.cnt));
		}
		if (item.isSell) {
			holder.priceTv.setTextColor(isRedRise ? greenColor : redColor);
			holder.progress.setBarColor(backgroundColor, isRedRise ? depthGreenColor : depthRedColor);
		} else {
			holder.priceTv.setTextColor(isRedRise ? redColor : greenColor);
			holder.progress.setBarColor(backgroundColor, isRedRise ? depthRedColor : depthGreenColor);
		}
		holder.priceTv.setText(TextUtils.isEmpty(item.price) ? "--" : item.price);

		//深度图
		if (BigDecimalUtils.isEmptyOrZero(item.cnt)) {
			holder.progress.setProgress(0, false);
		} else {
			holder.progress.setProgress((int) (item.percent * 100), false);
		}

		holder.rlRoot.setOnClickListener(v -> {
			if (onItemClickLisenter != null) {
				onItemClickLisenter.onItemClick(item);
			}
		});
	}

	public void setHeight(int height) {
		this.parentHeight = height;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.price_tv)
		TextView priceTv;
		@BindView(R2.id.amount_tv)
		TextView amount_tv;
		@BindView(R2.id.qmui_progress)
		QMUIProgressBar progress;
		@BindView(R2.id.rl_root)
		View rlRoot;

		ViewHolder(View view, OrderBookItemBinder binder) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}


	public interface OnItemClickLisenter {
		void onItemClick(OrderModel item);
	}
}
