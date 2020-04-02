package com.coinbene.manbiwang.kline.contractkline;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.coinbene.manbiwang.kline.spotkline.KlineInterface;
import com.coinbene.manbiwang.kline.spotkline.listener.ChartViewListener;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.spotkline.view.ChartView;
import com.coinbene.manbiwang.kline.spotkline.view.TimeViewLand;
import com.coinbene.manbiwang.kline.spotkline.view.TopViewLand;
import com.coinbene.manbiwang.kline.spotkline.view.ZhibiaoViewLand;

import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-11-26
 */
public class ContractKlineLandActivity extends CoinbeneBaseActivity implements KlineInterface.View {

	@BindView(R2.id.layout_close)
	RelativeLayout mLayoutClose;
	@BindView(R2.id.top_view)
	TopViewLand mTopView;
	@BindView(R2.id.chart_view)
	ChartView mChartView;
	@BindView(R2.id.zhibiao_view)
	ZhibiaoViewLand mZhibiaoView;
	@BindView(R2.id.time_view)
	TimeViewLand mTimeView;

	private String symbol;
	private int contractType;

	private KlineInterface.Presenter mPresenter;


	@AddFlowControl
	public static void startMe(Context context, String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return;
		}
		Intent intent = new Intent(context, ContractKlineLandActivity.class);
		intent.putExtra("symbol", symbol);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.kline_contract_land_activity;
	}

	@Override
	public void initView() {
		if (getIntent() != null) {
			symbol = getIntent().getStringExtra("symbol");
			contractType = symbol.contains("-") ? Constants.CONTRACT_TYPE_USDT : Constants.CONTRACT_TYPE_BTC;
			initSymbol(symbol);
		}

		if (TextUtils.isEmpty(symbol)) {
			finish();
			return;
		}

		if (symbol.contains("-")) {
			NewContractUsdtWebsocket.getInstance().changeSymbol(symbol);
		} else {
			NewContractBtcWebsocket.getInstance().changeSymbol(symbol);
		}
	}

	@Override
	public void setListener() {
		mLayoutClose.setOnClickListener(v -> finish());

		mZhibiaoView.setZhibiaoListener(new ZhibiaoListener() {
			@Override
			public void onMasterSelected(int masterType) {
				mChartView.setMasterType(masterType);
			}

			@Override
			public void onSub2Selected(String type) {
				mChartView.setSubZhibiao(type);
			}
		});


		mTimeView.setTimeListener(timeStatus -> initParamsAndLoadData(timeStatus));

		mChartView.setChartViewListener(new ChartViewListener() {
			@Override
			public void highLightPress(int index) {
				if (index < 0 || index >= mPresenter.getkLineDatas().size()) {
					return;
				}
				mTopView.highLightPress(mPresenter.getkLineDatas().get(index));
				mChartView.updateText(index);
			}

			@Override
			public void hideHighValueSelectedValue() {
				mTopView.hideHighValueSelectedValue();
				mChartView.updateText(mPresenter.getkLineDatas().size() - 1);
			}
		});
	}

	@Override
	public void initData() {
		initParamsAndLoadData(SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1));
	}

	@Override
	protected void onResume() {
		super.onResume();
		setFitsSystemWindows(true);
		mPresenter.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPresenter.onPause();
	}

	/**
	 * 初始化精度
	 *
	 * @param symbol
	 */
	private void initSymbol(String symbol) {
		mChartView.setContractType(contractType);
		mChartView.setIsContract(true);
		mTopView.setContractType(contractType);
		mTopView.setSymbol(symbol);
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			ContractInfoTable infoTable = ContractInfoController.getInstance().queryContrackByName(symbol);
			if (infoTable != null) {
				Constants.newScale = infoTable.precision;
				Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.precision);
			}
		} else {
			ContractUsdtInfoTable infoTable = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			mTopView.setContractTable(infoTable);
			mTopView.setIsContract(true);
			mChartView.setContractTable(infoTable);
			if (infoTable != null) {
				Constants.newScale = infoTable.precision;
				Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.precision);
			}
		}

		if (mPresenter == null) {
			mPresenter = new ContractKlinePresenter(this, this, symbol);
		} else {
			mPresenter.updateSymbol(symbol);
		}
	}

	private void initParamsAndLoadData(int timeStatus) {
		mChartView.setFenshi(timeStatus == 9);

		//初始化网络请求参数
		mPresenter.initParams(timeStatus);

		//加载k数据
		mPresenter.loadKlineData();
	}


	@Override
	public boolean needLock() {
		return false;
	}

	@Override
	public void onKlineDataLoadSuccess(DataParse dataParse, List<KLineBean> kLineDatas) {
		if (mTopView != null) {
			mTopView.hideHighValueSelectedValue();
		}
		if (mChartView != null) {
			mChartView.setData(dataParse, kLineDatas);
		}
	}

	@Override
	public void onQuoteDataReceived(WsMarketData quote) {
		runOnUiThread(() -> {
			if (mTopView != null) {
				mTopView.setContractData(symbol, quote);
			}
		});
	}

	@Override
	public void onRiseTypeChanged(int riseType) {
		runOnUiThread(() -> {
			if (mTopView != null) {
				mTopView.setRiseType(symbol, riseType);
			}
		});
	}
}
