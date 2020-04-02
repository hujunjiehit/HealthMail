package com.coinbene.manbiwang.contract.contractbtc.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.adapter.OrderDetailItemBinder;
import com.coinbene.manbiwang.contract.adapter.OrderListItemAdapter;
import com.coinbene.manbiwang.contract.adapter.OrderListLocalPriceBinder;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.model.http.OrderModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.MultiTypeAdapter;


public class ContractBtcOrderBookLayout extends LinearLayout {

	@BindView(R2.id.rl_order_list)
	RecyclerView rlOrderList;
	@BindView(R2.id.rl_order_detail)
	RecyclerView rlOrderDetail;
	private MultiTypeAdapter mOrderListAdapter, mOrderDetailAdapter;
	private OrderItemClick orderItemClick;
	//	private List<OrderModel> currentBuyList = Collections.synchronizedList(new ArrayList());
//	private List<OrderModel> currentSellList = Collections.synchronizedList(new ArrayList());
	private OrderLineItemModel currentLocalPriceModel;
	private int number;
	private int currentDisplay = Constants.TRADE_MARKET_POP_DEFAULT;
	private int curDisplayType = Constants.DISPLAY_ORDER_LIST;

	private int riseType = Constants.RISE_DEFAULT;//
	int position = 0;
	//	private List<KlineDealMedel.DataBean> orderDetailListCache = Collections.synchronizedList(new ArrayList());
	private String lastPrice;
	private TextView tvMarketPrice;

	public ContractBtcOrderBookLayout(Context context) {
		super(context);
	}

