package com.coinbene.common.activities.fragment.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.R;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.utils.LanguageHelper;

/**
 * ding
 * 2019-07-17
 * com.coinbene.common.activitys.adapters
 */
public class SearchPairAdapter extends BaseQuickAdapter<TradePairInfoTable, BaseViewHolder> {

    public SearchPairAdapter() {
        super(R.layout.common_item_search_pair);
    }

    @Override
    protected void convert(BaseViewHolder helper, TradePairInfoTable item) {

        if (!TextUtils.isEmpty(item.tradePairName)) {
            helper.setText(R.id.tv_PairName, item.tradePairName);
        }

        if (LanguageHelper.isChinese(helper.itemView.getContext())) {
            if (!TextUtils.isEmpty(item.localBaseAsset)) {
                helper.setText(R.id.tv_PairDetail, item.localBaseAsset);
            }
        } else {
            helper.setText(R.id.tv_PairDetail, item.englishBaseAsset);
        }

        if (item.isOptional) {
            helper.setImageResource(R.id.img_Collection, R.drawable.icon_self_select);
        } else {
            helper.setImageResource(R.id.img_Collection, R.drawable.icon_self);
        }

        helper.addOnClickListener(R.id.ll_add_self);
    }
}
