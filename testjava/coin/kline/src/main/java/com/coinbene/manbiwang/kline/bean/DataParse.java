package com.coinbene.manbiwang.kline.bean;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.NumberUtils;
import com.coinbene.common.utils.TradeUtils;
import com.github.mikephil.coinbene.components.YAxis;
import com.github.mikephil.coinbene.data.BarEntry;
import com.github.mikephil.coinbene.data.CandleEntry;
import com.github.mikephil.coinbene.data.Entry;
import com.github.mikephil.coinbene.data.LineDataSet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by loro on 2017/2/8.
 */
public class DataParse {
	private List<MinutesBean> datas = new CopyOnWriteArrayList<>();
	private List<String> xVals = new CopyOnWriteArrayList<>();//X轴数据
	private List<BarEntry> barEntries = new CopyOnWriteArrayList<>();//成交量数据
	private List<CandleEntry> candleEntries = new CopyOnWriteArrayList<>();//K线数据

	private List<Entry> ma5DataL = new CopyOnWriteArrayList<>();
	private List<Entry> ma10DataL = new CopyOnWriteArrayList<>();
	private List<Entry> ma20DataL = new CopyOnWriteArrayList<>();

	private List<Entry> ma5DataV = new CopyOnWriteArrayList<>();
	private List<Entry> ma10DataV = new CopyOnWriteArrayList<>();
	private List<Entry> ma20DataV = new CopyOnWriteArrayList<>();

	private List<BarEntry> macdData = new CopyOnWriteArrayList<>();
	private List<Entry> deaData = new CopyOnWriteArrayList<>();
	private List<Entry> difData = new CopyOnWriteArrayList<>();

	private List<Entry> kData = new CopyOnWriteArrayList<>();
	private List<Entry> dData = new CopyOnWriteArrayList<>();
	private List<Entry> jData = new CopyOnWriteArrayList<>();

	private List<BarEntry> barDatasRSI = new CopyOnWriteArrayList<>();
	private List<Entry> rsiData6 = new CopyOnWriteArrayList<>();
	private List<Entry> rsiData12 = new CopyOnWriteArrayList<>();
	private List<Entry> rsiData24 = new CopyOnWriteArrayList<>();

	private List<Entry> bollDataUP = new CopyOnWriteArrayList<>();
	private List<Entry> bollDataMB = new CopyOnWriteArrayList<>();
	private List<Entry> bollDataDN = new CopyOnWriteArrayList<>();

	private List<BarEntry> barDatasEXPMA = new CopyOnWriteArrayList<>();
	private List<Entry> expmaData5 = new CopyOnWriteArrayList<>();
	private List<Entry> expmaData10 = new CopyOnWriteArrayList<>();
	private List<Entry> expmaData20 = new CopyOnWriteArrayList<>();
	private List<Entry> expmaData60 = new CopyOnWriteArrayList<>();


	private float baseValue;
	private float permaxmin;
	private float volmax;
	private String code = "sz002081";
	private SparseArray<String> xValuesLabel = new SparseArray<>();
	private List<Entry> priceEntries;// 分时图 收盘价
	private List<Entry> avePriceEntries;//分时图 均价

	private List<Entry> markLineEntries;//标记线
	private List<Entry> currentPriceLineEntries;//最新价格

	private ArrayList<Integer> mMacdColors, mVolColos;

	//这里 不要清除 标记线的数据，markLineEntries,currentPriceLineEntries
	public void reset() {
		xVals.clear();
		barEntries.clear();
		candleEntries.clear();
		ma5DataL.clear();
		ma10DataL.clear();
		ma20DataV.clear();
		macdData.clear();
		deaData.clear();
		difData.clear();
		kData.clear();
		dData.clear();
		jData.clear();
		barDatasRSI.clear();
		rsiData6.clear();
		rsiData12.clear();
		rsiData24.clear();
		bollDataUP.clear();
		bollDataMB.clear();
		bollDataDN.clear();
		barDatasEXPMA.clear();
		expmaData5.clear();
		expmaData10.clear();
		expmaData20.clear();
		expmaData60.clear();
		xValuesLabel.clear();
		volmax = 0;
	}

