package com.coinbene.manbiwang.contract.newcontract.orderlayout.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.AppBarStateChangeListener;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.adapter.OrderListItemAdapter;
import com.coinbene.manbiwang.contract.adapter.OrderListLocalPriceBinder;
import com.coinbene.manbiwang.contract.newcontract.ContractViewModel;
import com.coinbene.manbiwang.contract.newcontract.orderlayout.listener.ContractDataListener;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-12-02
 */
public class ContractOrderBookFagment extends CoinbeneBaseFragment implements ContractDataListener {

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
	@BindView(R2.id.tv_market_price_value)
	TextView tvMarketPriceValue;
	private static final long PERIOD_TIME = 2000;
	private Timer timer;

	private WebsocketOperatiron.OrderbookDataListener mOrderListListener;

	private int tradeMarketPosition = Constants.TRADE_MARKET_POP_DEFAULT;

	int numbers;

	private String symbol;

	private OrderLineItemModel currPriceModel;

	private MultiTypeAdapter mContentAdapter;

	private OrderListItemAdapter orderBookItemBinder;

	private OrderListMapsModel orderListMapsModel;

	private SelectorDialog<String> mTypeSelectorDialog;

	private WsMarketData mTickerData;
	private OrderbookModel mOrderbookModel;

	private ContractType contractType = ContractType.USDT;

	private ContractViewModel contractViewModel;
	private boolean showHideContractPlan;
	private boolean isFirstLoad = true;
	private OrderListLocalPriceBinder orderListLocalPriceBinder;
	private Boolean showContractHighLever;

	private boolean isScrolling = false;