	public ContractBtcOrderBookLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ContractBtcOrderBookLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);

		rlOrderList.setFocusable(false);
		rlOrderDetail.setFocusable(false);
		rlOrderList.setHasFixedSize(true);
		rlOrderDetail.setHasFixedSize(true);
		rlOrderList.setNestedScrollingEnabled(false);
		rlOrderDetail.setNestedScrollingEnabled(false);

		mOrderListAdapter = new MultiTypeAdapter();
		OrderListItemAdapter orderListItemAdapter = new OrderListItemAdapter();
		orderListItemAdapter.setOnItemClickListener(price -> {
			if (orderItemClick != null) {
				orderItemClick.clickOrderItem(price);
			}
		});
		mOrderListAdapter.register(OrderModel.class, orderListItemAdapter);

		mOrderListAdapter.register(OrderLineItemModel.class, new OrderListLocalPriceBinder());
		rlOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
		rlOrderList.setAdapter(mOrderListAdapter);

		mOrderDetailAdapter = new MultiTypeAdapter();
		mOrderDetailAdapter.register(WsTradeList.class, new OrderDetailItemBinder());
		rlOrderDetail.setLayoutManager(new LinearLayoutManager(getContext()));
		rlOrderDetail.setAdapter(mOrderDetailAdapter);
	}


	/**
	 * 挂单数据刷新
	 *
	 * @param buyList
	 * @param sellList
	 * @param tickerData
	 */
	public void setOrderListData(List<OrderModel> buyList, List<OrderModel> sellList, WsMarketData tickerData) {
		if (currentLocalPriceModel == null) {
			currentLocalPriceModel = new OrderLineItemModel();
		}
		setMpPrice(currentLocalPriceModel.localPrice);

		if (tickerData != null) {
			currentLocalPriceModel.newPrice = TextUtils.isEmpty(tickerData.getLastPrice()) ? "--" : tickerData.getLastPrice();
			currentLocalPriceModel.localPrice = TextUtils.isEmpty(tickerData.getMarkPrice()) ? "--" : tickerData.getMarkPrice();
		}
		if (curDisplayType == Constants.DISPLAY_ORDER_LIST) {
			if (currentDisplay == Constants.TRADE_MARKET_POP_DEFAULT) {
				number = Constants.MARKET_NUMBER_SIX;
			} else if (currentDisplay == Constants.TRADE_MARKET_POP_BUY) {
				number = Constants.MARKET_NUMBER_TWELVE;
			} else if (currentDisplay == Constants.TRADE_MARKET_POP_SELL) {
				number = Constants.MARKET_NUMBER_TWELVE;
			}

			buyList = initOrderBookData(buyList, false);
			sellList = initOrderBookData(sellList, true);

			//目前buyList、sellList都是14条数据
			TradeUtils.caculatePercent(buyList, sellList);

			List<OrderModel> curBuyList = new ArrayList<>();
			List<OrderModel> curSellList = new ArrayList<>();


			//卖单取后面number条数据
			for (OrderModel item : sellList.subList(sellList.size() - number, sellList.size())) {
				curSellList.add(item);
			}

			//买单取前面number条数据
			for (OrderModel item : buyList.subList(0, number)) {
				curBuyList.add(item);
			}

			//默认
			List<Object> items = new ArrayList<>();
			if (currentDisplay == Constants.TRADE_MARKET_POP_DEFAULT) {
				items.addAll(curSellList);
				items.add(currentLocalPriceModel);
				items.addAll(curBuyList);
			}
			//买单
			else if (currentDisplay == Constants.TRADE_MARKET_POP_BUY) {
				items.add(currentLocalPriceModel);
				items.addAll(curBuyList);
			}
			//卖单
			else if (currentDisplay == Constants.TRADE_MARKET_POP_SELL) {
				items.addAll(curSellList);
				items.add(currentLocalPriceModel);
			}
			post(() -> {
				mOrderListAdapter.setItems(items);
				mOrderListAdapter.notifyDataSetChanged();

			});
		}

	}

	private void setMpPrice(String localPrice) {
		post(() -> {
			if (tvMarketPrice != null) {
					tvMarketPrice.setText(currentLocalPriceModel.localPrice);
				tvMarketPrice.setText(localPrice);
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
				} else {
					buySellList.add(orderModel);
				}
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


	public void setCurrentDisplay(int currentDisplay) {
		this.currentDisplay = currentDisplay;
		NewContractBtcWebsocket.getInstance().pullOrderListData();

//		setOrderListData(copyCacheList(currentBuyList), copyCacheList(currentSellList), currentLocalPriceModel);
	}

	/**
	 * 成交数据刷新
	 *
	 * @param orderDetailList
	 */
	public void setOrderDetailData(List<WsTradeList> orderDetailList) {

		if (orderDetailList != null && orderDetailList.size() > 1) {
			//拿到最近两次成交 刷新orderbook是涨还是跌
			if (currentLocalPriceModel != null) {
				riseType = BigDecimalUtils.compare(orderDetailList.get(0).getPrice(), orderDetailList.get(1).getPrice());
				if (curDisplayType == Constants.DISPLAY_ORDER_LIST && currentLocalPriceModel.riseType != riseType) {
					currentLocalPriceModel.riseType = riseType;
					post(() -> NewContractBtcWebsocket.getInstance().pullOrderListData());
				}
			}
		}


		if (curDisplayType == Constants.DISPLAY_ORDER_DETAIL) {
			List<WsTradeList> curTradeDetails = new ArrayList<>();
			if (orderDetailList != null && orderDetailList.size() > 10) {
				//卖单取后面number条数据
				for (int i = 0; i < 10; i++) {
					curTradeDetails.add(orderDetailList.get(i));
				}

			}


			post(() -> {
				if (curTradeDetails != null && curTradeDetails.size() > 0) {
					mOrderDetailAdapter.setItems(curTradeDetails);
					mOrderDetailAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

	}

	public void setOrderItemClick(OrderItemClick orderItemClick) {
		this.orderItemClick = orderItemClick;
	}

	public void setCurDisplayType(int curDisplayType) {
		this.curDisplayType = curDisplayType;
//		if (curDisplayType == DISPLAY_ORDER_LIST) {
//
//			setOrderListData(copyCacheList(currentBuyList), copyCacheList(currentSellList), currentLocalPriceModel);
//		} else if (curDisplayType == DISPLAY_ORDER_DETAIL) {
//			mOrderDetailAdapter.setItems(orderDetailListCache);
//			mOrderDetailAdapter.notifyDataSetChanged();
//		}
	}


	private List<OrderModel> copyCacheList(List<OrderModel> cacheList) {
		List<OrderModel> list = Collections.synchronizedList(new ArrayList());
		if (cacheList != null && cacheList.size() > 0) {
			list.addAll(cacheList);
		}
		return list;
	}

	public void setTvMarkPrice(TextView tvMarketPriceValue) {
		this.tvMarketPrice = tvMarketPriceValue;
	}

	/**
	 * 点击买卖盘通过此接口传递价格  到价格输入框
	 */
	public interface OrderItemClick {
		void clickOrderItem(String price);
	}
}
