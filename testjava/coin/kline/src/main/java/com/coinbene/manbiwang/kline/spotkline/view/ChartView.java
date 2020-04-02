package com.coinbene.manbiwang.kline.spotkline.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.NumberUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.coinbene.manbiwang.kline.contractkline.ContractKlineLandActivity;
import com.coinbene.manbiwang.kline.listener.ChartInfoViewHandler;
import com.coinbene.manbiwang.kline.listener.CoupleChartGestureListener;
import com.coinbene.manbiwang.kline.listener.SelectTimeListener;
import com.coinbene.manbiwang.kline.spotkline.SpotKlineLandActivity;
import com.coinbene.manbiwang.kline.spotkline.listener.ChartViewListener;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.widget.DKlineCombinedChart;
import com.coinbene.manbiwang.kline.widget.MyBottomMarkerView;
import com.coinbene.manbiwang.kline.widget.MyHMarkerView;
import com.coinbene.manbiwang.kline.widget.MyLeftMarkerView;
import com.github.mikephil.coinbene.charts.Chart;
import com.github.mikephil.coinbene.charts.CombinedChart;
import com.github.mikephil.coinbene.components.Description;
import com.github.mikephil.coinbene.components.Legend;
import com.github.mikephil.coinbene.components.XAxis;
import com.github.mikephil.coinbene.components.YAxis;
import com.github.mikephil.coinbene.data.BarData;
import com.github.mikephil.coinbene.data.BarDataSet;
import com.github.mikephil.coinbene.data.BarEntry;
import com.github.mikephil.coinbene.data.CandleData;
import com.github.mikephil.coinbene.data.CandleDataSet;
import com.github.mikephil.coinbene.data.CombinedData;
import com.github.mikephil.coinbene.data.Entry;
import com.github.mikephil.coinbene.data.LineData;
import com.github.mikephil.coinbene.data.LineDataSet;
import com.github.mikephil.coinbene.formatter.IValueFormatter;
import com.github.mikephil.coinbene.highlight.ChartHighlighter;
import com.github.mikephil.coinbene.highlight.Highlight;
import com.github.mikephil.coinbene.interfaces.datasets.ILineDataSet;
import com.github.mikephil.coinbene.listener.OnChartValueSelectedListener;
import com.github.mikephil.coinbene.utils.ViewPortHandler;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-11-22
 */
public class ChartView extends LinearLayout {

	@BindView(R2.id.MA_Text)
	TextView mMAText;
	@BindView(R2.id.kline_chart_k)
	DKlineCombinedChart mKlineChartK;
	@BindView(R2.id.kline_chart_volume)
	CombinedChart mKlineChartVolume;
	@BindView(R2.id.vol_Text)
	TextView mVolText;
	@BindView(R2.id.kline_chart_charts)
	CombinedChart mKlineChartCharts;
	@BindView(R2.id.StockIndex_Text)
	TextView mStockIndexText;

	private Context mContext;

	private int MAX_COUNT = 150;
	private int MIN_COUNT = 25;
	private int NORMAL_COUNT = 50;
	private int ONE_SCREENT_COUNT = 65;

	private int MAX_COUNT_LAND = 250;
	private int MIN_COUNT_LAND = 25;
	private int NORMAL_COUNT_LAND = 80;
	private int ONE_SCREENT_COUNT_LAND = 95;


	/**
	 * /每个柱状图的间距,
	 */

	private static final float K_BAR_WITH = 0.6f;
	/**
	 * /最小展示条数，不起作用
	 */

	private static final int K_MAX_SHOW_COUNT = 40;

	private boolean isFenshi = false;

	private int type;

	private int masterType;
	private String subZhibiao;

	private DataParse mData;
	List<KLineBean> kLineDatas;

	private ChartViewListener mChartViewListener;
	/**
	 * 涨跌颜色
	 */
	private boolean isRedRise;
	private int mRiseColor, mDropColor;

	private boolean isFirst;
	private boolean isLand = true;
	private boolean isContract = false;


	private String tradePairName;
	private int contractType;
	private ContractUsdtInfoTable table;

	public ChartView(Context context) {
		super(context);
		initView(context, null);
	}

