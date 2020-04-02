package com.coinbene.common.utils;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.manbiwang.model.contract.CalAvlPositionModel;
import com.coinbene.manbiwang.model.contract.PriceParamsModel;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * http://59.110.163.209:8090/pages/viewpage.action?pageId=7343283
 * 计算公式utils
 */
public class CalculationUtils {

	/**
	 * 合约开仓  可买可卖 张数 计算工具
	 * bigDecimalOne  1的BigDecimal
	 * bigDecimalTwo  2的BigDecimal
	 * 用于方法中的计算
	 */
	private static final BigDecimal bigDecimalOne = new BigDecimal(1);
	private static final BigDecimal bigDecimalBtc = new BigDecimal(0.5).setScale(1, BigDecimal.ROUND_DOWN);
	private static final BigDecimal bigDecimalOther = new BigDecimal(0.05).setScale(2, BigDecimal.ROUND_DOWN);
	private static final BigDecimal bigDecimalTwo = new BigDecimal(2);
	private static final BigDecimal bigDecimalZero = new BigDecimal(0);
	private static BigDecimal buyUnitPrice = null;
	private static BigDecimal sellUnitPrice = null;
	public static final BigDecimal maxAvlOpenAmount = new BigDecimal(999999999).setScale(0, BigDecimal.ROUND_DOWN);


