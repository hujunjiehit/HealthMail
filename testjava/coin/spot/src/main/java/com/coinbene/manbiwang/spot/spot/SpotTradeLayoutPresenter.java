package com.coinbene.manbiwang.spot.spot;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.BalanceOneRes;
import com.coinbene.manbiwang.model.http.OrderRes;
import com.coinbene.manbiwang.model.http.SpotPlaceOrderModel;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.tradelayout.impl.TradeLayoutInterface;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

/**
 * Created by june
 * on 2019-12-02
 */
public class SpotTradeLayoutPresenter implements TradeLayoutInterface.Presenter {

	private TradeLayoutInterface.View mView;

	private Activity mActivity;

	private TradePairInfoTable table;
	private String baseAsset;
	private String qouteAsset;

	public SpotTradeLayoutPresenter(TradeLayoutInterface.View mView) {
		this.mView = mView;
	}

	@Override
	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	@Override
	public void placeOrder(SpotPlaceOrderModel spotPlaceOrderModel) {
		if (mActivity == null) {
			return;
		}

		//如果是IEO，先弹窗确认对话框，ok后再继续处理
		if (spotPlaceOrderModel.isBuy() && table.sellDisabled == 1) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
			String ieoLabel = mActivity.getString(R.string.tips_ieo_label);
			ieoLabel = String.format(ieoLabel, baseAsset);
			dialog.setMessage(ieoLabel);
			dialog.setCancelable(false);
			dialog.setPositiveButton(mActivity.getString(R.string.trade_buy_ok), (dialog1, which) -> {
				realPlaceOrder(spotPlaceOrderModel);
				dialog1.dismiss();
			});
			dialog.setNegativeButton(mActivity.getString(R.string.btn_cancel), (dialog12, which) -> {
						dialog12.dismiss();
						if (mView != null) {
							mView.onPlaceOrderResult(false, spotPlaceOrderModel.getOrderType());
						}
					}
			);
			dialog.show();
			return;
		}

		realPlaceOrder(spotPlaceOrderModel);
	}

	private void realPlaceOrder(SpotPlaceOrderModel spotPlaceOrderModel) {
		//限价市价下单
		if (spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE || spotPlaceOrderModel.getOrderType() == Constants.MARKET_PRICE) {
			tradeOrder(spotPlaceOrderModel);
		}
		//止盈止损
		else if (spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS || spotPlaceOrderModel.getOrderType() == Constants.MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS) {
			tradePlanOrder(spotPlaceOrderModel);
		}
		//oco
		else if (spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE_OCO || spotPlaceOrderModel.getOrderType() == Constants.MARKET_PRICE_OCO) {
			tradeOcoOrder(spotPlaceOrderModel);
		}
	}

	private void tradeOcoOrder(SpotPlaceOrderModel spotPlaceOrderModel) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", "1");
		httpParams.put("tradePairName", spotPlaceOrderModel.getSymbol());
		httpParams.put("orderDirection", spotPlaceOrderModel.isBuy() ? 1 : 2);//委托方向 1:买入 2:卖出
		httpParams.put("orderType", spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE_OCO ? 6 : 7);//委托类型 6:OCO限价 7:OCO市价
		httpParams.put("limitTriggerPrice", spotPlaceOrderModel.getOcoPrice());
		httpParams.put("triggerPrice", spotPlaceOrderModel.getTouchPrice());
		if (spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE_OCO) {//限价
			httpParams.put("orderPrice", spotPlaceOrderModel.getPrice());//委托价格
			httpParams.put("orderQuantity", spotPlaceOrderModel.getQuantity());//委托数量
		} else {//市价  买入的时候  价格传买的多少钱   数量传0  卖出的时候价格传0，数量传卖出的资产
			httpParams.put("notional", spotPlaceOrderModel.isBuy() ? spotPlaceOrderModel.getQuantity() : "0");
			httpParams.put("orderPrice", spotPlaceOrderModel.isBuy() ? "0" : "0");
			httpParams.put("orderQuantity", spotPlaceOrderModel.isBuy() ? "0" : spotPlaceOrderModel.getQuantity());//委托数量
		}
		long startTime = System.currentTimeMillis();
		OkGo.<OrderRes>post(Constants.TRADE_OCO_ORDER).tag(this).params(httpParams).execute(new DialogCallback<OrderRes>(mActivity) {

			@Override
			public void onSuc(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(true, spotPlaceOrderModel.getOrderType());
					if (!TextUtils.isEmpty(response.body().getData()))
						DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
								new DataCollectionModel(response.body().getData(),
										DataCollectionModel.SPOT,
										(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));

				}

				if (spotPlaceOrderModel.isBuy()) {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_buy_success);
				} else {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_sell_success);
				}