	/**
	 * 将jsonobject转换为K线数据
	 */
	public List<KLineBean> parseKLine_new(KlineData klineData) {
		List<KLineBean> kLineBeans = new CopyOnWriteArrayList<>();
		if (klineData != null && klineData.getData() != null) {
			int count = klineData.getData().size();
			List<KlineData.DataBean> dataBeans = klineData.getData();
			SimpleDateFormat sdf = null;
			if (markLineEntries == null) {
				markLineEntries = new CopyOnWriteArrayList<>();
			}
			if (markLineEntries.size() > 0) {
				markLineEntries.clear();
			}

			if (currentPriceLineEntries == null) {
				currentPriceLineEntries = new CopyOnWriteArrayList<>();
			}
			if (currentPriceLineEntries.size() > 0) {
				currentPriceLineEntries.clear();
			}

			if (resolution.equals("1day") || resolution.equals("1week") || resolution.equals("1month") || resolution.equals("D") || resolution.equals("W") || resolution.equals("M")) {
				sdf = new SimpleDateFormat("MM/dd");
			} else {
				sdf = new SimpleDateFormat("HH:mm");
			}
			if (count == 0) {
				return kLineBeans;
			}
			KlineData.DataBean lastKlineBean = dataBeans.get(count - 1);
			float currentPrice = lastKlineBean == null ? 0 : lastKlineBean.getC();//最后一条数据的收盘价
			for (int i = 0; i < count; i++) {
				KLineBean kLineData = new KLineBean();
				try {
					Date date = new Date(Long.valueOf(dataBeans.get(i).getT()) * 1000);
					kLineData.date = sdf.format(date);
				} catch (Exception ex) {
				}
				kLineData.time = Long.valueOf(dataBeans.get(i).getT()) * 1000 + "";
				kLineData.open = dataBeans.get(i).getO();
				kLineData.close = dataBeans.get(i).getC();
				kLineData.high = dataBeans.get(i).getH();
				kLineData.low = dataBeans.get(i).getL();
				kLineData.vol = NumberUtils.stringToLong(dataBeans.get(i).getV());

				//计算涨跌幅
				kLineData.change = BigDecimalUtils.toPercentage(
						BigDecimalUtils.divideToStrHalfUp(String.valueOf(kLineData.close - kLineData.open), String.valueOf(kLineData.open), 4)
				);
				kLineBeans.add(kLineData);

				volmax = Math.max(kLineData.vol, volmax);

				markLineEntries.add(new Entry(i, dataBeans.get(i).getM()));
				//用最后一条数据的收盘价，作为全部节点的数值
				currentPriceLineEntries.add(new Entry(i, currentPrice));
			}
		}
		return kLineBeans;
	}

	/**
	 * 将jsonobject转换为K线数据
	 */
	public List<KLineBean> parseKLineContract(KlineData klineData, int contractType, String symbol) {
		ContractUsdtInfoTable table = null;
		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
		}


		List<KLineBean> kLineBeans = new CopyOnWriteArrayList<>();
		if (klineData != null && klineData.getData() != null) {
			int count = klineData.getData().size();
			List<KlineData.DataBean> dataBeans = klineData.getData();
			SimpleDateFormat sdf = null;
			if (markLineEntries == null) {
				markLineEntries = new CopyOnWriteArrayList<>();
			}
			if (markLineEntries.size() > 0) {
				markLineEntries.clear();
			}

			if (currentPriceLineEntries == null) {
				currentPriceLineEntries = new CopyOnWriteArrayList<>();
			}
			if (currentPriceLineEntries.size() > 0) {
				currentPriceLineEntries.clear();
			}

			if (resolution.equals("1day") || resolution.equals("1week") || resolution.equals("1month") || resolution.equals("D") || resolution.equals("W") || resolution.equals("M")) {
				sdf = new SimpleDateFormat("MM/dd");
			} else {
				sdf = new SimpleDateFormat("HH:mm");
			}
			if (count == 0) {
				return kLineBeans;
			}
			KlineData.DataBean lastKlineBean = dataBeans.get(count - 1);
			float currentPrice = lastKlineBean == null ? 0 : lastKlineBean.getC();//最后一条数据的收盘价
			for (int i = 0; i < count; i++) {
				KLineBean kLineData = new KLineBean();
				try {
					Date date = new Date(Long.valueOf(dataBeans.get(i).getT()) * 1000);
					kLineData.date = sdf.format(date);
				} catch (Exception ex) {
				}
				kLineData.time = Long.valueOf(dataBeans.get(i).getT()) * 1000 + "";
				kLineData.open = dataBeans.get(i).getO();
				kLineData.close = dataBeans.get(i).getC();
				kLineData.high = dataBeans.get(i).getH();
				kLineData.low = dataBeans.get(i).getL();
				if (contractType == Constants.CONTRACT_TYPE_USDT) {
					kLineData.vol = Float.valueOf(TradeUtils.getContractUsdtUnitValue(dataBeans.get(i).getV(), table));
//					DLog.e("kLineData.vol", "kLineData.date---." + "kLineData.vol --->" + kLineData.vol);


				} else {
					kLineData.vol = NumberUtils.stringToLong(dataBeans.get(i).getV());
				}


				//计算涨跌幅
				kLineData.change = BigDecimalUtils.toPercentage(
						BigDecimalUtils.divideToStrHalfUp(String.valueOf(kLineData.close - kLineData.open), String.valueOf(kLineData.open), 4)
				);
				kLineBeans.add(kLineData);

				volmax = Math.max(kLineData.vol, volmax);

				markLineEntries.add(new Entry(i, dataBeans.get(i).getM()));
				//用最后一条数据的收盘价，作为全部节点的数值
				currentPriceLineEntries.add(new Entry(i, currentPrice));
			}
		}
		return kLineBeans;
	}


