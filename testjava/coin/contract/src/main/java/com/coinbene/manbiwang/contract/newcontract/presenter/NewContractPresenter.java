package com.coinbene.manbiwang.contract.newcontract.presenter;

import android.app.Activity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.Product;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.ContractConfigController;
import com.coinbene.common.database.ContractConfigTable;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.ProtocolDialog;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.contract.CalAvlPositionModel;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;
import com.coinbene.manbiwang.model.contract.PriceParamsModel;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;
import com.coinbene.manbiwang.model.http.ContractLeverModel;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewContractPresenter implements NewContractInterface.Presenter {


	private NewContractInterface.View mContractView;
	private WeakReference<Activity> activity;
	private ContractUsdtInfoTable usdtTable;
	private ContractInfoTable btcTable;
	private ContractType contractType = ContractType.USDT;
	private String symbol;


	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(3);
	private ContractConfigTable contractConfigTable;
	private BaseUserEventListener userEventListener;


	public NewContractPresenter(NewContractInterface.View view, Activity activity) {
		this.mContractView = view;
		this.activity = new WeakReference<>(activity);
	}

	@Override
	public int getCecheLever() {
		if (isUsdtContract()) {
			return ContractUsdtInfoController.getInstance().getCurContractLever(symbol);
		} else if (btcTable != null) {
			return ContractInfoController.getInstance().getCurContractLever(symbol);
		}
		return 10;
	}

	@Override
	public void updateContractLever(int leverage) {
		if (contractType == ContractType.USDT) {
			ContractUsdtInfoController.getInstance().updataContractLever(symbol, leverage);
		} else if (btcTable != null) {
			ContractInfoController.getInstance().updataContractLever(symbol, leverage);
		}
	}

	@Override
	public void getAllData() {
		getSingAccountInfo();
		getSingerCurLeverage();
		getSingerCurrentOrderList();
		getSingerPositionlist();
	}

	@Override
	public void userConfigCreate(String key, String value) {

		HttpParams params = new HttpParams();
		params.put("settingKey", key);
		params.put("settingValue", value);

		OkGo.<BaseRes>post(Constants.UPDATE_USER_CONFIG).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				if (response.isSuccessful()) {

				}
				switch (key) {
					case "btcContract_protocol":
						SpUtil.setProtocolStatusForContract("btc", true);
						break;
					case "usdtContract_protocol":
						SpUtil.setProtocolStatusForContract("usdt", true);
						break;
					case "usdtContract_tradeUnit":
						SpUtil.setContractUsdtUnitSwitch(Integer.valueOf(value));
				}


			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	/**
	 * @param lastPrice
	 * @param inputView
	 * @return estimatedValue[0] 符号  	estimatedValue[1]数值  estimatedValue[2]单位
	 */
	@Override
	public void setQuantityPresition(PlusSubInputView inputView) {
		if (isUsdtContract()) {
			if (TradeUtils.getContractUsdtPrecision(usdtTable) == 0) {
				inputView.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
			} else {
				inputView.getmEditText().setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
				PrecisionUtils.setPrecision(inputView.getmEditText(), inputView.getText(), TradeUtils.getContractUsdtPrecision(usdtTable));
			}
		} else {
			inputView.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		}
	}


	@Override
	public String[] getEstimatedValue(String lastPrice, PlusSubInputView inputView) {
		String[] estimatedValue = new String[3];
		estimatedValue[0] = "";
		estimatedValue[1] = "";
		estimatedValue[2] = "";
		if(TextUtils.isEmpty(inputView.getText())){
			return estimatedValue;
		}

		if (isUsdtContract()) {
//				inputView.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
			if (SpUtil.getContractUsdtUnitSwitch() == 0) {
				if (!BigDecimalUtils.isEmptyOrZero(inputView.getText())) {
					estimatedValue[0] = "=";
					estimatedValue[1] = TradeUtils.getContractUsdtEstimatedValue(inputView.getText(), usdtTable);
					estimatedValue[2] = usdtTable.baseAsset;
				}
			} else {
				estimatedValue[0] = "=";
				estimatedValue[1] = inputView.getText();
				estimatedValue[2] = usdtTable.baseAsset;
			}
			return estimatedValue;
		} else {
//			inputView.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
			if (btcTable != null && !BigDecimalUtils.isEmptyOrZero(inputView.getText())) {
				if (TradeUtils.isContractBtc(btcTable)) {
					estimatedValue[0] = "=";
					estimatedValue[1] = CalculationUtils.getBtcContractEstimated(
							btcTable,
							symbol,
							inputView.getText());
					estimatedValue[2] = "USDT";
				} else {
					estimatedValue[0] = "≈";
					estimatedValue[1] = CalculationUtils.getBtcContractEstimated(
							btcTable,
							symbol,
							inputView.getText());
					estimatedValue[2] = "BTC";
				}
			}
		}

		return estimatedValue;
	}

	@AddFlowControl(timeInterval = 3000)
	@Override
	public void getPositionlist() {
//		DLog.e("newContractPresenter", "getPositionlist");
		getSingerPositionlist();
	}

	@AddFlowControl(timeInterval = 200)
	@NeedLogin
	public void getSingerPositionlist() {
//		DLog.e("newContractPresenter", "getSingerPositionlist");
		String url = isUsdtContract() ? Constants.ACCOUNT_POSITION_LIST_USDT : Constants.ACCOUNT_POSITION_LIST;
		OkGo.<ContractPositionListModel>get(url).tag(this).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
			@Override
			public void onSuc(Response<ContractPositionListModel> response) {
				if (response.body().getData() != null) {
					mContractView.setPisitionNumber(response.body().getData().size());
				}
			}

			@Override
			public void onE(Response<ContractPositionListModel> response) {
			}
		});


	}

	@Override
	public void subAll() {

		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onAccountChanged() {
					getSingAccountInfo();
//					getSingerPositionlist();
//					getSingerCurLeverage();
				}

				@Override
				public void onPositionsChanged() {
					getSingAccountInfo();
					getSingerPositionlist();
					getSingerCurLeverage();
				}

				@Override
				public void onCurorderChanged() {
//					getSingAccountInfo();

					getSingerCurrentOrderList();
					getSingerCurLeverage();
				}

//				@Override
//				public void onLiquidation() {
//					getSingAccountInfo();
//					getPositionlist();
//					getCurrentOrderList();
//				}
			};
		}
		UsereventWebsocket.getInstance().registerUsereventListener(isUsdtContract() ? WebSocketType.USDT : WebSocketType.BTC, userEventListener);
	}

	@Override
	public void unSubAll() {
		UsereventWebsocket.getInstance().unregisterUsereventListener(isUsdtContract() ? WebSocketType.USDT : WebSocketType.BTC, userEventListener);
	}

	@Override
	public String getContractUsdtUnitValue(String quantity) {
		if (isUsdtContract()) {
			if (SpUtil.getContractUsdtUnitSwitch() == 0) {
				return quantity;
			} else {
				if (usdtTable == null || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(usdtTable.multiplier)) {
					return "0";
				}
				return BigDecimalUtils.multiplyToStr(quantity, usdtTable.multiplier);
			}
		} else {
			return quantity;
		}
	}

	@Override
	public boolean checkProtocalStatus(View v) {
		if (isUsdtContract()) {
			if (!SpUtil.getProtocolStatusOfContract("usdt")) {
				ProtocolDialog dialog = new ProtocolDialog(v.getContext());
				dialog.setTitle(v.getContext().getResources().getString(R.string.usdt_protocol_title));
				dialog.setContent(v.getContext().getResources().getString(R.string.usdt_protocol_content));
				dialog.setPositiveText(v.getContext().getResources().getString(R.string.go_trading));
				dialog.setNegativeText(v.getContext().getResources().getString(R.string.not_trading));
				dialog.setProtocolText(v.getContext().getResources().getString(R.string.usdt_protocol_text));
				dialog.setProtocolUrl(UrlUtil.getUsdtProtocol());
				dialog.show();

				dialog.setDialogListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {
						userConfigCreate("usdtContract_protocol", "1");
					}
				});
				return false;
			}
		} else {
			if (!SpUtil.getProtocolStatusOfContract("btc")) {
				ProtocolDialog dialog = new ProtocolDialog(v.getContext());
				dialog.setTitle(v.getContext().getResources().getString(R.string.btc_protocol_title));
				dialog.setContent(v.getContext().getResources().getString(R.string.btc_protocol_content));
				dialog.setPositiveText(v.getContext().getResources().getString(R.string.go_trading));
				dialog.setNegativeText(v.getContext().getResources().getString(R.string.not_trading));
				dialog.setProtocolText(v.getContext().getResources().getString(R.string.btc_protocol_text));
				dialog.setProtocolUrl(UrlUtil.getBtcProtocol());
				dialog.show();

				dialog.setDialogListener(new DialogListener() {
					@Override
					public void clickNegative() {
					}

					@Override
					public void clickPositive() {
						userConfigCreate("btcContract_protocol", "1");
					}
				});
				return false;
			}
		}

		return true;
	}

	@Override
	public void doTransFer(View v) {

		if (!checkProtocalStatus(v)) {
			return;
		}
		if (isUsdtContract()) {
			UIBusService.getInstance().openUri(activity.get(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
					new TransferParams()
							.setAsset("USDT")
							.setFrom(Product.NAME_SPOT)
							.setTo(Product.NAME_USDT_CONTRACT)
							.toBundle());
		} else {

			UIBusService.getInstance().openUri(activity.get(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
					new TransferParams()
							.setAsset("BTC")
							.setFrom(Product.NAME_SPOT)
							.setTo(Product.NAME_BTC_CONTRACT)
							.toBundle());

		}

	}

	@Override
	public void doPlaceOrder(ContractPlaceOrderParmsModel orderModel) {
		if (isUsdtContract()) {

			placeOrderUsdt(orderModel);
		} else {
			placeOrderBtc(orderModel);
		}


	}

	private void placeOrderBtc(ContractPlaceOrderParmsModel orderModel) {

		//开仓
		if (orderModel.getTradeType() == Constants.TRADE_OPEN_LONG || orderModel.getTradeType() == Constants.TRADE_OPEN_SHORT) {
			HttpParams params = new HttpParams();
			params.put("symbol", orderModel.getSymbol());
			//订单类型，limit，限价；market，市价
			if (orderModel.getOrderType() == Constants.FIXED_PRICE) {//限价
				params.put("orderType", "limit");
			} else if (orderModel.getOrderType() == Constants.MARKET_PRICE) {//市价
				params.put("orderType", "market");
			} else if (orderModel.getOrderType() == Constants.FIXED_PRICE_HIGH_LEVER) {//高级委托
				if (orderModel.getHighLeverOrderType() == Constants.ONLY_MAKER) {//只做marker
					params.put("orderType", "postOnly");
				} else if (orderModel.getHighLeverOrderType() == Constants.ALL_DEAL_OR_ALL_CANCEL) {//全部成交或全部撤单
					params.put("orderType", "fok");
				} else if (orderModel.getHighLeverOrderType() == Constants.DEAL_CANCEL_SURPLUS) {//成交后撤单
					params.put("orderType", "ioc");
				}
			}
			if (!BigDecimalUtils.isEmptyOrZero(orderModel.getProfitPrice())) {
				params.put("takeProfitPrice", orderModel.getProfitPrice());
			}
			if (!BigDecimalUtils.isEmptyOrZero(orderModel.getLossPrice())) {
				params.put("stopLosePrice", orderModel.getLossPrice());
			}
			params.put("leverage", orderModel.getLever());
			if (!BigDecimalUtils.isEmptyOrZero(orderModel.getPrice()))
				params.put("orderPrice", orderModel.getPrice());
			params.put("quantity", orderModel.getNumber());
			params.put("marginMode", orderModel.getMarginMode());

			//方向 openLong(开多)，openShort开空
			params.put("direction", orderModel.getTradeType() == Constants.TRADE_OPEN_LONG ? "openLong" : "openShort");


//			https://a.coinbene.vip/api/contract-trade-api/trade/placeorder
//			long startTime = System.currentTimeMillis();

			OkGo.<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>post(Constants.TRADE_PLACE_ORDER).params(params).tag(this).execute(new DialogCallback<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>(activity.get()) {
				@Override
				public void onSuc(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {

//					DLog.d("httpTime", "placeOrder   ------>    startTime - endTime = " + (System.currentTimeMillis() - startTime));

					ToastUtil.show(R.string.buyorsell_success);
					mContractView.placeOrderSucces();
					if (!TextUtils.isEmpty(response.body().getData().getOrderId()))
						DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
								new DataCollectionModel(response.body().getData().getOrderId(),
										DataCollectionModel.CONTRACT,
										(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));
				}

				@Override
				public void onE(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {

				}

			});
		}
		//平仓
		else {
			HttpParams params = new HttpParams();
			params.put("symbol", orderModel.getSymbol());
			//订单类型，limit，限价；market，市价
			if (orderModel.getOrderType() == Constants.FIXED_PRICE) {//限价
				params.put("orderType", "limit");
			} else if (orderModel.getOrderType() == Constants.MARKET_PRICE) {//市价
				params.put("orderType", "market");
			} else if (orderModel.getOrderType() == Constants.FIXED_PRICE_HIGH_LEVER) {//高级委托
				if (orderModel.getHighLeverOrderType() == Constants.ONLY_MAKER) {//只做marker
					params.put("orderType", "postOnly");
				} else if (orderModel.getHighLeverOrderType() == Constants.ALL_DEAL_OR_ALL_CANCEL) {//全部成交或全部撤单
					params.put("orderType", "fok");
				} else if (orderModel.getHighLeverOrderType() == Constants.DEAL_CANCEL_SURPLUS) {//成交后撤单
					params.put("orderType", "ioc");
				}
			}
			if (orderModel.getOrderType() != Constants.MARKET_PRICE) {//市价不传
				params.put("orderPrice", orderModel.getPrice());
			}
			params.put("quantity", orderModel.getNumber());
			//方向closeLong(平多),closeShort(平空)
			params.put("direction", orderModel.getTradeType() == Constants.TRADE_CLOSE_LONG ? "closeLong" : "closeShort");
//			long startTime = System.currentTimeMillis();
			OkGo.<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>post(Constants.TRADE_CLOSE_ORDER).params(params).tag(this).execute(new DialogCallback<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>(activity.get()) {
				@Override
				public void onSuc(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {
					//平仓之后  需要拿到当前使用杠杆   因为有可能全部平仓没有持仓了   用户恢复选择杠杆
					ToastUtil.show(R.string.buyorsell_success);
					mContractView.placeOrderSucces();
					DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
							new DataCollectionModel(response.body().getData().getOrderId(),
									DataCollectionModel.CONTRACT,
									(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));
				}

				@Override
				public void onE(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {

				}

			});
		}

	}

	private void placeOrderUsdt(ContractPlaceOrderParmsModel orderModel) {
		//开仓
		if (orderModel.getTradeType() == Constants.TRADE_OPEN_LONG || orderModel.getTradeType() == Constants.TRADE_OPEN_SHORT) {
			HttpParams params = new HttpParams();
			params.put("symbol", orderModel.getSymbol());
			//订单类型，limit，限价；market，市价
			if (orderModel.getOrderType() == Constants.FIXED_PRICE) {//限价
				params.put("orderType", "limit");
				if (!BigDecimalUtils.isEmptyOrZero(orderModel.getProfitPrice())) {
					params.put("takeProfitPrice", orderModel.getProfitPrice());
				}
				if (!BigDecimalUtils.isEmptyOrZero(orderModel.getLossPrice())) {
					params.put("stopLosePrice", orderModel.getLossPrice());
				}
			} else if (orderModel.getOrderType() == Constants.MARKET_PRICE) {//市价
				params.put("orderType", "market");
			} else if (orderModel.getOrderType() == Constants.FIXED_PRICE_HIGH_LEVER) {//高级委托
				if (orderModel.getHighLeverOrderType() == Constants.ONLY_MAKER) {//只做marker
					params.put("orderType", "postOnly");
				} else if (orderModel.getHighLeverOrderType() == Constants.ALL_DEAL_OR_ALL_CANCEL) {//全部成交或全部撤单
					params.put("orderType", "fok");
				} else if (orderModel.getHighLeverOrderType() == Constants.DEAL_CANCEL_SURPLUS) {//成交后撤单
					params.put("orderType", "ioc");
				}
			}

			if (!BigDecimalUtils.isEmptyOrZero(orderModel.getProfitPrice())) {
				params.put("takeProfitPrice", orderModel.getProfitPrice());
			}
			if (!BigDecimalUtils.isEmptyOrZero(orderModel.getLossPrice())) {
				params.put("stopLosePrice", orderModel.getLossPrice());
			}
			params.put("leverage", orderModel.getLever());
			if (!BigDecimalUtils.isEmptyOrZero(orderModel.getPrice()))
				params.put("orderPrice", orderModel.getPrice());
			params.put("quantity", orderModel.getRealNumber());
			params.put("marginMode", orderModel.getMarginMode());

			//方向 openLong(开多)，openShort开空
			params.put("direction", orderModel.getTradeType() == Constants.TRADE_OPEN_LONG ? "openLong" : "openShort");


//			https://a.coinbene.vip/api/contract-trade-api/trade/placeorder
//			long startTime = System.currentTimeMillis();
			OkGo.<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>post(Constants.TRADE_PLACE_ORDER_USDT).params(params).tag(this).execute(new DialogCallback<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>(activity.get()) {
				@Override
				public void onSuc(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {

//					DLog.d("httpTime", "placeOrder   ------>    startTime - endTime = " + (System.currentTimeMillis() - startTime));

					ToastUtil.show(R.string.buyorsell_success);
					mContractView.placeOrderSucces();
					if (!TextUtils.isEmpty(response.body().getData().getOrderId()))
						DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
								new DataCollectionModel(response.body().getData().getOrderId(),
										DataCollectionModel.CONTRACT,
										(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));
				}

				@Override
				public void onE(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {

				}

			});
		}
		//平仓
		else {
			HttpParams params = new HttpParams();
			params.put("symbol", orderModel.getSymbol());
			//订单类型，limit，限价；market，市价
			if (orderModel.getOrderType() == Constants.FIXED_PRICE) {//限价
				params.put("orderType", "limit");
			} else if (orderModel.getOrderType() == Constants.MARKET_PRICE) {//市价
				params.put("orderType", "market");
			} else if (orderModel.getOrderType() == Constants.FIXED_PRICE_HIGH_LEVER) {//高级委托
				if (orderModel.getHighLeverOrderType() == Constants.ONLY_MAKER) {//只做marker
					params.put("orderType", "postOnly");
				} else if (orderModel.getHighLeverOrderType() == Constants.ALL_DEAL_OR_ALL_CANCEL) {//全部成交或全部撤单
					params.put("orderType", "fok");
				} else if (orderModel.getHighLeverOrderType() == Constants.DEAL_CANCEL_SURPLUS) {//成交后撤单
					params.put("orderType", "ioc");
				}
			}
			if (orderModel.getHighLeverOrderType() != Constants.MARKET_PRICE) {//市价不传
				params.put("orderPrice", orderModel.getPrice());
			}
			params.put("quantity", orderModel.getRealNumber());
			//方向closeLong(平多),closeShort(平空)
			params.put("direction", orderModel.getTradeType() == Constants.TRADE_CLOSE_LONG ? "closeLong" : "closeShort");
			long startTime = System.currentTimeMillis();
			OkGo.<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>post(Constants.TRADE_CLOSE_ORDER_USDT).params(params).tag(this).execute(new DialogCallback<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel>(activity.get()) {
				@Override
				public void onSuc(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {
					//平仓之后  需要拿到当前使用杠杆   因为有可能全部平仓没有持仓了   用户恢复选择杠杆
					ToastUtil.show(R.string.buyorsell_success);
					mContractView.placeOrderSucces();
					DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
							new DataCollectionModel(response.body().getData().getOrderId(),
									DataCollectionModel.CONTRACT,
									(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));

				}

				@Override
				public void onE(Response<com.coinbene.manbiwang.model.http.ContractPlaceOrderModel> response) {

				}

			});
		}
	}


	@Override
	public int getPricePresition() {
		if (isUsdtContract() && usdtTable != null) {
			return usdtTable.precision;
		} else if (btcTable != null) {
			return btcTable.precision;
		}
		return 0;
	}

	@Override
	public String getMinPriceChange() {
		if (isUsdtContract() && usdtTable != null) {
			return usdtTable.minPriceChange;
		} else if (btcTable != null) {
			return btcTable.minPriceChange;
		}
		return "";
	}

	@Override
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		contractConfigTable = ContractConfigController.getInstance().queryContrackConfig();
		if (TradeUtils.isUsdtContract(symbol)) {
			contractType = ContractType.USDT;
			usdtTable = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
		} else {
			contractType = ContractType.BTC;
			btcTable = ContractInfoController.getInstance().queryContrackByName(symbol);
		}
		getAllData();
	}

	@Override
	public String getQouteAsset() {
		if (isUsdtContract() && usdtTable != null) {
			return usdtTable.quoteAsset;
		} else if (btcTable != null) {
			return btcTable.quoteAsset;
		}
		return "";
	}

	@Override
	public String getContractUnit() {
		if (isUsdtContract())
			return TradeUtils.getContractUsdtUnit(usdtTable);
		return CBRepository.getContext().getString(R.string.number);
	}

	@Override
	public String getContractMinQuantityChange() {
		if (contractType == ContractType.USDT)
			return TradeUtils.getContractUsdtMultiplier(usdtTable);

		return "1";
	}

	private boolean isUsdtContract() {
		return contractType == ContractType.USDT;
	}


	private String getPriceMultiplier() {
		if (isUsdtContract() && usdtTable != null) {
			return usdtTable.costPriceMultiplier;
		} else if (btcTable != null) {
			return btcTable.costPriceMultiplier;
		}
		return "0";
	}

	private String getMultiplier() {
		if (isUsdtContract() && usdtTable != null) {
			return usdtTable.multiplier;
		} else if (btcTable != null) {
			return btcTable.multiplier;
		}
		return "";
	}


	@AddFlowControl(timeInterval = 1000)
	@Override
	public void calculationAvlOpen(PriceParamsModel priceParamsModel, CalAvlPositionModel calAvlPositionModel) {
		calculationSingerAvlOpen(priceParamsModel, calAvlPositionModel);
	}

	@NeedLogin
	@Override
	public void calculationSingerAvlOpen(PriceParamsModel priceParamsModel, CalAvlPositionModel calAvlPositionModel) {
		mThreadPool.execute(() -> {
//			DLog.e("priceParamsModel", priceParamsModel.getBuyOnePrice() + "===" + priceParamsModel.getSellOnePrice() + "===" + priceParamsModel.getMarkPrice() + "===" + priceParamsModel.getLastPrice() + "===" + priceParamsModel.getSymbol());
//			DLog.e("priceParamsModel", calAvlPositionModel.getAvlBalance() + "===" + calAvlPositionModel.getInputPrice() + "===" + calAvlPositionModel.getCurLever() + "===" + calAvlPositionModel.getCurOrderType() + "===" + calAvlPositionModel.getCurrentDirection());

			String[] avlOpenAccount = new String[2];
			avlOpenAccount[0] = "0";
			avlOpenAccount[1] = "0";
			if (calAvlPositionModel.getCurrentDirection() != Constants.INDEX_OPEN || BigDecimalUtils.isEmptyOrZero(calAvlPositionModel.getAvlBalance()) || calAvlPositionModel.getCurLever() == 0) {
//				DLog.e("priceParamsModel", "calAvlPositionModel.getCurrentDirection() != Constants.INDEX_OPEN=" + (calAvlPositionModel.getCurrentDirection() != Constants.INDEX_OPEN));
//				DLog.e("priceParamsModel", "BigDecimalUtils.isEmptyOrZero(calAvlPositionModel.getAvlBalance()=" + (BigDecimalUtils.isEmptyOrZero(calAvlPositionModel.getAvlBalance())));
//				DLog.e("priceParamsModel", " calAvlPositionModel.getCurLever()=" + (calAvlPositionModel.getCurLever() == 0));

				mContractView.setAvlOpenAccout(avlOpenAccount);
				return;
			}

			if (isUsdtContract()) {
				avlOpenAccount = CalculationUtils.calculationOther(calAvlPositionModel.getCurOrderType(), calAvlPositionModel.getInputPrice(),
						priceParamsModel.getBuyOnePrice(),
						priceParamsModel.getSellOnePrice(),
						priceParamsModel.getLastPrice(),
						priceParamsModel.getMarkPrice(),
						getPriceMultiplier(),
						calAvlPositionModel.getAvlBalance(),
						calAvlPositionModel.getCurLever(),
						contractConfigTable.takerFeeRate,
						getMultiplier(),
						getPricePresition(),
						getMinPriceChange());


				//大于等于 999999999则显示加号
				if (BigDecimalUtils.isThanOrEqual(avlOpenAccount[0], CalculationUtils.maxAvlOpenAmount)) {
					avlOpenAccount[0] = CalculationUtils.maxAvlOpenAmount.toPlainString();
				}
				if (BigDecimalUtils.isThanOrEqual(avlOpenAccount[1], CalculationUtils.maxAvlOpenAmount)) {
					avlOpenAccount[1] = CalculationUtils.maxAvlOpenAmount.toPlainString();
				}


			} else {//BTC可开计算

				if (symbol.contains("BTC")) {
					avlOpenAccount = CalculationUtils.calculationBtc(calAvlPositionModel.getCurOrderType(),
							calAvlPositionModel.getInputPrice(),
							priceParamsModel.getBuyOnePrice(),
							priceParamsModel.getSellOnePrice(),
							priceParamsModel.getLastPrice(),
							priceParamsModel.getMarkPrice(),
							getPriceMultiplier(),
							calAvlPositionModel.getAvlBalance(),
							calAvlPositionModel.getCurLever(),
							contractConfigTable.takerFeeRate,
							getPricePresition(),
							getMinPriceChange());
				} else {//其他可开计算
					avlOpenAccount = CalculationUtils.calculationOther(calAvlPositionModel.getCurOrderType(),
							calAvlPositionModel.getInputPrice(),
							priceParamsModel.getBuyOnePrice(),
							priceParamsModel.getSellOnePrice(),
							priceParamsModel.getLastPrice(),
							priceParamsModel.getMarkPrice(),
							getPriceMultiplier(),
							calAvlPositionModel.getAvlBalance(),
							calAvlPositionModel.getCurLever(),
							contractConfigTable.takerFeeRate,
							getMultiplier(),
							getPricePresition(),
							getMinPriceChange());
				}
				//大于等于 999999999则显示加号
				if (BigDecimalUtils.isThanOrEqual(avlOpenAccount[0], CalculationUtils.maxAvlOpenAmount)) {
					avlOpenAccount[0] = CalculationUtils.maxAvlOpenAmount.toPlainString();
				}
				if (BigDecimalUtils.isThanOrEqual(avlOpenAccount[1], CalculationUtils.maxAvlOpenAmount)) {
					avlOpenAccount[1] = CalculationUtils.maxAvlOpenAmount.toPlainString();
				}
			}


			mContractView.setAvlOpenAccout(avlOpenAccount);
		});
	}


	@AddFlowControl(timeInterval = 3000)
	@Override
	public void getCurLeverage() {
		getSingerCurLeverage();
	}

	@NeedLogin
	private void getSingerCurLeverage() {
		String url = isUsdtContract() ? Constants.TRADE_CUR_LEVERAGE_USDT : Constants.TRADE_CUR_LEVERAGE;
		OkGo.<ContractLeverModel>get(url).tag(this).params("symbol", symbol).execute(new NewJsonSubCallBack<ContractLeverModel>() {
			@Override
			public void onSuc(Response<ContractLeverModel> response) {
				ContractLeverModel.DataBean data = response.body().getData();

				if (data != null) {
					mContractView.setCurLeverData(data.getCurLeverage());
					mContractView.setMarginMode(data);
				}

			}

			@Override
			public void onE(Response<ContractLeverModel> response) {
				getCurLeverage();
				mContractView.setCurLeverData(getCecheLever());
			}
		});
	}

	@AddFlowControl(timeInterval = 3000)
	@Override
	public void getAccountInfo() {
//		DLog.e("newContractPresenter", "getAccountInfo");
		getSingAccountInfo();
	}

	@AddFlowControl(timeInterval = 200)
	@NeedLogin
	public void getSingAccountInfo() {
//		DLog.e("newContractPresenter", "getSingAccountInfo");

		String url = isUsdtContract() ? Constants.CONTRACT_ACCOUNT_INFO_USDT : Constants.CONTRACT_ACCOUNT_INFO;
		OkGo.<ContractAccountInfoModel>get(url).tag(this).params("symbol", symbol).execute(new NewJsonSubCallBack<ContractAccountInfoModel>() {
			@Override
			public void onSuc(Response<ContractAccountInfoModel> response) {
				if (response.body().getData() != null) {
					mContractView.setContractAccountInfo(response.body().getData());
				}
			}

			@Override
			public void onE(Response<ContractAccountInfoModel> response) {
			}
		});
	}


	@AddFlowControl(timeInterval = 3000)
	@Override
	public void getCurrentOrderList() {
		getSingerCurrentOrderList();
	}

	@AddFlowControl(timeInterval = 200)
	@NeedLogin
	public void getSingerCurrentOrderList() {
		String url = isUsdtContract() ? Constants.CONTRACT_CURRENT_DELEGATION_USDT : Constants.CONTRACT_CURRENT_DELEGATION;

		HttpParams params = new HttpParams();
		params.put("pageNum", 1);
		params.put("pageSize", 5);
		OkGo.<CurrentDelegationModel>get(url).params(params).tag(this).execute(new NewJsonSubCallBack<CurrentDelegationModel>() {
			@Override
			public void onSuc(Response<CurrentDelegationModel> response) {
				if (response.body().getData() != null) {
					mContractView.setCurrentOrderData(response.body().getData().getTotal());
				}
			}

			@Override
			public void onE(Response<CurrentDelegationModel> response) {
			}
		});
	}

	@Override
	public void cancelAll() {
		OkGo.getInstance().cancelTag(this);
	}

	@Override
	public void updateMarginMode(String mode) {
		String url = isUsdtContract() ? Constants.CONTRACT_POSTIONMODE_CHANGE_USDT : Constants.CONTRACT_POSTIONMODE_CHANGE;

		HttpParams params = new HttpParams();
		params.put("symbol", symbol);
		params.put("marginMode", mode);
		OkGo.<BaseRes>post(url).tag(this).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				mContractView.updateModeSuccess(mode);
				ToastUtil.show(R.string.finger_set_success);
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	@Override
	public String getPlaceOrderQuantity(String text) {
		if (isUsdtContract()) {
			return TradeUtils.getContractUsdtDialogShowValue(text, usdtTable);
		} else {
			return text;
		}

	}


	@AddFlowControl
	@Override
	public void calBond(PriceParamsModel priceParamsModel, CalAvlPositionModel calAvlPositionModel, String quantity) {
		mThreadPool.execute(() -> {
//			DLog.e("priceParamsModel", priceParamsModel.getBuyOnePrice() + "===" + priceParamsModel.getSellOnePrice() + "===" + priceParamsModel.getMarkPrice() + "===" + priceParamsModel.getLastPrice() + "===" + priceParamsModel.getSymbol());
//			DLog.e("priceParamsModel", calAvlPositionModel.getAvlBalance() + "===" + calAvlPositionModel.getInputPrice() + "===" + calAvlPositionModel.getCurLever() + "===" + calAvlPositionModel.getCurOrderType() + "===" + calAvlPositionModel.getCurrentDirection());

			String[] strings = new String[2];
			strings[0] = "0";
			strings[1] = "0";

			if (isUsdtContract()) {
				strings = CalculationUtils.calculationUsdtBond(calAvlPositionModel.getCurOrderType(), calAvlPositionModel.getInputPrice(),
						priceParamsModel.getBuyOnePrice(),
						priceParamsModel.getSellOnePrice(),
						priceParamsModel.getLastPrice(),
						priceParamsModel.getMarkPrice(),
						getPriceMultiplier(),
						calAvlPositionModel.getAvlBalance(),
						calAvlPositionModel.getCurLever(),
						contractConfigTable.takerFeeRate,
						getMultiplier(),
						getPricePresition(),
						getMinPriceChange(),
						quantity);
				strings[0]=String.format("%s  USDT", strings[0]);
				strings[1]=String.format("%s  USDT", strings[1]);

			} else {
				strings = CalculationUtils.calculationBtcBond(calAvlPositionModel.getCurOrderType(),
						calAvlPositionModel.getInputPrice(),
						priceParamsModel.getBuyOnePrice(),
						priceParamsModel.getSellOnePrice(),
						priceParamsModel.getLastPrice(),
						priceParamsModel.getMarkPrice(),
						getPriceMultiplier(),
						calAvlPositionModel.getAvlBalance(),
						calAvlPositionModel.getCurLever(),
						contractConfigTable.takerFeeRate,
						getPricePresition(),
						getMinPriceChange(),
						quantity);
				strings[0] = String.format("%s  BTC", strings[0]);
				strings[1] = String.format("%s  BTC", strings[1]);
			}
			mContractView.setBond(strings);
		});



	}


	@NeedLogin
	@Override
	public void calSingerBond(PriceParamsModel priceParamsModel, CalAvlPositionModel calAvlPositionModel, String quantity) {
		mThreadPool.execute(() -> {
//			DLog.e("priceParamsModel", priceParamsModel.getBuyOnePrice() + "===" + priceParamsModel.getSellOnePrice() + "===" + priceParamsModel.getMarkPrice() + "===" + priceParamsModel.getLastPrice() + "===" + priceParamsModel.getSymbol());
//			DLog.e("priceParamsModel", calAvlPositionModel.getAvlBalance() + "===" + calAvlPositionModel.getInputPrice() + "===" + calAvlPositionModel.getCurLever() + "===" + calAvlPositionModel.getCurOrderType() + "===" + calAvlPositionModel.getCurrentDirection());

			String[] strings = new String[2];
			strings[0] = "0";
			strings[1] = "0";

			if (isUsdtContract()) {
				strings = CalculationUtils.calculationUsdtBond(calAvlPositionModel.getCurOrderType(), calAvlPositionModel.getInputPrice(),
						priceParamsModel.getBuyOnePrice(),
						priceParamsModel.getSellOnePrice(),
						priceParamsModel.getLastPrice(),
						priceParamsModel.getMarkPrice(),
						getPriceMultiplier(),
						calAvlPositionModel.getAvlBalance(),
						calAvlPositionModel.getCurLever(),
						contractConfigTable.takerFeeRate,
						getMultiplier(),
						getPricePresition(),
						getMinPriceChange(),
						quantity);
				strings[0]=String.format("%s  USDT", strings[0]);
				strings[1]=String.format("%s  USDT", strings[1]);

			} else {
				strings = CalculationUtils.calculationBtcBond(calAvlPositionModel.getCurOrderType(),
						calAvlPositionModel.getInputPrice(),
						priceParamsModel.getBuyOnePrice(),
						priceParamsModel.getSellOnePrice(),
						priceParamsModel.getLastPrice(),
						priceParamsModel.getMarkPrice(),
						getPriceMultiplier(),
						calAvlPositionModel.getAvlBalance(),
						calAvlPositionModel.getCurLever(),
						contractConfigTable.takerFeeRate,
						getPricePresition(),
						getMinPriceChange(),
						quantity);
				strings[0] = String.format("%s  BTC", strings[0]);
				strings[1] = String.format("%s  BTC", strings[1]);
			}
			mContractView.setBond(strings);
		});



	}


	@Override
	public void onDestory() {
		unSubAll();
		userEventListener = null;
		mThreadPool.shutdown();
		mThreadPool = null;
		activity = null;
		mContractView = null;
		OkGo.getInstance().cancelTag(this);

	}

	public String getRealNumber(String text) {
		if (isUsdtContract()) {
			return TradeUtils.getContractUsdtPlaceOrderValue(text, usdtTable);
		} else {
			return text;
		}
	}
}