//				getBanlance();
			}

			@Override
			public void onE(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(false, spotPlaceOrderModel.getOrderType());
				}
			}
		});
	}

	private void tradePlanOrder(SpotPlaceOrderModel spotPlaceOrderModel) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", "1");
		httpParams.put("tradePairName", spotPlaceOrderModel.getSymbol());
		httpParams.put("orderDirection", spotPlaceOrderModel.isBuy() ? 1 : 2);//委托方向 1:买入 2:卖出
		httpParams.put("orderType", spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS ? 4 : 5);//委托类型 4:止盈止损限价 5:止盈止损市价
		httpParams.put("triggerPrice", spotPlaceOrderModel.getTouchPrice());
		if (spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS) {//限价
			httpParams.put("orderPrice", spotPlaceOrderModel.getPrice());//委托价格
			httpParams.put("orderQuantity", spotPlaceOrderModel.getQuantity());//委托数量
		} else {//市价  买入的时候  价格传买的多少钱   数量传0  卖出的时候价格传0，数量传卖出的资产
			httpParams.put("notional", spotPlaceOrderModel.isBuy() ? spotPlaceOrderModel.getQuantity() : "0");
			httpParams.put("orderPrice", spotPlaceOrderModel.isBuy() ? "0" : "0");
			httpParams.put("orderQuantity", spotPlaceOrderModel.isBuy() ? "0" : spotPlaceOrderModel.getQuantity());//委托数量
		}
		long startTime = System.currentTimeMillis();
		OkGo.<OrderRes>post(Constants.TRADE_PLAN_ORDER).tag(this).params(httpParams).execute(new DialogCallback<OrderRes>(mActivity) {

			@Override
			public void onSuc(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(true, spotPlaceOrderModel.getOrderType());
					if (!TextUtils.isEmpty(response.body().getData()))
						DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
								new DataCollectionModel(response.body().getData(),
										DataCollectionModel.SPOT,
										(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));

				}

				if (spotPlaceOrderModel.isBuy()) {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_buy_success);
				} else {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_sell_success);
				}

