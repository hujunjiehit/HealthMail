package com.coinbene.manbiwang.kline.newspotkline;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.CustomDialog;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.draw.Status;
import com.github.fujianlian.klinechart.formatter.DateFormatter;
import com.github.fujianlian.klinechart.formatter.MonthTimeFormatter;
import com.github.fujianlian.klinechart.formatter.TimeFormatter;

import java.util.List;

/**
 * Created by june
 * on 2020-03-03
 */
public abstract class BaseKlineActivity extends CoinbeneBaseActivity implements NewKlineInterface.View {

	protected boolean isFenshi;
	protected int mainType;
	protected String subType;

	private ProgressDialog progress;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
		}
	}

	/**
	 * 根据分时线类型，设置不同的时间格式
	 *
	 * @param chartView
	 * @param timeStatus
	 */
	protected void initChartView(KLineChartView chartView, int timeStatus) {
		if (chartView == null) {
			return;
		}

		chartView.setRedRise(SwitchUtils.isRedRise());

		if (timeStatus == 9) {
			//分时
			isFenshi = true;
		} else {
			isFenshi = false;
		}
		switch (getKlineTimeType(timeStatus).split("\\.")[0]) {
			case "1m":
				chartView.setDateTimeFormatter(new TimeFormatter());
				break;
			case "5m":
			case "15m":
			case "30m":
			case "1h":
			case "4h":
				chartView.setDateTimeFormatter(new MonthTimeFormatter());
				break;
			case "D":
			case "W":
			case "M":
				chartView.setDateTimeFormatter(new DateFormatter());
				break;
		}

		chartView.hideSelectData();
	}


	protected String getKlineTimeType(int timeStatus) {
		String type = "1m";
		switch (timeStatus) {
			case 0:
				type = "15m.384";
				break;
			case 1:
				type = "1h.720";
				break;
			case 2:
				type = "4h.720";
				break;
			case 3:
				type = "D.720";
				break;
			case 4:
				type = "1m.720";
				break;
			case 5:
				type = "5m.576";
				break;
			case 6:
				type = "30m.336";
				break;
			case 7:
				type = "W.257";
				break;
			case 8:
				type = "M.120";
				break;
			case 9:
				type = "1m.720";
				break;
		}
		return type;
	}


	protected void setMainDraw(KLineChartView chartView) {
		if (chartView == null) {
			return;
		}

		//chartView.hideSelectData();

		//是否是分时
		chartView.setMainDrawLine(isFenshi);

		//设置MA、BALL、NONE
		setMainType(chartView);

		//设置
		setSubType(chartView);
	}

	protected void setMainType(KLineChartView chartView) {
		if (chartView == null) {
			return;
		}
		if (isFenshi) {
			chartView.changeMainDrawType(Status.NONE);
		} else {
			switch (mainType) {
				case ZhibiaoListener.MASTER_TYPE_BOLL:
					chartView.changeMainDrawType(Status.BOLL);
					break;
				case ZhibiaoListener.MASTER_TYPE_MA:
					chartView.changeMainDrawType(Status.MA);
					break;
				case ZhibiaoListener.MASTER_TYPE_HIDE:
					chartView.changeMainDrawType(Status.NONE);
					break;
			}
		}
	}

	protected void setSubType(KLineChartView chartView) {
		if (chartView == null) {
			return;
		}
		// 0、 MACDDraw
		// 1、 KDJDraw
		// 2、 RSIDraw
		// 3、 WRDraw
		switch (subType) {
			case ZhibiaoListener.SUB2_HIDE:
				chartView.hideChildDraw();
				break;
			case ZhibiaoListener.MACD:
				chartView.setChildDraw(0);
				break;
			case ZhibiaoListener.KDJ:
				chartView.setChildDraw(1);
				break;
			case ZhibiaoListener.RSI:
				chartView.setChildDraw(2);
				break;
		}
	}


	@SuppressLint("ClickableViewAccessibility")
	protected void handleTouchListener(KLineChartView chartView, ViewGroup parentViewGroup) {
		if (chartView == null) {
			return;
		}
		//解决长按滑动冲突问题
		chartView.setOnTouchListener(new View.OnTouchListener() {
			private float x1, y1;
			private float x2, y2;
			private int touchSlop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();
			long mTime = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getPointerCount() == 2) {
					parentViewGroup.requestDisallowInterceptTouchEvent(true);
				} else {
					parentViewGroup.requestDisallowInterceptTouchEvent(false);
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mTime = System.currentTimeMillis();
					//当手指按下的时候
					x1 = event.getX();
					y1 = event.getY();
					if (!chartView.isMainRect(event.getX(), event.getY())) {
						chartView.hideSelectData();
					}

				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					if (chartView.isMainRect(event.getX(), event.getY())) {
						if (System.currentTimeMillis() - mTime < 300 && Math.abs(x1 - event.getX()) < touchSlop) {

							chartView.onChatClick(event);
							chartView.invalidate();
//							chartView.isClick = false;
						} else {
							if (chartView.x == event.getX()) {
								if (chartView.isLongPress) {
									chartView.isLongPress = false;
								}
							}
							chartView.touch = false;
							chartView.invalidate();
						}
					}

					parentViewGroup.requestDisallowInterceptTouchEvent(false);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (chartView.isLongPress()) {
						parentViewGroup.requestDisallowInterceptTouchEvent(true);
					} else {
						//当手指移动的时候
						x2 = event.getX();
						y2 = event.getY();
						if (Math.abs(y2 - y1) <= Math.abs(x2 - x1) && Math.abs(x2 - x1) > touchSlop) {
							//水平方向滑动
							parentViewGroup.requestDisallowInterceptTouchEvent(true);
						} else if (Math.abs(y2 - y1) > touchSlop) {
							//竖直方向滑动
						}
					}
				}
				return false;
			}
		});
	}

	@Override
	public void showLoading() {
		if (progress == null) {
			progress = new CustomDialog(this, R.style.CustomDialog);
		}
		runOnUiThread(() -> {
			if (progress != null && !progress.isShowing()) {
				progress.show();
				progress.setCancelable(true);
			}
		});
	}

	@Override
	public void hideLoading() {
		runOnUiThread(() -> {
			if (progress != null && progress.isShowing()) {
				progress.dismiss();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		progress = null;
	}


	protected void handleKlineData(List<KLineEntity> datas, boolean isFull, KLineChartView mChartView, KLineChartAdapter mAdapter) {
		if (isFull) {
			//全量数据
			DLog.e("klineTime","startCalculate");
			DataHelper.calculate(datas);
			mAdapter.addFooterData(datas);
			DLog.e("klineTime","endCalculate");
			runOnUiThread(() -> {
				mAdapter.notifyDataSetChanged();
				setMainDraw(mChartView);
				DLog.e("klineTime","notifyDataSetChanged");
			});
		} else {
			//增量数据
			if (mAdapter.getCount() < 1) {
				return;
			}

			synchronized (datas) {
				for (int i = 0; i < datas.size(); i++) {
					if (mAdapter.getDate(mAdapter.getCount() - 1).equals(datas.get(i).getDate())) {
						//update date
						Log.e("websocketKline","change item ===> " + datas.get(i).getDate());
						mAdapter.changeItem(mAdapter.getCount() - 1, datas.get(i));
					} else {
						//insert data
						Log.e("websocketKline","insert item ===> " + datas.get(i).getDate());
						if (BigDecimalUtils.compare(datas.get(i).getDate().replaceAll("[-\\s:]",""),
								mAdapter.getDate(mAdapter.getCount() - 1).replaceAll("[-\\s:]","")) > 0) {
							mAdapter.addOneData(datas.get(i));
						} else {
							Log.e("websocketKline","drop data ===> " + datas.get(i).getDate() + " ====> " + mAdapter.getDate(mAdapter.getCount() - 1));
							continue;
						}
					}
					DataHelper.calculate(mAdapter.getDatas());
					runOnUiThread(() -> {
						mAdapter.notifyDataSetChanged();
					});
				}
			}
		}
	}
}
