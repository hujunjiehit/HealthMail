package com.coinbene.common.utils;

import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalTable;
import com.coinbene.manbiwang.model.http.BannerList;

import java.util.List;

public class ListUtils {


	/**
	 * 两个list比较
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean twoListCompareUtil(List<BannerList.DataBean> a, List<BannerList.DataBean> b) {
		if (a.size() != b.size())
			return false;
//        Collections.sort(a);
//        Collections.sort(b);
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i)))
				return false;
		}
		return true;
	}


	/**
	 * 交易对list转string
	 *
	 * @param list
	 * @return
	 */
	public static String listToStringComma(List list) {
		StringBuilder sb = new StringBuilder();
		if (list.get(0) instanceof TradePairInfoTable) {
			if (list.size() == 1) {
				return ((TradePairInfoTable) list.get(0)).tradePair;
			}
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					sb.append(((TradePairInfoTable) list.get(i)).tradePair);
				} else
					sb.append(((TradePairInfoTable) list.get(i)).tradePair).append(",");
			}
		} else if (list.get(0) instanceof TradePairOptionalTable) {
			if (list.size() == 1) {
				return ((TradePairOptionalTable) list.get(0)).tradePair;
			}
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					sb.append(((TradePairOptionalTable) list.get(i)).tradePair);
				} else
					sb.append(((TradePairOptionalTable) list.get(i)).tradePair).append(",");
			}
		}

		return sb.toString();
	}


	/**
	 * 自选list转string
	 *
	 * @param list
	 * @return
	 */
	public static String listToStringOption(List<TradePairInfoTable> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		if (list.size() == 1) {
			return list.get(0).tradePair;
		}
		for (int i = list.size() - 1; i > -1; i--) {
			if (i == 0) {
				sb.append(list.get(i).tradePair);
			} else
				sb.append(list.get(i).tradePair).append(",");
		}
		return sb.toString();
	}

	/**
	 * 自选list转string
	 *
	 * @param list
	 * @return
	 */
	public static String listToString(List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		if (list.size() == 1) {
			return list.get(0);
		}
		for (int i = list.size() - 1; i > -1; i--) {
			if (i == 0) {
				sb.append(list.get(i));
			} else
				sb.append(list.get(i)).append(",");
		}
		return sb.toString();
	}


	public static <E> boolean isListEqual(List<E> list1, List<E> list2) {

		if (list1 == null || list2 == null) {
			return false;
		}

		// 两个list引用相同
		if (list1 == list2) {
			return true;
		}

		// 两个list都为空（包括空指针、元素个数为0）
		if ((list1 == null && list2 != null && list2.size() == 0)
				|| (list2 == null && list1 != null && list1.size() == 0)) {
			return true;
		}

		// 两个list元素个数不相同
		if (list1.size() != list2.size()) {
			return false;
		}

		// 两个list元素个数已经相同，再比较两者内容
		// 采用这种可以忽略list中的元素的顺序
		// 涉及到对象的比较是否相同时，确保实现了equals()方法
		if (!list1.containsAll(list2)) {
			return false;
		}

		return true;
	}
}
