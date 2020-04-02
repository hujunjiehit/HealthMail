package com.coinbene.manbiwang.spot.orderlayout.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.manbiwang.model.http.BookOrderRes;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.coinbene.manbiwang.service.spot.message.BuyOrSellPriceOneMessage;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.orderlayout.listener.OrderBookListener;
import com.coinbene.manbiwang.spot.orderlayout.listener.SpotDataListener;
import com.coinbene.manbiwang.spot.spot.adapter.OrderBookCenterBinder;
import com.coinbene.manbiwang.spot.spot.adapter.OrderBookItemBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-12-02
 */
public class OrderBookFagment extends CoinbeneBaseFragment implements SpotDataListener {

	@BindView(R2.id.tv_order_book_price)
	TextView mTvOrderBookPrice;
	@BindView(R2.id.tv_order_book_vol)
	TextView mTvOrderBookVol;
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.tv_order_type)
	QMUIRoundButton mTvOrderType;
	@BindView(R2.id.layout_order_type)
	ConstraintLayout mLayoutOrderType;

	private static final long PERIOD_TIME = 2000;
	private Timer timer;

	private NewSpotWebsocket.OrderbookDataListener mOrderListListener;

	private int tradeMarketPosition = Constants.TRADE_MARKET_POP_DEFAULT;
	int numbers;

	private String sellPriceOne = "";
	private String buyPriceOne = "";
	private String symbol;
	private String baseAsset;
	private String qouteAsset;

	private OrderLineItemModel currPriceModel;

	private MultiTypeAdapter mContentAdapter;
	private OrderBookItemBinder orderBookItemBinder;

	private OrderListMapsModel orderListMapsModel;

	private OrderBookListener mOrderBookListener;

	private SelectorDialog<String> mTypeSelectorDialog;

	private WsMarketData mTickerData;
	private OrderbookModel mOrderbookModel;

	@Override
	public int initLayout() {
		return R.layout.spot_order_book_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		orderBookItemBinder = new OrderBookItemBinder();

		orderBookItemBinder.setOnItemClickLisenter(item -> {
			if (mOrderBookListener != null) {
				mOrderBookListener.onPriceClick(item.price);
			}
		});

		mContentAdapter.register(OrderModel.class, orderBookItemBinder);
		mContentAdapter.register(OrderLineItemModel.class, new OrderBookCenterBinder());

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {
		if (mTypeSelectorDialog == null) {
			mTypeSelectorDialog = new SelectorDialog<>(getContext());
			List<String> typeList = new ArrayList<>();
			typeList.add(getString(R.string.market_default));
			typeList.add(getString(R.string.show_buy_market));
			typeList.add(getString(R.string.show_sell_merket));
			mTypeSelectorDialog.setDatas(typeList);
		}

		mTypeSelectorDialog.setSelectListener((data, positon) -> {
			mTvOrderType.setText(data);
			tradeMarketPosition = positon;
			NewSpotWebsocket.getInstance().pullOrderListData();
		});

		mTvOrderType.setOnClickListener(v -> {
			mTypeSelectorDialog.setDefaultPosition(tradeMarketPosition);
			mTypeSelectorDialog.show();
		});
	}

	@Override
	public void initData() {
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
		if (orderListMapsModel != null) {
			orderListMapsModel.quote = null;
		}
	}


	@Override
	public void registerDataListener() {
		if (mOrderListListener == null) {

			mOrderListListener = new NewSpotWebsocket.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {
					mOrderbookModel = orderbookModel;
					setOrderBook(
							TradeUtils.orderListMapToList(orderbookModel.getBuyMap(), false),
							TradeUtils.orderListMapToList(orderbookModel.getSellMap(), true),
							symbol
					);
				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {
					if (marketData == null) {
						return;
					}
					mTickerData = marketData;
					if (mOrderBookListener != null) {
						mOrderBookListener.onReceiveQuoteData(mTickerData);
					}
					if (mOrderbookModel != null) {
						setOrderBook(
								TradeUtils.orderListMapToList(mOrderbookModel.getBuyMap(), false),
								TradeUtils.orderListMapToList(mOrderbookModel.getSellMap(), true),
								symbol
						);
					}
				}
			};
		}
		NewSpotWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
	}

	@Override
	public void unRegisterDataListener() {
		NewSpotWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
	}

	@Override
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		String[] arrays = TradeUtils.parseSymbol(symbol);
		qouteAsset = arrays[1];
		baseAsset = arrays[0];
		if (mTvOrderBookPrice != null) {
			mTvOrderBookPrice.setText(String.format("%s(%s)", getResources().getString(R.string.price), qouteAsset));
		}

		if (mTvOrderBookVol != null) {
			mTvOrderBookVol.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), baseAsset));
		}
	}

	@Override
	public void onFragmentShow() {
		NewSpotWebsocket.getInstance().pullOrderListData();
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!WebSocketManager.getInstance().getConnectSubscriberStatus(Constants.BASE_WEBSOCKET)) {
					//getOrderBook(symbol);
				}
			}
		};
		timer.schedule(timerTask, 0, PERIOD_TIME);
	}

	@Override
	public void onFragmentHide() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void setOrderBook(List<OrderModel> buyList, List<OrderModel> sellList, String wsSymbol) {
		//7档挂单
		if (tradeMarketPosition == Constants.TRADE_MARKET_POP_DEFAULT) {
			numbers = Constants.MARKET_NUMBER_SVEEN;
		} else {
			numbers = Constants.MARKET_NUMBER_FORETEEN;
		}
		buyList = initOrderBookData(buyList, false);
		sellList = initOrderBookData(sellList, true);


		if (currPriceModel == null) {
			currPriceModel = new OrderLineItemModel();
		}

		if (mTickerData != null) {
			currPriceModel.newPrice = TextUtils.isEmpty(mTickerData.getLastPrice()) ? "--" : mTickerData.getLastPrice();
			currPriceModel.localPrice = TextUtils.isEmpty(mTickerData.getLocalPrice()) ? "--" : mTickerData.getLocalPrice();

			//买一价、卖一价
			buyPriceOne = mTickerData.getBestBidPrice();
			sellPriceOne = mTickerData.getBestAskPrice();

			EventBus.getDefault().post(new BuyOrSellPriceOneMessage(buyPriceOne, sellPriceOne, mTickerData.getSymbol()));
		}

		if (!isShowing) {
			return;
		}

		//目前buyList、sellList都是14条数据
		TradeUtils.caculatePercent(buyList, sellList);

		List<OrderModel> curBuyList = new ArrayList<>();
		List<OrderModel> curSellList = new ArrayList<>();


		//卖单取后面number条数据
		for (OrderModel item : sellList.subList(sellList.size() - numbers, sellList.size())) {
			curSellList.add(item);
		}

		//买单取前面number条数据
		for (OrderModel item : buyList.subList(0, numbers)) {
			curBuyList.add(item);
		}

		//默认
		List<Object> items = new ArrayList<>();
		//7档挂单
		if (tradeMarketPosition == Constants.TRADE_MARKET_POP_DEFAULT) {
			items.addAll(curSellList);
			items.add(currPriceModel);
			items.addAll(curBuyList);
		} else {
			numbers = Constants.MARKET_NUMBER_FORETEEN;
			//展示买单
			if (tradeMarketPosition == Constants.TRADE_MARKET_POP_BUY) {
				items.add(currPriceModel);
				items.addAll(curBuyList);
			}
			//展示卖单
			else if (tradeMarketPosition == Constants.TRADE_MARKET_POP_SELL) {
				items.addAll(curSellList);
				items.add(currPriceModel);
			}
		}

		getActivity().runOnUiThread(() -> {
			if (items.size() > 0) {
				mContentAdapter.setItems(items);
				mContentAdapter.notifyDataSetChanged();
			}
		});
	}

	private List<OrderModel> initOrderBookData(List<OrderModel> buySellList, boolean isSell) {
		int needSize = 14; //为了计算深度图，全部取14条数据
		if (buySellList == null) {
			buySellList = new ArrayList<>();
		}
		//补全数据
		if (buySellList.size() < needSize) {
			int count = needSize - buySellList.size();
			for (int i = 0; i < count; i++) {
				OrderModel orderModel = new OrderModel();
				orderModel.isSell = isSell;
				orderModel.isFalse = true;
				//买单加在最后  卖单家在最前
				if (isSell) {
					buySellList.add(0, orderModel);
				} else
					buySellList.add(orderModel);
			}
			return buySellList;
		} else {
			//如果是卖单  哪倒叙后的最后的数据
			List<OrderModel> result = new ArrayList<>();
			if (isSell) {
				for (int i = buySellList.size() - needSize; i < buySellList.size(); i++) {
					result.add(buySellList.get(i));
				}
			} else {
				for (int i = 0; i < needSize; i++) {
					result.add(buySellList.get(i));
				}
			}
			return result;
		}
	}

	//通过http请求 orderBook
	private void getOrderBook(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return;
		}
		HttpParams params = new HttpParams();
		params.put("tradePairName", symbol);
		params.put("num", Constants.MARKET_NUMBER_FORETEEN);
		OkGo.<BookOrderRes>get(Constants.MARKET_ORDER_LIST)
				.params(params)
				.tag(this)
				.execute(new NewJsonSubCallBack<BookOrderRes>() {
							 @Override
							 public void onSuc(Response<BookOrderRes> response) {

							 }

							 @Override
							 public BookOrderRes dealJSONConvertedResult(BookOrderRes bookOrderRes) {
								 if (bookOrderRes.getData().quote != null) {
									 orderListMapsModel.quote = bookOrderRes.getData().quote;
								 }
								 if (orderListMapsModel.getBuyMap().size() > 0) {
									 orderListMapsModel.getBuyMap().clear();
								 }
								 if (orderListMapsModel.getSellMap().size() > 0) {
									 orderListMapsModel.getSellMap().clear();
								 }

								 if (bookOrderRes.getData() != null && bookOrderRes.getData().getOrderDepth() != null && bookOrderRes.getData().getOrderDepth().asks != null
										 && bookOrderRes.getData().getOrderDepth().asks.size() > 0) {
									 for (int i = 0; i < bookOrderRes.getData().getOrderDepth().asks.size(); i++) {
										 orderListMapsModel.getBuyMap().put(bookOrderRes.getData().getOrderDepth().asks.get(i)[0], bookOrderRes.getData().getOrderDepth().asks.get(i)[1]);
									 }
								 }
								 if (bookOrderRes.getData() != null && bookOrderRes.getData().getOrderDepth() != null && bookOrderRes.getData().getOrderDepth().bids != null
										 && bookOrderRes.getData().getOrderDepth().bids.size() > 0) {
									 for (int i = 0; i < bookOrderRes.getData().getOrderDepth().bids.size(); i++) {
										 orderListMapsModel.getSellMap().put(bookOrderRes.getData().getOrderDepth().bids.get(i)[0], bookOrderRes.getData().getOrderDepth().bids.get(i)[1]);
									 }
								 }
								 //setOrderBook(TradeUtils.orderListMapToList(orderListMapsModel.buyMap, false), TradeUtils.orderListMapToList(orderListMapsModel.sellMap, true), orderListMapsModel.quote);
								 return super.dealJSONConvertedResult(bookOrderRes);
							 }

							 @Override
							 public void onE(Response<BookOrderRes> response) {
							 }
						 }
				);

	}

	public void setPriceClickListener(OrderBookListener mOrderBookListener) {
		this.mOrderBookListener = mOrderBookListener;
	}

}
