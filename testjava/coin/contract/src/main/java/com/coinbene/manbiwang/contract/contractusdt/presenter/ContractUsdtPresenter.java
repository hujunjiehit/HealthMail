package com.coinbene.manbiwang.contract.contractusdt.presenter;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.OrderListSortUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.bean.UserEventEnum;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;
import com.coinbene.manbiwang.model.http.ContractLeverModel;
import com.coinbene.manbiwang.model.http.ContractPlaceOrderModel;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.coinbene.manbiwang.model.http.FundingTimeModel;
import com.coinbene.manbiwang.model.http.KlineDealMedel;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.model.http.UserConfigModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.coinbene.manbiwang.model.websocket.WsContractQouteResponse;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 开仓
 * • 当前订单 （立即刷新1次）
 * • 可用余额（立即刷新1次）
 * • 最大开仓数量 （立即刷新1次）
 * • 当前杠杆 （立即刷新1次）
 * • 当前持仓（延迟刷新1次）
 * <p>
 * 平仓委托
 * • 刷新 当前订单 （立即刷新1次）
 * • 刷新 可用余额 （延迟刷新1次）
 * • 刷新 最大开仓数量 （延迟刷新1次）
 * • 刷新 当前杠杆 （延迟刷新1次）
 * • 刷新 当前持仓（延迟刷新1次）
 * • 刷新 历史委托（延迟刷新1次）
 * <p>
 * 撤单
 * • 刷新 当前订单 （立即刷新一次，如果用户多次点击撤单，提示用户正在撤单中或者操作繁忙，请稍后重试）
 * • 刷新 可用余额 （延迟刷新1次）
 * • 刷新 最大开仓数量（延迟刷新1次）
 * • 刷新 当前杠杆  （延迟刷新1次）
 * • 刷新 历史委托  （延迟刷新1次）
 * <p>
 * 划转
 * • 刷新 可用余额（立即刷新一次）
 * • 刷新 最大开仓数量（立即刷新一次）
 * • 刷新 当前持仓 （立即刷新一次）
 * <p>
 * 备注
 * - 其中刷新最大开仓数量需要在拿到最新可用余额再去计算
 */
public class ContractUsdtPresenter implements ContractInterface.Presenter {

	private ContractInterface.View mContractView;
	private String contractName;
	private OrderListMapsModel orderListMapsModel;
	private OrderListSortUtil sortUtil;
	private OrderLineItemModel localPriceModel;
	private boolean isSub;
	//    private List<KlineDealMedel.DataBean> tradeDetailList;
	private Map<String, KlineDealMedel.DataBean> tradeDetailMap;
	private Gson gson;
	private int currentOrderType = Constants.DISPLAY_ORDER_LIST;
	private Map<String, WsContractQouteResponse.DataBean> contractMap;
	//延迟执行时间
	private long delayTime = 1000;
	private String curMarkPriceAppend = "";//标记价   当标记价格变化的时候 需要刷新持仓和账户
	private String lastMarkPriceAppend = "";//标记价   当标记价格变化的时候 需要刷新持仓和账户

	private NewContractUsdtWebsocket.OrderbookDataListener mOrderBookDataListener;
	private NewContractUsdtWebsocket.TradeListDataListener mTradeDetailListener;

	private WsMarketData mTickerData;
	private OrderbookModel mOrderbookModel;

	UsereventWebsocket.UserEventListener userEventListener;

	public ContractUsdtPresenter(ContractInterface.View view, String contractName) {
		this.mContractView = view;
		mContractView.setPresenter(this);
		gson = new Gson();
		this.contractName = contractName;
	}

	@Override
	public void subAll() {
		if (isSub) {
			return;
		}
		isSub = true;

		if (mOrderBookDataListener == null) {
			mOrderBookDataListener = new NewContractUsdtWebsocket.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {
					mOrderbookModel = orderbookModel;
					onOrderbookDataArrived();
				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {
					mTickerData = marketData;
					onOrderbookDataArrived();
					if (mContractView != null) {
						mContractView.setQuoData(marketData);
					}
					markChangeFreshData();
				}
			};
		}

		if (mTradeDetailListener == null) {
			mTradeDetailListener = new NewContractUsdtWebsocket.TradeListDataListener() {
				@Override
				public void onDataArrived(List<WsTradeList> tradeLists, String symbol) {
					mContractView.setOrderDetail(tradeLists, symbol);
				}
			};
		}

		NewContractUsdtWebsocket.getInstance().changeSymbol(contractName);

		NewContractUsdtWebsocket.getInstance().registerOrderbookListener(mOrderBookDataListener);
		NewContractUsdtWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);

		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onAccountChanged() {
					getAccountInfo();
					getPositionlist();
				}

