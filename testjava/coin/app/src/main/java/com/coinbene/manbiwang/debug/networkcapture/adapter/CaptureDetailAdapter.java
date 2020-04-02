package com.coinbene.manbiwang.debug.networkcapture.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.R;

import java.util.List;

/**
 * Created by june
 * on 2019-12-20
 */
public class CaptureDetailAdapter extends BaseQuickAdapter<CaptureDetailAdapter.Entry, BaseViewHolder> {

	public CaptureDetailAdapter(@Nullable List<Entry> data) {
		super(R.layout.capture_detail_item, data);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, Entry item) {
		helper.setText(R.id.tv_title, item.title);
		helper.setText(R.id.tv_value, item.value);

	}

	public static class Entry {
		String title;
		String value;

		public Entry(String title, String value) {
			this.title = title;
			this.value = value;
		}
	}
}