	@Override
	public int initLayout() {
		return R.layout.contract_order_book_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		orderBookItemBinder = new OrderListItemAdapter();


		mContentAdapter.register(OrderModel.class, orderBookItemBinder);
		orderListLocalPriceBinder = new OrderListLocalPriceBinder();
		mContentAdapter.register(OrderLineItemModel.class, orderListLocalPriceBinder);


		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mContentAdapter);

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
	}

	@Override
	public void setListener() {
		orderBookItemBinder.setOnItemClickListener(item -> {
			contractViewModel.getClickPrice().postValue(item);
		});


		contractViewModel.getShowContractPlan().observe(this, show -> {
			showHideContractPlan = show;
			orderListLocalPriceBinder.setShowHideContractPlan(show);
			pullOrderBookData();
		});


		contractViewModel.getShowContractHighLever().observe(this, show -> {
			showContractHighLever = show;
			orderListLocalPriceBinder.setShowHideContractHighLever(show);
			pullOrderBookData();
		});
//		contractViewModel.getSymbol().observe(this, new Observer<String>() {
//			@Override
//			public void onChanged(String symbol) {
//				initUnit(symbol);
//
//			}
//		});

		contractViewModel.getUnitType().observe(this, integer -> initUnit(symbol));

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
			pullOrderBookData();
		});

		mTvOrderType.setOnClickListener(v -> {
			mTypeSelectorDialog.setDefaultPosition(tradeMarketPosition);
			mTypeSelectorDialog.show();
		});
	}

	private void initUnit(String symbol) {
//		if (!isShowing)
//			return;
		if (mTvOrderBookPrice == null) {
			return;
		}
		if (contractType == ContractType.USDT) {
			mTvOrderBookPrice.setText(String.format("%s(%s)", getString(R.string.price), TradeUtils.getContractUsdtQoute(symbol)));
			mTvOrderBookVol.setText(String.format("%s(%s)", getString(R.string.contract_trade_number), TradeUtils.getContractUsdtUnit(symbol)));
		} else {
			mTvOrderBookPrice.setText(String.format("%s(%s)", getString(R.string.price), TradeUtils.getContractBtcQoute(symbol)));
			mTvOrderBookVol.setText(String.format("%s(%s)", getString(R.string.contract_trade_number), getString(R.string.number)));
		}
		pullOrderBookData();
	}

	@Override
	public void initData() {
	}


	private void clearOrderBookData() {
		if (orderListMapsModel == null) {
			orderListMapsModel = new OrderListMapsModel();
		}
		if (orderListMapsModel.getBuyMap() == null) {
			orderListMapsModel.setBuyMap(new ConcurrentHashMap<>());
		}
		if (orderListMapsModel.getSellMap() == null) {
			orderListMapsModel.setSellMap(new ConcurrentHashMap<>());
		}
		setOrderBook(
				TradeUtils.orderListMapToList(orderListMapsModel.getBuyMap(), false),
				TradeUtils.orderListMapToList(orderListMapsModel.getSellMap(), true),
				symbol
		);
	}


	@Override
	public void registerDataListener() {
		DLog.e("testLeack", "registerDataListener," + contractType);
		if (mOrderListListener == null) {
			mOrderListListener = new WebsocketOperatiron.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String orderBooksymbol) {
					if (orderbookModel == null || !orderBooksymbol.equals(symbol)) {
						return;
					}
					mOrderbookModel = orderbookModel;
					setOrderBook(
							TradeUtils.orderListMapToList(orderbookModel.getBuyMap(), false),
							TradeUtils.orderListMapToList(orderbookModel.getSellMap(), true),
							symbol
					);
				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String tickerSymbol) {
					if (marketData == null || !tickerSymbol.equals(symbol)) {
						return;
					}
					mTickerData = marketData;

					//刷新是涨还是跌箭头
					if (currPriceModel == null) {
						currPriceModel = new OrderLineItemModel();
					}
					currPriceModel.riseType = mTickerData.getRiseType();

					if (mOrderbookModel != null) {
						setOrderBook(
								TradeUtils.orderListMapToList(mOrderbookModel.getBuyMap(), false),
								TradeUtils.orderListMapToList(mOrderbookModel.getSellMap(), true),
								symbol
						);
					}

					if (contractViewModel != null) {
						//买一价、卖一价、最新价、标记价
						contractViewModel.setBuyPriceOne(mTickerData.getBestBidPrice());
						contractViewModel.setSellPriceOne(mTickerData.getBestAskPrice());
						contractViewModel.setLastPrice(mTickerData.getLastPrice());
						contractViewModel.setMarkPrice(mTickerData.getMarkPrice());
						contractViewModel.setPriceSymbol(mTickerData.getSymbol());
						contractViewModel.postPriceModel();
					}
				}
			};
		}

		if (contractType == ContractType.USDT) {
			NewContractUsdtWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
		} else {
			NewContractBtcWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
		}
	}

	@Override
	public void unRegisterDataListener() {
		DLog.e("testLeack", "unRegisterDataListener," + contractType);
		if (contractType == ContractType.USDT) {
			NewContractUsdtWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
		} else {
			NewContractBtcWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
		}
	}

	@Override
	public void setSymbol(String symbol) {
		clearOrderBookData();

		unRegisterDataListener();

		this.symbol = symbol;
		contractType = TradeUtils.isUsdtContract(symbol) ? ContractType.USDT : ContractType.BTC;
		if (orderListLocalPriceBinder != null) {
			orderListLocalPriceBinder.setContractType(contractType);
		}
		initUnit(symbol);
		registerDataListener();

		initItemBinderParms();
	}

	@Override
	public void onScrollStatedChanged(AppBarStateChangeListener.ScrollState scrollState) {
		if (scrollState == AppBarStateChangeListener.ScrollState.SCROLLING) {
			isScrolling = true;
		} else {
			isScrolling = false;
			pullOrderBookData();
		}
	}

	@Override
	public void onFragmentShow() {
		pullOrderBookData();
		initUnit(symbol);
		initItemBinderParms();
		if (isFirstLoad) {
			isFirstLoad = false;
			clearOrderBookData();
		}
	}

	private void initItemBinderParms() {
		orderBookItemBinder.setContractType(contractType);
		orderBookItemBinder.setSymbol(symbol);

	}


	private void pullOrderBookData() {
		if (contractType == ContractType.USDT) {
			NewContractUsdtWebsocket.getInstance().pullOrderListData(symbol);
		} else {
			NewContractBtcWebsocket.getInstance().pullOrderListData(symbol);
		}
	}


	@Override
	public void onFragmentHide() {
	}

	public void setOrderBook(List<OrderModel> buyList, List<OrderModel> sellList, String wsSymbol) {
		if (!isShowing) {
			return;
		}

		if (isScrolling) {
			return;
		}

		//挂单档位
		if (tradeMarketPosition == Constants.TRADE_MARKET_POP_DEFAULT) {
			if (showContractHighLever || !showHideContractPlan) {
				numbers = Constants.MARKET_NUMBER_SIX;
			} else {
				numbers = Constants.MARKET_NUMBER_EIGHT;
			}
		} else {
			if (showContractHighLever || !showHideContractPlan) {
				numbers = Constants.MARKET_NUMBER_TWELVE;
			} else {
				numbers = Constants.MARKET_NUMBER_SIXTEEN;
			}
		}
		buyList = initOrderBookData(buyList, false);
		sellList = initOrderBookData(sellList, true);


		if (currPriceModel == null) {
			currPriceModel = new OrderLineItemModel();
		}

		if (mTickerData != null) {
			currPriceModel.newPrice = TextUtils.isEmpty(mTickerData.getLastPrice()) ? "--" : mTickerData.getLastPrice();
			currPriceModel.localPrice = TextUtils.isEmpty(mTickerData.getLocalPrice()) ? "--" : mTickerData.getLocalPrice();
			currPriceModel.upsAndDowns = mTickerData.getUpsAndDowns();
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
				if (mTickerData != null) {
					tvMarketPriceValue.setText(mTickerData.getMarkPrice());
				}
				mContentAdapter.setItems(items);
				mContentAdapter.notifyDataSetChanged();
			}
		});
	}

	private List<OrderModel> initOrderBookData(List<OrderModel> buySellList, boolean isSell) {
		int needSize;
		//为了计算深度图，7或者14  取14    8或者16  取16
		if (numbers == Constants.MARKET_NUMBER_SIX || numbers == Constants.MARKET_NUMBER_TWELVE)
			needSize = Constants.MARKET_NUMBER_TWELVE;
		else {
			needSize = Constants.MARKET_NUMBER_SIXTEEN;
		}

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
}