	public ChartView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, attrs);
	}

	private void initView(Context context, @Nullable AttributeSet attrs) {
		this.mContext = context;

		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.chart_view);
			boolean isLandScape = typedArray.getBoolean(R.styleable.chart_view_isLandscape, false);
			if (!isLandScape) {
				//竖屏
				isLand = false;
				LayoutInflater.from(context).inflate(R.layout.kline_chart_view, this, true);
			} else {
				//横屏
				isLand = true;
				LayoutInflater.from(context).inflate(R.layout.kline_chart_view_land, this, true);
			}
			typedArray.recycle();
		} else {
			LayoutInflater.from(context).inflate(R.layout.kline_chart_view_land, this, true);
		}

		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}
		initData();

	}


	public void initData(){

		masterType = SpUtil.get(mContext, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
		subZhibiao = SpUtil.get(mContext, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.MACD);

		isRedRise = SwitchUtils.isRedRise();
		if (isRedRise) {
			mRiseColor = mContext.getResources().getColor(R.color.res_red);
			mDropColor = mContext.getResources().getColor(R.color.res_green);
		} else {
			mRiseColor = mContext.getResources().getColor(R.color.res_green);
			mDropColor = mContext.getResources().getColor(R.color.res_red);
		}

		initChartKline(mKlineChartK);
		initChartVolume(mKlineChartVolume);
		initChartCharts(mKlineChartCharts);

		setChartListener(mKlineChartK, mKlineChartVolume, mKlineChartCharts);

		View mChangOrienImg = findViewById(R.id.chang_orien_img);
		if (mChangOrienImg != null) {
			mChangOrienImg.setOnClickListener(v -> {
				if (isContract) {
					ContractKlineLandActivity.startMe(mContext, tradePairName);
				} else {
					SpotKlineLandActivity.startMe(mContext, tradePairName);
				}
			});
		}
	}


	public void setType(int type) {
		this.type = type;
	}

	public void setTradePairName(String tradePairName) {
		this.tradePairName = tradePairName;
	}

	public void setIsContract(boolean isContract) {
		this.isContract = isContract;
	}

	public void setSubZhibiao(String subZhibiao) {
		if (!this.subZhibiao.equals(subZhibiao)) {
			this.subZhibiao = subZhibiao;
			setSelectedZhibiao(subZhibiao);
		}
	}

	public void setMasterType(int masterType) {
		if (this.masterType != masterType) {
			this.masterType = masterType;
			setSelectedMasterType(masterType);
		}
	}

	public void setFenshi(boolean fenshi) {
		this.isFenshi = fenshi;
	}


	public void setChartViewListener(ChartViewListener mChartViewListener) {
		this.mChartViewListener = mChartViewListener;
	}

	/**
	 * 初始化上面的chart公共属性
	 */
	private void initChartKline(DKlineCombinedChart mChartKline) {
		mChartKline.setScaleEnabled(true);//启用图表缩放事件
//        mChartKline.setDrawBorders(true);//是否绘制边线
//        mChartKline.setBorderWidth(1);//边线宽度，单位dp
		mChartKline.setDragEnabled(true);//启用图表拖拽事件
		mChartKline.setScaleXEnabled(true);
		mChartKline.setScaleYEnabled(false);//启用Y轴上的缩放mChartKline
//        mChartKline.setBorderColor(getResources().getColor(R.color.res_border_color));//边线颜色
//        mChartKline.setDescription("");//右下角对图表的描述信息
		Description description = new Description();
		description.setText("");
		mChartKline.setDescription(description);
		mChartKline.setMinOffset(0f);
		mChartKline.setMaxVisibleValueCount(K_MAX_SHOW_COUNT);
		mChartKline.setExtraOffsets(0f, QMUIDisplayHelper.dp2px(mContext, 2), 5f, 5f);

		mChartKline.setHardwareAccelerationEnabled(true);
		mChartKline.setHighlighter(new ChartHighlighter() {
			@Override
			protected float getHighlightPos(Highlight h) {
				return super.getHighlightPos(h);
			}

		});

		Legend lineChartLegend = mChartKline.getLegend();
		lineChartLegend.setEnabled(false);//是否绘制 Legend 图例
		lineChartLegend.setForm(Legend.LegendForm.CIRCLE);

		//蜡烛图X轴
		XAxis xAxisKline = mChartKline.getXAxis();
		xAxisKline.setDrawLabels(true); //是否显示X坐标轴上的刻度，默认是true
		xAxisKline.setDrawGridLines(false);//是否显示X坐标轴上的刻度竖线，默认是true
		xAxisKline.setDrawAxisLine(true); //是否绘制坐标轴的线，即含有坐标的那条线，默认是true
		xAxisKline.setAxisLineColor(getResources().getColor(R.color.color_kline_cursor));
		xAxisKline.setAxisLineWidth(1);

//        xAxisKline.enableGridDashedLine(10f, 10f, 0f);//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
		xAxisKline.setTextColor(getResources().getColor(R.color.kline_text_color));//设置字的颜色
		xAxisKline.setPosition(XAxis.XAxisPosition.BOTTOM);//设置值显示在什么位置
		xAxisKline.setAvoidFirstLastClipping(false);//设置首尾的值是否自动调整，避免被遮挡
		xAxisKline.setCenterAxisLabels(false);
		if (isLand) {
			xAxisKline.setLabelCount(8);
		} else {
			xAxisKline.setLabelCount(4);
		}
		xAxisKline.setValueFormatter((value, axis) -> {
			if (kLineDatas == null || kLineDatas.size() == 0) {
				return "";
			} else {
				int index = (int) value;
				if (index < 0 || index >= kLineDatas.size()) {
					return "";
				} else if (kLineDatas.size() <= 5) {
//                    Log.e("mxd", "kLineDatas.size()<5"+"index="+index);
					return "";
				} else {
					if (kLineDatas.get(index) == null) {
						return "";
					}
					if (TextUtils.isEmpty(kLineDatas.get(index).date)) {
						return "";
					}
					return kLineDatas.get(index).date;
				}
			}
		});
		//蜡烛图左Y轴
		YAxis axisLeftKline = mChartKline.getAxisLeft();
		axisLeftKline.setDrawGridLines(false);
		axisLeftKline.setDrawAxisLine(false);
		axisLeftKline.setDrawZeroLine(false);
		axisLeftKline.setDrawLabels(false);
//        axisLeftKline.enableGridDashedLine(10f, 10f, 0f);
//        axisLeftKline.setTextColor(getResources().getColor(R.color.kline_text_color));
//        axisLeftKline.setGridColor(getResources().getColor(R.color.kline_text_color));
//        axisLeftKline.setDisplayList(YAxis.YAxisLabelPosition.INSIDE_CHART);
//        axisLeftKline.setLabelCount(4, true); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
		axisLeftKline.setDrawTopYLabelEntry(false);
		axisLeftKline.setDrawZeroLine(false);
//        axisLeftKline.setDrawTopBottomGridLine(false);

		//蜡烛图右Y轴
		YAxis axisRightKline = mChartKline.getAxisRight();
		axisRightKline.setDrawLabels(false);
		axisRightKline.setDrawGridLines(false);
		axisRightKline.setDrawAxisLine(false);
//        axisRightKline.setDisplayList(YAxis.YAxisLabelPosition.INSIDE_CHART);
		mChartKline.setAutoScaleMinMaxEnabled(true);

		mChartKline.setDragDecelerationEnabled(true);
		mChartKline.setDragDecelerationFrictionCoef(0.8f);
	}

	/**
	 * 初始化下面的chart公共属性
	 */
	private void initChartVolume(CombinedChart mChartVolume) {
//        mChartVolume.setDrawBorders(true);  //边框是否显示
//        mChartVolume.setBorderWidth(1);//边框的宽度，float类型，dp单位
//        mChartVolume.setBorderColor(getResources().getColor(R.color.res_border_color));//边框颜色
		Description description = new Description();
		description.setText("");
		mChartVolume.setDescription(description); //图表默认右下方的描述，参数是String对象
		mChartVolume.setDragEnabled(true);// 是否可以拖拽
		mChartVolume.setScaleXEnabled(true);
		mChartVolume.setScaleYEnabled(false); //是否可以缩放 仅y轴
		mChartVolume.setMinOffset(0f);
		mChartVolume.setMaxVisibleValueCount(K_MAX_SHOW_COUNT);
		mChartVolume.setExtraOffsets(0f, 0f, 5f, 0f);
// 缩放x轴时自动调整y轴
		mChartVolume.setAutoScaleMinMaxEnabled(true);
		mChartVolume.setHardwareAccelerationEnabled(true);

		// 关闭双击缩放事件
		mChartVolume.setDoubleTapToZoomEnabled(false);

		Legend combinedchartLegend = mChartVolume.getLegend(); // 设置比例图标示，就是那个一组y的value的
		combinedchartLegend.setEnabled(false);//是否绘制比例图

		//bar x y轴
		XAxis xAxisVolume = mChartVolume.getXAxis();
		xAxisVolume.setEnabled(false);
//        xAxisVolume.setDrawGridLines(true);

		YAxis axisLeftVolume = mChartVolume.getAxisLeft();
//        axisLeftVolume.setShowOnlyMinMax(true);//设置Y轴坐标最小为多少
		axisLeftVolume.setDrawGridLines(false);
		axisLeftVolume.setDrawAxisLine(false);
		axisLeftVolume.setDrawLabels(false);

		YAxis axisRightVolume = mChartVolume.getAxisRight();
		axisRightVolume.setDrawLabels(false);
		axisRightVolume.setDrawGridLines(false);
		axisRightVolume.setDrawAxisLine(false);

		mChartVolume.setDragDecelerationEnabled(true);
		mChartVolume.setDragDecelerationFrictionCoef(0.8f);
	}

	/**
	 * 初始化下面的chart公共属性
	 */
	private void initChartCharts(CombinedChart mChartCharts) {
		mChartCharts.setScaleEnabled(true);//启用图表缩放事件
//        mChartCharts.setDrawBorders(true);//是否绘制边线
//        mChartCharts.setBorderWidth(1);//边线宽度，单位dp
		mChartCharts.setDragEnabled(true);//启用图表拖拽事件
		mChartCharts.setScaleXEnabled(true);
		mChartCharts.setScaleYEnabled(false);//启用Y轴上的缩放
//        mChartCharts.setBorderColor(getResources().getColor(R.color.res_border_color));//边线颜色
		Description description = new Description();
		description.setText("");
		mChartCharts.setDescription(description);
		mChartCharts.setHardwareAccelerationEnabled(true);

		mChartCharts.setMinOffset(0f);
		mChartCharts.setMaxVisibleValueCount(K_MAX_SHOW_COUNT);
		mChartCharts.setExtraOffsets(0f, 0f, 5f, 0f);
// 关闭双击缩放事件
		mChartCharts.setDoubleTapToZoomEnabled(false);

		Legend lineChartLegend = mChartCharts.getLegend();
		lineChartLegend.setEnabled(false);//是否绘制 Legend 图例

		//bar x y轴
		XAxis xAxisCharts = mChartCharts.getXAxis();
		xAxisCharts.setEnabled(false);

		YAxis axisLeftCharts = mChartCharts.getAxisLeft();
		axisLeftCharts.setDrawGridLines(false);
		axisLeftCharts.setDrawAxisLine(false);
		axisLeftCharts.setDrawLabels(false);
//        axisLeftCharts.enableGridDashedLine(10f, 10f, 0f);
//        axisLeftCharts.setTextColor(getResources().getColor(R.color.kline_text_color));
//        axisLeftCharts.setDisplayList(YAxis.YAxisLabelPosition.INSIDE_CHART);
		axisLeftCharts.setLabelCount(0);//setLabelCount(3, false); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
//        axisLeftCharts.setSpaceTop(40);

		YAxis axisRightCharts = mChartCharts.getAxisRight();
		axisRightCharts.setDrawLabels(false);
		axisRightCharts.setDrawGridLines(false);
		axisRightCharts.setDrawAxisLine(false);
		mChartCharts.setAutoScaleMinMaxEnabled(true);

		mChartCharts.setDragDecelerationEnabled(true);
		mChartCharts.setDragDecelerationFrictionCoef(0.8f);
	}

	private void setChartListener(DKlineCombinedChart mChartKline, CombinedChart mChartVolume, CombinedChart mChartCharts) {
		// 将K线控的滑动事件传递给交易量控件
		mChartKline.setOnChartGestureListener(new CoupleChartGestureListener(mChartKline, new Chart[]{mChartVolume, mChartCharts}));
		// 将交易量控件的滑动事件传递给K线控件
		mChartVolume.setOnChartGestureListener(new CoupleChartGestureListener(mChartVolume, new Chart[]{mChartKline, mChartCharts}));

		mChartCharts.setOnChartGestureListener(new CoupleChartGestureListener(mChartCharts, new Chart[]{mChartKline, mChartVolume}));

		mChartKline.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, Highlight h) {
//                Highlight highlight = new Highlight(h.getX(), h.getY(), h.getDataSetIndex());
//                mChartVolume.highlightValues(new Highlight[]{highlight});
//                mChartCharts.highlightValues(new Highlight[]{highlight});
//

				float touchy = h.getYPx() - mChartKline.getHeight();
				Highlight h1 = mChartVolume.getHighlightByTouchPoint(h.getXPx(), touchy);
				if (h1 != null) {
					mChartVolume.highlightValues(new Highlight[]{h1});
				}
				float touchy_charts = h.getYPx() - mChartKline.getHeight() - mChartVolume.getHeight();
				Highlight h2 = mChartCharts.getHighlightByTouchPoint(h.getXPx(), touchy_charts);
				if (h2 != null) {
					mChartCharts.highlightValues(new Highlight[]{h2});
				}

				highLightPress(e.getX());
			}

			@Override
			public void onNothingSelected() {
				mChartKline.highlightValues(null);
				mChartVolume.highlightValues(null);
				mChartCharts.highlightValues(null);

				hideHighValueSelectedValue();
			}
		});

		mChartVolume.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, Highlight h) {
				float touchy = h.getYPx() + mChartKline.getHeight();
				Highlight h1 = mChartKline.getHighlightByTouchPoint(h.getXPx(), touchy);
				if (h1 != null) {
					mChartKline.highlightValues(new Highlight[]{h1});
				}

				float touchy_charts = h.getYPx() - mChartVolume.getHeight();
				Highlight h2 = mChartCharts.getHighlightByTouchPoint(h.getXPx(), touchy_charts);
				if (h2 != null) {
					mChartCharts.highlightValues(new Highlight[]{h2});
				}

				highLightPress(e.getX());
			}

			@Override
			public void onNothingSelected() {
				mChartKline.highlightValues(null);
				mChartVolume.highlightValues(null);
				mChartCharts.highlightValues(null);

				hideHighValueSelectedValue();
			}
		});

		mChartCharts.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, Highlight h) {
				float touchy = h.getYPx() + mChartVolume.getHeight() + mChartKline.getHeight();
				Highlight h1 = mChartKline.getHighlightByTouchPoint(h.getXPx(), touchy);
				if (h1 != null) {
					mChartKline.highlightValues(new Highlight[]{h1});
				}
				float touchy2 = h.getYPx() + mChartVolume.getHeight();
				Highlight h2 = mChartVolume.getHighlightByTouchPoint(h.getXPx(), touchy2);
				if (h2 != null) {
					mChartVolume.highlightValues(new Highlight[]{h2});
				}

				highLightPress(e.getX());
			}

			@Override
			public void onNothingSelected() {
				mChartKline.highlightValues(null);
				mChartVolume.highlightValues(null);
				mChartCharts.highlightValues(null);

				hideHighValueSelectedValue();
			}
		});
		mChartKline.setOnTouchListener(new ChartInfoViewHandler(mChartKline));
		mChartVolume.setOnTouchListener(new ChartInfoViewHandler(mChartVolume));
		mChartCharts.setOnTouchListener(new ChartInfoViewHandler(mChartCharts));
	}

	private void hideHighValueSelectedValue() {
		if (mChartViewListener != null) {
			mChartViewListener.hideHighValueSelectedValue();
		}
	}

	private void highLightPress(float x) {
		if (mChartViewListener != null) {
			mChartViewListener.highLightPress((int) x);
		}
		updateText((int) x);
	}

	public void setData(DataParse dataParse, List<KLineBean> kLineDatas) {
		this.mData = dataParse;
		this.kLineDatas = kLineDatas;

		isFirst = true;

		mKlineChartK.setMarkerData(kLineDatas);
		setMarkerViewButtom(mKlineChartK, kLineDatas);

		if (isFenshi) {
			mMAText.setText("");
			mMAText.setVisibility(View.INVISIBLE);
			setFenshiLineChart(kLineDatas);
		} else {
			if (masterType == 0) {
				mMAText.setText("");
				mMAText.setVisibility(View.INVISIBLE);
				setKLineByChart(kLineDatas);
			} else if (masterType == 1) {
				mMAText.setVisibility(View.VISIBLE);
				setKMALineByChart(kLineDatas);
			} else if (masterType == 2) {
				mMAText.setVisibility(View.VISIBLE);
				setKBollLineByChart(kLineDatas);
			}
		}

		setVolumeByChart(mKlineChartVolume, kLineDatas);

		String stockStr = "";
		switch (subZhibiao) {
			case ZhibiaoListener.MACD:
				setMACDByChart(mKlineChartCharts, kLineDatas);
				stockStr = getResources().getString(R.string.macd_normal);
				break;
			case ZhibiaoListener.KDJ:
				setKDJByChart(mKlineChartCharts, kLineDatas);
				stockStr = getResources().getString(R.string.kdj_normal);
				break;
			case ZhibiaoListener.RSI:
				setRSIByChart(mKlineChartCharts, kLineDatas);
				stockStr = getResources().getString(R.string.rsi_normal);
				break;
		}
		mStockIndexText.setText(stockStr);

		updateText(kLineDatas.size() - 1);

		isFirst = false;

	}

	private void setMarkerViewButtom(DKlineCombinedChart combinedChart, List<KLineBean> kLineDatas) {
		MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(mContext, R.layout.mymarkerview);
		MyBottomMarkerView bottomMarkerView = new MyBottomMarkerView(mContext, R.layout.bottom_markerview);
		MyHMarkerView hightMarkerView = new MyHMarkerView(mContext, R.layout.mymarkerview_line);
		combinedChart.setMarker(leftMarkerView, bottomMarkerView, hightMarkerView, kLineDatas);
	}

	private void setFenshiLineChart(List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}

		mData.initFenshiLineChart(kLineDatas);

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (mKlineChartK.getBarData() != null) {
			mKlineChartK.getBarData().clearValues();
		}
		if (mKlineChartK.getLineData() != null) {
			mKlineChartK.getLineData().clearValues();
		}
		if (mKlineChartK.getCandleData() != null) {
			mKlineChartK.getCandleData().clearValues();
		}

		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setLine(mData.getPriceEntries()));
		LineData lineData = new LineData(sets);

		CombinedData combinedData = new CombinedData();
		combinedData.setData(lineData);

		mKlineChartK.setData(combinedData);

		if (isFirst) {
			mKlineChartK.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			mKlineChartK.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));

			setPostScaleHandler(mKlineChartK);
			if (mData.getCandleEntries() != null && mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				mKlineChartK.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				mKlineChartK.moveViewToX(0);
			}
		}
		mKlineChartK.notifyDataSetChanged();
		mKlineChartK.invalidate();
	}

	private void setPostScaleHandler(CombinedChart combinedChart) {
		if (kLineDatas == null || kLineDatas.size() == 0) {
			return;
		}
		combinedChart.resetZoom();
		if (isLand) {
			combinedChart.setVisibleXRange((float) MAX_COUNT_LAND * 2, (float) MIN_COUNT_LAND);
			float currentScale = calMaxScale(kLineDatas.size());
			float lastScale = combinedChart.getViewPortHandler().getScaleX();
			float toScale = currentScale / lastScale;

			combinedChart.zoom(toScale, 0f, 0f, 0f);
		} else {
			combinedChart.setVisibleXRange((float) MAX_COUNT * 2, (float) MIN_COUNT);
			float currentScale = calMaxScale(kLineDatas.size());//MAX_COUNT * 2f / INIT_COUNT;
			float lastScale = combinedChart.getViewPortHandler().getScaleX();
			float toScale = currentScale / lastScale;

			combinedChart.zoom(toScale, 0f, 0f, 0f);
		}
	}

	public float calMaxScale(float count) {
		float xScale = 1;
		if (count >= 1400) {
			xScale = isLand ? 16f : 18f;
		} else if (count >= 1200) {
			xScale = isLand ? 14f : 16f;
		} else if (count >= 1000) {
			xScale = isLand ? 12f : 14f;
		} else if (count >= 800) {
			xScale = isLand ? 10f : 12f;
		} else if (count >= 700) {
			xScale = isLand ? 8f : 10f;
		} else if (count >= 500) {
			xScale = isLand ? 6f : 8f;
		} else if (count >= 300) {
			xScale = isLand ? 4.5f : 5.5f;
		} else if (count >= 150) {
			xScale = isLand ? 2f : 2f;
		} else if (count >= 100) {
			xScale = 1.5f;
		}
		return xScale;
	}

	private LineDataSet setLine(List<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
		lineDataSetMa.setDrawValues(false);

		lineDataSetMa.setColor(getResources().getColor(R.color.fenshi_line));
//        lineDataSetMa.setCircleColor(ContextCompat.getColor(SpotKlineActivityOld.this, R.color.ma10));

		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setHighlightEnabled(true);
		lineDataSetMa.setHighlightLineWidth(1f);
		lineDataSetMa.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		lineDataSetMa.setDrawFilled(true);
		lineDataSetMa.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.fenshi_chart_sharp));
