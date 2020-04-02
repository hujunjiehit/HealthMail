package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.TradePairResponse;
import com.coinbene.common.utils.SwitchUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;


/**
 * Created by mengxiangdong on 2017/12/13.
 */

public class TradePairInfoController {

	private static TradePairInfoController pairInfoController;

	private TradePairInfoController() {

	}

	public static TradePairInfoController getInstance() {
		if (pairInfoController == null) {
			synchronized (TradePairInfoController.class) {
				if (pairInfoController == null) {
					pairInfoController = new TradePairInfoController();
				}
			}
		}
		return pairInfoController;
	}

	/**
	 * 将数据放到数据库，并且清除之前的数据
	 *
	 * @param dataBeans
	 */
	public synchronized void addDataToDataBase(List<TradePairResponse.DataBean.ListBeanX> dataBeans) {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		tableBox.removeAll();
		ArrayList<TradePairGroupTable> groupTables = new ArrayList<>();

		if (dataBeans != null && dataBeans.size() > 0) {
			//添加自选
			TradePairGroupTable groupTableSlef = new TradePairGroupTable();
			groupTableSlef.groupName = TradePairGroupTable.SELF_GROUP;
			groupTableSlef.sort = 0;
			groupTables.add(groupTableSlef);
			//添加合约
			if (SwitchUtils.isOpenContract()) {
				TradePairGroupTable groupTableContrack = new TradePairGroupTable();
				groupTableContrack.groupName = TradePairGroupTable.CONTRACT_GROUP;
				groupTableContrack.sort = 0;
				groupTables.add(groupTableContrack);
			}
		}

		List<TradePairInfoTable> tableList = new ArrayList<>();
		for (int i = 0; i < dataBeans.size(); i++) {
			TradePairResponse.DataBean.ListBeanX model = dataBeans.get(i);

			if (model == null) {
				continue;
			}
			TradePairGroupTable groupTable = new TradePairGroupTable();
			groupTable.groupName = model.getGroupName();
			groupTable.sort = model.getSort();
			groupTables.add(groupTable);

			if (model.getList() == null || model.getList().size() == 0) {
				continue;
			}
			List<TradePairResponse.DataBean.ListBeanX.ListBean> listBean = model.getList();
			for (int j = 0; j < listBean.size(); j++) {
				TradePairResponse.DataBean.ListBeanX.ListBean listBean1 = listBean.get(j);
				TradePairInfoTable table = new TradePairInfoTable();
				table.sort = listBean1.getSort();
				table.pricePrecision = listBean1.getPricePrecision();
				table.localBaseAsset = listBean1.getLocalBaseAsset();
				table.englishBaseAsset = listBean1.getEnglishBaseAsset();

				table.tradePair = listBean1.getTradePair();
				table.tradePairName = listBean1.getTradePairName();
				table.volumePrecision = listBean1.getVolumePrecision();

				table.minVolume = listBean1.getMinVolume();
				table.priceChangeScale = listBean1.getPriceChangeScale();
				table.takeFee = listBean1.getTakeFee();
				table.makeFee = listBean1.getMakeFee();
				table.groupName = model.getGroupName();
				table.group_sort = model.getSort();

				table.sellDisabled = listBean1.getSellDisabled();

				table.isLatest = listBean1.isLatest();
				table.isHot = listBean1.isHot();
				if (listBean1.getTags() != null && listBean1.getTags().size() > 0) {
					for (int k = 0; k < listBean1.getTags().size(); k++) {
						TagsTable tag = new TagsTable();
						tag.iconUrl = listBean1.getTags().get(k);
						table.tags.add(tag);
					}
				}
				tableList.add(table);
			}
		}
		CBRepository.getBoxStore().runInTx(new Runnable() {
			@Override
			public void run() {
				tableBox.removeAll();
				tableBox.put(tableList);
			}
		});
		TradePairGroupController.getInstance().addDataToDataBase(groupTables);
	}

	public List<TradePairInfoTable> queryDataLikeStr(boolean isCh, String nameStr) {
		if (TextUtils.isEmpty(nameStr)) {
			return new ArrayList<>();
		}
		List<TradePairInfoTable> tradePairTableList;
		nameStr = nameStr.toUpperCase();
		Box<TradePairInfoTable> tradePairTableBox = CBRepository.boxFor(TradePairInfoTable.class);
		if (isCh) {
//            Log.e("hy", "true");
			tradePairTableList = tradePairTableBox.query().contains(TradePairInfoTable_.localBaseAsset, nameStr).or().contains(TradePairInfoTable_.tradePair, nameStr).build().find();
		} else {
//            Log.e("hy", "false");
			tradePairTableList = tradePairTableBox.query().contains(TradePairInfoTable_.englishBaseAsset, nameStr).or().contains(TradePairInfoTable_.tradePair, nameStr).build().find();
		}
		return tradePairTableList;
	}

