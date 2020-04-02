package com.coinbene.manbiwang.market.activity.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author mxd on 2018/3/11.
 */

public class SelfItemBinder extends ItemViewBinder<TradePairInfoTable, SelfItemBinder.ViewHolder> {

	private OnItemClickLisener onItemClickLisener;


	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.self_item_layout, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull TradePairInfoTable item) {

		if (!TextUtils.isEmpty(item.tradePairName))
			holder.name_tv.setText(item.tradePairName);
		holder.rootLayout.setEnabled(false);
		holder.rootLayout.setOnClickListener(v -> {
			if (onItemClickLisener != null) {
				onItemClickLisener.itemClick(getPosition(holder));
			}
		});
		holder.iv_delete.setOnClickListener(v -> {
			if (onItemClickLisener != null) {
				onItemClickLisener.delete(getPosition(holder));
			}
		});
		holder.ll_delete.setOnClickListener(v -> {
			if (onItemClickLisener != null) {
				onItemClickLisener.delete(getPosition(holder));
			}
		});
		holder.rl_to_top.setOnClickListener(v -> {
			if (onItemClickLisener != null) {
				onItemClickLisener.toTop(getPosition(holder));
			}
		});
	}


	public OnItemClickLisener getOnItemClickLisener() {
		return onItemClickLisener;
	}

	public void setOnItemClickLisener(OnItemClickLisener onItemClickLisener) {
		this.onItemClickLisener = onItemClickLisener;
	}

	public interface OnItemClickLisener {
		void itemClick(int position);

		void delete(int position);

		void toTop(int position);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.root_layout)
		View rootLayout;
		@BindView(R2.id.name_text)
		TextView name_tv;
		@BindView(R2.id.name_text_detail)
		TextView name_text_detail;
		@BindView(R2.id.iv_delete)
		ImageView iv_delete;
		@BindView(R2.id.iv_to_top)
		ImageView iv_to_top;
		@BindView(R2.id.rl_to_top)
		View rl_to_top;
		@BindView(R2.id.iv_drag)
		ImageView iv_drag;
		@BindView(R2.id.ll_delete)
		LinearLayout ll_delete;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
