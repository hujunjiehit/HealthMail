package com.coinbene.manbiwang.record.coinrecord.adapter;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.balance.TransferUtils;
import com.coinbene.manbiwang.model.http.TransferRecordResponse;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.balance.Product;
import com.coinbene.manbiwang.record.R;

/**
 * Created by june
 * on 2019-09-12
 */
public class TransferRecordAdapter extends BaseQuickAdapter<TransferRecordResponse.DataBean.ListBean, BaseViewHolder> {

	private SparseArray<Product> mProductMap;

	public TransferRecordAdapter() {
		super(R.layout.record_item_tansfer_record);
	}

	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (mProductMap == null) {
			mProductMap = new SparseArray<>();
			TransferUtils.initProductMap(parent.getContext(), mProductMap);
		}
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, TransferRecordResponse.DataBean.ListBean item) {
		//资产类型
		helper.setText(R.id.textView8, item.getAsset());

		//数量
		helper.setText(R.id.tansfer_record_assets, PrecisionUtils.getSingleRoundDown(item.getAmount(), "0.00000000"));

		//时间
		helper.setText(R.id.transfer_record_time, TimeUtils.getDateTimeFromMillisecond(Long.valueOf(item.getCreateTime())));

		//来自
		if (mProductMap.get(item.getFromProduct()).getType() == Product.TYPE_MARGIN && !TextUtils.isEmpty(item.getFromSubProduct())) {
			helper.setText(R.id.transfer_record_from,
					String.format("%s(%s)", mProductMap.get(item.getFromProduct()).getProductName(), item.getFromSubProduct()));
		} else {
			helper.setText(R.id.transfer_record_from, mProductMap.get(item.getFromProduct()).getProductName());
		}

		//转至
		if (mProductMap.get(item.getToProduct()).getType() == Product.TYPE_MARGIN && !TextUtils.isEmpty(item.getToSubProduct())) {
			helper.setText(R.id.transfer_record_to,
					String.format("%s(%s)", mProductMap.get(item.getToProduct()).getProductName(), item.getToSubProduct()));
		} else {
			helper.setText(R.id.transfer_record_to, mProductMap.get(item.getToProduct()).getProductName());
		}
	}
}