//        lineDataSetMa.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setDrawCircleHole(false);

		return lineDataSetMa;
	}
	/**
	 * 标记线
	 *
	 * @return
	 */
	private LineData generateMarkLine() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setMarkLine(mData.getMarkLineEntries()));
		sets.add(setCurrentPriceLine(mData.getCurrentPriceLineEntries()));
		return new LineData(sets);
	}
	private void setKLineByChart(List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}
		CandleData candleData = generateCandleData();
		LineData markLineData = null;
		if(isContract){
			 markLineData = generateMarkLine();
		}
		//最新价的线
		LineData currentPriceLineData = generateCurrentPriceLine();
		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (mKlineChartK.getBarData() != null) {
			mKlineChartK.getBarData().clearValues();
		}
		if (mKlineChartK.getLineData() != null) {
			mKlineChartK.getLineData().clearValues();
		}
		if (mKlineChartK.getCandleData() != null) {
			mKlineChartK.getCandleData().clearValues();
		}
		CombinedData combinedData = new CombinedData();
		combinedData.setData(candleData);
		combinedData.setData(currentPriceLineData);
		if(isContract){
			combinedData.setData(markLineData);
		}
		mKlineChartK.setData(combinedData);

		if (isFirst) {
			mKlineChartK.getXAxis().setAxisMinimum(candleData.getXMin() - 0.5f);
			mKlineChartK.getXAxis().setAxisMaximum(getInitCount(candleData.getXMax() + 0.5f));
			setPostScaleHandler(mKlineChartK);
			if (mData.getCandleEntries() != null && mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				mKlineChartK.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				mKlineChartK.moveViewToX(0);
			}
		}

		mKlineChartK.notifyDataSetChanged();
		mKlineChartK.invalidate();
	}

	private void setKMALineByChart(List<KLineBean> kLineDatas) {

		if (mData == null) {
			return;
		}
		mData.initKLineMA(kLineDatas);
		LineData maData = generateMA();
		CandleData candleData = generateCandleData();

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (mKlineChartK.getBarData() != null) {
			mKlineChartK.getBarData().clearValues();
		}
		if (mKlineChartK.getLineData() != null) {
			mKlineChartK.getLineData().clearValues();
		}
		if (mKlineChartK.getCandleData() != null) {
			mKlineChartK.getCandleData().clearValues();
		}

		CombinedData combinedData = new CombinedData();
		combinedData.setData(maData);
		combinedData.setData(candleData);
		mKlineChartK.setData(combinedData);

		if (isFirst) {
			mKlineChartK.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			mKlineChartK.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));

			setPostScaleHandler(mKlineChartK);
			if (mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				mKlineChartK.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				mKlineChartK.moveViewToX(0);
			}
		}

		mKlineChartK.notifyDataSetChanged();
		mKlineChartK.invalidate();
	}

	private void setKBollLineByChart(List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}
		mData.initBOLL(kLineDatas);
		LineData bollData = generateBoll();
		CandleData candleData = generateCandleData();

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (mKlineChartK.getBarData() != null) {
			mKlineChartK.getBarData().clearValues();
		}
		if (mKlineChartK.getLineData() != null) {
			mKlineChartK.getLineData().clearValues();
		}
		if (mKlineChartK.getCandleData() != null) {
			mKlineChartK.getCandleData().clearValues();
		}
		CombinedData combinedData = new CombinedData();
		combinedData.setData(bollData);
		combinedData.setData(candleData);
		mKlineChartK.setData(combinedData);

		if (isFirst) {
			mKlineChartK.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			mKlineChartK.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));

			setPostScaleHandler(mKlineChartK);
			if (mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				mKlineChartK.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				mKlineChartK.moveViewToX(0);
			}
		}

		mKlineChartK.notifyDataSetChanged();
		mKlineChartK.invalidate();

	}

	private void setVolumeByChart(CombinedChart combinedChart, List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}
		mData.initVolumeMA(kLineDatas);
		BarDataSet set = new BarDataSet(mData.getBarEntries(), "成交量");
		// 高亮线关闭，用曲线联动
		set.setHighlightEnabled(false);
		set.setHighLightColor(getResources().getColor(R.color.minute_yellow));

		// 是否绘制数值
		set.setDrawValues(false);
		// 使用左轴数据
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		if (mData.getVolColos() != null && mData.getVolColos().size() > 0) {
			set.setColors(mData.getVolColos());
		}

		BarData barData = new BarData(set);
		barData.setBarWidth(K_BAR_WITH);

		List<ILineDataSet> sets = new CopyOnWriteArrayList<>();
		sets.add(setLineDataSet(0, mData.getMa5VolData()));
		sets.add(setLineDataSet(1, mData.getMa10VolData()));
		sets.add(setLineDataSet(2, mData.getMa20VolData()));

		LineData lineData = new LineData(sets);

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (combinedChart.getBarData() != null) {
			combinedChart.getBarData().clearValues();
		}
		if (combinedChart.getLineData() != null) {
			combinedChart.getLineData().clearValues();
		}
		if (combinedChart.getCandleData() != null) {
			combinedChart.getCandleData().clearValues();
		}

		CombinedData combinedData = new CombinedData();
		combinedData.setData(barData);
		combinedData.setData(lineData);
		combinedChart.setData(combinedData);

		if (isFirst) {
			combinedChart.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			combinedChart.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));
			setPostScaleHandler(combinedChart);
			// 移动到最后
			if (mData.getCandleEntries() != null && mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				combinedChart.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				combinedChart.moveViewToX(0);
			}
		}

		combinedChart.notifyDataSetChanged();
		combinedChart.invalidate();


	}

	private CandleData generateCandleData() {
		CandleDataSet set = new CandleDataSet(mData.getCandleEntries(), "");
		set.setHighlightEnabled(true);
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		set.setValueTextSize(10f);
		set.setDecreasingPaintStyle(Paint.Style.FILL);
		set.setIncreasingPaintStyle(Paint.Style.FILL);
		set.setDecreasingColor(mDropColor);//设置开盘价高于收盘价的颜色
		set.setIncreasingColor(mRiseColor);//设置开盘价地狱收盘价的颜色
		set.setNeutralColor(mRiseColor);//设置开盘价等于收盘价的颜色
		set.setShadowColorSameAsCandle(true);
		set.setHighlightLineWidth(1f);
		set.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		set.setDrawValues(true);
		set.setValueTextColor(getResources().getColor(R.color.kline_text_color));
		set.setValueFormatter(new IValueFormatter() {
			@Override
			public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
				return BigDecimalUtils.parseENum(value);
			}
		});

		CandleData candleData = new CandleData();
		candleData.addDataSet(set);
		return candleData;
	}

	/**
	 * 最新价
	 *
	 * @return
	 */
	private LineData generateCurrentPriceLine() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setCurrentPriceLine(mData.getCurrentPriceLineEntries()));
		return new LineData(sets);
	}

	/**
	 * 当前价格
	 *
	 * @param lineEntries
	 * @return
	 */
	private synchronized LineDataSet setCurrentPriceLine(List<Entry> lineEntries) {
		//解决切换横屏的崩溃问题
		if (lineEntries == null) {
			lineEntries = new ArrayList<>();
		}
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "currentPirce" + type);
		lineDataSetMa.setDrawValues(false);

		lineDataSetMa.setColor(getResources().getColor(R.color.kline_current_price_color));

		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.enableDashedLine(12, 9, 0);
		lineDataSetMa.setHighlightEnabled(false);
		lineDataSetMa.setDrawFilled(false);
		lineDataSetMa.setDrawCircles(false);

		return lineDataSetMa;
	}

	private LineData generateMA() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		/******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
		sets.add(setMaLine(5, mData.getMa5DataL()));
		sets.add(setMaLine(10, mData.getMa10DataL()));
		sets.add(setMaLine(20, mData.getMa20DataL()));
		if (isContract) {
			sets.add(setMarkLine(mData.getMarkLineEntries()));
		}
		sets.add(setCurrentPriceLine(mData.getCurrentPriceLineEntries()));


		return new LineData(sets);
	}

	/**
	 * 标记线
	 *
	 * @param lineEntries
	 * @return
	 */
	private synchronized LineDataSet setMarkLine(List<Entry> lineEntries) {
		//解决切换横屏的崩溃问题
		if (lineEntries == null) {
			lineEntries = new ArrayList<>();
		}
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "markline" + type);
		lineDataSetMa.setDrawValues(false);

		lineDataSetMa.setColor(getResources().getColor(R.color.res_assistColor_23));

		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setHighlightEnabled(false);
		lineDataSetMa.setDrawFilled(false);
		lineDataSetMa.setDrawCircles(false);

		return lineDataSetMa;
	}

	private LineData generateBoll() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setBollMaLine(0, mData.getBollDataUP()));
		sets.add(setBollMaLine(1, mData.getBollDataMB()));
		sets.add(setBollMaLine(2, mData.getBollDataDN()));

		if (isContract) {
			sets.add(setMarkLine(mData.getMarkLineEntries()));
		}
		sets.add(setCurrentPriceLine(mData.getCurrentPriceLineEntries()));
		return new LineData(sets);
	}

	private LineDataSet setMaLine(int ma, List<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + ma);
		lineDataSetMa.setDrawValues(false);
		if (ma == 5) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
		} else if (ma == 10) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
		} else if (ma == 20) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
		}
		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

		if (type == 5) {
			lineDataSetMa.setHighlightEnabled(true);
			lineDataSetMa.setHighlightLineWidth(1f);
			lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
			lineDataSetMa.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		} else {
			lineDataSetMa.setHighlightEnabled(false);
		}
		return lineDataSetMa;
	}

	private LineDataSet setBollMaLine(int type, List<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "kdj" + type);
		lineDataSetMa.setDrawValues(false);
		//DEA
		if (type == 0) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
		} else if (type == 1) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
		} else if (type == 2) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
		}

		if (type == 0) {
			lineDataSetMa.setHighlightEnabled(false);//boll这部分必须的设置
			lineDataSetMa.setHighlightLineWidth(1f);
			lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
			lineDataSetMa.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		} else {
			lineDataSetMa.setHighlightEnabled(false);
		}

		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

		return lineDataSetMa;
	}

	private LineDataSet setLineDataSet(int type, List<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "line" + type);
		//DEA
		if (type == 0) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
		} else if (type == 1) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
		} else if (type == 2) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
		}

		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
		lineDataSetMa.setDrawValues(false);//是否绘制数据
		if (type == 0) {
			lineDataSetMa.setHighlightEnabled(true);
			lineDataSetMa.setHighlightLineWidth(1f);
			lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
			lineDataSetMa.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		} else {
			lineDataSetMa.setHighlightEnabled(false);
		}
