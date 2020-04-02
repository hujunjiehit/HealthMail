package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.ContractListModel;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class ContractInfoController {

	private static ContractInfoController contractInfoController;

	private ContractInfoController() {

	}

	public static ContractInfoController getInstance() {
		if (contractInfoController == null) {
			synchronized (ContractInfoController.class) {
				if (contractInfoController == null) {
					contractInfoController = new ContractInfoController();
				}
			}
		}
		return contractInfoController;
	}


	/**
	 * @param contractList
	 * @return
	 */
	public synchronized void addInToDatabase(List<ContractListModel.DataBean.ListBean> contractList) {
		if (contractList == null || contractList.size() == 0) {
			return;
		}
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		if (tableBox == null) {
			return;
		}

		List<ContractInfoTable> contractInfoTables = new ArrayList<>();
		for (int i = 0; i < contractList.size(); i++) {
			ContractInfoTable contractInfoTable = tableBox.query().equal(ContractInfoTable_.name, contractList.get(i).getSymbol()).build().findFirst();
			if (contractInfoTable == null) {
				contractInfoTable = new ContractInfoTable();
			}
			if (!contractInfoTable.equalsObject(contractList.get(i))) {
				contractInfoTable.maxTradeAmount = contractList.get(i).getMaxTradeAmount();
				contractInfoTable.minTradeAmount = contractList.get(i).getMinTradeAmount();
				contractInfoTable.name = contractList.get(i).getSymbol();
				contractInfoTable.multiplier = contractList.get(i).getMultiplier();
				contractInfoTable.minPriceChange = contractList.get(i).getMinPriceChange();
				contractInfoTable.precision = contractList.get(i).getPrecision();
				contractInfoTable.baseAsset = contractList.get(i).getBaseAsset();
				contractInfoTable.quoteAsset = contractList.get(i).getQuoteAsset();
				contractInfoTable.costPriceMultiplier = contractList.get(i).getCostPriceMultiplier();
				contractInfoTable.leverages = contractList.get(i).getLeverages();
				contractInfoTable.curLever = getCurContractLever(contractList.get(i).getSymbol());
				contractInfoTables.add(contractInfoTable);
			}
		}

		if (contractInfoTables.size() > 0) {
			tableBox.put(contractInfoTables);
		}
	}


	/**
	 * 查询所有合约
	 *
	 * @return
	 */
	public List<ContractInfoTable> queryContrackList() {
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		return tableBox.query().build().find();
	}

	/**
	 * 查询合约列表信息是否存在
	 *
	 * @return
	 */
	public boolean checkExistContrackList() {
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		return tableBox.query().build().find().size() > 0;
	}

	/**
	 * 通过合约名称查询合约
	 *
	 * @return
	 */
	public ContractInfoTable queryContrackByName(String contractName) {
		if (TextUtils.isEmpty(contractName)) {
			return null;
		}
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		return tableBox.query().equal(ContractInfoTable_.name, contractName).build().findFirst();
	}

	/**
	 * 通过合约名称查询合约
	 *
	 * @return
	 */
	public boolean checkContractIsExist(String contractName) {
		if (TextUtils.isEmpty(contractName)) {
			return false;
		}
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		ContractInfoTable table = tableBox.query().equal(ContractInfoTable_.name, contractName).build().findFirst();
		return table != null;
	}


	/**
	 * 根据合约名  更新当前合约下应该展示的杠杆倍数   （主要用于记录设备下每个杠杆用户所选择的倍数）
	 *
	 * @param contractName
	 * @param lever
	 */
	public void updataContractLever(String contractName, int lever) {

		if (TextUtils.isEmpty(contractName) || lever == 0) {
			return;
		}
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		ContractInfoTable table = tableBox.query().equal(ContractInfoTable_.name, contractName).build().findFirst();
		if (table == null) {
			return;
		}
		table.curLever = lever;
		tableBox.put(table);
	}

	/**
	 * 得到当前合约的杠杆倍数
	 */
	public int getCurContractLever(String contractName) {
		if (TextUtils.isEmpty(contractName)) {
			return 10;
		}
		Box<ContractInfoTable> tableBox = CBRepository.boxFor(ContractInfoTable.class);
		ContractInfoTable table = tableBox.query().equal(ContractInfoTable_.name, contractName).build().findFirst();
		if (table == null || table.curLever == 0) {
			return 10;
		}
		return table.curLever;
	}


}
