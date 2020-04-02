package com.coinbene.common.utils;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TradeUtils {
	private static OrderListSortUtil sortUtil;

	/**
	 * 计算深度百分比
	 *
	 * @param newBuyList  买单14条数据  降序排序
	 * @param newSellList 卖单14条数据 降序排序
	 */
	public static void caculatePercent(List<OrderModel> newBuyList, List<OrderModel> newSellList) {
		String buyCount = "0", sellCount = "0";
		for (int i = 0; i <= newBuyList.size() - 1; i++) {
			OrderModel item = newBuyList.get(i);
			buyCount = BigDecimalUtils.add(item.cnt, buyCount);
			item.addUpCnt = buyCount;
		}

		for (int i = newSellList.size() - 1; i >= 0; i--) {
			OrderModel item = newSellList.get(i);
			sellCount = BigDecimalUtils.add(item.cnt, sellCount);
			item.addUpCnt = sellCount;
		}

		String maxCount = BigDecimalUtils.isGreaterThan(buyCount, sellCount) ? buyCount : sellCount;
		if (BigDecimalUtils.isEmptyOrZero(maxCount)) {
			return;
		}

		for (int i = 0; i <= newBuyList.size() - 1; i++) {
			OrderModel item = newBuyList.get(i);
			item.percent = Double.parseDouble(BigDecimalUtils.divideToStr(item.addUpCnt, maxCount, 2));
		}

		for (int i = newSellList.size() - 1; i >= 0; i--) {
			OrderModel item = newSellList.get(i);
			item.percent = Double.parseDouble(BigDecimalUtils.divideToStr(item.addUpCnt, maxCount, 2));
		}
	}

	/**
	 * 计算K线深度百分比
	 *
	 * @param newBuyList  买单14条数据  降序排序
	 * @param newSellList 卖单14条数据 升序排序
	 */
	public static void caculatePercentForKline(List<OrderModel> newBuyList, List<OrderModel> newSellList) {
		String buyCount = "0", sellCount = "0";
		for (int i = 0; i <= newBuyList.size() - 1; i++) {
			OrderModel item = newBuyList.get(i);
			buyCount = BigDecimalUtils.add(item.cnt, buyCount);
			item.addUpCnt = buyCount;
		}

		for (int i = 0; i <= newSellList.size() - 1; i++) {
			OrderModel item = newSellList.get(i);
			sellCount = BigDecimalUtils.add(item.cnt, sellCount);
			item.addUpCnt = sellCount;
		}

		String maxCount = BigDecimalUtils.isGreaterThan(buyCount, sellCount) ? buyCount : sellCount;
		if (BigDecimalUtils.isEmptyOrZero(maxCount)) {
			return;
		}

		for (int i = 0; i <= newBuyList.size() - 1; i++) {
			OrderModel item = newBuyList.get(i);
			item.percent = Double.parseDouble(BigDecimalUtils.divideToStr(item.addUpCnt, maxCount, 2));
		}

		for (int i = 0; i <= newSellList.size() - 1; i++) {
			OrderModel item = newSellList.get(i);
			item.percent = Double.parseDouble(BigDecimalUtils.divideToStr(item.addUpCnt, maxCount, 2));
		}
	}

	/**
	 * 从Map中提取现货行情数据
	 *
	 * @param bean
	 * @param dataMap
	 */
	public static void getMarketDataFromMap(TradePairMarketRes.DataBean bean, Map<String, TradePairMarketRes.DataBean> dataMap) {
		try {
			if (bean == null || dataMap == null || dataMap.size() == 0 || dataMap.get(bean.getPairID()) == null) {
				return;
			}
			bean.setP(dataMap.get(bean.getPairID()).getP());
			bean.setN(dataMap.get(bean.getPairID()).getN());
			bean.setV(dataMap.get(bean.getPairID()).getV());
			bean.setA(dataMap.get(bean.getPairID()).getA());
			bean.setNl(dataMap.get(bean.getPairID()).getNl());
			bean.setO(dataMap.get(bean.getPairID()).getO());
			bean.setS(dataMap.get(bean.getPairID()).getS());
			bean.setR(dataMap.get(bean.getPairID()).getR());
			bean.setNu(dataMap.get(bean.getPairID()).getNu());
			bean.setV24(dataMap.get(bean.getPairID()).getV24());
			bean.setAmt24(dataMap.get(bean.getPairID()).getAmt24());
			bean.setHot(dataMap.get(bean.getPairID()).isHot());
			bean.setLatest(dataMap.get(bean.getPairID()).isLatest());
		} catch (NullPointerException e) {

		}
	}

	/**
	 * 从Map中提取合约行情数据
	 *
	 * @param bean
	 */
	public static void getContractDataFromMap(TradePairMarketRes.DataBean bean) {
		if (bean.getContractType() == Constants.CONTRACT_TYPE_BTC) {
			//getContractDataFromMap(bean, MarketWebsosket.getInstance().getContractBtcTradePairMap());
		} else if (bean.getContractType() == Constants.CONTRACT_TYPE_USDT) {
			//getContractDataFromMap(bean, MarketWebsosket.getInstance().getContractUsdtTradePairMap());
		}
	}

	/**
	 * 从Map中提取合约行情数据
	 *
	 * @param bean
	 * @param dataMap
	 */
	public static void getContractDataFromMap(TradePairMarketRes.DataBean bean, Map<String, TradePairMarketRes.DataBean> dataMap) {
		try {
			if (bean == null || dataMap == null || dataMap.size() == 0 || dataMap.get(bean.getName()) == null) {
				return;
			}
			bean.setP(dataMap.get(bean.getName()).getP());
			bean.setN(dataMap.get(bean.getName()).getN());
			bean.setV24(dataMap.get(bean.getName()).getV24());
			bean.setAmt24(dataMap.get(bean.getName()).getAmt24());

			bean.setNl(NewMarketWebsocket.getInstance().getCurrentExchangeRate().getSymbol() +
					BigDecimalUtils.multiplyDown(dataMap.get(bean.getName()).getN(), NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate(), 4));

			bean.setA(dataMap.get(bean.getName()).getA());
			bean.setS(dataMap.get(bean.getName()).getS());
		} catch (NullPointerException e) {

		}
	}

	/**
	 * 风险率计算规则：后台返的数据乘以100，再加上百分号
	 * 如果风险率为0或者负数，显示 "--"
	 *
	 * @param riskRate
	 */
	public static String getDisplayRiskRate(String riskRate) {
		if (BigDecimalUtils.compareZero(riskRate) <= 0) {
			return "--";
		}
		return BigDecimalUtils.toPercentage(riskRate, 2);
	}

	/**
	 * String 如果为空  则限时 --
	 */
	public static String getLineForNullString(String string) {
		if (BigDecimalUtils.isEmptyOrZero(string)) {
			return "--";
		}
		return string;
	}


	/**
	 * 获取杠杆交易的币种列表
	 */
	public static List<String> getMarginAssetList() {
		List<MarginSymbolTable> list = MarginSymbolController.getInstance().querySymbolList();
		List<String> assetList = new ArrayList<>();
		if (list == null || list.size() == 0) {
			return assetList;
		}

		for (MarginSymbolTable marginSymbolTable : list) {
			//添加分母
			if (!assetList.contains(marginSymbolTable.quote)) {
				assetList.add(marginSymbolTable.quote);
			}
		}

		for (MarginSymbolTable marginSymbolTable : list) {
			//添加分子
			if (!assetList.contains(marginSymbolTable.base)) {
				assetList.add(marginSymbolTable.base);
			}
		}
		return assetList;
	}

	/**
	 * 获取杠杆交易的币对列表
	 */
	public static List<String> getMarginPairList() {
		List<MarginSymbolTable> list = MarginSymbolController.getInstance().querySymbolList();
		List<String> pairList = new ArrayList<>();
		if (list == null || list.size() == 0) {
			return pairList;
		}
		for (MarginSymbolTable marginSymbolTable : list) {
			if (!pairList.contains(marginSymbolTable.symbol)) {
				pairList.add(marginSymbolTable.symbol);
			}
		}
		return pairList;
	}

	/**
	 * 得到未实现盈亏总计
	 *
	 * @param listData
	 * @return
	 */
	public static String getTotalUnrealizedProfitLoss(List<ContractPositionListModel.DataBean> listData) {
		if (listData == null || listData.size() == 0) {
			return "";
		}
		String totalUnrealized = "0";
		for (ContractPositionListModel.DataBean dataBean : listData) {
			totalUnrealized = BigDecimalUtils.add(totalUnrealized, dataBean.getUnrealisedPnl());
		}
		if (BigDecimalUtils.compareZero(totalUnrealized) > 0) {
			totalUnrealized = "+" + totalUnrealized;
		}

		return totalUnrealized;
	}

	/**
	 * 七日年化收益率，不截取精度，乘以100加上百分号
	 */
	public static String getLastSevenDayAnnual(String lastSevenDayAnnual) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(lastSevenDayAnnual);
		stringBuilder.append("%");
		return stringBuilder.toString();
	}


	/**
	 * 挂单map 转换为list
	 *
	 * @param map
	 * @return
	 */
	public static List<OrderModel> orderListMapToList(ConcurrentHashMap<String, String> map, boolean isSell) {
		List<OrderModel> list = new ArrayList<>();

		if (map == null || map.size() == 0) {
			return list;
		}

		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			OrderModel orderModel = new OrderModel();
			orderModel.isSell = isSell;
			orderModel.price = entry.getKey();
			orderModel.cnt = entry.getValue();
			orderModel.percent = 0.0;
			list.add(orderModel);
		}
		if (sortUtil == null) {
			sortUtil = new OrderListSortUtil();
		}
		Collections.sort(list, sortUtil);


		return list;
	}


	/**
	 * 解析交易对分子分母
	 *
	 * @param symbol
	 * @return
	 */
	public static String[] parseSymbol(String symbol) {
		String[] arrays = new String[2];
		if (TextUtils.isEmpty(symbol) || !symbol.contains("/")) {
			return arrays;
		}

		arrays[0] = symbol.substring(0, symbol.indexOf('/'));
		arrays[1] = symbol.substring(symbol.indexOf('/') + 1);
		return arrays;
	}

	/**
	 * 获取合约单位
	 *
	 * @param table
	 * @return
	 */
	public static String getContractUsdtUnit(ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return CBRepository.getContext().getString(R.string.number);
		} else {
			if (table == null) {
				return "";
			}
			return table.baseAsset;
		}
	}

	/**
	 * 获取合约单位
	 *
	 * @param symbol
	 * @return
	 */
	public static String getContractUsdtUnit(String symbol) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return CBRepository.getContext().getString(R.string.number);
		} else {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			if (table == null) {
				return "";
			}
			return table.baseAsset;
		}
	}


	/**
	 * 获取USDT合约分母
	 *
	 * @param symbol
	 * @return
	 */
	public static String getContractUsdtQoute(String symbol) {
		ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
		if (table == null) {
			return "";
		}
		return table.quoteAsset;
	}

	/**
	 * 获取BTC合约分母
	 *
	 * @param symbol
	 * @return
	 */
	public static String getContractBtcQoute(String symbol) {
		ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(symbol);
		if (table == null) {
			return "";
		}
		return table.quoteAsset;
	}

	/**
	 * 根据合约单位 转换不同的value值
	 *
	 * @param quantity
	 * @return
	 */
	public static String getContractUsdtUnitValue(String quantity, ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return quantity;
		} else {
			if (table == null || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(table.multiplier)) {
				return "0";
			}
			return BigDecimalUtils.multiplyToStr(quantity, table.multiplier);
		}
	}


	public static String getContractUsdtEstimatedValue(String quantity, ContractUsdtInfoTable table) {
		if (table == null || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(table.multiplier)) {
			return "0";
		}
		return BigDecimalUtils.multiplyToStr(quantity, table.multiplier);
	}

	/**
	 * 根据合约单位，下单的时候转换为张
	 */
	public static String getContractUsdtPlaceOrderValue(String quantity, ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return quantity;
		} else {
			if (table == null || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(table.multiplier)) {
				return "";
			}
			return BigDecimalUtils.divideToStr(quantity, table.multiplier, 0);
		}
	}


	/**
	 * 根据合约单位，下单的时候弹窗显示的单位，按乘数实际下单的单位换算
	 */
	public static String getContractUsdtDialogShowValue(String quantity, ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return quantity;
		}
		return BigDecimalUtils.multiplyToStr(getContractUsdtPlaceOrderValue(quantity, table), table.multiplier);
	}


