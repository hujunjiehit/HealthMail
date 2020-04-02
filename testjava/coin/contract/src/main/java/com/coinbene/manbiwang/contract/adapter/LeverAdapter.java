package com.coinbene.manbiwang.contract.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.contract.R;

import java.util.List;

public class LeverAdapter extends RecyclerView.Adapter<BaseViewHolder> {

	private List<String> list;
	private LeverListener listener;
	private String currentLever;

	public LeverAdapter(String currentLever) {
		this.currentLever = currentLever;
	}

	public void setCurrentLever(String currentLever) {
		this.currentLever = currentLever;
	}

	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lever, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

		if (list.get(position) == null) {
			return;
		}
		String str = String.format("%sX", list.get(position));
		if (!TextUtils.isEmpty(currentLever) && currentLever.trim().equals(str.trim())) {
			holder.setTextColor(R.id.item_lever_text, "#3b7bfd");
		} else {
			holder.setTextColor(R.id.item_lever_text, "#7c88a0");
		}
		holder.setText(R.id.item_lever_text, str);


		holder.itemView.setOnClickListener(v -> {
			if (listener != null) {
				listener.leverClick(list.get(position), str);
			}

		});

	}

	@Override
	public int getItemCount() {
		return list == null ? 0 : list.size();
	}

	public void setItem(List<String> list) {
		if (list != null) {
			this.list = list;
		}
		notifyDataSetChanged();
	}


	public void setListener(LeverListener listener) {
		this.listener = listener;
	}

	public interface LeverListener {
		void leverClick(String lever, String str);
	}

}