	public List<TradePairInfoTable> getTradePairInfoList(String groupName) {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().equal(TradePairInfoTable_.groupName, groupName).order(TradePairInfoTable_.sort).build().find();
	}

	public void clearTradePairInfo() {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		tableBox.removeAll();
	}

	public TradePairInfoTable queryDataById(String tradePair) {
//        tradePair = tradePair.toUpperCase()
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().equal(TradePairInfoTable_.tradePair, tradePair).order(TradePairInfoTable_.sort).build().findFirst();
	}

	public List<TradePairInfoTable> queryDataByBalance(String coin) {
//        tradePair = tradePair.toUpperCase()
		if (TextUtils.isEmpty(coin)) {
			return null;
		}
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		List<TradePairInfoTable> tradePairTableList;
		tradePairTableList = tableBox.query().startsWith(TradePairInfoTable_.tradePair, coin).build().find();
		return tradePairTableList;
	}

	public List<TradePairInfoTable> queryDataByBalanceName(String coin) {
//        tradePair = tradePair.toUpperCase()
		if (TextUtils.isEmpty(coin)) {
			return null;
		}
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		List<TradePairInfoTable> tradePairTableList;
		tradePairTableList = tableBox.query().startsWith(TradePairInfoTable_.tradePairName, coin).build().find();
		return tradePairTableList;
	}

	public TradePairInfoTable queryDataByTradePairName(String tradePairName) {
//        tradePair = tradePair.toUpperCase()
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().equal(TradePairInfoTable_.tradePairName, tradePairName).or().equal(TradePairInfoTable_.tradePair, tradePairName).order(TradePairInfoTable_.sort).build().findFirst();
	}

	/**
	 * 根据交易对查询数据库
	 *
	 * @return
	 */
	public TradePairInfoTable queryDataByTradePair(String tradePair) {
		if(TextUtils.isEmpty(tradePair)){
			return new TradePairInfoTable();
		}
		if(tradePair.contains("/")){
			tradePair = tradePair.replace("/","");
		}
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().equal(TradePairInfoTable_.tradePair, tradePair).build().findFirst();
	}

	public List<TradePairInfoTable> queryTradePairByGroup(String groupName) {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().equal(TradePairInfoTable_.groupName, groupName).order(TradePairInfoTable_.sort).build().find();
	}


	public List<TradePairInfoTable> queryAllTrade() {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().build().find();
	}

	/**
	 * 是否存在交易对
	 * @return
	 */
	public boolean checkTradeExist() {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		return tableBox.query().build().find() != null && tableBox.query().build().find().size() != 0;
	}

	/**
	 * 从数据库中查询 分子(BaseAsset)
	 * true:存在；false:不存在
	 * 如果输入CONI, 需要查找 CONI/USDT，CONI/ETH,CONI/BTC
	 *
	 * @param baseAsset
	 * @return
	 */
	public boolean isBaseAssetInDataBase(String baseAsset) {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		List<TradePairInfoTable> pairInfoTableArray = tableBox.query().build().find();
		if (pairInfoTableArray == null || pairInfoTableArray.size() == 0) {
			return false;
		}
		for (int i = 0; i < pairInfoTableArray.size(); i++) {
			String[] array = pairInfoTableArray.get(i).tradePairName.split("/");
			if (array != null && array.length == 2) {
				if (array[0].toUpperCase().equals(baseAsset.toUpperCase())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 查询交易对是否在数据库中存在与否
	 * BTC/USDT,<br></>
	 * true:存在,false:不存在
	 *
	 * @param tradePair
	 * @return
	 */
	public boolean isTradePairInDataBase(String tradePair) {
		Box<TradePairInfoTable> tableBox = CBRepository.boxFor(TradePairInfoTable.class);
		TradePairInfoTable tradePairInfoTable = tableBox.query().equal(TradePairInfoTable_.tradePair, tradePair).build().findFirst();
		return tradePairInfoTable != null;
	}


}
