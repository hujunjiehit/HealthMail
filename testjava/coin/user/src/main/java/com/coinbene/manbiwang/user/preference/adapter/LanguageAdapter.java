package com.coinbene.manbiwang.user.preference.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.manbiwang.model.http.LanguageModel;
import com.coinbene.manbiwang.user.R;

import java.util.List;

/**
 * ding
 * 2019-12-12
 * com.coinbene.manbiwang.user.preference.adapter
 */
public class LanguageAdapter extends BaseQuickAdapter<ProductConfig.SupportLanguage, BaseViewHolder> {

	public LanguageAdapter() {
		super(R.layout.site_selector_item);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, ProductConfig.SupportLanguage item) {
		helper.setText(R.id.launague_tv, item.getName());
		helper.setVisible(R.id.check_imgview, item.isSelected());
		helper.setVisible(R.id.line_view, helper.getAdapterPosition() != 0);
	}


}