	/**
	 * BTC可开张数
	 * BTC开仓价格选取如下	开仓数量=可用余额*开仓价格*杠杆/（杠杆*taker手续费*2+1）
	 * <p>
	 * <p>
	 * BTC限价价格
	 * 1. 填入价≥卖一价
	 * 买入 ：成本计算选取价格=买一价*（1-0.06%）
	 * 卖出 ：成本计算选取价格=填入价*（1-0.06%）
	 * 2. 填入价≤买一价
	 * 买入 ：成本计算选取价格=填入价*（1-0.06%）
	 * 卖出 ：成本计算选取价格=填入价*（1-0.06%）
	 * 3. 买一价＜填入价＜卖一价
	 * 买入 ：成本计算选取价格=买一价*（1-0.06%）
	 * 卖出 ：成本计算选取价格=买一价*（1-0.06%）
	 * <p>
	 * <p>
	 * BTC市价价格	   买入 ：成本计算选取价格=买一价*（1-0.06%）
	 * 卖出 ：成本计算选取价格=买一价*（1-0.06%）
	 *
	 * @param fixPriceType        限价 0   市价  1；
	 * @param inputPrice          填入价格
	 * @param firstBuy            盘口买一加
	 * @param firstSell           盘口卖一价
	 * @param lastPrice           最新价
	 * @param costPriceMultiplier 开仓成本选取规则乘数
	 * @param avlBalance          可用余额
	 * @param lever               杠杆
	 * @param takeFee             take费率
	 * @param perscent            精度
	 * @param mpPrice             标记价格
	 * @param minPriceChange      最小价格变化
	 */
	public static String[] calculationBtc(int fixPriceType, String inputPrice, String firstBuy, String firstSell, String lastPrice,
										  String mpPrice, String costPriceMultiplier, String avlBalance, int lever, String takeFee,
										  int perscent, String minPriceChange) {


		String arr[] = {"0", "0"};

		if (BigDecimalUtils.isEmptyOrZero(avlBalance) || BigDecimalUtils.isEmptyOrZero(mpPrice)) {
			return arr;
		}
		BigDecimal avlBalanceBd = new BigDecimal(avlBalance);
		BigDecimal leverBd = new BigDecimal(lever);
		BigDecimal takeFeeBd = new BigDecimal(takeFee);
		BigDecimal mpBd = new BigDecimal(mpPrice);


		BigDecimal oneLongNumberPrice = null;
		BigDecimal oneShortNumberPrice = null;
		BigDecimal minPriceChangeBg = null;
		if (!TextUtils.isEmpty(minPriceChange)) {
			minPriceChangeBg = new BigDecimal(minPriceChange).setScale(perscent, BigDecimal.ROUND_DOWN);
		} else {
			minPriceChangeBg = bigDecimalBtc;
		}
		//市价
		if (fixPriceType == 1) {
			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				if (TextUtils.isEmpty(lastPrice)) {
					return arr;
				}

				buyUnitPrice = new BigDecimal(lastPrice);
				sellUnitPrice = new BigDecimal(lastPrice);
			} else {
				BigDecimal firsetBuyBd = new BigDecimal(firstBuy);
				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal multiplier = new BigDecimal(costPriceMultiplier);
				buyUnitPrice = convertPrice(firstSellBd.multiply(multiplier), perscent, minPriceChangeBg);
				sellUnitPrice = convertPrice(firsetBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
			}
			if (buyUnitPrice.compareTo(bigDecimalZero) != 0)
				oneLongNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(buyUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(buyUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(buyUnitPrice, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);

			if (sellUnitPrice.compareTo(bigDecimalZero) != 0)
				oneShortNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(sellUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(sellUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(buyUnitPrice, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);


		}
		//限价和高级委托
		else {
			if (BigDecimalUtils.isEmptyOrZero(inputPrice)) {
				return arr;
			}
			BigDecimal inputPriceBd = new BigDecimal(inputPrice);
			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				buyUnitPrice = new BigDecimal(inputPrice);
				sellUnitPrice = new BigDecimal(inputPrice);
			} else {

				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal firstBuyBd = new BigDecimal(firstBuy);
				BigDecimal multiplier = new BigDecimal(costPriceMultiplier);

				//填入价≥卖一价
				if (inputPriceBd.compareTo(firstSellBd) >= 0) {
					buyUnitPrice = convertPrice(firstBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplier), perscent, minPriceChangeBg);
				}
				//填入价≤买一价
				else if (inputPriceBd.compareTo(firstBuyBd) <= 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplier), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplier), perscent, minPriceChangeBg);
				}
				//买一价＜填入价＜卖一价
				else if (inputPriceBd.compareTo(firstBuyBd) > 0 && inputPriceBd.compareTo(firstSellBd) < 0) {
					buyUnitPrice = convertPrice(firstBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(firstBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
				}
			}
			if (buyUnitPrice.compareTo(bigDecimalZero) != 0)
				oneLongNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(buyUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(buyUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(inputPriceBd, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);

			if (sellUnitPrice.compareTo(bigDecimalZero) != 0)
				oneShortNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(sellUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(sellUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(inputPriceBd, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);


		}
		if (oneLongNumberPrice != null && oneLongNumberPrice.compareTo(bigDecimalZero) != 0) {
			arr[0] = avlBalanceBd.divide(oneLongNumberPrice, 0, BigDecimal.ROUND_DOWN).toPlainString();
		}
		if (oneShortNumberPrice != null && oneShortNumberPrice.compareTo(bigDecimalZero) != 0) {
			arr[1] = avlBalanceBd.divide(oneShortNumberPrice, 0, BigDecimal.ROUND_DOWN).toPlainString();
		}


		if (BigDecimalUtils.lessThanToZero(arr[0])) {
			arr[0] = "0";
		}

		if (BigDecimalUtils.lessThanToZero(arr[1])) {
			arr[1] = "0";
		}
		return arr;
	}


	public static String[] calculationBtcBond(int fixPriceType, String inputPrice, String firstBuy, String firstSell, String lastPrice,
											  String mpPrice, String costPriceMultiplier, String avlBalance, int lever, String takeFee,
											  int perscent, String minPriceChange, String quantity) {


		String arr[] = {"0", "0"};

		if (BigDecimalUtils.isEmptyOrZero(avlBalance) || BigDecimalUtils.isEmptyOrZero(mpPrice) || BigDecimalUtils.isEmptyOrZero(quantity)) {
			return arr;
		}
		BigDecimal avlBalanceBd = new BigDecimal(avlBalance);
		BigDecimal leverBd = new BigDecimal(lever);
		BigDecimal takeFeeBd = new BigDecimal(takeFee);
		BigDecimal mpBd = new BigDecimal(mpPrice);


		BigDecimal oneLongNumberPrice = null;
		BigDecimal oneShortNumberPrice = null;
		BigDecimal minPriceChangeBg = null;
		if (!TextUtils.isEmpty(minPriceChange)) {
			minPriceChangeBg = new BigDecimal(minPriceChange).setScale(perscent, BigDecimal.ROUND_DOWN);
		} else {
			minPriceChangeBg = bigDecimalBtc;
		}
		//市价
		if (fixPriceType == 1) {
			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				if (TextUtils.isEmpty(lastPrice)) {
					return arr;
				}

				buyUnitPrice = new BigDecimal(lastPrice);
				sellUnitPrice = new BigDecimal(lastPrice);
			} else {
				BigDecimal firsetBuyBd = new BigDecimal(firstBuy);
				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal multiplier = new BigDecimal(costPriceMultiplier);
				buyUnitPrice = convertPrice(firstSellBd.multiply(multiplier), perscent, minPriceChangeBg);
				sellUnitPrice = convertPrice(firsetBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
			}
			if (buyUnitPrice.compareTo(bigDecimalZero) != 0)
				oneLongNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(buyUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(buyUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(buyUnitPrice, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);

			if (sellUnitPrice.compareTo(bigDecimalZero) != 0)
				oneShortNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(sellUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(sellUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(buyUnitPrice, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);


		}
		//限价和高级委托
		else {
			if (BigDecimalUtils.isEmptyOrZero(inputPrice)) {
				return arr;
			}
			BigDecimal inputPriceBd = new BigDecimal(inputPrice);
			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				buyUnitPrice = new BigDecimal(inputPrice);
				sellUnitPrice = new BigDecimal(inputPrice);
			} else {

				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal firstBuyBd = new BigDecimal(firstBuy);
				BigDecimal multiplier = new BigDecimal(costPriceMultiplier);

				//填入价≥卖一价
				if (inputPriceBd.compareTo(firstSellBd) >= 0) {
					buyUnitPrice = convertPrice(firstBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplier), perscent, minPriceChangeBg);
				}
				//填入价≤买一价
				else if (inputPriceBd.compareTo(firstBuyBd) <= 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplier), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplier), perscent, minPriceChangeBg);
				}
				//买一价＜填入价＜卖一价
				else if (inputPriceBd.compareTo(firstBuyBd) > 0 && inputPriceBd.compareTo(firstSellBd) < 0) {
					buyUnitPrice = convertPrice(firstBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(firstBuyBd.multiply(multiplier), perscent, minPriceChangeBg);
				}
			}
			if (buyUnitPrice.compareTo(bigDecimalZero) != 0)
				oneLongNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(buyUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(buyUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(inputPriceBd, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);

			if (sellUnitPrice.compareTo(bigDecimalZero) != 0)
				oneShortNumberPrice = (bigDecimalTwo.multiply(takeFeeBd).divide(sellUnitPrice, 10, BigDecimal.ROUND_UP))
						.add((bigDecimalOne.divide(sellUnitPrice.multiply(leverBd), 10, BigDecimal.ROUND_UP))
								.subtract(BigDecimal.valueOf(Math.min(0d,
										bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_UP)
												.subtract(bigDecimalOne.divide(inputPriceBd, 10, BigDecimal.ROUND_UP)).doubleValue()))))
						.setScale(10, BigDecimal.ROUND_UP);


		}

		BigDecimal quantutyBd = new BigDecimal(quantity);
		arr[0] = oneLongNumberPrice.multiply(quantutyBd).setScale(4, BigDecimal.ROUND_UP).toPlainString();
		arr[1] = oneShortNumberPrice.multiply(quantutyBd).setScale(4, BigDecimal.ROUND_UP).toPlainString();
		if (BigDecimalUtils.lessThanToZero(arr[0])) {
			arr[0] = "0";
		}
		if (BigDecimalUtils.lessThanToZero(arr[1])) {
			arr[1] = "0";
		}
		return arr;
	}

	/*
	 * http://59.110.163.209:8090/pages/viewpage.action?pageId=20645367
	 * BTC合约下的btcUSDT合约 占用保证金计算公式
	 *
	 * @param priceParamsModel
	 * @param calAvlPositionModel
	 * @param usdtTable
	 * @param quantity
	 * @return
	 */
	public static String[] calculationUsdtBond(int fixPriceType, String inputPrice, String firstBuy, String firstSell, String lastPrice,
											   String mpPrice, String multiplier, String avlBalance, int lever,
											   String takeFee, String multiplyNumber, int perscent, String minPriceChange, String quantity) {
		String arr[] = {"0", "0"};

		if (BigDecimalUtils.isEmptyOrZero(avlBalance) || BigDecimalUtils.isEmptyOrZero(mpPrice) || BigDecimalUtils.isEmptyOrZero(quantity)) {
			return arr;
		}

		BigDecimal multiplierBd = new BigDecimal(multiplier);
		BigDecimal avlBalanceBd = new BigDecimal(avlBalance);
		BigDecimal leverBd = new BigDecimal(lever);
		BigDecimal takeFeeBd = new BigDecimal(takeFee);
		BigDecimal multiplyNumberBd = new BigDecimal(multiplyNumber);
		BigDecimal mpBd = new BigDecimal(mpPrice);
		BigDecimal oneLongNumberPrice = null;
		BigDecimal oneShortNumberPrice = null;
		BigDecimal minPriceChangeBg = null;
		if (!TextUtils.isEmpty(minPriceChange)) {
			minPriceChangeBg = new BigDecimal(minPriceChange).setScale(perscent, BigDecimal.ROUND_DOWN);
		} else {
			minPriceChangeBg = bigDecimalOther;
		}
		//市价
		if (fixPriceType == 1) {
			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				if (TextUtils.isEmpty(lastPrice)) {
					return arr;
				}
				buyUnitPrice = new BigDecimal(lastPrice);
				sellUnitPrice = new BigDecimal(lastPrice);
			} else {
				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal firsetBuyBd = new BigDecimal(firstBuy);
				buyUnitPrice = convertPrice(firstSellBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				sellUnitPrice = convertPrice(firsetBuyBd.multiply(multiplierBd), perscent, minPriceChangeBg);
			}
			oneLongNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(buyUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(buyUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(mpBd.subtract(buyUnitPrice)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);


			oneShortNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(sellUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(sellUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(sellUnitPrice.subtract(mpBd)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);

		} else { //限价和高级委托

			if (BigDecimalUtils.isEmptyOrZero(inputPrice)) {
				return arr;
			}
			BigDecimal inputPriceBd = new BigDecimal(inputPrice);

			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				buyUnitPrice = new BigDecimal(inputPrice);
				sellUnitPrice = new BigDecimal(inputPrice);
			} else {
				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal firstBuyBd = new BigDecimal(firstBuy);


				//填入价≥卖一价
				if (inputPriceBd.compareTo(firstSellBd) >= 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				}
				//填入价≤买一价
				else if (inputPriceBd.compareTo(firstBuyBd) <= 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(firstBuyBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				}
				//买一价＜填入价＜卖一价
				else if (inputPriceBd.compareTo(firstBuyBd) > 0 && inputPriceBd.compareTo(firstSellBd) < 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				}
			}

			oneLongNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(buyUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(buyUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(mpBd.subtract(inputPriceBd)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);
//                                            .subtract(bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_DOWN)).doubleValue()))));


			oneShortNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(sellUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(sellUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(inputPriceBd.subtract(mpBd)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);

		}
		BigDecimal quantutyBd = new BigDecimal(quantity);
		arr[0] = oneLongNumberPrice.multiply(quantutyBd).setScale(2, BigDecimal.ROUND_UP).toPlainString();
		arr[1] = oneShortNumberPrice.multiply(quantutyBd).setScale(2, BigDecimal.ROUND_UP).toPlainString();
		if (BigDecimalUtils.lessThanToZero(arr[0])) {
			arr[0] = "0";
		}
		if (BigDecimalUtils.lessThanToZero(arr[1])) {
			arr[1] = "0";

		}
		return arr;
	}


	/**
	 * http://59.110.163.209:8090/pages/viewpage.action?pageId=7347021
	 * BTC其他可开张数
	 * 非BTC开仓数量	开仓数量=可用余额*杠杆/[开仓价*乘数（杠杆*taker手续费*2+1）]
	 * 计算非BTC合约的 可买可卖数量
	 * <p>
	 * ETH市价	   买入 ：成本计算选取价格=卖一价*（1+0.06%）
	 * 卖出 ：成本计算选取价格=买一价*（1+0.06%）
	 * <p>
	 * ETH限价	1. 填入价≥卖一价
	 * 买入 ：成本计算选取价格=填入价*（1+0.06%）
	 * 卖出 ：成本计算选取价格=填入价*（1+0.06%）
	 * <p>
	 * 2. 填入价≤买一价
	 * 买入 ：成本计算选取价格=填入价*（1+0.06%）
	 * 卖出 ：成本计算选取价格=买一价*（1+0.06%）
	 * <p>
	 * 3. 买一价＜填入价＜卖一价
	 * 买入 ：成本计算选取价格=填入价*（1+0.06%）
	 * 卖出 ：成本计算选取价格=填入价*（1+0.06%）
	 *
	 * @param inputPrice     填入价格
	 * @param firstBuy       盘口买一加
	 * @param firstSell      盘口卖一价
	 * @param multiplier     开仓成本选取规则乘数
	 * @param avlBalance     可用余额
	 * @param lever          杠杆
	 * @param takeFee        take费率
	 * @param multiplyNumber 乘数
	 * @param minPriceChange 最小价格变化
	 */


	public static String[] calculationOther(int fixPriceType, String inputPrice, String firstBuy, String firstSell, String lastPrice,
											String mpPrice, String multiplier, String avlBalance, int lever,
											String takeFee, String multiplyNumber, int perscent, String minPriceChange) {
		String arr[] = {"0", "0"};

		if (BigDecimalUtils.isEmptyOrZero(avlBalance) || BigDecimalUtils.isEmptyOrZero(mpPrice)) {
			return arr;
		}

		BigDecimal multiplierBd = new BigDecimal(multiplier);
		BigDecimal avlBalanceBd = new BigDecimal(avlBalance);
		BigDecimal leverBd = new BigDecimal(lever);
		BigDecimal takeFeeBd = new BigDecimal(takeFee);
		BigDecimal multiplyNumberBd = new BigDecimal(multiplyNumber);
		BigDecimal mpBd = new BigDecimal(mpPrice);
		BigDecimal oneLongNumberPrice = null;
		BigDecimal oneShortNumberPrice = null;
		BigDecimal minPriceChangeBg = null;
		if (!TextUtils.isEmpty(minPriceChange)) {
			minPriceChangeBg = new BigDecimal(minPriceChange).setScale(perscent, BigDecimal.ROUND_DOWN);
		} else {
			minPriceChangeBg = bigDecimalOther;
		}
		//市价
		if (fixPriceType == 1) {
			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				if (TextUtils.isEmpty(lastPrice)) {
					return arr;
				}
				buyUnitPrice = new BigDecimal(lastPrice);
				sellUnitPrice = new BigDecimal(lastPrice);
			} else {
				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal firsetBuyBd = new BigDecimal(firstBuy);
				buyUnitPrice = convertPrice(firstSellBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				sellUnitPrice = convertPrice(firsetBuyBd.multiply(multiplierBd), perscent, minPriceChangeBg);
			}
			oneLongNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(buyUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(buyUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(mpBd.subtract(buyUnitPrice)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);


			oneShortNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(sellUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(sellUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(sellUnitPrice.subtract(mpBd)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);

		} else { //限价和高级委托

			if (BigDecimalUtils.isEmptyOrZero(inputPrice)) {
				return arr;
			}
			BigDecimal inputPriceBd = new BigDecimal(inputPrice);

			if (TextUtils.isEmpty(firstBuy) || TextUtils.isEmpty(firstSell)) {
				buyUnitPrice = new BigDecimal(inputPrice);
				sellUnitPrice = new BigDecimal(inputPrice);
			} else {
				BigDecimal firstSellBd = new BigDecimal(firstSell);
				BigDecimal firstBuyBd = new BigDecimal(firstBuy);


				//填入价≥卖一价
				if (inputPriceBd.compareTo(firstSellBd) >= 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				}
				//填入价≤买一价
				else if (inputPriceBd.compareTo(firstBuyBd) <= 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(firstBuyBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				}
				//买一价＜填入价＜卖一价
				else if (inputPriceBd.compareTo(firstBuyBd) > 0 && inputPriceBd.compareTo(firstSellBd) < 0) {
					buyUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
					sellUnitPrice = convertPrice(inputPriceBd.multiply(multiplierBd), perscent, minPriceChangeBg);
				}
			}

			oneLongNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(buyUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(buyUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(mpBd.subtract(inputPriceBd)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);
//                                            .subtract(bigDecimalOne.divide(mpBd, 10, BigDecimal.ROUND_DOWN)).doubleValue()))));


			oneShortNumberPrice = (bigDecimalTwo.multiply(multiplyNumberBd).multiply(sellUnitPrice).multiply(takeFeeBd))
					.add((multiplyNumberBd.multiply(sellUnitPrice).divide(leverBd, 10, BigDecimal.ROUND_UP))
							.subtract(BigDecimal.valueOf(Math.min(0d,
									multiplyNumberBd.multiply(inputPriceBd.subtract(mpBd)).doubleValue())))).setScale(10, BigDecimal.ROUND_UP);

		}
		if (oneLongNumberPrice.compareTo(bigDecimalZero) != 0) {
			arr[0] = avlBalanceBd.divide(oneLongNumberPrice, 0, BigDecimal.ROUND_DOWN).toPlainString();
		}
		if (oneShortNumberPrice.compareTo(bigDecimalZero) != 0) {
			arr[1] = avlBalanceBd.divide(oneShortNumberPrice, 0, BigDecimal.ROUND_DOWN).toPlainString();
		}
		if (BigDecimalUtils.lessThanToZero(arr[0])) {
			arr[0] = "0";
		}
		if (BigDecimalUtils.lessThanToZero(arr[1])) {
			arr[1] = "0";
		}
		return arr;
	}


	/**
	 * 单价需要按照scale的精度   minPriceChange最小进位   BTC是0.5   其他是0.05
	 *
	 * @param price
	 * @param scale
	 * @param minPriceChange
	 * @return
	 */
	public static BigDecimal convertPrice(BigDecimal price, int scale, BigDecimal minPriceChange) {
		//不包含.5的  直接返回
		if (!minPriceChange.toPlainString().contains("5")) {
			return price.setScale(scale, BigDecimal.ROUND_UP);
		}
		BigDecimal before = price.setScale(scale, BigDecimal.ROUND_DOWN);
		BigDecimal after = price.setScale(scale - 1, BigDecimal.ROUND_DOWN);
		if (before.subtract(after).compareTo(minPriceChange) >= 0) {
			return after.add(minPriceChange).setScale(scale);
		} else {
			return after.setScale(scale);
		}
	}


	/**
	 * otc计算可买的usdt量
	 *
	 * @param usdt
	 * @param minCount
	 * @param maxCount
	 * @param price
	 * @return
	 */
	public static String[] calculateOtcBuyAll(String usdt, String minCount, String
			maxCount, String price) {
		if (TextUtils.isEmpty(usdt) || TextUtils.isEmpty(minCount) || TextUtils.isEmpty(maxCount) || TextUtils.isEmpty(price))
			return null;
		String[] result = new String[2];
		BigDecimal us = new BigDecimal(usdt);
		BigDecimal min = new BigDecimal(minCount);
		BigDecimal max = new BigDecimal(maxCount);
		BigDecimal p = new BigDecimal(price);
		BigDecimal mutResult = us.multiply(p);
		int minResult = mutResult.compareTo(min);
		int maxResult = mutResult.compareTo(max);

		if (minResult == -1) {//量和单价相乘还小与最小的限额
			return null;
		} else if (minResult != -1 && maxResult != 1) {
			result[0] = PrecisionUtils.getRoundDown(us.toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(mutResult.toPlainString(), Constants.cnyPrecision);
		} else if (maxResult == 1) {
			result[0] = PrecisionUtils.getRoundDown(max.divide(p, 6, BigDecimal.ROUND_DOWN).toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(max.toPlainString(), Constants.cnyPrecision);
		}
		return result;
	}


	/**
	 * otc计算可卖的usdt量
	 *
	 * @param usdt
	 * @param minCount
	 * @param maxCount
	 * @param price
	 * @return
	 */
	public static String[] calculateOtcSellAll(String usdt, String minCount, String
			maxCount, String price, String avlUsdt) {
		String[] result = new String[2];
		if (TextUtils.isEmpty(usdt) || TextUtils.isEmpty(minCount) || TextUtils.isEmpty(maxCount) || TextUtils.isEmpty(price))
			return null;
		if (TextUtils.isEmpty(avlUsdt)) {
			avlUsdt = "0";
		}
		BigDecimal us = new BigDecimal(usdt);
		BigDecimal min = new BigDecimal(minCount);
		BigDecimal max = new BigDecimal(maxCount);
		BigDecimal p = new BigDecimal(price);
		BigDecimal avlUs = new BigDecimal(avlUsdt);
		BigDecimal mutResult = us.multiply(p);
		BigDecimal divResult = min.divide(p, 6, BigDecimal.ROUND_DOWN);
		BigDecimal avlMutPrice = avlUs.multiply(p);
		int minResult = mutResult.compareTo(min);
		int maxResult = mutResult.compareTo(max);
		int avlResult = avlUs.compareTo(us);
		int minUsResult = avlUs.compareTo(divResult);
		int avlComPrice = avlMutPrice.compareTo(max);
		if (minResult == -1) {//量和单价相乘还小与最小的限额
			return null;
		} else if (minResult != -1 && maxResult != 1 && avlResult != -1) {
			result[0] = PrecisionUtils.getRoundDown(us.toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(mutResult.toPlainString(), Constants.cnyPrecision);
		} else if (minResult != -1 && maxResult != 1 && avlResult == -1 && minUsResult != -1) {
			result[0] = PrecisionUtils.getRoundDown(avlUs.toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(avlMutPrice.toPlainString(), Constants.cnyPrecision);
		} else if (minResult != -1 && maxResult != 1 && minUsResult == -1) {//可用余额不足
			result[0] = PrecisionUtils.getRoundDown(avlUs.toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(avlMutPrice.toPlainString(), Constants.cnyPrecision);
		} else if (maxResult == 1 && avlComPrice == 1) {
			result[0] = PrecisionUtils.getRoundDown(max.divide(p, 6, BigDecimal.ROUND_DOWN).toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(max.toPlainString(), Constants.cnyPrecision);
		} else if (maxResult == 1 && avlComPrice != 1) {
//            result[0] = getRoundDown(max.divide(avlUs, 6, BigDecimal.ROUND_DOWN).toPlainString(), Constants.usdtPrecision);
			result[0] = PrecisionUtils.getRoundDown(avlUs.toPlainString(), Constants.usdtPrecision);
			result[1] = PrecisionUtils.getRoundDown(avlUs.multiply(p).toPlainString(), Constants.cnyPrecision);
		}
		return result;
	}

	/**
	 * 计算止盈止损预期盈亏
	 * <p>
	 * 根据委托限价和开仓均价计算收益。预期盈亏计算精度小数点后10位，向下取整，展示精度小数点后4位向下取整。盈亏单位BTC，精度小数点后4位，向下取整。小数点后0不展示。
	 * 开仓均价：仓位的开仓均价
	 * 委托价格：止盈止损的填入委托价格
	 * BTC
	 * 多仓-止盈&止损：（1/开仓均价-1/委托价格）*平仓合约张数
	 * 空仓-止盈&止损：（1/委托价格-1/开仓均价）*平仓合约张数
	 *
	 * @parm side 持仓多空方向
	 * @parm avgPrice 开仓均价
	 * @parm entrustPrice 委托价格
	 * @parm cont 平仓合约张数
	 * @parm multiplier 乘数
	 */
	public static String calculateBtcExpectedReturn(String side, String avgPrice, String entrustPrice, String cont) {


		BigDecimal bdAvgPrice = new BigDecimal(avgPrice);
		BigDecimal bdEntrustPrice = new BigDecimal(entrustPrice);
		BigDecimal bdCont = new BigDecimal(cont);


		if (side.equals("long")) {
			return (bigDecimalOne.divide(bdAvgPrice, 10, BigDecimal.ROUND_DOWN)
					.subtract(bigDecimalOne.divide(bdEntrustPrice, 10, BigDecimal.ROUND_DOWN)))
					.multiply(bdCont)
					.setScale(4, BigDecimal.ROUND_DOWN).toPlainString();

		} else {
			return (bigDecimalOne.divide(bdEntrustPrice, 10, BigDecimal.ROUND_DOWN)
					.subtract(bigDecimalOne.divide(bdAvgPrice, 10, BigDecimal.ROUND_DOWN)))
					.multiply(bdCont)
					.setScale(4, BigDecimal.ROUND_DOWN).toPlainString();
		}
	}

	/**
	 * * ETH
	 * * 多仓-止盈&止损：（委托价格-开仓均价）*平仓合约张数*乘数
	 * * 空仓-止盈&止损：（开仓均价-委托价格）*平仓合约张数*乘数
	 * *
	 *
	 * @param side
	 * @param avgPrice
	 * @param entrustPrice
	 * @param cont
	 * @param multiplier
	 * @return
	 */
	public static String calculateEthExpectedReturn(String side, String avgPrice, String entrustPrice, String cont, String multiplier, int precision) {

		if (TextUtils.isEmpty(avgPrice) || TextUtils.isEmpty(entrustPrice) || TextUtils.isEmpty(cont) || TextUtils.isEmpty(multiplier)) {
			return "--";
		}
		BigDecimal bdAvgPrice = new BigDecimal(avgPrice);
		BigDecimal bdEntrustPrice = new BigDecimal(entrustPrice);
		BigDecimal bdCont = new BigDecimal(cont);
		BigDecimal bdMultiplier = new BigDecimal(multiplier);

		if (side.equals("long")) {
			return (bdEntrustPrice.subtract(bdAvgPrice))
					.multiply(bdCont)
					.multiply(bdMultiplier)
					.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();

		} else {
			return (bdAvgPrice.subtract(bdEntrustPrice))
					.multiply(bdCont)
					.multiply(bdMultiplier)
					.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
		}
	}


	/**
	 * * ETH
	 * * 多仓-止盈&止损：（委托价格-开仓均价）*平仓合约张数*乘数
	 * * 空仓-止盈&止损：（开仓均价-委托价格）*平仓合约张数*乘数
	 * *
	 * usdt合约需要判断是否是切换单位
	 *
	 * @param side
	 * @param avgPrice
	 * @param entrustPrice
	 * @param cont
	 * @param multiplier
	 * @return
	 */
	public static String calculateEthExpectedReturnUsdtContract(String side, String avgPrice, String entrustPrice, String cont, String multiplier, int precision) {

		if (TextUtils.isEmpty(avgPrice) || TextUtils.isEmpty(entrustPrice) || TextUtils.isEmpty(cont) || TextUtils.isEmpty(multiplier)) {
			return "--";
		}
		BigDecimal bdAvgPrice = new BigDecimal(avgPrice);
		BigDecimal bdEntrustPrice = new BigDecimal(entrustPrice);
		BigDecimal bdCont = new BigDecimal(cont);
		BigDecimal bdMultiplier = new BigDecimal(multiplier);

		if (side.equals("long")) {
			if (SpUtil.getContractUsdtUnitSwitch() == 0) {
				return (bdEntrustPrice.subtract(bdAvgPrice))
						.multiply(bdCont)
						.multiply(bdMultiplier)
						.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
			} else {
				return (bdEntrustPrice.subtract(bdAvgPrice))
						.multiply(bdCont)
						.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
			}

		} else {

			if (SpUtil.getContractUsdtUnitSwitch() == 0) {
				return (bdAvgPrice.subtract(bdEntrustPrice))
						.multiply(bdCont)
						.multiply(bdMultiplier)
						.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
			} else {
				return (bdAvgPrice.subtract(bdEntrustPrice))
						.multiply(bdCont)
						.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
			}
		}
	}


	/**
	 * 计算可买 可卖数量  从seekbar百分比计算
	 *
	 * @param unitPrice       单价
	 * @param percentage      仓位百分比
	 * @param avlBalance      可用余额
	 * @param isBuy           买卖方向
	 * @param takeFee         take费
	 * @param volumePrecision 数量精度
	 *                        <p>
	 *                        <p>
	 *                        买入
	 *                        总额 = 价格*数量*（1+takeFee）  价格精度向上取整     数量精度向下取整
	 *                        卖出
	 *                        总额 =  价格*数量*（1-takeFee） 价格精度向下取整		数量精度向下取整
	 */

	public static String calQuantityFromSeekBar(String unitPrice, String percentage, String avlBalance, String takeFee, int volumePrecision, boolean isBuy) {
		if (BigDecimalUtils.isEmptyOrZero(unitPrice) || BigDecimalUtils.isEmptyOrZero(percentage) || BigDecimalUtils.isEmptyOrZero(avlBalance)) {
			return "";
		}
		if (isBuy) {
			return BigDecimalUtils.divideToStr(BigDecimalUtils.multiplyToStr(avlBalance, percentage)
					, BigDecimalUtils.multiply(unitPrice, BigDecimalUtils.add("1", takeFee)).toPlainString()
					, volumePrecision);
		} else {
			return BigDecimalUtils.setScaleDown(BigDecimalUtils.multiplyToStr(avlBalance, percentage)
					, volumePrecision);
		}
	}


	/**
	 * 计算买入卖出总额
	 *
	 * @param unitPrice      单价
	 * @param vol            数量
	 * @param isBuy          买卖方向
	 * @param takeFee        take费
	 * @param pricePrecision 价格精度
	 *                       <p>
	 *                       <p>
	 *                       买入向上取整
	 *                       卖出向下取整
	 *                       买入总额 = 价格*数量*（1+takeFee）  价格精度向上取整
	 *                       卖出总额 =  价格*数量*（1-takeFee） 价格精度向下取整
	 */
	public static String calTotalPrice(String unitPrice, String vol, String takeFee, int pricePrecision, boolean isBuy) {
		if (BigDecimalUtils.isEmptyOrZero(unitPrice) || BigDecimalUtils.isEmptyOrZero(vol)) {
			return "";
		}
		if (isBuy) {
			return BigDecimalUtils.multiplyUp(
					BigDecimalUtils.multiplyToStr(unitPrice, vol),
					BigDecimalUtils.add("1", takeFee),
					pricePrecision);


		} else {
			return BigDecimalUtils.multiplyDown(
					BigDecimalUtils.multiplyToStr(unitPrice, vol),
					BigDecimalUtils.subtract("1", takeFee),
					pricePrecision);

		}

	}


	/**
	 * 现货  杠杆计算买入卖出数量 从总额的变化
	 *
	 * @param unitPrice
	 * @param totalPrice
	 * @param takeFee
	 * @param volumePrecision
	 * @param isBuy
	 */
	public static String calQuantityFromTotalPrice(String unitPrice, String totalPrice, String takeFee, int volumePrecision, boolean isBuy) {
		if (BigDecimalUtils.isEmptyOrZero(totalPrice) || BigDecimalUtils.isEmptyOrZero(unitPrice)) {
			return "";
		}
		if (isBuy) {
			return BigDecimalUtils.divideToStr(
					BigDecimalUtils.divideToStr(totalPrice, unitPrice, volumePrecision + 2)
					, BigDecimalUtils.add("1", String.valueOf(takeFee))
					, volumePrecision);
		} else {
			return BigDecimalUtils.divideToStrUp(
					BigDecimalUtils.divideToStr(totalPrice, unitPrice, volumePrecision + 2)
					, BigDecimalUtils.subtract("1", String.valueOf(takeFee))
					, volumePrecision);
		}

	}


	/**
	 * 处理杠杆数据
	 */
	public static String[] leverHandle(String symbol) {
		ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(symbol);
		String[] levers = new String[0];
		if (table != null && !TextUtils.isEmpty(table.leverages)) {
			levers = table.leverages.split(",");
			for (int i = 0; i < levers.length; i++) {
				levers[i] = levers[i] + "X";
			}
			return levers;
		}
		return levers;
	}

	/**
	 * 市价下预估可买或者可卖多少
	 *
	 * @param isBuyTab      买卖方向
	 * @param price         输入金额或者数量  买入的时候是金额  卖出是数量
	 * @param lastPrice     最新价
	 * @param buyPrice_one  买一价
	 * @param sellPrice_one 卖一价
	 * @param precision     精度
	 *                      <p>
	 *                      <p>
	 *                      可以买到的数量  = [用户输入金额*（1-0.001）]/最新成交价
	 *                      可以卖出的金额 = （用户输入的数量）*最新成交价*（1-0.001）
	 */
	public static String calMarketExpect(boolean isBuyTab, String price, String lastPrice, String buyPrice_one, String sellPrice_one, int pricePrecision, int precision, String takefee, String base, String qoute) {
		if (TextUtils.isEmpty(price)) {
			if (isBuyTab) {
				return String.format("%s%s", "--", base);
			} else {
				return String.format("%s%s", "--", qoute);
			}
		}


		if (isBuyTab) {
			return String.format(String.format("%s%s", BigDecimalUtils.divideToStr(
					BigDecimalUtils.multiplyToStr(price, BigDecimalUtils.subtract("1", takefee)),
					!TextUtils.isEmpty(lastPrice) ? lastPrice : sellPrice_one,
					precision), base));
		} else {
			return
					String.format(String.format("%s%s", BigDecimalUtils.multiplyDown(BigDecimalUtils.multiplyToStr(price, !TextUtils.isEmpty(lastPrice) ? lastPrice : buyPrice_one),
							BigDecimalUtils.subtract("1", takefee),
							pricePrecision), qoute));

		}
	}

	/**
	 * 市价止盈止损计算预估可买或者预估可买
	 *
	 * @param buyTab          是否是买
	 * @param priceOrQuantity 买的时候是金额，卖的时候是数量
	 * @param touchPrice
	 * @return
	 */
	public static String calMarketLossExpect(boolean buyTab, String priceOrQuantity, String touchPrice, String base, String qoute, int pricePrecision) {
		if (TextUtils.isEmpty(priceOrQuantity) || TextUtils.isEmpty(touchPrice)) {
			if (buyTab) {
				return String.format("%s%s", "--", base);
			} else {
				return String.format("%s%s", "--", qoute);
			}
		}
		if (buyTab) {
			return String.format(String.format("%s%s", BigDecimalUtils.divideToStr(
					priceOrQuantity,
					touchPrice,
					pricePrecision), base));
		} else {
			return
					String.format(String.format("%s%s", BigDecimalUtils.multiplyDown(priceOrQuantity,
							touchPrice,
							pricePrecision), qoute));

		}
	}


	/**
	 * btc合约计算合约面值
	 */
	public static String getBtcContractEstimated(ContractInfoTable table, String symbol, String quantity) {
		if (table == null) {
			return "";
		}
		if ("BTCUSDT".equals(table.name)) {
			return BigDecimalUtils.multiplyToStr("1", quantity);
		} else {
			String lastPrice = NewMarketWebsocket.getInstance().getContractMarketMap().get(symbol) == null ?
					"0" : NewMarketWebsocket.getInstance().getContractMarketMap().get(symbol).getLastPrice();
			return BigDecimalUtils.multiplyHalfUp(BigDecimalUtils.multiplyToStr(table.multiplier, lastPrice), quantity, 4);
		}
	}


	/**
	 * btc合约计算合约面值
	 */
//	public static String getBtcContractEstimatedOne(ContractInfoTable table, String symbol) {
//		if (table == null) {
//			return "";
//		}
//		if ("BTCUSDT".equals(table.name)) {
//			return "1USDT";
//		} else {
//			String lastPrice = NewMarketWebsocket.getInstance().getContractMarketMap().get(symbol) == null ?
//					"0" : NewMarketWebsocket.getInstance().getContractMarketMap().get(symbol).getLastPrice();
//			return BigDecimalUtils.multiplyHalfUp(table.multiplier, lastPrice, 4) + "BTC";
//		}
//	}


	/**
	 * 开仓通过百分比计算数量
	 *
	 * @param persent
	 * @param avlLong
	 * @param avlShort
	 * @param precision
	 * @return
	 */
	public static String calAvlOpenPersent(String persent, String avlLong, String avlShort, int precision) {
		String s = BigDecimalUtils.multiplyDown(persent, String.valueOf(Math.min(Tools.parseDouble(avlLong, 0), Tools.parseDouble(avlShort, 0))), precision);
		return BigDecimalUtils.isEmptyOrZero(s) ? "" : s;
	}

	/**
	 * 平仓通过百分比计算数量
	 *
	 * @param persent
	 * @param avlLong
	 * @param avlShort
	 * @param precision
	 * @return
	 */
	public static String calAvlClosePersent(String persent, String avlLong, String avlShort, int precision) {
		String s = BigDecimalUtils.multiplyDown(persent, String.valueOf(Math.max(Tools.parseDouble(avlLong, 0), Tools.parseDouble(avlShort, 0))), precision);
		return BigDecimalUtils.isEmptyOrZero(s) ? "" : s;
	}

	/**
	 * 通过数量计算开仓占用多少百分比
	 */
	public static int calPersentOpenFromQutity(String quantity, String avlLong, String avlShort) {
		if (BigDecimalUtils.isEmptyOrZero(quantity)) {
			return 0;
		}
		int percentage = BigDecimalUtils.divideHalfUp(quantity, String.valueOf(Math.min(Tools.parseDouble(avlLong, 0), Tools.parseDouble(avlShort, 0))), 2);
		//小于0是为了 让算出来的数据大于integer的最大值的时候会变成负数
		if (percentage > 100 || percentage < 0) {
			percentage = 100;

		}
		return percentage;
	}

	/**
	 * 通过数量计算平仓占用多少百分比
	 */
	public static int calPersentCloseFromQutity(String quantity, String avlLong, String avlShort) {
		if (BigDecimalUtils.isEmptyOrZero(quantity)) {
			return 0;
		}
		int percentage = BigDecimalUtils.divideHalfUp(quantity, String.valueOf(Math.max(Tools.parseDouble(avlLong, 0), Tools.parseDouble(avlShort, 0))), 2);
		//小于0是为了 让算出来的数据大于integer的最大值的时候会变成负数
		if (percentage > 100 || percentage < 0) {
			percentage = 100;

		}
		return percentage;
	}


}
