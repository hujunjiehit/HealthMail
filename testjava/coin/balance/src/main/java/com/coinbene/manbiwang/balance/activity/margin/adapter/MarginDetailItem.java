package com.coinbene.manbiwang.balance.activity.margin.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.coinbene.manbiwang.model.http.MarginOpenOrderlistModel;
import com.coinbene.manbiwang.model.http.SingleAccountModel;

/**
 * Created by june
 * on 2019-08-20
 */
public class MarginDetailItem implements MultiItemEntity {

	public static final int TYPE_HEADER = 1;
	public static final int TYPE_DETAIL = 2;
	public static final int TYPE_DETAIL_EMPTY = 3;

	private int itemType;

	private SingleAccountModel singleAccountModel;
	private MarginOpenOrderlistModel.DataBean.ListBean orderListItem;

	public MarginDetailItem(int itemType) {
		this.itemType = itemType;
	}

	@Override
	public int getItemType() {
		return itemType;
	}

	public SingleAccountModel getSingleAccountModel() {
		return singleAccountModel;
	}

	public void setSingleAccountModel(SingleAccountModel singleAccountModel) {
		this.singleAccountModel = singleAccountModel;
	}

	public MarginOpenOrderlistModel.DataBean.ListBean getOrderListItem() {
		return orderListItem;
	}

	public void setOrderListItem(MarginOpenOrderlistModel.DataBean.ListBean orderListItem) {
		this.orderListItem = orderListItem;
	}
}