				@Override
				public void onPositionsChanged() {
					getAccountInfo();
					getPositionlist();
				}

				@Override
				public void onCurorderChanged() {
					getAccountInfo();
					getCurrentOrderList();
				}

				@Override
				public void onLiquidation() {
					getAccountInfo();
					getPositionlist();
					getCurrentOrderList();
				}
			};
		}
		UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.USDT, userEventListener);

		refreshAccountInfo();
	}

	private void onOrderbookDataArrived() {
		if (mTickerData == null || mOrderbookModel == null) {
			return;
		}
		mContractView.setOrderListData(
				orderListMapToList(mOrderbookModel.getBuyMap(), false),
				orderListMapToList(mOrderbookModel.getSellMap(), true),
				mTickerData,
				contractName);
	}


	@Override
	public void refreshAccountInfo() {
		initOrderData();
		if (CommonUtil.isLoginAndUnLocked()) {
			getCurLeverage();
			getContractAccountInfo();
			getPositionlist();
			getAllPositionList();
			getCurrentOrderList();
		}
	}

	@Override
	public void unsubAll() {
		if (!isSub) {
			return;
		}
		isSub = false;

		UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.USDT, userEventListener);

		NewContractUsdtWebsocket.getInstance().unregisterOrderbookListener(mOrderBookDataListener);
		NewContractUsdtWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void openContract() {
		//flag：1打开，0关闭（用不上）
		OkGo.<BaseRes>post(Constants.OPEN_USDT_CONTRACT).params("flag", "1").tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				if (response.isSuccessful()) {
					SpUtil.setContractStatus(CBRepository.getContext(), SpUtil.USDT_CONTRACT_STATUS, true);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	@Override
	public void agreeProtocol(String key, String value) {
		HttpParams params = new HttpParams();
		params.put("settingKey", key);
		params.put("settingValue", value);

		OkGo.<UserConfigModel>post(Constants.UPDATE_USER_CONFIG).params(params).tag(this).execute(new NewJsonSubCallBack<UserConfigModel>() {
			@Override
			public void onSuc(Response<UserConfigModel> response) {
				switch (key) {
					case "usdtContract_protocol":
						SpUtil.setProtocolStatusForContract("usdt", true);
						break;
					case "usdtContract_tradeUnit":
						SpUtil.setContractUsdtUnitSwitch(Integer.valueOf(value));
						ServiceRepo.getContractService().totalListenerUpdateUnit();
						NewContractUsdtWebsocket.getInstance().pullOrderListData();
						NewContractUsdtWebsocket.getInstance().pullTradeDetailData();

				}

			}

			@Override
			public void onE(Response<UserConfigModel> response) {

			}
		});
	}

	public void setContractName(String contrackName) {
		this.contractName = contrackName;
	}

	@Override
	public void setOrderType(int orderType) {
		this.currentOrderType = orderType;

		//切换的时候拉取一次全量数据
		if (orderType == Constants.DISPLAY_ORDER_LIST) {
			NewContractUsdtWebsocket.getInstance().pullOrderListData(contractName);
		} else {
			NewContractUsdtWebsocket.getInstance().pullTradeDetailData(contractName);
		}

	}

	@Override
	public void getFundingTime() {
		OkGo.<FundingTimeModel>get(Constants.MARKET_CONTRACT_FUNDING_TIME_USDT).tag(this).execute(new NewJsonSubCallBack<FundingTimeModel>() {
			@Override
			public void onSuc(Response<FundingTimeModel> response) {
				if (response.body().getData() != null) {
					mContractView.setSettlement(response.body().getData().getFundingTime() - response.body().getData().getCurrentTime());
				}

			}

			@Override
			public void onE(Response<FundingTimeModel> response) {
			}


		});
	}

	@AddFlowControl
	@Override
	public void getCurLeverage() {
		OkGo.<ContractLeverModel>get(Constants.TRADE_CUR_LEVERAGE_USDT).tag(this).params("symbol", contractName).execute(new NewJsonSubCallBack<ContractLeverModel>() {
			@Override
			public void onSuc(Response<ContractLeverModel> response) {
				ContractLeverModel.DataBean data = response.body().getData();

				if (data != null) {
					mContractView.setCurLeverData(data.getCurLeverage());
					mContractView.setMarginMode(data.getMarginMode(), data.getMarginModeSetting());
				}

			}

			@Override
			public void onE(Response<ContractLeverModel> response) {
				mContractView.setCurLeverData(0);
			}
		});
	}

	@Override
	public void cancelOrder(String orderId, Activity activity) {
		long startTime = System.currentTimeMillis();

		OkGo.<ContractPlaceOrderModel>post(Constants.TRADE_CANCEL_ORDER_USDT).tag(this).params("orderId", orderId).execute(new DialogCallback<ContractPlaceOrderModel>(activity) {
			@Override
			public void onSuc(Response<ContractPlaceOrderModel> response) {
//				DLog.d("httpTime", "cancelOrder   ------>    startTime - endTime = " + (System.currentTimeMillis() - startTime));
				if (response.body().isOrderCanceling()) {
					ToastUtil.show(R.string.order_canceling);
					getCurrentOrderList();
					return;
				}

				ToastUtil.show(R.string.cancel_entrust_success);
				DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appCancelOrder,
						new DataCollectionModel(response.body().getData().getOrderId(),
								DataCollectionModel.CONTRACT,
								(response.getRawResponse().receivedResponseAtMillis()-response.getRawResponse().sentRequestAtMillis())+""));

				eventClientPost(UserEventEnum.cancelOrder);


			}

			@Override
			public void onE(Response<ContractPlaceOrderModel> response) {
//                ToastUtil.show(R.string.cancel_order_fail);

			}


		});
	}

	@AddFlowControl
	@Override
	public void getPositionlist() {
		OkGo.<ContractPositionListModel>get(Constants.ACCOUNT_POSITION_LIST_USDT).tag(this).params("symbol", contractName).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
			@Override
			public void onSuc(Response<ContractPositionListModel> response) {
				if (response.body().getData() != null) {
					mContractView.setPisitionListData(response.body().getData());
				}
			}

			@Override
			public void onE(Response<ContractPositionListModel> response) {
			}
		});
	}

	@AddFlowControl
	@Override
	public void getAllPositionList() {
		OkGo.<ContractPositionListModel>get(Constants.ACCOUNT_POSITION_LIST_USDT).tag(this).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
			@Override
			public void onSuc(Response<ContractPositionListModel> response) {
				if (response.body().getData() != null) {
					mContractView.setAllPisitionListData(response.body().getData());
				}
			}

			@Override
			public void onE(Response<ContractPositionListModel> response) {
			}
		});
	}

	@AddFlowControl
	@Override
	public void getContractAccountInfo() {
		if (!CommonUtil.isLoginAndUnLocked()) {
			return;
		}
		OkGo.<ContractAccountInfoModel>get(Constants.CONTRACT_ACCOUNT_INFO_USDT).tag(this).params("symbol", contractName).execute(new NewJsonSubCallBack<ContractAccountInfoModel>() {
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

	@AddFlowControl
	@Override
	public void getCurrentOrderList() {
		HttpParams params = new HttpParams();
		params.put("pageNum", 1);
		params.put("pageSize", 10);
		OkGo.<CurrentDelegationModel>get(Constants.CONTRACT_CURRENT_DELEGATION_USDT).params(params).tag(this).execute(new NewJsonSubCallBack<CurrentDelegationModel>() {
			@Override
			public void onSuc(Response<CurrentDelegationModel> response) {
				if (response.body().getData() != null) {
					mContractView.setCurrentOrderData(response.body().getData().getList(), response.body().getData().getTotal());
				}
			}

			@Override
			public void onE(Response<CurrentDelegationModel> response) {
			}
		});
	}


	@Override
	public void setPositionMode(String symbol, String mode) {
		HttpParams params = new HttpParams();
		params.put("symbol", symbol);
		params.put("marginMode", mode);
		OkGo.<BaseRes>post(Constants.CONTRACT_POSTIONMODE_CHANGE_USDT).tag(this).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				mContractView.updateModeSuccess();
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}


	/**
	 * 交易  开仓  平仓
	 *
	 * @param currentTradeDirection 1;//买入开多2;//卖出开空 3;//买入平空4;//卖出平多
	 * @param fixPriceType          0限价   1 市价
	 * @param lever                 杠杆倍数
	 * @param price                 限价价格
	 * @param account               数量
	 */
	@Override
	public void placeOrder(int currentTradeDirection, int fixPriceType, int curHighLeverEntrust, int lever, String price, String account, String mode, String contractName,String profitPrice,String lossPrice, Activity activity) {


//		long startTime = System.currentTimeMillis();
		//开仓
		if (currentTradeDirection == Constants.TRADE_OPEN_LONG || currentTradeDirection == Constants.TRADE_OPEN_SHORT) {
			HttpParams params = new HttpParams();
			params.put("symbol", contractName);
			//订单类型，limit，限价；market，市价
			if (fixPriceType == Constants.FIXED_PRICE) {//限价
				params.put("orderType", "limit");
				if(!BigDecimalUtils.isEmptyOrZero(profitPrice)){
					params.put("takeProfitPrice", profitPrice);
				}
				if(!BigDecimalUtils.isEmptyOrZero(lossPrice)){
					params.put("stopLosePrice", lossPrice);
				}
			} else if (fixPriceType == Constants.MARKET_PRICE) {//市价
				params.put("orderType", "market");
			} else if (fixPriceType == Constants.FIXED_PRICE_HIGH_LEVER) {//高级委托
				if (curHighLeverEntrust == Constants.ONLY_MAKER) {//只做marker
					params.put("orderType", "postOnly");
				} else if (curHighLeverEntrust == Constants.ALL_DEAL_OR_ALL_CANCEL) {//全部成交或全部撤单
					params.put("orderType", "fok");
				} else if (curHighLeverEntrust == Constants.DEAL_CANCEL_SURPLUS) {//成交后撤单
					params.put("orderType", "ioc");
				}
			}

			if(!BigDecimalUtils.isEmptyOrZero(profitPrice)){
				params.put("takeProfitPrice", profitPrice);
			}
			if(!BigDecimalUtils.isEmptyOrZero(lossPrice)){
				params.put("stopLosePrice", lossPrice);
			}
			params.put("leverage", lever);
			if(!BigDecimalUtils.isEmptyOrZero(price))
				params.put("orderPrice", price);
			params.put("quantity", account);
			params.put("marginMode", mode);

			//方向 openLong(开多)，openShort开空
			params.put("direction", currentTradeDirection == Constants.TRADE_OPEN_LONG ? "openLong" : "openShort");


//			https://a.coinbene.vip/api/contract-trade-api/trade/placeorder
//			long startTime = System.currentTimeMillis();
			OkGo.<ContractPlaceOrderModel>post(Constants.TRADE_PLACE_ORDER_USDT).params(params).tag(this).execute(new DialogCallback<ContractPlaceOrderModel>(activity) {
				@Override
				public void onSuc(Response<ContractPlaceOrderModel> response) {

//					DLog.d("httpTime", "placeOrder   ------>    startTime - endTime = " + (System.currentTimeMillis() - startTime));

					ToastUtil.show(R.string.buyorsell_success);
					mContractView.placeOrderSucces();
					if (!TextUtils.isEmpty(response.body().getData().getOrderId()))
						DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
								new DataCollectionModel(response.body().getData().getOrderId(),
										DataCollectionModel.CONTRACT,
										(response.getRawResponse().receivedResponseAtMillis()-response.getRawResponse().sentRequestAtMillis())+""));
					eventClientPost(UserEventEnum.openOrder);
				}

				@Override
				public void onE(Response<ContractPlaceOrderModel> response) {

				}

			});
		}
		//平仓
		else {
			HttpParams params = new HttpParams();
			params.put("symbol", contractName);
			//订单类型，limit，限价；market，市价
			if (fixPriceType == Constants.FIXED_PRICE) {//限价
				params.put("orderType", "limit");
			} else if (fixPriceType == Constants.MARKET_PRICE) {//市价
				params.put("orderType", "market");
			} else if (fixPriceType == Constants.FIXED_PRICE_HIGH_LEVER) {//高级委托
				if (curHighLeverEntrust == Constants.ONLY_MAKER) {//只做marker
					params.put("orderType", "postOnly");
				} else if (curHighLeverEntrust == Constants.ALL_DEAL_OR_ALL_CANCEL) {//全部成交或全部撤单
					params.put("orderType", "fok");
				} else if (curHighLeverEntrust == Constants.DEAL_CANCEL_SURPLUS) {//成交后撤单
					params.put("orderType", "ioc");
				}
			}
			if (fixPriceType != Constants.MARKET_PRICE) {//市价不传
				params.put("orderPrice", price);
			}
			params.put("quantity", account);
			//方向closeLong(平多),closeShort(平空)
			params.put("direction", currentTradeDirection == Constants.TRADE_CLOSE_LONG ? "closeLong" : "closeShort");
			long startTime = System.currentTimeMillis();
			OkGo.<ContractPlaceOrderModel>post(Constants.TRADE_CLOSE_ORDER_USDT).params(params).tag(this).execute(new DialogCallback<ContractPlaceOrderModel>(activity) {
				@Override
				public void onSuc(Response<ContractPlaceOrderModel> response) {
					//平仓之后  需要拿到当前使用杠杆   因为有可能全部平仓没有持仓了   用户恢复选择杠杆
					ToastUtil.show(R.string.buyorsell_success);
					mContractView.placeOrderSucces();
					DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
							new DataCollectionModel(response.body().getData().getOrderId(),
									DataCollectionModel.CONTRACT,
									(response.getRawResponse().receivedResponseAtMillis()-response.getRawResponse().sentRequestAtMillis())+""));

					eventClientPost(UserEventEnum.closeOrder);


				}

				@Override
				public void onE(Response<ContractPlaceOrderModel> response) {

				}

			});
		}
	}

	/**
	 * 标记价格变化  刷新 逻辑
	 */
	private void markChangeFreshData() {
		if (NewMarketWebsocket.getInstance().getContractMarketMap() == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (WsMarketData value : NewMarketWebsocket.getInstance().getContractMarketMap().values()) {
			if (value.getSymbol().contains("-")) {
				//usdt合约
				curMarkPriceAppend += value.getMarkPrice();
				sb.append(value.getMarkPrice());
			}
		}
		curMarkPriceAppend = sb.toString();
		if (!TextUtils.isEmpty(lastMarkPriceAppend) && !TextUtils.isEmpty(curMarkPriceAppend) && !lastMarkPriceAppend.equals(curMarkPriceAppend)) {
			if (ServiceRepo.getUserService().isLogin()) {
				eventClientPost(UserEventEnum.markPriceChange);
			}
		}
		lastMarkPriceAppend = curMarkPriceAppend;
	}


	/**
	 * 挂单map 转换为list
	 *
	 * @param map
	 * @return
	 */
	private synchronized List<OrderModel> orderListMapToList(ConcurrentHashMap<String, String> map, boolean isSell) {
		List<OrderModel> list = new ArrayList<>();

		if (map == null || map.size() == 0) {
			return list;
		}

		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
//			BigDecimal bigDecimal = new BigDecimal(entry.getValue());
//			if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
//				it.remove();
//				continue;
//			}
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

	private void initOrderData() {
		if (localPriceModel == null) {
			localPriceModel = new OrderLineItemModel();
		} else {
			localPriceModel.riseType = 0;
			localPriceModel.lastPrice = "";
		}
		if (orderListMapsModel == null) {
			orderListMapsModel = new OrderListMapsModel();
		}
		if (orderListMapsModel.getBuyMap() == null) {
			orderListMapsModel.setBuyMap(new ConcurrentHashMap<>());
		}
		if (orderListMapsModel.getSellMap() == null) {
			orderListMapsModel.setSellMap(new ConcurrentHashMap<>());
		}

		if (orderListMapsModel.getBuyMap().size() > 0) {
			orderListMapsModel.getBuyMap().clear();
		}

		if (orderListMapsModel.getSellMap().size() > 0) {
			orderListMapsModel.getSellMap().clear();

		}

		if (tradeDetailMap == null) {
			tradeDetailMap = new TreeMap<>((o1, o2) -> o2.compareTo(o1));
		}
//		if (tradeDetailMap.size() > 0) {
//			tradeDetailMap.clear();
//		}
	}

	/**
	 * client event serverEvent 事件 处理总和
	 *
	 * @param eventType
	 */
	private void eventClientPost(UserEventEnum eventType) {
		switch (eventType) {
			case cancelOrder://用户撤单
				getCurrentOrderList();
				new Handler().postDelayed(() -> {
					getContractAccountInfo();
					getCurLeverage();
//					getHistoryOrder();
				}, delayTime);
				break;
			case openOrder://用户开仓
				getCurrentOrderList();
				getContractAccountInfo();
				getCurLeverage();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getPositionlist();
						getAllPositionList();
					}
				},delayTime);
				break;
			case closeOrder://用户平仓
				getCurrentOrderList();
				new Handler().postDelayed(() -> {
					getContractAccountInfo();
					getCurLeverage();
					getPositionlist();
					getAllPositionList();
//					getHistoryOrder();
				}, delayTime);
				break;
			case markPriceChange://标记价格变化
				getContractAccountInfo();
				getPositionlist();
				break;
		}
	}

	private void getAccountInfo() {
		getContractAccountInfo();
		getCurLeverage();
	}

	@Override
	public void onDestory() {
		OkGo.getInstance().cancelTag(this);
	}
}
