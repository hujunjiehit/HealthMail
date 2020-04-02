package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.CoinTotalInfoModel;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;


/**
 * Created by mengxiangdong on 2017/12/13.
 * 个人资产列表,所有币的可用资产，剩余资产
 */

public class BalanceController {

	private static BalanceController balanceController;

	private BalanceController() {

	}

	public static BalanceController getInstance() {
		if (balanceController == null) {
			synchronized (BalanceController.class) {
				if (balanceController == null) {
					balanceController = new BalanceController();
				}
			}
		}
		return balanceController;
	}

	/**
	 * 只有两个地方调用，把更新的部分去掉
	 *
	 * @param balanceModelList
	 * @return
	 */
	public synchronized boolean addInToDatabase(List<CoinTotalInfoModel.DataBean.ListBean> balanceModelList) {
		boolean isChanged = false;
		if (balanceModelList == null || balanceModelList.size() == 0) {
			return false;
		}
		Box<BalanceInfoTable> tableBox = CBRepository.boxFor(BalanceInfoTable.class);
		if (tableBox != null && tableBox.count() > 0) {
			tableBox.removeAll();
		}


		List<BalanceInfoTable> balanceTables = new ArrayList<>();
		for (int i = 0; i < balanceModelList.size(); i++) {
			BalanceInfoTable balanceTable = new BalanceInfoTable();
			balanceTable.asset = balanceModelList.get(i).getAsset();
			balanceTable.bank = balanceModelList.get(i).getBank();
			balanceTable.frozenBalance = balanceModelList.get(i).getFrozenBalance();
			balanceTable.localAssetName = balanceModelList.get(i).getLocalAssetName();
//            balanceTable.precision = CommonUtil.changeNumToPrecisionStr(balanceModelList.get(i).getPrecision());
			balanceTable.transferPrecision = String.valueOf(balanceModelList.get(i).getTransferPrecision());
			balanceTable.totalBalance = balanceModelList.get(i).getTotalBalance();
			balanceTable.deposit = balanceModelList.get(i).isDeposit();
			balanceTable.transfer = balanceModelList.get(i).isTransfer();
			balanceTable.withdrawPrecision = String.valueOf(balanceModelList.get(i).getWithdrawPrecision());
			balanceTable.withdraw = balanceModelList.get(i).isWithdraw();
			balanceTable.localPreestimate = balanceModelList.get(i).getLocalPreestimate();
			balanceTable.preestimateBTC = balanceModelList.get(i).getPreestimateBTC();
			balanceTable.minDeposit = balanceModelList.get(i).getMinDeposit();
			balanceTable.minWithdraw = balanceModelList.get(i).getMinWithdraw();
//            balanceTable.smallAmountThreshold = balanceModelList.get(i).getSmallAmountThreshold();
			balanceTable.sort = balanceModelList.get(i).getSort();
			balanceTable.useTag = balanceModelList.get(i).isUseTag();
			balanceTable.withdrawFee = balanceModelList.get(i).getWithdrawFee();
			balanceTable.withdrawMinConfirmations = "" + balanceModelList.get(i).getWithdrawMinConfirmations();//getWithdrawMinConfirmations();

//            balanceTable.iconUrl = balanceModelList.get(i).getIconUrl();
			balanceTable.englishAssetName = balanceModelList.get(i).getEnglishAssetName();
			balanceTable.availableBalance = balanceModelList.get(i).getAvailableBalance();
			balanceTable.depositPrecision = String.valueOf(balanceModelList.get(i).getDepositPrecision());
			balanceTable.depositMinConfirmations = "" + balanceModelList.get(i).getDepositMinConfirmations();//getDepositMinConfirmations();
			balanceTable.accountType = String.valueOf(balanceModelList.get(i).getAccountType());
			if (!TextUtils.isEmpty(balanceModelList.get(i).getDepositHints()))
				balanceTable.depositHints = balanceModelList.get(i).getDepositHints();
			if (!TextUtils.isEmpty(balanceModelList.get(i).getTagLabel()))
				balanceTable.tagLabel = balanceModelList.get(i).getTagLabel();
			if (!TextUtils.isEmpty(balanceModelList.get(i).getWithdrawHints()))
				balanceTable.withdrawHints = balanceModelList.get(i).getWithdrawHints();
			balanceTable.withdrawTag = balanceModelList.get(i).isWithdrawTag();

			balanceTable.banDepositReason = balanceModelList.get(i).getBanDepositReason();
			balanceTable.banWithdrawReason = balanceModelList.get(i).getBanWithdrawReason();
			balanceTable.banTransferReason = balanceModelList.get(i).getBanTransferReason();
			balanceTable.chain = balanceModelList.get(i).getChain();
			if (balanceModelList.get(i).getChains() != null) {
				tableBox.attach(balanceTable);
				for (CoinTotalInfoModel.DataBean.ListBean.ChainsBean chain : balanceModelList.get(i).getChains()) {
					BalanceChainTable chainTable = new BalanceChainTable();
					chainTable.protocolName = chain.getProtocolName();
					chainTable.asset = chain.getAsset();
					chainTable.bank = chain.getBank();
					chainTable.withdrawPrecision = String.valueOf(chain.getWithdrawPrecision());
					chainTable.transferPrecision = String.valueOf(chain.getTransferPrecision());
					chainTable.depositPrecision = String.valueOf(chain.getDepositPrecision());
					chainTable.deposit = chain.isDeposit();
					chainTable.withdrawFee = chain.getWithdrawFee();
					chainTable.transfer = chain.isTransfer();
					chainTable.withdraw = chain.isWithdraw();
					chainTable.useTag = chain.isUseTag();
					chainTable.minDeposit = chain.getMinDeposit();
					chainTable.minWithdraw = chain.getMinWithdraw();
					chainTable.withdrawMinConfirmations = chain.getWithdrawMinConfirmations();
					chainTable.depositMinConfirmations = String.valueOf(chain.getDepositMinConfirmations());
					chainTable.assetType = chain.getAssetType();
					chainTable.banDepositReason = chain.getBanDepositReason();
					chainTable.banWithdrawReason = chain.getBanWithdrawReason();
					chainTable.banTransferReason = chain.getBanTransferReason();
					chainTable.depositHints = chain.getDepositHints();
					chainTable.withdrawHints = chain.getWithdrawHints();
					chainTable.tagLabel = chain.getTagLabel();
					chainTable.chain = chain.getChain();
					balanceTable.balanceChains.add(chainTable);
				}
			}
			balanceTables.add(balanceTable);
		}
		tableBox.put(balanceTables);
		isChanged = true;
		return isChanged;
	}

