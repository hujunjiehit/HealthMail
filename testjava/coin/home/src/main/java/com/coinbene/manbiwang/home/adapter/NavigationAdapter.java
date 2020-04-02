package com.coinbene.manbiwang.home.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.manbiwang.home.R;
import com.coinbene.manbiwang.model.http.AppConfigModel;

/**
 * ding
 * 2020-01-06
 * com.coinbene.manbiwang.home.adapter
 */
public class NavigationAdapter extends BaseQuickAdapter<AppConfigModel.MainNavigationBean, BaseViewHolder> {

	public NavigationAdapter() {
		super(R.layout.item_home_navigation);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, AppConfigModel.MainNavigationBean item) {
		GlideUtils.loadImageViewLoad(CBRepository.getContext(),item.getImgUrl(),helper.getView(R.id.img_Icon),R.drawable.icon_home_loading,R.drawable.icon_home_loading);
		String text = item.getLang().get(LanguageHelper.getLocaleCode(helper.getView(R.id.tv_Dec).getContext()));
		helper.setText(R.id.tv_Dec, !TextUtils.isEmpty(text)?text:item.getLang().get("en_US"));

	}
}
