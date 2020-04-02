package com.coinbene.common.utils;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.http.OrderModel;

import java.util.Comparator;

/**
 * 挂单排序
 *
 * @author huyong
 */
public class OrderListSortUtil implements Comparator<OrderModel> {

	//是否倒叙
	boolean isAsc = true;

	public void setAsc(boolean asc) {
		isAsc = asc;
	}

	@Override
	public int compare(OrderModel o1, OrderModel o2) {
		String p1 = null;
		if (TextUtils.isEmpty(o1.price) || "-".equals(o1.price)) {
			p1 = "0";
		} else {
			p1 = o1.price;
		}
		String p2 = null;
		if (TextUtils.isEmpty(o2.price) || "-".equals(o2.price)) {
			p2 = "0";
		} else {
			p2 = o2.price;
		}
		if (isAsc) {
			return Double.valueOf(p2).compareTo(Double.valueOf(p1));
		} else {
			return Double.valueOf(p1).compareTo(Double.valueOf(p2));
		}
	}
}
