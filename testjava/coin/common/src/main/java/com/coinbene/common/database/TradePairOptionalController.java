package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.SiteController;
import com.coinbene.manbiwang.model.http.FavoriteListModel;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

/**
 * Created by mengxiangdong on 2018/6/15.
 */

public class TradePairOptionalController {

	private static TradePairOptionalController pairInfoController;

	private TradePairOptionalController() {

	}

	public static TradePairOptionalController getInstance() {
		if (pairInfoController == null) {
			synchronized (TradePairOptionalController.class) {
				if (pairInfoController == null) {
					pairInfoController = new TradePairOptionalController();
				}
			}
		}
		return pairInfoController;
	}

	public void addOptionalTradePair(FavoriteListModel favoriteListModel) {
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		if (favoriteListModel.getData() == null || favoriteListModel.getData().size() == 0) {
			return;
		} else {
			List<TradePairOptionalTable> optionalList = new ArrayList<>();
			List<FavoriteListModel.DataBean> beanList = favoriteListModel.getData();
			for (int i = 0; i < beanList.size(); i++) {
				FavoriteListModel.DataBean favoriteModel = beanList.get(i);

				TradePairOptionalTable tradePairInfoTable = new TradePairOptionalTable();
				tradePairInfoTable.site = favoriteModel.getSite();
				tradePairInfoTable.tradePair = favoriteModel.getTradePair();
				tradePairInfoTable.tradePairName = favoriteModel.getTradePairName();
				tradePairInfoTable.sort = favoriteModel.getSort();
				optionalList.add(tradePairInfoTable);
			}
			CBRepository.getBoxStore().runInTx(new Runnable() {
				@Override
				public void run() {
					tableBox.removeAll();
					tableBox.put(optionalList);
				}
			});
		}

	}

	public void notifyOptionTables(List<String> tradePairList) {
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		if (tradePairList == null || tradePairList.size() == 0) {
			tableBox.removeAll();
			return;
		} else {
			List<TradePairOptionalTable> afterTables = new ArrayList<>();
			for (int i = tradePairList.size() - 1; i >= 0; i--) {
				TradePairOptionalTable origin = getOptionalByTradePair(tradePairList.get(i));
				TradePairOptionalTable table = new TradePairOptionalTable();
				table.site = SiteController.getInstance().getSiteName();
				table.sort = origin.sort;
				table.tradePair = origin.tradePair;
				table.tradePairName = origin.tradePairName;
				table.sort = tradePairList.size() - i;
				afterTables.add(table);
			}
			CBRepository.getBoxStore().runInTx(() -> {
				tableBox.removeAll();
				tableBox.put(afterTables);
			});
		}
	}

	public TradePairOptionalTable getOptionalByTradePair(String tradePair) {
		if (TextUtils.isEmpty(tradePair)) {
			return new TradePairOptionalTable();
		}
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		if (tableBox == null) {
			return new TradePairOptionalTable();
		}
		TradePairOptionalTable table = tableBox.query().equal(TradePairOptionalTable_.tradePair, tradePair).build().findFirst();
		if (table == null) {
			return new TradePairOptionalTable();
		} else {
			return table;
		}
	}



	public void removeAll() {
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		tableBox.removeAll();
	}

	/**
	 * 将交易对添加到自选；如果已经存在，则删除之前的，重新添加
	 * 获取最大的sort，然后sort+1作为新的sort；根据sort倒序排列
	 */
	public void addOneOptionalTradePair(String tradePair, String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		TradePairOptionalTable tradePairInfoTable = tableBox.query().equal(TradePairOptionalTable_.tradePairName, tradePairName).build().findFirst();
		//先删除已经存在的，然后再添加
		if (tradePairInfoTable != null) {
			tableBox.remove(tradePairInfoTable.id);
		}
		TradePairOptionalTable newOptionalTable = new TradePairOptionalTable();
		newOptionalTable.site = SiteController.getInstance().getSiteName();
		newOptionalTable.tradePair = tradePair;
		newOptionalTable.tradePairName = tradePairName;

		long count = tableBox.count();
		if (count == 0) {
			newOptionalTable.sort = 1;
			tableBox.put(newOptionalTable);
		} else {
			List<TradePairOptionalTable> tradePairOptionalTableList = tableBox.query().orderDesc(TradePairOptionalTable_.sort).build().find();
			if (tradePairOptionalTableList != null && tradePairOptionalTableList.size() > 0) {
				TradePairOptionalTable table = tradePairOptionalTableList.get(0);
				int newSort = table.sort + 1;
				newOptionalTable.sort = newSort;
				tableBox.put(newOptionalTable);
			}
		}

	}


	/**
	 * 删除交易对从自选列表中
	 *
	 * @param tradePair
	 */
	public void deleteOptionalTradePair(String tradePair) {
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		TradePairOptionalTable optionalTable = tableBox.query().equal(TradePairOptionalTable_.tradePair, tradePair).build().findFirst();
		if (optionalTable != null) {
			tableBox.remove(optionalTable.id);
		}
	}

	/**
	 * 查询全部的自选交易对,没有自选，则返回Null
	 *
	 * @return
	 */
	public List<TradePairInfoTable> queryTradePairOptional() {
		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		List<TradePairOptionalTable> optionalArray = tableBox.query().orderDesc(TradePairOptionalTable_.sort).build().find();//倒序排序
		if (optionalArray == null || optionalArray.size() == 0) {
			return new ArrayList<>();
		}
		List<TradePairInfoTable> tradePairInfoTableList = new ArrayList<>();
		for (int i = 0; i < optionalArray.size(); i++) {
			TradePairOptionalTable optionalTable = optionalArray.get(i);
			Box<TradePairInfoTable> tableBox_tradePairTable = CBRepository.boxFor(TradePairInfoTable.class);
			TradePairInfoTable tradePairInfoTable = tableBox_tradePairTable.query().equal(TradePairInfoTable_.tradePair, optionalTable.tradePair).build().findFirst();
			if (tradePairInfoTable != null) {
				tradePairInfoTableList.add(tradePairInfoTable);
			}
		}
		return tradePairInfoTableList;
	}





//	/**
//	 * 查询全部的自选交易对,没有自选，则返回Null
//	 *
//	 * @return
//	 */
//	public List<TradePairOptionalTable> queryOptional() {
//		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
//		List<TradePairOptionalTable> optionalArray = tableBox.query().orderDesc(TradePairOptionalTable_.sort).build().find();//倒序排序
//		if (optionalArray == null || optionalArray.size() == 0) {
//			return null;
//		}
//		return optionalArray;
//	}

	public boolean isOptionalTradePair(String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return false;
		}
		if (tradePairName.contains("/")){
			tradePairName = tradePairName.replace("/", "");
		}

		Box<TradePairOptionalTable> tableBox = CBRepository.boxFor(TradePairOptionalTable.class);
		if (tableBox == null) {
			return false;
		}
		TradePairOptionalTable table = tableBox.query().equal(TradePairOptionalTable_.tradePair, tradePairName).build().findFirst();
		if (table == null) {
			return false;
		} else {
			return true;
		}
	}
}
