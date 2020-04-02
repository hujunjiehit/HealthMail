package com.coinbene.manbiwang.user.about.adapter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.about.CommunityActivity;

/**
 * ding
 * 2019-12-25
 * com.coinbene.manbiwang.user.about.adapter
 */
public class CommunityAdapter extends BaseQuickAdapter<CommunityActivity.CommunityModel, BaseViewHolder> {
	public CommunityAdapter() {
		super(R.layout.item_community);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CommunityActivity.CommunityModel item) {
		if (helper.getAdapterPosition() == getItemCount() - 1) {
			helper.setGone(R.id.item_divider, false);
		}

		helper.setText(R.id.tv_CommunityDec, item.getDec());
		helper.setText(R.id.tv_CommunityName,item.getCommunityName());
		helper.setImageResource(R.id.iv_CommunityIcon,item.getIconID());
	}
}