//	/**
//	 * 根据合约单位设置输入框精度
//	 */
//	public static int getContractUsdtPrecision(ContractInfoTable table) {
//		if (table == null || BigDecimalUtils.isGreaterThan(table.multiplier, "1")) {
//			return 0;
//		}
//		return PrecisionUtils.getStringLengthIndex(table.multiplier);
//	}

	/**
	 * 根据usdt合约单位输入框精度
	 */
	public static int getContractUsdtPrecision(ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return 0;
		} else {
			if (table == null || BigDecimalUtils.isGreaterThan(table.multiplier, "1")) {
				return 0;
			}
			return PrecisionUtils.getStringLengthIndex(table.multiplier);
		}
	}


	/**
	 * 根据usdt合约单位输入框精度
	 */
	public static int getContractUsdtPrecision(String symbol) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return 0;
		} else {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);

			if (table == null || BigDecimalUtils.isGreaterThan(table.multiplier, "1")) {
				return 0;
			}
			return PrecisionUtils.getStringLengthIndex(table.multiplier);
		}
	}


	/**
	 * 根据usdt合约单位输入框精度
	 */
	public static String getContractUsdtMultiplier(ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return "1";
		} else {
			return table.multiplier;
		}
	}


	/**
	 * 根据btc合约单位输入框精度
	 */
	public static String getContractBtcMultiplier(ContractInfoTable table) {
		return "1";
	}


	/**
	 * 检查最小下单数量是不是1张
	 */
	public static boolean checkContractUsdtInput(String input, ContractUsdtInfoTable table) {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			return true;
		}
		if (table == null && TextUtils.isEmpty(table.multiplier)) {
			return true;
		}
		if (BigDecimalUtils.isLessThan(input, BigDecimalUtils.multiplyToStr("1", table.multiplier))) {
			return false;
		}
		return true;
	}

	/**
	 * btc合约下 是不是btcusdt合约的判断
	 *
	 * @param contractInfoTable
	 * @return
	 */
	public static boolean isContractBtc(ContractInfoTable contractInfoTable) {
		if (contractInfoTable == null) {
			return false;
		}
		return "BTCUSDT".endsWith(contractInfoTable.name);
	}

	public static String getUpsAndDowns(String close, String open) {
		//0点涨跌幅
		return BigDecimalUtils.toPercentage(
				BigDecimalUtils.divideToStrHalfUp(
						BigDecimalUtils.subtract(close, open), open, 4));
	}

	/**
	 * 获取涨跌幅
	 *
	 * @param data
	 * @return
	 */
	public static String getUpsAndDowns(WsMarketData data) {
		//0点涨跌幅
		return BigDecimalUtils.toPercentage(
				BigDecimalUtils.divideToStrHalfUp(
						BigDecimalUtils.subtract(data.getLastPrice(), data.getOpenPrice()),
						data.getOpenPrice(),
						4));

//		if (SiteHelper.isMainSite()) {
//			//0点涨跌幅
//			return BigDecimalUtils.toPercentage(
//					BigDecimalUtils.divideToStrHalfUp(
//							BigDecimalUtils.subtract(data.getLastPrice(), data.getOpenPrice()),
//							data.getOpenPrice(),
//							4));
//		} else {
//			//24h涨跌幅
//			return BigDecimalUtils.toPercentage(
//				BigDecimalUtils.divideToStrHalfUp(
//						BigDecimalUtils.subtract(data.getLastPrice(), data.getOpen24h()),
//						data.getOpen24h(),
//				4));
//		}
	}

	/**
	 * 获取涨跌幅
	 *
	 * @param data
	 * @return
	 */
	public static String getChangeValue(WsMarketData data) {
		if (SpUtil.getUpsAndDownsType() == Constants.UPS_AND_DOWNS_TYPE_ZERO) {
			//0点涨跌值
			return BigDecimalUtils.subtract(data.getLastPrice(), data.getOpenPrice());
		} else {
			//24h涨跌值
			return BigDecimalUtils.subtract(data.getLastPrice(), data.getOpen24h());
		}
	}

	/**
	 * 根据当地汇率计算本地价格
	 *
	 * @return
	 */
	public static String getLocalPrice(WsMarketData data) {
		if (NewMarketWebsocket.getInstance().getCurrentExchangeRate() == null) {
			return "";
		}

		if (data.getContractType() != ContractType.NONE) {
			return getContractLocalPrice(data);
		}

		if ("USDT".equals(data.getQuoteAsset())) {
			//USDT分组本地价格计算
			return NewMarketWebsocket.getInstance().getCurrentExchangeRate().getSymbol() +
					BigDecimalUtils.multiplyHalfUp(data.getLastPrice(), NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate(), 4);
		} else if ("BRL".equals(data.getQuoteAsset())) {
			//BRL分组本地价格   （最新价/巴西语言汇率） * 当地语言的汇率  <==>  最新价* (当地语言的汇率 / 巴西语言汇率 )
			if (NewMarketWebsocket.getInstance().getExchangeRateMap() == null || NewMarketWebsocket.getInstance().getExchangeRateMap().get("pt_BR") == null) {
				return "";
			}
			if (NewMarketWebsocket.getInstance().getExchangeRateMap().get("pt_BR").getRate() == null) {
				return "";
			}
			return NewMarketWebsocket.getInstance().getCurrentExchangeRate().getSymbol() +
					BigDecimalUtils.multiplyHalfUp(
							data.getLastPrice(),
							BigDecimalUtils.divideToStr(
									NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate(),
									NewMarketWebsocket.getInstance().getExchangeRateMap().get("pt_BR").getRate(),
									5),
							4);
		} else {
			//其它分组本地价格
			if (NewMarketWebsocket.getInstance().getSpotMarketMap().get(data.getQuoteAsset() + "USDT") == null) {
				return "";
			}
			return NewMarketWebsocket.getInstance().getCurrentExchangeRate().getSymbol() +
					BigDecimalUtils.multiplyHalfUp(
							BigDecimalUtils.multiplyToStr(
									data.getLastPrice(),
									NewMarketWebsocket.getInstance().getSpotMarketMap().get(data.getQuoteAsset() + "USDT").getLastPrice()
							),
							NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate(),
							4
					);
		}
	}


	/**
	 * 从合约得到usdt估值
	 */
	public static String getUsdtEstimated(String symbol, String price) {
		if (TextUtils.isEmpty(symbol) || BigDecimalUtils.isEmptyOrZero(price)) {
			return "";
		}
		WsMarketData wsMarketData = NewMarketWebsocket.getInstance().getContractMarketMap().get(symbol);
		if (wsMarketData == null || TextUtils.isEmpty(wsMarketData.getLastPrice())) {
			return "";
		}
		return BigDecimalUtils.multiplyDown(price, wsMarketData.getLastPrice(), 2);
	}

	/**
	 * 计算合约本地价格
	 *
	 * @param data
	 * @return
	 */
	private static String getContractLocalPrice(WsMarketData data) {
		return NewMarketWebsocket.getInstance().getCurrentExchangeRate().getSymbol() +
				BigDecimalUtils.multiplyHalfUp(data.getLastPrice(), NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate(), 4);
	}

	/**
	 * 从Map中提取现货行情数据
	 *
	 * @param bean
	 * @param dataMap
	 */
	public static void getMarketDataFromMap(WsMarketData bean, Map<String, WsMarketData> dataMap) {
		try {
			if (bean == null || dataMap == null || dataMap.size() == 0 || dataMap.get(bean.getSymbol()) == null) {
				return;
			}
			bean.setLastPrice(dataMap.get(bean.getSymbol()).getLastPrice());
			bean.setVolume24h(dataMap.get(bean.getSymbol()).getVolume24h());
			bean.setContractType(dataMap.get(bean.getSymbol()).getContractType());
			bean.setUpsAndDowns(getUpsAndDowns(dataMap.get(bean.getSymbol())));
		} catch (NullPointerException e) {

		}
	}


	/**
	 * 得到usdt合约的分子
	 */
	public static String getUsdtContractBase(String symbol) {
		ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
		if (table != null) {
			return table.baseAsset;
		}
		return "";
	}


	/**
	 * 得到usdt合约的分子  或者btc合约symbol
	 */
	public static String getContractBase(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return "";
		}
		if (isUsdtContract(symbol)) {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			if (table != null) {
				return table.baseAsset;
			}
		} else {
			return symbol;
		}

		return "";
	}


	/**
	 * 得到usdt合约的分子
	 */
	public static boolean isUsdtContract(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return false;
		}
		if (symbol.contains("-")) {
			return true;
		}
		return false;
	}

	/**
	 * 得到合约类型的枚举值
	 */
	public static ContractType getContractType(String symbol) {
		return isUsdtContract(symbol) ? ContractType.USDT : ContractType.BTC;
	}
}
