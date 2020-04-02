package com.coinbene.manbiwang.contract.dialog;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.MarketType;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.http.FundingTimeModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContractParmsDialog extends QMUIDialog {
	@BindView(R2.id.tv_symbol)
	TextView tvSymbol;
	@BindView(R2.id.tv_last_price)
	TextView tvLastPrice;
	@BindView(R2.id.tv_ups_and_downs)
	TextView tvUpsAndDowns;
	@BindView(R2.id.tv_cur_fee)
	TextView tvCurFee;
	@BindView(R2.id.tv_settlement)
	TextView tvSettlement;
	@BindView(R2.id.tv_settlement_value)
	TextView tvSettlementValue;
	@BindView(R2.id.tv_fee_value)
	TextView tvFeeValue;
	@BindView(R2.id.iv_close)
	ImageView ivClose;

	private String symbol;
	private NewMarketWebsocket.MarketDataListener contractQouteListener;
	private long defaultCountDownTime = 8 * 60 * 60 * 1000;//默认距费用结算  8小时
	private CountDownTimer timer;


	public ContractParmsDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_contract_parms);

		ButterKnife.bind(this);

		initView();

		setListener();
	}

	private void setListener() {
		ivClose.setOnClickListener(v -> dismiss());
	}

	private void initView() {

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (contractQouteListener == null) {
			contractQouteListener = dataMap -> {
				if (dataMap == null || dataMap.size() == 0) {
					return;
				}
				WsMarketData wsMarketData = dataMap.get(symbol);
				tvLastPrice.post(() -> setData(wsMarketData));
			};
		}

		NewMarketWebsocket.getInstance().registerMarketDataListener(contractQouteListener, TradeUtils.getContractType(symbol) == ContractType.USDT ? MarketType.USDT_CONTRACT : MarketType.BTC_CONTRACT);
	}

	@Override
	protected void onStop() {
		super.onStop();
		destroyTimer();
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(contractQouteListener, TradeUtils.getContractType(symbol) == ContractType.USDT ? MarketType.USDT_CONTRACT : MarketType.BTC_CONTRACT);
	}

	@Override
	public void show() {
		getFundingTime();
		NewMarketWebsocket.getInstance().pullMarketData();
		tvSymbol.setText(String.format(getContext().getString(R.string.forever_no_delivery), TradeUtils.getContractBase(symbol)));
		super.show();
	}

	private void setData(WsMarketData wsMarketData) {
		if (wsMarketData != null) {
			tvLastPrice.setText(wsMarketData.getLastPrice());

			String upsAndDowns = TextUtils.isEmpty(wsMarketData.getUpsAndDowns()) ? "0.00%" : wsMarketData.getUpsAndDowns();
			if (upsAndDowns.equals("0.00%")) {
				tvUpsAndDowns.setText(wsMarketData.getUpsAndDowns());
				tvLastPrice.setTextColor(SwitchUtils.isRedRise() ? getContext().getResources().getColor(R.color.res_red) : getContext().getResources().getColor(R.color.res_green));
				tvUpsAndDowns.setTextColor(SwitchUtils.isRedRise() ? getContext().getResources().getColor(R.color.res_red) : getContext().getResources().getColor(R.color.res_green));
			} else if (upsAndDowns.contains("-")) {
				tvUpsAndDowns.setText(wsMarketData.getUpsAndDowns());
				tvLastPrice.setTextColor(SwitchUtils.isRedRise() ? getContext().getResources().getColor(R.color.res_green) : getContext().getResources().getColor(R.color.res_red));
				tvUpsAndDowns.setTextColor(SwitchUtils.isRedRise() ? getContext().getResources().getColor(R.color.res_green) : getContext().getResources().getColor(R.color.res_red));
			} else {
				tvUpsAndDowns.setText(String.format("+%s", wsMarketData.getUpsAndDowns()));
				tvLastPrice.setTextColor(SwitchUtils.isRedRise() ? getContext().getResources().getColor(R.color.res_red) : getContext().getResources().getColor(R.color.res_green));
				tvUpsAndDowns.setTextColor(SwitchUtils.isRedRise() ? getContext().getResources().getColor(R.color.res_red) : getContext().getResources().getColor(R.color.res_green));
			}

			if (!TextUtils.isEmpty(wsMarketData.getFundingRate())) {
				tvFeeValue.setText(TextUtils.isEmpty(wsMarketData.getFundingRate()) ? "--" : BigDecimalUtils.toPercentage(wsMarketData.getFundingRate(), "0.0000%"));
			}
		}
	}

	public ContractParmsDialog(Context context, int styleRes) {
		super(context, styleRes);
	}


	@Override
	public void dismiss() {
		super.dismiss();


	}

	public void initDialog(String currentSymbol) {
		this.symbol = currentSymbol;
	}


	public void getFundingTime() {
		String url = TradeUtils.getContractType(symbol) == ContractType.USDT ? Constants.MARKET_CONTRACT_FUNDING_TIME_USDT : Constants.MARKET_CONTRACT_FUNDING_TIME;
		OkGo.<FundingTimeModel>get(url).tag(this).execute(new NewJsonSubCallBack<FundingTimeModel>() {
			@Override
			public void onSuc(Response<FundingTimeModel> response) {
				if (response.body().getData() != null) {
					initTimer(response.body().getData().getFundingTime() - response.body().getData().getCurrentTime());
				}

			}

			@Override
			public void onE(Response<FundingTimeModel> response) {
			}


		});
	}


	/**
	 * 费用结算倒计时
	 *
	 * @param time
	 */
	public void initTimer(long time) {
//		foundTime = time;
		if (time <= 0) {

			time = defaultCountDownTime;
		} else {
			time = time * 1000;
		}
		destroyTimer();
		timer = new CountDownTimer(time, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tvSettlementValue.setText(TimeUtils.secondToHourMinSecond(millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
//				tvF8SettlementValue.setText("0");
				destroyTimer();
				//小于0  执行默认的开始时间
				initTimer(-1);


			}
		};
		timer.start();
	}

	/**
	 * 销毁倒计时
	 */
	private void destroyTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
