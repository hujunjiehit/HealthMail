package com.coinbene.manbiwang.market.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.websocket.model.WsMarketData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by june
 * on 2019-07-27
 */
public class TradePairDiffCallback<T extends WsMarketData> extends DiffUtil.Callback {

	public static final String KEY_UPS_AND_DOWNS = "upsAndDowns";
	public static final String KEY_LAST_PRICE = "lastPrice";
	public static final String KEY_VOLUME_24H = "volume24h";
	public static final String KEY_IS_OPTIONAL = "isOptional";
	public static final String KEY_TAG_CHANGED = "tagChanged";



	private List<T> oldList, newList;

	public TradePairDiffCallback(List<T> oldList, List<T> newList) {
		this.oldList = oldList;
		this.newList = newList;
	}

	@Override
	public int getOldListSize() {
		return oldList != null ? oldList.size() : 0;
	}

	@Override
	public int getNewListSize() {
		return newList != null ? newList.size() : 0;
	}

	@Override
	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
		if (oldList.get(oldItemPosition).getSymbol() == null) {
			return true;
		}
		return oldList.get(oldItemPosition).getSymbol().equals(newList.get(newItemPosition).getSymbol());
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
		if (oldList.get(oldItemPosition).getLastPrice() == null || oldList.get(oldItemPosition).getUpsAndDowns() == null
				|| oldList.get(oldItemPosition).getLocalPrice() == null || oldList.get(oldItemPosition).getVolume24h() == null) {
			return false;
		}
		return oldList.get(oldItemPosition).getLastPrice().equals(newList.get(newItemPosition).getLastPrice())
				&& oldList.get(oldItemPosition).getUpsAndDowns().equals(newList.get(newItemPosition).getUpsAndDowns())
				&& oldList.get(oldItemPosition).getLocalPrice().equals(newList.get(newItemPosition).getLocalPrice())
				&& oldList.get(oldItemPosition).getVolume24h().equals(newList.get(newItemPosition).getVolume24h())
//				&& oldList.get(oldItemPosition).isHot() == newList.get(newItemPosition).isHot()
				&& oldList.get(oldItemPosition).getTagString().equals(newList.get(newItemPosition).getTagString())
				&& oldList.get(oldItemPosition).isOptional() == newList.get(newItemPosition).isOptional();
//				&& oldList.get(oldItemPosition).isLatest() == newList.get(newItemPosition).isLatest();
	}

	@Nullable
	@Override
	public Object getChangePayload(int oldItemPosition, int newItemPosition) {
		WsMarketData oldItem = oldList.get(oldItemPosition);
		WsMarketData newItem = newList.get(newItemPosition);

		List<MarketChangeType> changeTypeList = new ArrayList<>();
		if (!oldItem.getUpsAndDowns().equals(newItem.getUpsAndDowns())) {
			changeTypeList.add(MarketChangeType.UPS_AND_DOWNS);
		}
		if (!oldItem.getLastPrice().equals(newItem.getLastPrice())) {
			changeTypeList.add(MarketChangeType.LAST_PRICE);
		}
		if (!oldItem.getVolume24h().equals(newItem.getVolume24h())) {
			changeTypeList.add(MarketChangeType.VOLUME_24H);
		}
		if (oldItem.isOptional() != newItem.isOptional()) {
			changeTypeList.add(MarketChangeType.IS_OPTIONAL);
		}
		if (!oldItem.getTagString().equals(newItem.getTagString())) {
			changeTypeList.add(MarketChangeType.TAGS);
		}
		return changeTypeList;
	}

	public static void notifyDataSetChanged(List oldList, List newList, RecyclerView.Adapter adapter) {
		TradePairDiffCallback diffCallback = new TradePairDiffCallback(oldList, newList);

		DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback, true);

		result.dispatchUpdatesTo(adapter);
	}
}
