package com.coinbene.manbiwang.fortune.activity.adapter;

import androidx.annotation.NonNull;
import android.util.SparseArray;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.YbbRecordModel;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.manbiwang.fortune.R;

/**
 * Created by june
 * on 2019-10-18
 */
public class YbbRecordAdapter extends BaseQuickAdapter<YbbRecordModel.DataBean.ListBean, BaseViewHolder> {

	SparseArray<String> bizTypeMap;

	public YbbRecordAdapter() {
		super(R.layout.fortune_ybb_record_item);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, YbbRecordModel.DataBean.ListBean item) {

		if (bizTypeMap == null) {
			bizTypeMap = new SparseArray<>();
			bizTypeMap.put(3, mContext.getString(R.string.transferIn));
			bizTypeMap.put(4, mContext.getString(R.string.transferOut));
			bizTypeMap.put(5, mContext.getString(R.string.options_profit));
		}

		helper.setText(R.id.tv_type_desc, String.format("%s%s",item.getAsset(), bizTypeMap.get(item.getBizType(), "--")));

		helper.setText(R.id.tv_change_value, item.getChange());

		helper.setText(R.id.tv_create_time, TimeUtils.getDateTimeFromMillisecond(Tools.parseLong(item.getCreateTime())));

		helper.setText(R.id.tv_balance, String.format("%s: %s", mContext.getString(R.string.res_overage), item.getBalance()));
	}
}