	public boolean updateBalancebyAsset(String asset, String totalBalance, String frozenBalance, String availableBalance) {
		Box<BalanceInfoTable> tableBox = CBRepository.boxFor(BalanceInfoTable.class);
		boolean isChanged = false;
		BalanceInfoTable table = tableBox.query().equal(BalanceInfoTable_.asset, asset).build().findFirst();
		if (table == null) {
			table = new BalanceInfoTable();
		}

		if (TextUtils.isEmpty(table.availableBalance)) {
			table.availableBalance = "0";
		}

		if (TextUtils.isEmpty(table.totalBalance)) {
			return isChanged;
		}

		if (TextUtils.isEmpty(table.frozenBalance)) {
			return isChanged;
		}

		if (TextUtils.isEmpty(availableBalance)) {
			return isChanged;
		}

		if (!table.totalBalance.equals(totalBalance) || !table.frozenBalance.equals(frozenBalance) || !availableBalance.equals(table.availableBalance)) {
			table.totalBalance = totalBalance;
			table.frozenBalance = frozenBalance;
			table.availableBalance = availableBalance;
			tableBox.put(table);
			isChanged = true;
		}
		return isChanged;
	}


	/**
	 * 更新币种可用余额
	 *
	 * @param asset
	 * @param availableBalance
	 */
	public void updateBalancebyAsset(String asset, String availableBalance) {
		Box<BalanceInfoTable> tableBox = CBRepository.boxFor(BalanceInfoTable.class);
		BalanceInfoTable table = tableBox.query().equal(BalanceInfoTable_.asset, asset).build().findFirst();
		if (table == null) {
			table = new BalanceInfoTable();
			table.asset= asset;
		}

		if (TextUtils.isEmpty(table.availableBalance)) {
			table.availableBalance = "0";
		}
		if (!table.availableBalance.equals(availableBalance)) {
			table.availableBalance = availableBalance;
			tableBox.put(table);
		}
	}


	public BalanceInfoTable findByAsset(String assetId) {
		if (TextUtils.isEmpty(assetId)) {
			return null;
		}
		Box<BalanceInfoTable> userBox = CBRepository.boxFor(BalanceInfoTable.class);
		BalanceInfoTable table = userBox.query().equal(BalanceInfoTable_.asset, assetId).build().findFirst();
		return table;
	}

	public List<BalanceInfoTable> getAllAssets() {
		Box<BalanceInfoTable> userBox = CBRepository.boxFor(BalanceInfoTable.class);
		return userBox.query().notEqual(BalanceInfoTable_.asset, "BRL").order(BalanceInfoTable_.sort).build().find();
	}

	public void clearBalanceDataBase() {
		Box<BalanceInfoTable> tableBox = CBRepository.boxFor(BalanceInfoTable.class);
		tableBox.removeAll();
	}

	public List<BalanceInfoTable> queryDataLikeStr(boolean localzh, String inputStr) {
		if (TextUtils.isEmpty(inputStr)) {
			return null;
		}
		Box<BalanceInfoTable> tableBox = CBRepository.boxFor(BalanceInfoTable.class);
		if (localzh) {
			return tableBox.query().contains(BalanceInfoTable_.localAssetName, inputStr).or().
					contains(BalanceInfoTable_.asset, inputStr).order(BalanceInfoTable_.sort).and().notEqual(BalanceInfoTable_.asset, "BRL").build().find();
		} else {
			return tableBox.query().contains(BalanceInfoTable_.englishAssetName, inputStr).or()
					.contains(BalanceInfoTable_.asset, inputStr).order(BalanceInfoTable_.sort).and().notEqual(BalanceInfoTable_.asset, "BRL").build().find();
		}
	}
}
