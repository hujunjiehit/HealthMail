package com.coinbene.manbiwang.debug.networkcapture.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.R;
import com.coinbene.manbiwang.networkcapture.CaptureEntry;

import java.util.List;

/**
 * Created by june
 * on 2019-12-20
 */
public class CaptureListAdapter extends BaseQuickAdapter<CaptureEntry, BaseViewHolder> {

	public CaptureListAdapter(@Nullable List<CaptureEntry> data) {
		super(R.layout.capture_list_item, data);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, CaptureEntry item) {
		helper.setText(R.id.tv_number, helper.getAdapterPosition() + "");

		helper.setText(R.id.tv_status, item.getResponseStatus());

		//helper.setText(R.id.tv_url, item.getRequestUrl().substring(0,20));
	}
}
