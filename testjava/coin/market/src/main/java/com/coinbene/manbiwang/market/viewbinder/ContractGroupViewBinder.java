package com.coinbene.manbiwang.market.viewbinder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-09-13
 */
public class ContractGroupViewBinder extends ItemViewBinder<String, ContractGroupViewBinder.ViewHolder> {

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_contract_group, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull String contractGroup) {
		if (!TextUtils.isEmpty(contractGroup)) {
			holder.mTvGroupName.setText(contractGroup);
		}
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
