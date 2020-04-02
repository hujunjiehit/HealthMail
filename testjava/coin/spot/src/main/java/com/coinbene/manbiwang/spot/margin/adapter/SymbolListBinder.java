package com.coinbene.manbiwang.spot.margin.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 杠杆切换币对
 */

public class SymbolListBinder extends ItemViewBinder<String, SymbolListBinder.ViewHolder> {

	private OnItemClickContrackListener onItemClickContrackListener;
	private String selectSymbol;
	private int selectColor, defauteColor;


	public void setSelect(String symbol) {
		this.selectSymbol = symbol;
	}

	public void setOnItemClickContrackListener(OnItemClickContrackListener onItemClickContrackListener) {
		this.onItemClickContrackListener = onItemClickContrackListener;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.spot_item_margin_change, parent, false);
		selectColor = parent.getResources().getColor(R.color.res_blue);
		defauteColor = parent.getResources().getColor(R.color.res_textColor_1);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull String symbol) {

		holder.tvSymbol.setText(TextUtils.isEmpty(symbol) ? "--" : symbol);

		if (getPosition(holder) == getAdapter().getItemCount() - 1) {
			holder.viewLine.setVisibility(View.GONE);
		} else {
			holder.viewLine.setVisibility(View.VISIBLE);
		}


		if (!TextUtils.isEmpty(selectSymbol) && selectSymbol.equals(symbol)) {
			holder.tvSymbol.setTextColor(selectColor);
		} else {
			holder.tvSymbol.setTextColor(defauteColor);
		}

		holder.llRoot.setOnClickListener(v -> {
			if (onItemClickContrackListener != null) {
				onItemClickContrackListener.onItemClickContrck(symbol);
			}
		});

	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R2.id.tv_symbol)
		TextView tvSymbol;
		@BindView(R2.id.ll_root)
		View llRoot;
		@BindView(R2.id.view_line)
		View viewLine;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	public interface OnItemClickContrackListener {
		void onItemClickContrck(String symbol);
	}
}