//    /**
//     * 将jsonobject转换为K线数据
//     */
//    public void parseKLine_future_markline(KlineData klineData) {
//        if (markLineEntries == null) {
//            markLineEntries = new ArrayList<>();
//        }
//        if (markLineEntries.size() > 0) {
//            markLineEntries.clear();
//        }
//        if (klineData != null && klineData.getData() != null) {
//            int count = klineData.getData().size();
//            List<KlineData.DataBean> dataBeans = klineData.getData();
//            for (int i = 0; i < count; i++) {
//                markLineEntries.add(new Entry(i, dataBeans.get(i).getO()));
//            }
//        }
//    }


	//得到成交量
	public void initLineDatas(List<KLineBean> datas) {
		if (null == datas) {
			return;
		}
		xVals = new CopyOnWriteArrayList<>();//X轴数据
		barEntries = new CopyOnWriteArrayList<>();//成交量数据
		candleEntries = new CopyOnWriteArrayList<>();//K线数据
		for (int i = 0; i < datas.size(); i++) {
			xVals.add(datas.get(i).date);
			barEntries.add(new BarEntry(i, datas.get(i).vol));
			candleEntries.add(new CandleEntry(i, datas.get(i).high, datas.get(i).low, datas.get(i).open, datas.get(i).close));
		}
	}

	/**
	 * 初始化K线图均线
	 *
	 * @param datas
	 */
	public void initKLineMA(List<KLineBean> datas) {
		if (null == datas) {
			return;
		}
		ma5DataL = new CopyOnWriteArrayList<>();
		ma10DataL = new CopyOnWriteArrayList<>();
		ma20DataL = new CopyOnWriteArrayList<>();

		KMAEntity kmaEntity5 = new KMAEntity(datas, 5);
		KMAEntity kmaEntity10 = new KMAEntity(datas, 10);
		KMAEntity kmaEntity20 = new KMAEntity(datas, 20);
		for (int i = 0; i < kmaEntity5.getMAs().size(); i++) {
			if (i >= 5) {
				ma5DataL.add(new Entry(i, kmaEntity5.getMAs().get(i)));
			}
			if (i >= 10) {
				ma10DataL.add(new Entry(i, kmaEntity10.getMAs().get(i)));
			}
			if (i >= 20) {
				ma20DataL.add(new Entry(i, kmaEntity20.getMAs().get(i)));
			}

			if (kmaEntity5.getMAs().size() == datas.size()) {
				if (datas.get(i) == null) {
					continue;
				}
				if (kmaEntity5.getMAs() != null) {
					datas.get(i).ma5 = kmaEntity5.getMAs().get(i);
				}
				if (kmaEntity10.getMAs() != null) {
					datas.get(i).ma10 = kmaEntity10.getMAs().get(i);
				}
				if (kmaEntity20.getMAs() != null) {
					datas.get(i).ma20 = kmaEntity20.getMAs().get(i);
				}
			}
		}

	}

	/**
	 * 初始化成交量均线
	 *
	 * @param datas
	 */
	public void initVolumeMA(List<KLineBean> datas) {
		if (null == datas) {
			return;
		}
		ma5DataV = new CopyOnWriteArrayList<>();
		ma10DataV = new CopyOnWriteArrayList<>();
		ma20DataV = new CopyOnWriteArrayList<>();

		VMAEntity vmaEntity5 = new VMAEntity(datas, 5);
		VMAEntity vmaEntity10 = new VMAEntity(datas, 10);
		VMAEntity vmaEntity20 = new VMAEntity(datas, 20);
//        VMAEntity vmaEntity30 = new VMAEntity(datas, 30);
		for (int i = 0; i < vmaEntity5.getMAs().size(); i++) {
			ma5DataV.add(new Entry(i, vmaEntity5.getMAs().get(i)));
			if (i < vmaEntity10.getMAs().size()) {
				ma10DataV.add(new Entry(i, vmaEntity10.getMAs().get(i)));
			}
			if (i < vmaEntity20.getMAs().size()) {
				ma20DataV.add(new Entry(i, vmaEntity20.getMAs().get(i)));
			}
//            ma30DataV.add(new Entry(i, vmaEntity30.getMAs().get(i)));
		}

	}

	public void initFenshiLineChart(List<KLineBean> datas) {
		if (datas == null) {
			return;
		}
		if (priceEntries != null) {
			priceEntries.clear();
		}
		if (avePriceEntries != null) {
			avePriceEntries.clear();
		}
		if (priceEntries == null)
			priceEntries = new ArrayList<>();
		if (avePriceEntries == null)
			avePriceEntries = new ArrayList<>();
//        AveEntity aveEntity = new AveEntity(datas);
		for (int i = 0; i < datas.size(); i++) {
			priceEntries.add(new Entry(i, datas.get(i).close));
//            avePriceEntries.add(new Entry(i, datas.get(i).ave));
		}
	}

	/**
	 * 初始化MACD
	 *
	 * @param datas
	 */
	public void initMACD(List<KLineBean> datas) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		MACDEntity macdEntity = new MACDEntity(datas);

		macdData = new CopyOnWriteArrayList<>();
		deaData = new CopyOnWriteArrayList<>();
		difData = new CopyOnWriteArrayList<>();
		for (int i = 0; i < macdEntity.getMACD().size(); i++) {
			macdData.add(new BarEntry(i, macdEntity.getMACD().get(i)));
			deaData.add(new Entry(i, macdEntity.getDEA().get(i)));
			difData.add(new Entry(i, macdEntity.getDIF().get(i)));
		}
	}

	/**
	 * 初始化KDJ
	 *
	 * @param datas
	 */
	public void initKDJ(List<KLineBean> datas) {
		KDJEntity kdjEntity = new KDJEntity(datas, 9);

		kData = new CopyOnWriteArrayList<>();
		dData = new CopyOnWriteArrayList<>();
		jData = new CopyOnWriteArrayList<>();
		for (int i = 0; i < kdjEntity.getD().size(); i++) {
			kData.add(new Entry(i, kdjEntity.getK().get(i)));
			dData.add(new Entry(i, kdjEntity.getD().get(i)));
			jData.add(new Entry(i, kdjEntity.getJ().get(i)));
		}
	}

	/**
	 * 初始化RSI
	 *
	 * @param datas
	 */
	public void initRSI(List<KLineBean> datas) {

		RSIEntity rsiEntity6 = new RSIEntity(datas, 6);
		RSIEntity rsiEntity12 = new RSIEntity(datas, 12);
		RSIEntity rsiEntity24 = new RSIEntity(datas, 24);

		barDatasRSI = new ArrayList<>();
		rsiData6 = new ArrayList<>();
		rsiData12 = new ArrayList<>();
		rsiData24 = new ArrayList<>();
		for (int i = 0; i < rsiEntity6.getRSIs().size(); i++) {
			barDatasRSI.add(new BarEntry(i, 0));
			rsiData6.add(new Entry(i, rsiEntity6.getRSIs().get(i)));
			if (i < rsiEntity12.getRSIs().size()) {
				rsiData12.add(new Entry(i, rsiEntity12.getRSIs().get(i)));
			} else {
				rsiData12.add(new Entry(i, 0));
			}
			if (i < rsiEntity24.getRSIs().size()) {
				rsiData24.add(new Entry(i, rsiEntity24.getRSIs().get(i)));
			} else {
				rsiData24.add(new Entry(i, 0));
			}
		}
	}

	public static final int DEFAULT_INDEX_BOLL = 20;

	/**
	 * 初始化BOLL
	 *
	 * @param datas
	 */
	public void initBOLL(List<KLineBean> datas) {
		BOLLEntity bollEntity = new BOLLEntity(datas, DEFAULT_INDEX_BOLL);

		bollDataUP = new CopyOnWriteArrayList<>();
		bollDataMB = new CopyOnWriteArrayList<>();
		bollDataDN = new CopyOnWriteArrayList<>();
		DecimalFormat decimalFormatter = new DecimalFormat(Constants.accurasyStr);
		decimalFormatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		for (int i = 0; i < bollEntity.getUPs().size(); i++) {
			if (i >= (DEFAULT_INDEX_BOLL - 1)) {//boll线再绘制的时候，前19个，如果给了NAN，也同样出现问题；
				bollDataUP.add(new Entry(i, Float.valueOf(decimalFormatter.format(bollEntity.getUPs().get(i)))));
				bollDataMB.add(new Entry(i, Float.valueOf(decimalFormatter.format(bollEntity.getMBs().get(i)))));
				bollDataDN.add(new Entry(i, Float.valueOf(decimalFormatter.format(bollEntity.getDNs().get(i)))));
			}
		}
	}

	@NonNull
	private LineDataSet setMaLine(int ma, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + ma);
		if (ma == 5) {
			lineDataSetMa.setHighlightEnabled(true);
			lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
			lineDataSetMa.setHighLightColor(res_redColor);
		} else {/*此处必须得写*/
			lineDataSetMa.setHighlightEnabled(false);
		}
		lineDataSetMa.setDrawValues(false);
		if (ma == 5) {
			lineDataSetMa.setColor(res_redColor);
		} else if (ma == 10) {
			lineDataSetMa.setColor(res_assistColor_22Color);
		} else if (ma == 20) {
			lineDataSetMa.setColor(res_blueColor);
		} else {
			lineDataSetMa.setColor(ma30Color);
		}
		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

		lineDataSetMa.setHighlightEnabled(false);
		return lineDataSetMa;
	}

	@NonNull
	private LineDataSet setKDJMaLine(int type, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
		lineDataSetMa.setHighlightEnabled(false);
		lineDataSetMa.setDrawValues(false);

		//DEA
		if (type == 0) {
			lineDataSetMa.setColor(res_redColor);
		} else if (type == 1) {
			lineDataSetMa.setColor(res_assistColor_22Color);
		} else if (type == 2) {
			lineDataSetMa.setColor(res_blueColor);
		} else {
			lineDataSetMa.setColor(ma30Color);
		}

		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

		return lineDataSetMa;
	}

	private int res_redColor, res_assistColor_22Color, res_blueColor, ma30Color;

	public void setres_redColor(int res_redColor) {
		this.res_redColor = res_redColor;
	}

	public void setres_assistColor_22Color(int res_assistColor_22Color) {
		this.res_assistColor_22Color = res_assistColor_22Color;
	}

	public void setres_blueColor(int res_blueColor) {
		this.res_blueColor = res_blueColor;
	}

	public void setMa30Color(int ma30Color) {
		this.ma30Color = ma30Color;
	}

	/**
	 * 得到Y轴最小值
	 *
	 * @return
	 */
	public float getMin() {
		return baseValue - permaxmin;
	}

	/**
	 * 得到Y轴最大值
	 *
	 * @return
	 */
	public float getMax() {
		return baseValue + permaxmin;
	}

	/**
	 * 得到百分百最大值
	 *
	 * @return
	 */
	public float getPercentMax() {
		return permaxmin / baseValue;
	}

	/**
	 * 得到百分比最小值
	 *
	 * @return
	 */
	public float getPercentMin() {
		return -getPercentMax();
	}

	/**
	 * 得到成交量最大值
	 *
	 * @return
	 */
	public float getVolmax() {
		return volmax;
	}


	/**
	 * 得到分时图数据
	 *
	 * @return
	 */
	public List<MinutesBean> getDatas() {
		return datas;
	}

	/**
	 * 得到X轴数据
	 *
	 * @return
	 */
	public List<String> getXVals() {
		return xVals;
	}

	/**
	 * 得到K线数据
	 *
	 * @return
	 */
	public List<CandleEntry> getCandleEntries() {
		return candleEntries;
	}

	/**
	 * 得到成交量数据
	 *
	 * @return
	 */
	public List<BarEntry> getBarEntries() {
		return barEntries;
	}


	/**
	 * 得到K线图5日均线
	 *
	 * @return
	 */
	public List<Entry> getMa5DataL() {
		return ma5DataL;
	}


	/**
	 * 得到K线图10日均线
	 *
	 * @return
	 */
	public List<Entry> getMa10DataL() {
		return ma10DataL;
	}

	/**
	 * 得到K线图20日均线
	 *
	 * @return
	 */
	public List<Entry> getMa20DataL() {
		return ma20DataL;
	}

	/**
	 * 得到MACD bar
	 *
	 * @return
	 */
	public List<BarEntry> getMacdData() {
		return macdData;
	}

	/**
	 * 得到MACD dea
	 *
	 * @return
	 */
	public List<Entry> getDeaData() {
		return deaData;
	}

	/**
	 * 得到MACD dif
	 *
	 * @return
	 */
	public List<Entry> getDifData() {
		return difData;
	}

	/**
	 * 得到DKJ k
	 *
	 * @return
	 */
	public List<Entry> getkData() {
		return kData;
	}

	/**
	 * 得到KDJ d
	 *
	 * @return
	 */
	public List<Entry> getdData() {
		return dData;
	}

	/**
	 * 得到KDJ j
	 *
	 * @return
	 */
	public List<Entry> getjData() {
		return jData;
	}

	/**
	 * 得到RSI bar
	 *
	 * @return
	 */
	public List<BarEntry> getBarDatasRSI() {
		return barDatasRSI;
	}

	/**
	 * 得到RSI 6
	 *
	 * @return
	 */
	public List<Entry> getRsiData6() {
		return rsiData6;
	}

	/**
	 * 得到RSI 12
	 *
	 * @return
	 */
	public List<Entry> getRsiData12() {
		return rsiData12;
	}

	/**
	 * 得到RSI 24
	 *
	 * @return
	 */
	public List<Entry> getRsiData24() {
		return rsiData24;
	}

	public List<Entry> getBollDataUP() {
		return bollDataUP;
	}

	public List<Entry> getBollDataMB() {
		return bollDataMB;
	}

	public List<Entry> getBollDataDN() {
		return bollDataDN;
	}

	public List<Entry> getMa5VolData() {
		return ma5DataV;
	}

	public List<Entry> getMa10VolData() {
		return ma10DataV;
	}

	public List<Entry> getMa20VolData() {
		return ma20DataV;
	}


	private String resolution;

	public void setPeriodType(String resolution) {
		this.resolution = resolution;
	}

	public void setPriceEntries(List<Entry> priceEntries) {
		this.priceEntries = priceEntries;
	}

	public List<Entry> getPriceEntries() {
		return priceEntries;
	}

	public void setAvePriceEntries(List<Entry> avePriceEntries) {
		this.avePriceEntries = avePriceEntries;
	}

	public List<Entry> getAvePriceEntries() {
		return this.avePriceEntries;
	}

	public void setMarkLineEntries(List<Entry> markLineEntries) {
		this.markLineEntries = markLineEntries;
	}

	public List<Entry> getMarkLineEntries() {
		return this.markLineEntries;
	}

	public List<Entry> getCurrentPriceLineEntries() {
		return currentPriceLineEntries;
	}

	public void setCurrentPriceLineEntries(List<Entry> currentPriceLineEntries) {
		this.currentPriceLineEntries = currentPriceLineEntries;
	}

	public ArrayList<Integer> getMacdColors() {
		return mMacdColors;
	}

	public void setMacdColors(ArrayList<Integer> mMacdColors) {
		this.mMacdColors = mMacdColors;
	}

	public ArrayList<Integer> getVolColos() {
		return mVolColos;
	}

	public void setVolColos(ArrayList<Integer> mVolColos) {
		this.mVolColos = mVolColos;
	}
}
