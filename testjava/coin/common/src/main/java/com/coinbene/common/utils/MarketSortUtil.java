package com.coinbene.common.utils;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.websocket.model.WsMarketData;

import java.util.Comparator;

/**
 * 行情排序
 *
 * @author huyong
 */
public class MarketSortUtil implements Comparator {
	/**
	 * 排序规则   升序、降序
	 */
	private String ASC;
	/**
	 * //排序字段
	 */
	private String sortField;
	/**
	 * 是否是默认排序  通过sort
	 */
	private boolean isSort;

	public MarketSortUtil() {
	}

	public MarketSortUtil(boolean isSort, String ASC, String sortField) {
		this.ASC = ASC;
		this.sortField = sortField;
		this.isSort = isSort;
	}

	public void setASC(String ASC) {
		this.ASC = ASC;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public void setSort(boolean sort) {
		isSort = sort;
	}

	@Override
	public int compare(Object o1, Object o2) {
		WsMarketData data1 = (WsMarketData) o1;
		WsMarketData data2 = (WsMarketData) o2;
		if (isSort) {
//            DLog.e("sort", "sort1 = " + data1.getSort() + "sort2 = " + data2.getSort());
			return data1.getSort() - data2.getSort();
		} else if (ASC.equals("ASC")) {
			if (sortField.equals(Constants.SORT_COIN_NAME)) {
//                DLog.e("sort", "s1 = " + data1.getS() + "s2 = " + data2.getS());
				String s1;
				if (TextUtils.isEmpty(data1.getSymbol())) {
					s1 = data1.getTradePairName();
				} else {
					s1 = data1.getSymbol();
				}
				String s2;
				if (TextUtils.isEmpty(data2.getSymbol())) {
					s2 = data2.getTradePairName();
				} else {
					s2 = data2.getSymbol();
				}
				return s1.compareTo(s2);
			} else if (sortField.equals(Constants.SORT_V24_VOL)) {

				String amt1;
				if (TextUtils.isEmpty(data1.getVolume24h()) || "--".equals(data1.getVolume24h())) {
					amt1 = "0";
				} else {
					amt1 = data1.getVolume24h();
				}


				String amt2;
				if (TextUtils.isEmpty(data2.getVolume24h()) || "--".equals(data2.getVolume24h())) {
					amt2 = "0";
				} else {
					amt2 = data2.getVolume24h();
				}
//                DLog.e("sort", "amt1 = " + amt1 + "amt2 = " + amt2);
				return Double.valueOf(amt1).compareTo(Double.valueOf(amt2));
			} else if (sortField.equals(Constants.SORT_LAST_PRICE)) {
				String nl1;
				if (TextUtils.isEmpty(data1.getLastPrice()) || "--".equals(data1.getLastPrice())) {
					nl1 = "0";
				} else {
					nl1 = data1.getLastPrice();

				}
				String nl2;
				if (TextUtils.isEmpty(data2.getLastPrice()) || "--".equals(data2.getLastPrice())) {
					nl2 = "0";
				} else {
					nl2 = data2.getLastPrice();

				}
//                DLog.e("sort", "nl1 = " + nl1 + "nl2 = " + nl2);
				return Double.valueOf(nl1).compareTo(Double.valueOf(nl2));
			} else if (sortField.equals(Constants.SORT_FALL_REISE)) {
				String p1 = null;
				if (TextUtils.isEmpty(data1.getUpsAndDowns()) || "--".equals(data1.getUpsAndDowns())) {
					p1 = "0";
				} else {
					if (!Character.isDigit(data1.getUpsAndDowns().charAt(data1.getUpsAndDowns().length() - 1))) {
						p1 = data1.getUpsAndDowns().substring(0, data1.getUpsAndDowns().length() - 1);
					} else {
						p1 = data1.getUpsAndDowns();
					}

				}

				String p2;
				if (TextUtils.isEmpty(data2.getUpsAndDowns()) || "--".equals(data2.getUpsAndDowns())) {
					p2 = "0";
				} else {
					if (!Character.isDigit(data2.getUpsAndDowns().charAt(data2.getUpsAndDowns().length() - 1))) {
						p2 = data2.getUpsAndDowns().substring(0, data2.getUpsAndDowns().length() - 1);
					} else {
						p2 = data2.getUpsAndDowns();
					}

				}
//                DLog.e("sort", "p1 = " + p1 + "p2 = " + p2);
				return Double.valueOf(p1).compareTo(Double.valueOf(p2));
			}
		} else {
			if (sortField.equals(Constants.SORT_COIN_NAME)) {
				String s1;
				if (TextUtils.isEmpty(data1.getSymbol())) {
					s1 = data1.getTradePairName();
				} else {
					s1 = data1.getSymbol();
				}
				String s2;
				if (TextUtils.isEmpty(data2.getSymbol())) {
					s2 = data2.getTradePairName();
				} else {
					s2 = data2.getSymbol();
				}
				return s2.compareTo(s1);
			} else if (sortField.equals(Constants.SORT_V24_VOL)) {
				String amt2;
				if (TextUtils.isEmpty(data2.getVolume24h())) {
					amt2 = "0";
				} else {
					amt2 = data2.getVolume24h();
				}
				String amt1;
				if (TextUtils.isEmpty(data1.getVolume24h())) {
					amt1 = "0";
				} else {
					amt1 = data1.getVolume24h();
				}

				return Double.valueOf(amt2).compareTo(Double.valueOf(amt1));
			} else if (sortField.equals(Constants.SORT_LAST_PRICE)) {
				String nl1;
				if (TextUtils.isEmpty(data1.getLastPrice()) || "--".equals(data1.getLastPrice())) {
					nl1 = "0";
				} else {
					nl1 = data1.getLastPrice();

				}


				String nl2;
				if (TextUtils.isEmpty(data2.getLastPrice()) || "--".equals(data2.getLastPrice())) {
					nl2 = "0";
				} else {
					nl2 = data2.getLastPrice();

				}

				return Double.valueOf(nl2).compareTo(Double.valueOf(nl1));
			} else if (sortField.equals(Constants.SORT_FALL_REISE)) {
				String p1;
				if (TextUtils.isEmpty(data1.getUpsAndDowns()) || "--".equals(data1.getUpsAndDowns())) {
					p1 = "0";
				} else {
					if (!Character.isDigit(data1.getUpsAndDowns().charAt(data1.getUpsAndDowns().length() - 1))) {
						p1 = data1.getUpsAndDowns().substring(0, data1.getUpsAndDowns().length() - 1);
					} else {
						p1 = data1.getUpsAndDowns();
					}
				}

				String p2;
				if (TextUtils.isEmpty(data2.getUpsAndDowns()) || "--".equals(data2.getUpsAndDowns())) {
					p2 = "0";
				} else {
					if (!Character.isDigit(data2.getUpsAndDowns().charAt(data2.getUpsAndDowns().length() - 1))) {
						p2 = data2.getUpsAndDowns().substring(0, data2.getUpsAndDowns().length() - 1);
					} else {
						p2 = data2.getUpsAndDowns();
					}

				}
//                DLog.e("sort", "p2 = " + p2 + "p1 = " + p1);
				return Double.valueOf(p2).compareTo(Double.valueOf(p1));
			}
		}
		return 0;
	}
}
