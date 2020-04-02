package com.coinbene.common.database;

import androidx.annotation.NonNull;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.SwitchUtils;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.QueryFilter;

/**
 * 交易对分组信息
 * Created by mengxiangdong on 2017/12/13.
 */

public class TradePairGroupController {

	private static TradePairGroupController pairGroupController;

	private TradePairGroupController() {

	}

	public static TradePairGroupController getInstance() {
		if (pairGroupController == null) {
			synchronized (TradePairGroupController.class) {
				if (pairGroupController == null) {
					pairGroupController = new TradePairGroupController();
				}
			}
		}
		return pairGroupController;
	}

	/**
	 * 把交易分组信息，将数据放到数据库，并且清除之前的数据
	 *
	 * @param groupTables
	 */
	public void addDataToDataBase(List<TradePairGroupTable> groupTables) {
		if (groupTables != null && groupTables.size() > 0) {
			Box<TradePairGroupTable> tableBox = CBRepository.boxFor(TradePairGroupTable.class);
			tableBox.removeAll();
			tableBox.put(groupTables);
		}
	}

	//如果有合约则查询  没有则过滤
	public List<TradePairGroupTable> getTradePairGroups() {
		Box<TradePairGroupTable> tableBox = CBRepository.boxFor(TradePairGroupTable.class);
		if (SwitchUtils.isOpenContract()) {
			return tableBox.query().order(TradePairGroupTable_.sort).build().find();
		} else {
			return tableBox.query().order(TradePairGroupTable_.sort).filter(entity -> {
				if (entity.groupName.equals(TradePairGroupTable.CONTRACT_GROUP)) {
					return false;
				}
				return true;
			}).build().find();
		}


	}


	//过滤合约（有些地方是必须过滤的  比如切换交易对   搜索等）
	public List<TradePairGroupTable> getTradePairGroupsFilterContrack() {
		Box<TradePairGroupTable> tableBox = CBRepository.boxFor(TradePairGroupTable.class);
		return tableBox.query().order(TradePairGroupTable_.sort).filter(new QueryFilter<TradePairGroupTable>() {
			@Override
			public boolean keep(@NonNull TradePairGroupTable entity) {
				if (entity.groupName.equals(TradePairGroupTable.CONTRACT_GROUP)) {
					return false;
				}
				return true;
			}
		}).build().find();
	}


	private boolean checkContractInGroups() {
		Box<TradePairGroupTable> tableBox = CBRepository.boxFor(TradePairGroupTable.class);
		TradePairGroupTable contractGroupTable = tableBox.query().equal(TradePairGroupTable_.groupName, TradePairGroupTable.CONTRACT_GROUP).build().findFirst();
		return contractGroupTable != null;

	}

	/**
	 * 插入合约tab 根据条件
	 */
	public void insertContractGroup() {
		if (SwitchUtils.isOpenContract() && !checkContractInGroups()) {
			Box<TradePairGroupTable> tableBox = CBRepository.boxFor(TradePairGroupTable.class);
			List<TradePairGroupTable> tables = tableBox.query().order(TradePairGroupTable_.sort).build().find();
			if (tables.size() > 0) {
				TradePairGroupTable groupTableContrack = new TradePairGroupTable();
				groupTableContrack.groupName = TradePairGroupTable.CONTRACT_GROUP;
				groupTableContrack.sort = 0;
				tables.add(1, groupTableContrack);
				tableBox.put(tables);
			}
		}


	}


	public void clearTradePairGroupTable() {
		Box<TradePairGroupTable> tradePairBox = CBRepository.boxFor(TradePairGroupTable.class);
		tradePairBox.removeAll();
		pairGroupController = null;
	}
}