//        lineDataSetMa.setHighlightEnabled(false);
		return lineDataSetMa;
	}

	private void setMACDByChart(CombinedChart combinedChart, List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}
		mData.initMACD(kLineDatas);

		BarDataSet set = generateBarDataSet(mData.getMacdData());
		// 高亮线开启
		set.setHighlightEnabled(false);
		// 是否绘制数值
		set.setDrawValues(false);
		// 使用左轴数据
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		if (mData.getMacdColors() != null && mData.getMacdColors().size() > 0) {
			set.setColors(mData.getMacdColors());
		}
		BarData barData = new BarData(set);
		barData.setBarWidth(0.15f);

		LineData lineData = generateMACD();

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (combinedChart.getBarData() != null) {
			combinedChart.getBarData().clearValues();
		}
		if (combinedChart.getLineData() != null) {
			combinedChart.getLineData().clearValues();
		}
		if (combinedChart.getCandleData() != null) {
			combinedChart.getCandleData().clearValues();
		}

		CombinedData combinedData = new CombinedData();
		combinedData.setData(barData);
		combinedData.setData(lineData);
		combinedChart.setData(combinedData);

		if (isFirst) {
			combinedChart.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			combinedChart.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));
			setPostScaleHandler(combinedChart);
			// 移动到最后
			if (mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				combinedChart.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				combinedChart.moveViewToX(0);
			}
		}

		combinedChart.notifyDataSetChanged();
		combinedChart.invalidate();
	}

	private void setKDJByChart(CombinedChart combinedChart, List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}
		mData.initKDJ(kLineDatas);

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (combinedChart.getBarData() != null) {
			combinedChart.getBarData().clearValues();
		}
		if (combinedChart.getLineData() != null) {
			combinedChart.getLineData().clearValues();
		}
		if (combinedChart.getCandleData() != null) {
			combinedChart.getCandleData().clearValues();
		}

		LineData kdjData = generateKDJData();

		CombinedData combinedData = new CombinedData();
		combinedData.setData(kdjData);
		combinedChart.setData(combinedData);

		if (isFirst) {
			combinedChart.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			combinedChart.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));
			setPostScaleHandler(combinedChart);
			// 移动到最后
			if (mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				combinedChart.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				combinedChart.moveViewToX(0);
			}
		}

		combinedChart.notifyDataSetChanged();
		combinedChart.invalidate();
	}

	private void setRSIByChart(CombinedChart combinedChart, List<KLineBean> kLineDatas) {
		if (mData == null) {
			return;
		}
		mData.initRSI(kLineDatas);

		// 清空数据，防止图表类型不一值报空；回收旧数据
		if (combinedChart.getBarData() != null) {
			combinedChart.getBarData().clearValues();
		}
		if (combinedChart.getLineData() != null) {
			combinedChart.getLineData().clearValues();
		}
		if (combinedChart.getCandleData() != null) {
			combinedChart.getCandleData().clearValues();
		}

		LineData rsiData = generateRSIData();
		BarData rsiBarData = generateBarData(mData.getBarDatasRSI());

		CombinedData combinedData = new CombinedData();
		combinedData.setData(rsiBarData);
		combinedData.setData(rsiData);
		combinedChart.setData(combinedData);


		if (isFirst) {
			combinedChart.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
			combinedChart.getXAxis().setAxisMaximum(getInitCount(combinedData.getXMax() + 0.5f));
			// 移动到最后
			setPostScaleHandler(combinedChart);
			if (mData.getCandleEntries().size() > (isLand ? NORMAL_COUNT_LAND : NORMAL_COUNT)) {
				combinedChart.moveViewToX(mData.getCandleEntries().size() - 1);
			} else {
				combinedChart.moveViewToX(0);
			}
		}

		combinedChart.notifyDataSetChanged();
		combinedChart.invalidate();
	}

	private BarData generateBarData(List<BarEntry> barEntryList) {
		BarDataSet set = generateBarDataSet(barEntryList);
		return new BarData(set);
	}

	private BarDataSet generateBarDataSet(List<BarEntry> barEntryList) {
		BarDataSet set = new BarDataSet(barEntryList, "BarDataSet");
//        set.setBarSpacePercent(20); //bar空隙
		set.setHighlightEnabled(true);
		set.setHighLightAlpha(255);
		set.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		set.setDrawValues(false);
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		set.setColor(getResources().getColor(R.color.transparent));
		return set;
	}

	public LineData generateMACD() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setMACDMaLine(0, mData.getXVals(), mData.getDeaData()));
		sets.add(setMACDMaLine(1, mData.getXVals(), mData.getDifData()));
