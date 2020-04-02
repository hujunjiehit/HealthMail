package com.coinbene.manbiwang.spot.margin;

import android.app.Activity;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.manbiwang.model.http.MarginSingleAccountModel;
import com.coinbene.manbiwang.model.http.OrderRes;
import com.coinbene.manbiwang.model.http.SpotPlaceOrderModel;
import com.coinbene.manbiwang.spot.tradelayout.impl.TradeLayoutInterface;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by june
 * on 2019-12-07
 */
public class MarginTradeLayoutPresenter implements TradeLayoutInterface.Presenter {

	private TradeLayoutInterface.View mView;
	private Activity mActivity;

	private MarginSymbolTable table;

	public MarginTradeLayoutPresenter(TradeLayoutInterface.View mView) {
		this.mView = mView;
	}

	@Override
	public void placeOrder(SpotPlaceOrderModel placeOrderModel) {
		HttpParams params = new HttpParams();
		params.put("accountType", "margin");
		params.put("direction", placeOrderModel.isBuy() ? "buy" : "sell");
		params.put("orderType", "limit");
		params.put("price", placeOrderModel.getPrice());
		params.put("quantity", placeOrderModel.getQuantity());
		params.put("symbol", placeOrderModel.getSymbol());
		OkGo.<OrderRes>post(Constants.MARGIN_PLACE_ORDER).tag(this).params(params).execute(new DialogCallback<OrderRes>(mActivity) {

			@Override
			public void onSuc(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(true,placeOrderModel.getOrderType());
				}
				getBanlance();

			}

			@Override
			public void onE(Response<OrderRes> response) {
				if (mView != null) {
					mView.onPlaceOrderResult(false,placeOrderModel.getOrderType());
				}
			}
		});
	}

	@Override
	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	@AddFlowControl
	@Override
	public void getBanlance() {
		if (table == null || TextUtils.isEmpty(table.symbol)) {
			return;
		}
		OkGo.<MarginSingleAccountModel>get(Constants.GET_MARGIN_SINGLE_ACCOUNT).tag(this).params("symbol", table.symbol).execute(new NewJsonSubCallBack<MarginSingleAccountModel>() {

			@Override
			public void onSuc(Response<MarginSingleAccountModel> response) {
				MarginSingleAccountModel.DataBean data = response.body().getData();
				if (data != null) {
					//MarginFragment.onMarginBalance接收事件
					EventBus.getDefault().post(data);
				}
			}

			@Override
			public void onE(Response<MarginSingleAccountModel> response) {
			}
		});
	}


	@Override
	public void setSymbol(String symbol) {
		table = MarginSymbolController.getInstance().querySymbolByName(symbol);
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
		return table == null ? "" :table.takeFee;
	}

	@Override
	public String getMinVolume() {
		return table == null ? "" : table.minVolume;
	}

	@Override
	public String getPriceChangeScale() {
		return "";
	}

	@Override
	public void onDestory() {
		OkGo.getInstance().cancelTag(this);
	}
}
