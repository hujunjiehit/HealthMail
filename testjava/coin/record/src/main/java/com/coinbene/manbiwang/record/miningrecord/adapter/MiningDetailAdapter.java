package com.coinbene.manbiwang.record.miningrecord.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.manbiwang.record.R;

import java.util.List;

/**
 * Created by june
 * on 2019-08-06
 */
public class MiningDetailAdapter extends BaseMultiItemQuickAdapter<MiningItem, BaseViewHolder> {

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with
	 * some initialization data.
	 *
	 * @param data A new list is created out of this one to avoid mutable list
	 */
	public MiningDetailAdapter(List<MiningItem> data) {
		super(data);
		addItemType(MiningItem.TYPE_SUMMARY, R.layout.record_layout_mining_summary_item);
		addItemType(MiningItem.TYPE_DETAIL, R.layout.record_layout_mining_detail_item);
	}

	@Override
	protected void convert(@NonNull BaseViewHolder helper, MiningItem item) {
		switch (item.getItemType()) {
			case MiningItem.TYPE_SUMMARY:
				helper.setText(R.id.tv_mining_total, item.getTokenAmount() + " CFT");
				helper.setText(R.id.tv_mining_day, item.getTradeDayToken());
				helper.setText(R.id.tv_mining_order, item.getOrderToken());
				helper.setText(R.id.tv_mining_sort, item.getSortToken());
				String locked = helper.itemView.getContext().getResources().getString(R.string.res_mining_locked);
				String unlocked = helper.itemView.getContext().getResources().getString(R.string.res_mining_unlocked);
				helper.setText(R.id.tv_mining_summary, String.format("%s %s CFT, %s %s CFT", unlocked, item.getPaidAmount(), locked, item.getFrozenAmount()));
				helper.setVisible(R.id.layout_mining_records, item.isShowMiningRecords());
				break;
			case MiningItem.TYPE_DETAIL:
				helper.setText(R.id.tv_mining_date, " " + item.getDate());
				helper.setText(R.id.tv_mining_day, item.getTradeDayToken());
				helper.setText(R.id.tv_mining_order, item.getOrderToken());
				helper.setText(R.id.tv_mining_sort, item.getSortToken());
				helper.setText(R.id.tv_mining_total, item.getTokenAmount());
				helper.setText(R.id.tv_mining_unlock, item.getPaidAmount());
				helper.setText(R.id.tv_mining_locked, item.getFrozenAmount());
				break;
			default:
				break;
		}
	}
}