//        sets.add(setMACDMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getDifData()));
		return new LineData(sets);
	}

	private LineDataSet setMACDMaLine(int type, List<String> xVals, List<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
		lineDataSetMa.setHighlightEnabled(false);
		lineDataSetMa.setDrawValues(false);
		//DEA
		if (type == 0) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
		} else {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
		}

		if (type == 0) {
			lineDataSetMa.setHighlightEnabled(true);
			lineDataSetMa.setHighlightLineWidth(1f);
			lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
			lineDataSetMa.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		} else {
			lineDataSetMa.setHighlightEnabled(false);
		}

		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

		return lineDataSetMa;
	}


	public LineData generateKDJData() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setKDJMaLine(0, mData.getkData()));
		sets.add(setKDJMaLine(1, mData.getdData()));
		sets.add(setKDJMaLine(2, mData.getjData()));
		return new LineData(sets);
	}

	public LineData generateRSIData() {
		ArrayList<ILineDataSet> sets = new ArrayList<>();
		sets.add(setKDJMaLine(0, mData.getRsiData6()));
		sets.add(setKDJMaLine(1, mData.getRsiData12()));
		sets.add(setKDJMaLine(2, mData.getRsiData24()));
		return new LineData(sets);
	}

	private LineDataSet setKDJMaLine(int type, List<Entry> lineEntries) {
		LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "kdj" + type);
		lineDataSetMa.setDrawValues(false);
		//DEA
		if (type == 0) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
		} else if (type == 1) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
		} else if (type == 2) {
			lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
		}

		if (type == 0) {
			lineDataSetMa.setHighlightEnabled(true);
			lineDataSetMa.setHighlightLineWidth(1f);
			lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
			lineDataSetMa.setHighLightColor(getResources().getColor(R.color.kline_highlight));
		} else {
			lineDataSetMa.setHighlightEnabled(false);
		}

		lineDataSetMa.setLineWidth(1f);
		lineDataSetMa.setDrawCircles(false);
		lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

		return lineDataSetMa;
	}

	private void setSelectedMasterType(int masterType) {
		if (isFenshi) {
			return;
		} else {
			if (masterType == ZhibiaoListener.MASTER_TYPE_HIDE) {//都隐藏
				mMAText.setVisibility(View.INVISIBLE);
				setKLineByChart(kLineDatas);
			} else if (masterType == ZhibiaoListener.MASTER_TYPE_MA) {//ma显示
				mMAText.setVisibility(View.VISIBLE);
				setKMALineByChart(kLineDatas);
			} else if (masterType == ZhibiaoListener.MASTER_TYPE_BOLL) {//boll显示
				mMAText.setVisibility(View.VISIBLE);
				setKBollLineByChart(kLineDatas);
			}

			if (kLineDatas == null || kLineDatas.size() == 0) {
				return;
			}
			updateText(kLineDatas.size() - 1);
		}
	}

	public void updateText(int index) {
		if (isContract) {
			updateContractText(index);
		} else {
			updateSpotText(index);
		}
	}

	@SuppressLint("StringFormatMatches")
	private void updateSpotText(int index) {
		if (index < 0 || index >= kLineDatas.size()) {
			return;
		}
		try {
			if (!isFenshi) {//分时图不需要指标
				if (masterType == ZhibiaoListener.MASTER_TYPE_HIDE) {
					mMAText.setText("");
					mMAText.setVisibility(View.INVISIBLE);
				} else if (masterType == ZhibiaoListener.MASTER_TYPE_MA) {
					if (null != mData.getMa5DataL() && mData.getMa5DataL().size() > 0) {
						//                Log.e("mxd","index="+index+",kLineDatas.size="+kLineDatas.size()+"kLineDatas.get(index).ma5="+kLineDatas.get(index).ma5);
						String maString = String.format(getResources().getString(R.string.ma_highlight_new),
								NumberUtils.floatToString(kLineDatas.get(index).ma5),
								NumberUtils.floatToString(kLineDatas.get(index).ma10),
								NumberUtils.floatToString(kLineDatas.get(index).ma20));
						mMAText.setText(getSpannableString(maString,
								getResources().getColor(R.color.ma5),
								getResources().getColor(R.color.ma10),
								getResources().getColor(R.color.ma20)));
					}
				} else if (masterType == ZhibiaoListener.MASTER_TYPE_BOLL) {
					String bollString = "non";
					if (index >= (DataParse.DEFAULT_INDEX_BOLL - 1)) {
						int newIndex = index - (DataParse.DEFAULT_INDEX_BOLL - 1);
						if (newIndex < mData.getBollDataMB().size()) {
							bollString = String.format(getResources().getString(R.string.boll_highlight_new),
									NumberUtils.floatToString(mData.getBollDataMB().get(newIndex).getY()),
									NumberUtils.floatToString(mData.getBollDataUP().get(newIndex).getY()),
									NumberUtils.floatToString(mData.getBollDataDN().get(newIndex).getY()));
						} else {
							bollString = String.format(getResources().getString(R.string.boll_highlight),
									Float.NaN,
									Float.NaN,
									Float.NaN);
						}
					} else {
						bollString = String.format(getResources().getString(R.string.boll_highlight),
								Float.NaN,
								Float.NaN,
								Float.NaN);
					}
					mMAText.setText(getSpannableString(
							bollString,
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma20)));
				}
			}
			if (kLineDatas != null && kLineDatas.size() > index) {
				KLineBean kLineBean = kLineDatas.get(index);
				setVol(kLineBean);

			}

			SpannableString spanString = new SpannableString("");
			switch (subZhibiao) {
				case ZhibiaoListener.MACD: {
					String str = String.format(getResources().getString(R.string.macd_highlight_new),
							NumberUtils.floatToString(mData.getDifData().get(index).getY()),
							NumberUtils.floatToString(mData.getDeaData().get(index).getY()),
							NumberUtils.floatToString(mData.getMacdData().get(index).getY()));

					spanString = getSpannableString(str,
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma20));

					break;
				}
				case ZhibiaoListener.KDJ: {
					String str = String.format(getResources().getString(R.string.kdj_highlight_new),
							NumberUtils.floatToString(mData.getkData().get(index).getY()),
							NumberUtils.floatToString(mData.getdData().get(index).getY()),
							NumberUtils.floatToString(mData.getjData().get(index).getY()));

					spanString = getSpannableString(str,
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma20));

					break;
				}
				case ZhibiaoListener.RSI:
					if (index < mData.getRsiData6().size()) {
						String str = String.format(getResources().getString(R.string.rsi_highlight_new),
								NumberUtils.floatToString(mData.getRsiData6().get(index).getY()),
								NumberUtils.floatToString(mData.getRsiData12().get(index).getY()),
								NumberUtils.floatToString(mData.getRsiData24().get(index).getY()));

						spanString = getSpannableString(str,
								getResources().getColor(R.color.ma5),
								getResources().getColor(R.color.ma10),
								getResources().getColor(R.color.ma20));

					}

					break;
			}
			mStockIndexText.setText(spanString);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	private void updateContractText(int index) {
		if (index < 0 || index >= kLineDatas.size()) {
			return;
		}
		try {
			String markLineStr = getResources().getString(R.string.mark_price);
			float markLine = 0;
			if (index >= mData.getMarkLineEntries().size()) {
				markLine = 0;
			} else {
				markLine = mData.getMarkLineEntries().get(index).getY();
			}
			if (!isFenshi) {//分时图不需要指标
				if (masterType == 0) {
					String mark_Str = String.format(getResources().getString(R.string.mark_future), markLineStr, NumberUtils.floatToString(markLine));

					mMAText.setVisibility(View.VISIBLE);
					mMAText.setText(getSpannableString(mark_Str, getResources().getColor(R.color.res_assistColor_23)));
				} else if (masterType == 1) {
					String maString = "";
					if (null != mData.getMa5DataL() && mData.getMa5DataL().size() > 0) {
//                Log.e("mxd","index="+index+",kLineDatas.size="+kLineDatas.size()+"kLineDatas.get(index).ma5="+kLineDatas.get(index).ma5);

						maString = String.format(getResources().getString(R.string.ma_future_highlight),
								NumberUtils.floatToString(kLineDatas.get(index).ma5),
								NumberUtils.floatToString(kLineDatas.get(index).ma10),
								NumberUtils.floatToString(kLineDatas.get(index).ma20), markLineStr, NumberUtils.floatToString(markLine));
					} else {
						maString = String.format(getResources().getString(R.string.ma_future_highlight),
								"NaN",
								"NaN",
								"NaN", markLineStr, NumberUtils.floatToString(markLine));
					}
					mMAText.setText(getSpannableString(maString,
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma20),
							getResources().getColor(R.color.res_assistColor_23)));
				} else if (masterType == 2) {
					String bollString;
					if (index >= (DataParse.DEFAULT_INDEX_BOLL - 1)) {
						bollString = String.format(getResources().getString(R.string.boll_highlight_future),
								NumberUtils.floatToString(mData.getBollDataMB().get(index - (DataParse.DEFAULT_INDEX_BOLL - 1)).getY()),
								NumberUtils.floatToString(mData.getBollDataUP().get(index - (DataParse.DEFAULT_INDEX_BOLL - 1)).getY()),
								NumberUtils.floatToString(mData.getBollDataDN().get(index - (DataParse.DEFAULT_INDEX_BOLL - 1)).getY()),
								markLineStr, NumberUtils.floatToString(markLine)
						);
					} else {
						bollString = String.format(getResources().getString(R.string.boll_highlight_nan_future),
								"NaN",
								"NaN",
								"NaN", markLineStr, NumberUtils.floatToString(markLine));
					}
					mMAText.setText(getSpannableString(bollString,
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma20),
							getResources().getColor(R.color.res_assistColor_23)));
				}
			}

			KLineBean kLineBean = kLineDatas.get(index);
			setVol(kLineBean);

			SpannableString spanString = new SpannableString("");
			switch (subZhibiao) {
				case SelectTimeListener.MACD: {
					String str = String.format(getResources().getString(R.string.macd_highlight),
							NumberUtils.floatToString(mData.getDifData().get(index).getY()),
							NumberUtils.floatToString(mData.getDeaData().get(index).getY()),
							NumberUtils.floatToString(mData.getMacdData().get(index).getY()));

					spanString = getSpannableString(str,
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma20));

					break;
				}
				case SelectTimeListener.KDJ: {
					String str = String.format(getResources().getString(R.string.kdj_highlight),
							NumberUtils.floatToString(mData.getkData().get(index).getY()),
							NumberUtils.floatToString(mData.getdData().get(index).getY()),
							NumberUtils.floatToString(mData.getjData().get(index).getY()));

					spanString = getSpannableString(str,
							getResources().getColor(R.color.ma5),
							getResources().getColor(R.color.ma10),
							getResources().getColor(R.color.ma20));

					break;
				}
				case SelectTimeListener.RSI:
					if (index < mData.getRsiData6().size()) {
						String str = String.format(getResources().getString(R.string.rsi_highlight),
								NumberUtils.floatToString(mData.getRsiData6().get(index).getY()),
								NumberUtils.floatToString(mData.getRsiData12().get(index).getY()),
								NumberUtils.floatToString(mData.getRsiData24().get(index).getY()));

						spanString = getSpannableString(str,
								getResources().getColor(R.color.ma5),
								getResources().getColor(R.color.ma10),
								getResources().getColor(R.color.ma20));

					}

					break;
			}
			mStockIndexText.setText(spanString);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	private SpannableString getSpannableString(String str, int partColor0) {
		SpannableString spanString = new SpannableString(str);
		int end = str.length();

		spanString.setSpan(new ForegroundColorSpan(partColor0),
				0, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
		return spanString;
	}

	private SpannableString getSpannableString(String str, int partColor0, int partColor1, int partColor2) {
		String[] splitString = str.split("[●]");
		SpannableString spanString = new SpannableString(str);
		try {
			int pos0 = splitString[0].length();
			int pos1 = pos0 + splitString[1].length() + 1;
			int end = str.length();

			spanString.setSpan(new ForegroundColorSpan(partColor0),
					pos0, pos1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

			if (splitString.length > 2) {
				int pos2 = pos1 + splitString[2].length() + 1;

				spanString.setSpan(new ForegroundColorSpan(partColor1),
						pos1, pos2, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

				spanString.setSpan(new ForegroundColorSpan(partColor2),
						pos2, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
			} else {
				spanString.setSpan(new ForegroundColorSpan(partColor1),
						pos1, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return spanString;
	}

	private SpannableString getSpannableString(String str, int partColor0, int partColor1, int partColor2, int partColor3) {
		String[] splitString = str.split("[●]");
		SpannableString spanString = new SpannableString(str);

		int pos0 = splitString[0].length();
		int pos1 = pos0 + splitString[1].length() + 1;
		int end = str.length();

		spanString.setSpan(new ForegroundColorSpan(partColor0),
				pos0, pos1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

		if (splitString.length > 3) {
			int pos2 = pos1 + splitString[2].length() + 1;
			int pos3 = pos2 + splitString[3].length() + 1;

			spanString.setSpan(new ForegroundColorSpan(partColor1),
					pos1, pos2, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

			spanString.setSpan(new ForegroundColorSpan(partColor2),
					pos2, pos3, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

			spanString.setSpan(new ForegroundColorSpan(partColor3),
					pos3, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);


		} else if (splitString.length > 2) {
			int pos2 = pos1 + splitString[2].length() + 1;

			spanString.setSpan(new ForegroundColorSpan(partColor1),
					pos1, pos2, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

			spanString.setSpan(new ForegroundColorSpan(partColor2),
					pos2, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
		} else {
			spanString.setSpan(new ForegroundColorSpan(partColor1),
					pos1, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
		}

		return spanString;
	}

	private void setSelectedZhibiao(String zhibiao_type) {
		if (!TextUtils.isEmpty(zhibiao_type)) {
			String stock_str = "";
			if (zhibiao_type.equals(ZhibiaoListener.MACD)) {
				setMACDByChart(mKlineChartCharts, kLineDatas);
				stock_str = getResources().getString(R.string.macd_normal);
			} else if (zhibiao_type.equals(ZhibiaoListener.KDJ)) {
				setKDJByChart(mKlineChartCharts, kLineDatas);
				stock_str = getResources().getString(R.string.kdj_normal);
			} else if (zhibiao_type.equals(ZhibiaoListener.RSI)) {
				setRSIByChart(mKlineChartCharts, kLineDatas);
				stock_str = getResources().getString(R.string.rsi_normal);
			}
			mStockIndexText.setText(stock_str);
		}
	}

	private float getInitCount(float count) {
		if (isLand) {
			return count >= ONE_SCREENT_COUNT_LAND ? count : ONE_SCREENT_COUNT_LAND;
		} else {
			return count >= ONE_SCREENT_COUNT ? count : ONE_SCREENT_COUNT;
		}
	}

	public void setContractType(int contractType) {
		this.contractType = contractType;
	}

	public void setContractTable(ContractUsdtInfoTable table) {
		this.table = table;
	}


	private void setVol(KLineBean kLineBean) {
		if (isContract) {
			if (contractType == Constants.CONTRACT_TYPE_USDT) {
				mVolText.setText(String.format("VOL:%s %s", BigDecimalUtils.floatToString(kLineBean.vol,2), TradeUtils.getContractUsdtUnit(table)));
			} else {
				mVolText.setText(String.format("VOL:%s %s", BigDecimalUtils.floatToString(kLineBean.vol,2), getResources().getString(R.string.number)));
			}
		} else {
			mVolText.setText(String.format("VOL:%s",BigDecimalUtils.floatToString(kLineBean.vol,2)));
		}

	}

	public void hideLine() {
		mKlineChartCharts.highlightValues(null);
		mKlineChartVolume.highlightValues(null);
		mKlineChartK.highlightValues(null);
	}
}
