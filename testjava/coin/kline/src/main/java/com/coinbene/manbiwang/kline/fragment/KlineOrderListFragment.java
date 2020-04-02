package com.coinbene.manbiwang.kline.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.activities.adapter.OrderBookAsksItemBinder;
import com.coinbene.common.activities.adapter.OrderBookBidsItemBinder;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.OrderListSortUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.common.widget.WrapperLinearLayoutManager;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.fragment.klineinterface.ActivityInterface;
import com.coinbene.manbiwang.kline.spotkline.KlineInterface;
import com.coinbene.manbiwang.model.http.KlineTopData;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-11-20
 */
public class KlineOrderListFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.recycler_view_bids)
	RecyclerView mRecyclerViewBids;
	@BindView(R2.id.recycler_view_asks)
	RecyclerView mRecyclerViewAsks;

	@BindView(R2.id.tv_quantity_bids)
	TextView tvQuantityBids;
	@BindView(R2.id.tv_quantity_asks)
	TextView tvQuantityAsks;

	@BindView(R2.id.tv_price)
	TextView tvPrice;

	private MultiTypeAdapter sellAdapter;
	private MultiTypeAdapter buyAdapter;

	private NewSpotWebsocket.OrderbookDataListener mOrderListListener;

	private OrderListSortUtil sortUtil;

	private List<OrderModel> sellObjects, buyObjects;

	private OrderListMapsModel orderListMapsModel;

	private TimerTask timerTask;
	private Timer timer;
	private static final long PERIOD_TIME = 2000;

	private int number = 14;

	private String tradePairName;
	private boolean isContract;
	private int contractType;

	ActivityInterface.IActivityListener mActivityListener;
	private String base;
	private String quote;
	private OrderBookBidsItemBinder orderBookBidsItemBinder;
	private OrderBookAsksItemBinder orderBookAsksItemBinder;

	public static KlineOrderListFragment newInstance() {
		Bundle args = new Bundle();
		KlineOrderListFragment fragment = new KlineOrderListFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.kline_fragment_order_list;
	}

	@Override
	public void initView(View rootView) {

		int greenColor = getContext().getResources().getColor(R.color.res_green);
		int redColor = getContext().getResources().getColor(R.color.res_red);
		boolean isRedRise = SwitchUtils.isRedRise();
		int amountTextColor = getContext().getResources().getColor(R.color.color_kline_text1);

		sellAdapter = new MultiTypeAdapter();
		orderBookBidsItemBinder = new OrderBookBidsItemBinder(false, greenColor, redColor, isRedRise, amountTextColor);
		orderBookBidsItemBinder.setUsedKline(true);
		sellAdapter.register(OrderModel.class, orderBookBidsItemBinder);
		mRecyclerViewAsks.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
		mRecyclerViewAsks.setAdapter(sellAdapter);

		buyAdapter = new MultiTypeAdapter();
		orderBookAsksItemBinder = new OrderBookAsksItemBinder(true, greenColor, redColor, isRedRise, amountTextColor);
		orderBookAsksItemBinder.setUsedKline(true);
		buyAdapter.register(OrderModel.class, orderBookAsksItemBinder);
		mRecyclerViewBids.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
		mRecyclerViewBids.setAdapter(buyAdapter);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	private void initType(String tradePairName) {
		this.tradePairName = tradePairName;

		if (tradePairName.contains("/")) {
			isContract = false;
			String[] strs = TradeUtils.parseSymbol(tradePairName);
			base = strs[0];
			quote = strs[1];
			tvQuantityAsks.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), base));
			tvQuantityBids.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), base));
		} else {
			isContract = true;
			contractType = tradePairName.contains("-") ? Constants.CONTRACT_TYPE_USDT : Constants.CONTRACT_TYPE_BTC;
			if (contractType == Constants.CONTRACT_TYPE_USDT) {
				ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(tradePairName);
				quote = table.quoteAsset;
				base = table.baseAsset;
				tvQuantityAsks.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(table)));
				tvQuantityBids.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(table)));
			} else {
				ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(tradePairName);
				quote = table.quoteAsset;
				base = table.baseAsset;
				tvQuantityAsks.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), getResources().getString(com.coinbene.common.R.string.number)));
				tvQuantityBids.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), getResources().getString(com.coinbene.common.R.string.number)));
			}
			orderBookBidsItemBinder.setSymbol(tradePairName);
			orderBookAsksItemBinder.setSymbol(tradePairName);
			orderBookBidsItemBinder.setContractType(contractType);
			orderBookAsksItemBinder.setContractType(contractType);
		}
		tvPrice.setText(String.format("%s(%s)", getResources().getString(R.string.price), quote));


	}

	@Override
	public void onStart() {
		super.onStart();
		if (getActivity() instanceof ActivityInterface) {
			if (mActivityListener == null) {
				mActivityListener = tradePairName -> initType(tradePairName);
			}
			if (!isActivityExist()) {
				return;
			}
			((ActivityInterface) getActivity()).registerActivityListener(mActivityListener);
		}

		if (mOrderListListener == null) {
			mOrderListListener = new NewSpotWebsocket.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {
					if (!isActivityExist()) {
						return;
					}
					updateView(
							orderListMapToList(orderbookModel.getBuyMap(), false),
							orderListMapToList(orderbookModel.getSellMap(), true)
					);
				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {

				}
			};
		}

		if (!isContract) {
			//现货
			NewSpotWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
		} else {
			if (contractType == Constants.CONTRACT_TYPE_BTC) {
				NewContractBtcWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				NewContractUsdtWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
			}
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		if (!isContract) {
			NewSpotWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
		} else {
			if (contractType == Constants.CONTRACT_TYPE_BTC) {
				NewContractBtcWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				NewContractUsdtWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
			}
		}
	}

	@Override
	public void onFragmentShow() {
		if (!isContract) {
			NewSpotWebsocket.getInstance().pullOrderListData();
		} else {
			if (contractType == Constants.CONTRACT_TYPE_BTC) {
				NewContractBtcWebsocket.getInstance().pullOrderListData();
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				NewContractUsdtWebsocket.getInstance().pullOrderListData();
			}
		}

		if (!isContract) {
			//现货才需要定时任务
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					if (!WebSocketManager.getInstance().getConnectSubscriberStatus(Constants.BASE_WEBSOCKET)) {
						//loadHttpOrderList();
					}
				}
			};
			timer.schedule(timerTask, 0, PERIOD_TIME);
		}
	}


	@Override
	public void onFragmentHide() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 现货的数据
	 *
	 * @param buyList
	 * @param sellList
	 */
	private void updateView(List<OrderModel> buyList, List<OrderModel> sellList) {

		//补全14条数据
		this.sellObjects = initOrderBookData(sellList, true);//这里防止倒序
		this.buyObjects = initOrderBookData(buyList, false);

		//计算深度
		TradeUtils.caculatePercentForKline(buyObjects, sellObjects);

		if (!isActivityExist()) {
			return;
		}

		getActivity().runOnUiThread(() -> {
			if (buyObjects != null && buyAdapter != null) {
				buyAdapter.setItems(buyObjects);
				buyAdapter.notifyDataSetChanged();
			}
			if (sellObjects != null && sellAdapter != null) {
				sellAdapter.setItems(sellObjects);
				sellAdapter.notifyDataSetChanged();
			}
		});
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

	/**
	 * 补全数据
	 *
	 * @param buySellList
	 * @param isSell
	 * @return
	 */
	private List<OrderModel> initOrderBookData(List<OrderModel> buySellList, boolean isSell) {
		if (buySellList == null) {
			buySellList = new ArrayList<>();
		}
		if (sortUtil == null) {
			sortUtil = new OrderListSortUtil();
		}
		if (isSell) {
			sortUtil.setAsc(false);
		} else {
			sortUtil.setAsc(true);
//			Collections.sort(buySellList, sortUtil);
		}
		Collections.sort(buySellList, sortUtil);


		//补全数据
		if (buySellList.size() < number) {
			int count = number - buySellList.size();
			for (int i = 0; i < count; i++) {
				OrderModel orderModel = new OrderModel();
				orderModel.isSell = isSell;
				orderModel.isFalse = true;
				orderModel.cnt = "0";
				orderModel.percent = 0.0;
				orderModel.price = "--";
				buySellList.add(orderModel);
			}
			return buySellList;
		} else {
			List<OrderModel> result = new ArrayList<>();
			for (int i = 0; i < number; i++) {
				result.add(buySellList.get(i));
			}
			return result;
		}
	}


	//顶部数据和14档图
	private void loadHttpOrderList() {
		if (orderListMapsModel == null) {
			orderListMapsModel = new OrderListMapsModel();
		}
		if (orderListMapsModel.getBuyMap() == null) {
			orderListMapsModel.setBuyMap(new ConcurrentHashMap<>());
		}
		if (orderListMapsModel.getSellMap() == null) {
			orderListMapsModel.setSellMap(new ConcurrentHashMap<>());
		}

		HttpParams httpParams = new HttpParams();
		httpParams.put("tradePairName", tradePairName);
		httpParams.put("num", Constants.MARKET_NUMBER_FORETEEN);
		OkGo.<KlineTopData>get(Constants.MARKET_ORDER_LIST).params(httpParams).tag(this).execute(new NewJsonSubCallBack<KlineTopData>() {

			@Override
			public void onSuc(Response<KlineTopData> response) {

			}

			@Override
			public KlineTopData dealJSONConvertedResult(KlineTopData klineTopData) {
				if (klineTopData.getData().quote != null) {
					orderListMapsModel.quote = klineTopData.getData().quote;
				}

				if (orderListMapsModel.getBuyMap().size() > 0) {
					orderListMapsModel.getBuyMap().clear();
				}
				if (orderListMapsModel.getSellMap().size() > 0) {
					orderListMapsModel.getSellMap().clear();
				}

				if (klineTopData.getData() != null && klineTopData.getData().getOrderDepth() != null && klineTopData.getData().getOrderDepth().asks != null
						&& klineTopData.getData().getOrderDepth().asks.size() > 0) {
					for (int i = 0; i < klineTopData.getData().getOrderDepth().asks.size(); i++) {
						orderListMapsModel.getBuyMap().put(klineTopData.getData().getOrderDepth().asks.get(i)[0], klineTopData.getData().getOrderDepth().asks.get(i)[1]);
					}
				}
				if (klineTopData.getData() != null && klineTopData.getData().getOrderDepth() != null && klineTopData.getData().getOrderDepth().bids != null
						&& klineTopData.getData().getOrderDepth().bids.size() > 0) {
					for (int i = 0; i < klineTopData.getData().getOrderDepth().bids.size(); i++) {
						orderListMapsModel.getSellMap().put(klineTopData.getData().getOrderDepth().bids.get(i)[0], klineTopData.getData().getOrderDepth().bids.get(i)[1]);
					}
				}

				//getOrderListToList(orderListMapsModel);
				if (getActivity() instanceof KlineInterface.View) {
					//((KlineInterface.View) getActivity()).onQuoteDataReceived(orderListMapsModel.quote);
				}

				//getActivity().runOnUiThread(() -> updateView(orderListMapToList(orderListMapsModel.buyMap, false), orderListMapToList(orderListMapsModel.sellMap, true)));

				return super.dealJSONConvertedResult(klineTopData);
			}


			@Override
			public void onE(Response<KlineTopData> response) {

			}

		});
	}

}
