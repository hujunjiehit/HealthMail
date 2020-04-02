package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.LeverSymbolListModel;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class MarginSymbolController {

	private static MarginSymbolController marginSymbolController;

	private MarginSymbolController() {

	}

	public static MarginSymbolController getInstance() {
		if (marginSymbolController == null) {
			synchronized (MarginSymbolController.class) {
				if (marginSymbolController == null) {
					marginSymbolController = new MarginSymbolController();
				}
			}
		}
		return marginSymbolController;
	}


	/**
	 * @param symbolList
	 * @return
	 */
	public synchronized void addInToDatabase(List<LeverSymbolListModel.DataBean> symbolList) {
		if (symbolList == null || symbolList.size() == 0) {
			return;
		}
		Box<MarginSymbolTable> tableBox = CBRepository.boxFor(MarginSymbolTable.class);
		if (tableBox != null && tableBox.count() > 0) {
			tableBox.removeAll();
		}
		List<MarginSymbolTable> marginSymbolTables = new ArrayList<>();
		for (int i = 0; i < symbolList.size(); i++) {
			MarginSymbolTable marginSymbolTable = new MarginSymbolTable();
			marginSymbolTable.base = symbolList.get(i).getBase();
			marginSymbolTable.quote = symbolList.get(i).getQuote();
			marginSymbolTable.symbol = symbolList.get(i).getSymbol();
			marginSymbolTable.baseInterestRate = symbolList.get(i).getBaseInterestRate();
			marginSymbolTable.initialPrice = symbolList.get(i).getInitialPrice();
			marginSymbolTable.leverage = symbolList.get(i).getLeverage();
			marginSymbolTable.makeFee = symbolList.get(i).getMakeFee();
			marginSymbolTable.takeFee = symbolList.get(i).getTakeFee();
			marginSymbolTable.volumePrecision = symbolList.get(i).getVolumePrecision();
			marginSymbolTable.pricePrecision = symbolList.get(i).getPricePrecision();
			marginSymbolTable.minVolume = symbolList.get(i).getMinVolume();
			marginSymbolTable.sellDisabled = symbolList.get(i).getSellDisabled();
			marginSymbolTable.baseInterestRate = symbolList.get(i).getBaseInterestRate();
			marginSymbolTable.quoteInterestRate = symbolList.get(i).getQuoteInterestRate();
			marginSymbolTables.add(marginSymbolTable);
		}
		tableBox.put(marginSymbolTables);
	}


	/**
	 * 查询所有杠杆币种
	 *
	 * @return
	 */
	public List<MarginSymbolTable> querySymbolList() {
		Box<MarginSymbolTable> tableBox = CBRepository.boxFor(MarginSymbolTable.class);
		return tableBox.query().build().find();
	}


	/**
	 * 查询第一个币对
	 *
	 * @return
	 */
	public MarginSymbolTable queryFirstSymbol() {
		Box<MarginSymbolTable> tableBox = CBRepository.boxFor(MarginSymbolTable.class);
		return tableBox.query().build().findFirst();
	}


	/**
	 * 根据币对查询数据库
	 *
	 * @return
	 */
	public MarginSymbolTable querySymbolByName(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return new MarginSymbolTable();
		}
		Box<MarginSymbolTable> tableBox = CBRepository.boxFor(MarginSymbolTable.class);
		return tableBox.query().equal(MarginSymbolTable_.symbol, symbol).build().findFirst();
	}

	/**
	 * 不需要返回new MarginSymbolTable()
	 * @param tradePairName
	 * @return
	 */
	public MarginSymbolTable getMarginTable(String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return null;
		}
		Box<MarginSymbolTable> tableBox = CBRepository.boxFor(MarginSymbolTable.class);
		if (tableBox == null) {
			return null;
		}
		MarginSymbolTable table = tableBox.query().equal(MarginSymbolTable_.symbol, tradePairName).build().findFirst();
		return table;
	}


}
