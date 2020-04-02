package com.coinbene.manbiwang.record;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-09-20
 */
public class RecordGroupViewBinder extends ItemViewBinder<String, RecordGroupViewBinder.ViewHolder> {



	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.record_item_record_group, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull String recordGroup) {
		holder.mTvGroupName.setText(recordGroup);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_group_name)
		TextView mTvGroupName;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