//				getBanlance();
			}

			@Override
			public void onE(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(false, spotPlaceOrderModel.getOrderType());
				}
			}
		});
	}

	private void tradeOrder(SpotPlaceOrderModel spotPlaceOrderModel) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", "1");
		httpParams.put("tradePairName", spotPlaceOrderModel.getSymbol());
		httpParams.put("orderDirection", spotPlaceOrderModel.isBuy() ? 1 : 2);//委托方向 1:买入 2:卖出
		httpParams.put("orderType", spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE ? 1 : 2);//委托类型 1:限价 2:市价

		if (spotPlaceOrderModel.getOrderType() == Constants.FIXED_PRICE) {//限价
			httpParams.put("orderPrice", spotPlaceOrderModel.getPrice());//委托价格
			httpParams.put("orderQuantity", spotPlaceOrderModel.getQuantity());//委托数量
		} else {//市价  买入的时候  价格传买的多少钱   数量传0  卖出的时候价格传0，数量传卖出的资产
			httpParams.put("notional", spotPlaceOrderModel.isBuy() ? spotPlaceOrderModel.getQuantity() : "0");
			httpParams.put("orderPrice", spotPlaceOrderModel.isBuy() ? "0" : "0");
			httpParams.put("orderQuantity", spotPlaceOrderModel.isBuy() ? "0" : spotPlaceOrderModel.getQuantity());//委托数量
		}
		long startTime = System.currentTimeMillis();

		OkGo.<OrderRes>post(Constants.TRADE_ORDER).tag(this).params(httpParams).execute(new DialogCallback<OrderRes>(mActivity) {

			@Override
			public void onSuc(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(true, spotPlaceOrderModel.getOrderType());
					if (!TextUtils.isEmpty(response.body().getData()))
						DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
								new DataCollectionModel(response.body().getData(), DataCollectionModel.SPOT,
										(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));

				}

				if (spotPlaceOrderModel.isBuy()) {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_buy_success);
				} else {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_sell_success);
				}

				getBanlance();
			}

			@Override
			public void onE(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(false, spotPlaceOrderModel.getOrderType());
				}
			}
		});
	}


	@AddFlowControl(strategy = FlowControlStrategy.throttleLast,timeInterval = 500)
	@Override
	public void getBanlance() {
		getBaseBalance();
		getQouteBalance();
	}

	private void getQouteBalance() {
		if (TextUtils.isEmpty(qouteAsset)) {
			return;
		}
		HttpParams params1 = new HttpParams();
		params1.put("asset", qouteAsset);
		params1.put("accountType", "1");

		OkGo.<BalanceOneRes>get(Constants.ACCOUNT_BALANCE_ONE).tag(this).params(params1).execute(new NewJsonSubCallBack<BalanceOneRes>() {

			@Override
			public void onSuc(Response<BalanceOneRes> response) {
			}

			@Override
			public BalanceOneRes dealJSONConvertedResult(BalanceOneRes balanceOneRes) {
				DLog.e("BalanceOneRes", "getQouteBalance=" + qouteAsset + balanceOneRes.getData().getAvailableBalance());
				if (balanceOneRes == null || balanceOneRes.getData() == null) {
					return null;
				}
				BalanceController.getInstance().updateBalancebyAsset(qouteAsset, balanceOneRes.getData().getAvailableBalance());
				return balanceOneRes;
			}

			@Override
			public void onE(Response<BalanceOneRes> response) {
			}

		});
	}

	private void getBaseBalance() {
		if (TextUtils.isEmpty(baseAsset)) {
			return;
		}
		HttpParams params = new HttpParams();
		params.put("asset", baseAsset);
		params.put("accountType", "1");

		OkGo.<BalanceOneRes>get(Constants.ACCOUNT_BALANCE_ONE).tag(this).params(params).execute(new NewJsonSubCallBack<BalanceOneRes>() {

			@Override
			public void onSuc(Response<BalanceOneRes> response) {
			}

			@Override
			public BalanceOneRes dealJSONConvertedResult(BalanceOneRes balanceOneRes) {
				DLog.e("BalanceOneRes", "getBaseBalance=" + baseAsset + balanceOneRes.getData().getAvailableBalance());
				if (balanceOneRes == null || balanceOneRes.getData() == null) {
					return null;
				}
				BalanceController.getInstance().updateBalancebyAsset(baseAsset, balanceOneRes.getData().getAvailableBalance());
				return balanceOneRes;
			}

			@Override
			public void onE(Response<BalanceOneRes> response) {
			}

		});
	}

	@Override
	public void setSymbol(String symbol) {
		table = TradePairInfoController.getInstance().queryDataByTradePair(symbol);
		String[] arrays = TradeUtils.parseSymbol(table.tradePairName);
		baseAsset = arrays[0];
		qouteAsset = arrays[1];
	}

	@Override
	public int getPricePrecision() {
		return table == null ? 0 : table.pricePrecision;
	}

	@Override
	public int getVolumePrecision() {
		return table == null ? 0 : table.volumePrecision;
	}

	@Override
	public String getTakeFee() {
		return table == null ? "" : table.takeFee;
	}

	@Override
	public String getMinVolume() {
		return table == null ? "" : table.minVolume;
	}

	@Override
	public String getPriceChangeScale() {
		return table == null ? "" : table.priceChangeScale;
	}

	@Override
	public void onDestory() {
		OkGo.getInstance().cancelTag(this);
	}
}
