package com.coinbene.manbiwang.spot.spot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 *
 */

public class OrderBookCenterBinder extends ItemViewBinder<OrderLineItemModel, OrderBookCenterBinder.ViewHolder> {

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.spot_fr_order_item_line, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderLineItemModel item) {
		holder.priceTv.setTextColor(SwitchUtils.isRedRise() ? holder.priceTv.getResources().getColor(R.color.res_red) : holder.priceTv.getResources().getColor(R.color.res_green));
		holder.priceTv.setText(item.newPrice);
		holder.local_price_tv.setText(new StringBuilder().append("≈").append(StringUtils.getCnyReplace(item.localPrice)).toString());
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.current_price_tv)
		TextView priceTv;
		@BindView(R2.id.local_price_tv)
		TextView local_price_tv;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
