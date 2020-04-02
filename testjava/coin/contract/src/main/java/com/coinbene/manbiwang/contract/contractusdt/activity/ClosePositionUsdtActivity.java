package com.coinbene.manbiwang.contract.contractusdt.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.activities.adapter.OrderBookAsksItemBinder;
import com.coinbene.common.activities.adapter.OrderBookBidsItemBinder;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.OrderListSortUtil;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.core.CBWebSocketSubscriber;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.dialog.ContractTradeDialog;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;
import com.coinbene.manbiwang.model.http.ContractPlaceOrderModel;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.coinbene.manbiwang.model.websocket.WsContractOrderBookModel;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

public class ClosePositionUsdtActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.input_price)
	PlusSubInputView inputPrice;
	@BindView(R2.id.buy_coin_name)
	TextView buyCoinName;
	@BindView(R2.id.tv_cur_hold)
	TextView tvCurHold;

	@BindView(R2.id.rl_price)
	RelativeLayout rlPrice;
	@BindView(R2.id.input_num)
	PlusSubInputView inputNum;
	@BindView(R2.id.coin_num_name)
	TextView coinNumName;
	@BindView(R2.id.rl_number)
	RelativeLayout rlNumber;
	@BindView(R2.id.tv_avl_close)
	TextView tvAvlClose;
	@BindView(R2.id.tv_avl_close_value)
	TextView tvAvlCloseValue;
	@BindView(R2.id.seek_bar)
	BubbleSeekBar seekBar;
	@BindView(R2.id.sell_tv)
	TextView sellTv;
	@BindView(R2.id.rly_bids)
	RecyclerView rlyBids;
	@BindView(R2.id.rly_asks)
	RecyclerView rlyAsks;
	@BindView(R2.id.ll_dialog_root)
	LinearLayout llDialogRoot;
	@BindView(R2.id.tv_title)
	TextView tvTitle;
	@BindView(R2.id.iv_close)
	ImageView ivClose;
	@BindView(R2.id.tv_limit_price)
	TextView tvLimitPrice;
	@BindView(R2.id.tv_market_price)
	TextView tvMarketPrice;
	@BindView(R2.id.view_limit)
	View viewLimit;
	@BindView(R2.id.view_market)
	View viewMarket;
	@BindView(R2.id.tv_profit)
	TextView tvProfit;
	@BindView(R2.id.tv_quantity_bids)
	TextView tvQuantityBids;
	@BindView(R2.id.tv_quantity_asks)
	TextView tvQuantityAsks;

	@BindView(R2.id.tv_price)
	TextView tvPrice;
	@BindView(R2.id.tv_estimated_value)
	TextView tvEstimatedValue;


	private int currentPriceType = 0;//0限价   1市价
	private MultiTypeAdapter sellAdapter;
	private MultiTypeAdapter buyAdapter;
	private OrderListSortUtil sortUtil;
	private int number = 5;
	private boolean isChangeSeekBar;
	private ContractPositionListModel.DataBean data;
	private ContractUsdtInfoTable table;
	private String placeOrderTitle;
	private Gson gson;
	private OrderListMapsModel orderListMapsModel;
	private String contractName = "BTCUSDT";
	private ContractTradeDialog tradeDialog;
	private int curHighLeverEntrust = Constants.ONLY_MAKER;
	private String side;
	private String avlPosition = "0";
	private String avgPrice;
	private boolean isNeedDisplayNp = true;
	private String newPrice = "";

	private CBWebSocketSubscriber<WsContractOrderBookModel> mSubscriber;

	private WebsocketOperatiron.OrderbookDataListener mOrderListListener;
	private OrderBookAsksItemBinder sellBind;
	private OrderBookBidsItemBinder buyBind;
	private BaseTextWatcher watcherCont;

	private WsMarketData mTickerData;
	private OrderbookModel mOrderbookModel;
	private ContractPlaceOrderParmsModel contractPlaceOrderModel;

	public static void startMe(Context context, String contractName, String side, String avlPosition, String avgPrice) {

		NewContractUsdtWebsocket.getInstance().changeSymbol(contractName);

		Intent intent = new Intent(context, ClosePositionUsdtActivity.class);
		intent.putExtra("contractName", contractName);
		intent.putExtra("side", side);
		intent.putExtra("avlPosition", avlPosition);
		intent.putExtra("avgPrice", avgPrice);
		context.startActivity(intent);

	}

	@Override
	public int initLayout() {
		overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
		return R.layout.activity_close_position;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void setListener() {
		listener();
	}

	@Override
	public void initData() {
		getData();
	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void listener() {
		ivClose.setOnClickListener(this);
		tvLimitPrice.setOnClickListener(this);
		tvMarketPrice.setOnClickListener(this);
		sellTv.setOnClickListener(this);
		inputNum.setHint(String.format("%s(%s)", getString(R.string.input_num_hint), TradeUtils.getContractUsdtUnit(table)));
		inputNum.setMinPriceChange(TradeUtils.getContractUsdtMultiplier(table));
		inputNum.getmEditText().setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				KeyboardUtils.hideKeyboard(inputNum);
				return true;
			}
			return false;
		});
		watcherCont = new BaseTextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				if (table != null) {
					if (TradeUtils.getContractUsdtPrecision(table) == 0) {
						inputNum.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

					} else {
						inputNum.getmEditText().setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
						PrecisionUtils.setPrecision(inputNum.getmEditText(), s, TradeUtils.getContractUsdtPrecision(table));

					}
				}
				if (!isChangeSeekBar) {
					changeToSeekBar();
				}
				if (currentPriceType == 0)//限价的时候去计算
					calculationReturn();
//				inputNum.setSeparatorText(watcherCont);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calEstimatedValue();
			}
		};
		inputNum.addTextChangedListener(watcherCont);
		seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

			@Override
			public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
				tvCurHold.setText(String.format("%s%d%%", getString(R.string.cur_position), progress));
				if (fromUser) {
					isChangeSeekBar = true;
					changeToCloseCont(progress);
					isChangeSeekBar = false;
				}

			}

			@Override
			public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

			}

			@Override
			public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

			}
		});
	}

	private void calEstimatedValue() {
		if (SpUtil.getContractUsdtUnitSwitch() == 0) {
			if (!BigDecimalUtils.isEmptyOrZero(inputNum.getText()))
				tvEstimatedValue.setText(String.format("=%s %s", TradeUtils.getContractUsdtEstimatedValue(inputNum.getText(), table), table.baseAsset));
			else {
				tvEstimatedValue.setText("");
			}
		} else
			tvEstimatedValue.setText("");
	}

	private void changeToCloseCont(int progress) {
		if (TextUtils.isEmpty(avlPosition)) {
			return;
		}
		BigDecimal bdAvl = new BigDecimal(avlPosition);
		BigDecimal bdPg = new BigDecimal(progress);
		BigDecimal onHundred = new BigDecimal(100);
		inputNum.setText(bdPg.divide(onHundred).multiply(bdAvl).setScale(TradeUtils.getContractUsdtPrecision(table), BigDecimal.ROUND_DOWN).toPlainString());
		inputNum.setSelection(inputNum.getText().length());
	}

	private void calculationReturn() {
		if (BigDecimalUtils.isEmptyOrZero(inputPrice.getText())) {
			tvProfit.setText(String.format("%s-- USDT", getString(R.string.expected_return)));
			return;
		}
		if (BigDecimalUtils.isEmptyOrZero(inputNum.getText())) {
			tvProfit.setText(String.format("%s-- USDT", getString(R.string.expected_return)));
			return;
		}

//        if (contractName.contains("BTC")) {
//            tvProfit.setText(String.format("%s%s", getString(R.string.expected_return), String.format("%s BTC", CalculationUtils.calculateBtcExpectedReturn(
//                    side,
//                    avgPrice,
//                    inputPrice.getText().toString(),
//                    inputNum.getText().toString()))));
//        } else {
		tvProfit.setText(String.format("%s%s", getString(R.string.expected_return), String.format("%s USDT", CalculationUtils.calculateEthExpectedReturnUsdtContract(
				side, avgPrice,
				inputPrice.getText().toString(),
				inputNum.getText().toString(), table.multiplier,
				2))));
//        }
	}

	private void changeToSeekBar() {
		if (TextUtils.isEmpty(avlPosition)) {
			seekBar.setProgress(0);
			return;
		}

		int persent = BigDecimalUtils.divideHalfUp(inputNum.getText().toString(), avlPosition, 2);
		if (persent < 100) {
			seekBar.setProgress(persent);
		} else {
			seekBar.setProgress(100);
		}
	}

	private void getData() {


		avlPosition = TradeUtils.getContractUsdtUnitValue(avlPosition, table);
		inputNum.setText(avlPosition);
		if (TextUtils.isEmpty(contractName) || TextUtils.isEmpty(side)) {
			return;
		}
		if (table == null) {
			return;
		}
		setInputPrecision(table.precision, table.minPriceChange);


		if (side.equals("long")) {
			placeOrderTitle = getString(R.string.sell_close_long);
			sellTv.setText(R.string.side_close_long);

			sellTv.setBackground(SwitchUtils.isRedRise() ?
					getResources().getDrawable(R.drawable.bg_green_sharp)
					: getResources().getDrawable(R.drawable.bg_red_sharp));


			seekBar.setThumbColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
			seekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));


		} else {
			placeOrderTitle = getString(R.string.buy_close_short);
			sellTv.setText(R.string.side_close_short);

			sellTv.setBackground(SwitchUtils.isRedRise() ?
					getResources().getDrawable(R.drawable.bg_red_sharp)
					: getResources().getDrawable(R.drawable.bg_green_sharp));
			seekBar.setThumbColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
			seekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));

		}
		tvTitle.setText(String.format("%s %s", String.format(getString(R.string.forever_no_delivery), table.baseAsset), placeOrderTitle));

		tvAvlCloseValue.setText(String.format("%s %s", avlPosition, TradeUtils.getContractUsdtUnit(table)));
		coinNumName.setText(TradeUtils.getContractUsdtUnit(table));
		setOrderBookData(null, null, "", "");
		calEstimatedValue();
	}

	private void initModel() {
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		initModel();
		if (mOrderListListener == null) {
			mOrderListListener = new WebsocketOperatiron.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {
					mOrderbookModel = orderbookModel;
					if (!symbol.equals(contractName)) {
						return;
					}
					onOrderbookDataArrived();
				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {
					mTickerData = marketData;
					if (!symbol.equals(contractName)) {
						return;
					}
					onOrderbookDataArrived();
				}
			};
		}

		NewContractUsdtWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
	}

	private void onOrderbookDataArrived() {
		if (mTickerData == null || mOrderbookModel == null) {
			return;
		}
		setOrderBookData(orderListMapToList(mOrderbookModel.getBuyMap(), false), orderListMapToList(mOrderbookModel.getSellMap(), true), mTickerData.getLastPrice(), contractName);
	}

	@Override
	protected void onPause() {
		super.onPause();

		NewContractUsdtWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
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
			BigDecimal bigDecimal = new BigDecimal(entry.getValue());
			if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
				it.remove();
				continue;
			}
			OrderModel orderModel = new OrderModel();
			orderModel.isSell = isSell;
			orderModel.price = entry.getKey();
			orderModel.cnt = entry.getValue();
			orderModel.percent = 0.0;

			list.add(orderModel);
		}


		return list;
	}

	public void setOrderBookData(List<OrderModel> buyList, List<OrderModel> sellList, String nl, String symbol) {
		buyBind.setSymbol(symbol);
		sellBind.setSymbol(symbol);
		List<OrderModel> buyItemsLarge = initOrderBookData(buyList, false);
		List<OrderModel> sellItemsLarge = initOrderBookData(sellList, true);
		//此时buyItems和sellItems都是14条数据，需要从中取出五条数据

		TradeUtils.caculatePercentForKline(buyItemsLarge, sellItemsLarge);

		//从中取出五条数据

		//买单14条降序排序，取前五条
		List<OrderModel> buyItems = buyItemsLarge.subList(0, number);

		//卖单14条升序排序，取前五条
		List<OrderModel> sellItems = sellItemsLarge.subList(0, number);

		this.runOnUiThread(() -> {

			if (buyAdapter != null) {
				buyAdapter.setItems(buyItems);
				buyAdapter.notifyDataSetChanged();
			}
			if (sellAdapter != null) {
				sellAdapter.setItems(sellItems);
				sellAdapter.notifyDataSetChanged();
			}
			if (isNeedDisplayNp && currentPriceType == 0 && !TextUtils.isEmpty(nl)) {
				inputPrice.setText(nl);
				this.newPrice = nl;
				isNeedDisplayNp = false;
			}

		});

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_close) {
			KeyboardUtils.hideKeyboard(inputNum);
			finish();
		} else if (id == R.id.tv_limit_price) {
			if (currentPriceType == 0) {
				return;
			}
			if (!TextUtils.isEmpty(newPrice)) {
				inputPrice.setText(newPrice);
			}
			inputPrice.setEnablePlusSub(true);
			inputPrice.setEnabled(true);
			inputPrice.setHint(R.string.price);
			currentPriceType = 0;
			rlPrice.setBackground(getResources().getDrawable(R.drawable.input_edit_bg));
			tvLimitPrice.setTextColor(getResources().getColor(R.color.res_blue));
			tvMarketPrice.setTextColor(getResources().getColor(R.color.res_textColor_1));
			viewLimit.setVisibility(View.VISIBLE);
			viewMarket.setVisibility(View.INVISIBLE);
			tvProfit.setVisibility(View.VISIBLE);
		} else if (id == R.id.tv_market_price) {
			if (currentPriceType == 1) {
				return;
			}
			inputPrice.setEnablePlusSub(false);
			inputPrice.setEnabled(false);
			inputPrice.setText("");
			inputPrice.setHint(R.string.market_price);
			currentPriceType = 1;
			tvLimitPrice.setTextColor(getResources().getColor(R.color.res_textColor_1));
			tvMarketPrice.setTextColor(getResources().getColor(R.color.res_blue));
			viewLimit.setVisibility(View.INVISIBLE);
			viewMarket.setVisibility(View.VISIBLE);
			rlPrice.setBackground(getResources().getDrawable(R.drawable.shape_edit_disable_background));
			tvProfit.setVisibility(View.INVISIBLE);
		} else if (id == R.id.sell_tv) {//限价的时候需要判断输入的价格是不是空或者是不是0
			if (currentPriceType == 0 && BigDecimalUtils.isEmptyOrZero(inputPrice.getText())) {
				ToastUtil.show(R.string.please_input_coin_price_tip);
				return;
			}
			if (BigDecimalUtils.isEmptyOrZero(inputNum.getText())) {
				ToastUtil.show(R.string.please_input_coin_num_tip);
				return;
			}
			if (!TextUtils.isEmpty(avlPosition) && BigDecimalUtils.isGreaterThan(inputNum.getText(), avlPosition)) {
				ToastUtil.show(R.string.close_amount_insufficient_tip);
				return;
			}

			if (!TradeUtils.checkContractUsdtInput(inputNum.getText(), table)) {
				ToastUtil.show(String.format("%s%s", getString(R.string.usdt_contract_min_place_order), table.multiplier));
				return;
			}

			if (SpUtil.isContractSureDialogNotShow()) {
				closeOrder(side.equals("long") ? Constants.TRADE_CLOSE_LONG : Constants.TRADE_CLOSE_SHORT
						, inputPrice.getText(), inputNum.getText(), contractName);
				return;
			}

			if (contractPlaceOrderModel == null) {
				contractPlaceOrderModel = new ContractPlaceOrderParmsModel();
			}
			contractPlaceOrderModel.setEstimatedValue(tvEstimatedValue.getText().toString());
			contractPlaceOrderModel.setPrice(inputPrice.getText());
			contractPlaceOrderModel.setNumber(TradeUtils.getContractUsdtDialogShowValue(inputNum.getText(), table));
			contractPlaceOrderModel.setTradeType(side.equals("long") ? Constants.TRADE_CLOSE_LONG : Constants.TRADE_CLOSE_SHORT);
			contractPlaceOrderModel.setSymbol(contractName);
			contractPlaceOrderModel.setOrderType(currentPriceType);
			contractPlaceOrderModel.setUnit(TradeUtils.isUsdtContract(contractName) ? TradeUtils.getContractUsdtUnit(contractName) : getString(R.string.number));
			contractPlaceOrderModel.setHighLeverOrderType(curHighLeverEntrust);


			if (tradeDialog == null) {
				tradeDialog = new ContractTradeDialog(this);
				tradeDialog.setOnClickListener(new ContractTradeDialog.OnClickListener() {
					@Override
					public void clickCancel() {

					}

					@Override
					public void placeOrder(ContractPlaceOrderParmsModel orderModel) {
						closeOrder(orderModel.getTradeType(), orderModel.getPrice(), orderModel.getNumber(), orderModel.getSymbol());
					}


				});
			}

			tradeDialog.buildPrams(contractPlaceOrderModel).show();

//			tradeDialog.buildTitle(currentPriceType == 0 ? getString(R.string.fixed_price) : getString(R.string.market_price), placeOrderTitle, contractName)
//					.buildPrice(PrecisionUtils.appendZero(inputPrice.getText(), table.precision))
//					.buildNumber(String.format("%s %s", TradeUtils.getContractUsdtDialogShowValue(inputNum.getText(), table), TradeUtils.getContractUsdtUnit(table)))
//					.buildEstimatedValue(tvEstimatedValue.getText().toString())
//					.setFixPriceType(currentPriceType, curHighLeverEntrust)
//					.setRedRase(SwitchUtils.isRedRise())
//					.setDirection(side.equals("long") ? Constants.TRADE_CLOSE_LONG : Constants.TRADE_CLOSE_SHORT)
//					.show();
		}
	}


	private void closeOrder(int tradeDirection, String price, String number, String contractName) {
		HttpParams params = new HttpParams();
		params.put("symbol", contractName);
		//订单类型，limit，限价；market，市价
		params.put("orderType", currentPriceType == 1 ? "market" : "limit");
		if (currentPriceType == 0) {//限价价格（市价不传）
			params.put("orderPrice", price);
		}
		params.put("quantity", TradeUtils.getContractUsdtPlaceOrderValue(inputNum.getText().toString(), table));
		params.put("source", "android");
		//方向closeLong(平多),closeShort(平空)
		params.put("direction", tradeDirection == Constants.TRADE_CLOSE_LONG ? "closeLong" : "closeShort");
		long startTime = System.currentTimeMillis();
		OkGo.<ContractPlaceOrderModel>post(Constants.TRADE_CLOSE_ORDER_USDT).params(params).tag(this).execute(new DialogCallback<ContractPlaceOrderModel>(this) {
			@Override
			public void onSuc(Response<ContractPlaceOrderModel> response) {
				ToastUtil.show(R.string.buyorsell_success);
				DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appPlaceOrder,
						new DataCollectionModel(response.body().getData().getOrderId(),
								DataCollectionModel.CONTRACT,
								(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis()) + ""));

				finish();
			}

			@Override
			public void onE(Response<ContractPlaceOrderModel> response) {

			}

		});
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
		}
		Collections.sort(buySellList, sortUtil);
		//补全数据
		if (buySellList.size() < 14) {
			int count = 14 - buySellList.size();
			for (int i = 0; i < count; i++) {
				OrderModel orderModel = new OrderModel();
				orderModel.isSell = isSell;
				orderModel.isFalse = true;
				orderModel.cnt = "0";
				orderModel.percent = 0.0;
				buySellList.add(orderModel);
			}
			return buySellList;
		} else {
			List<OrderModel> result = new ArrayList<>();
			for (int i = 0; i < 14; i++) {
				result.add(buySellList.get(i));
			}
			return result;
		}
	}

	private void setInputPrecision(int precision, String minPriceChange) {
		inputPrice.setMinPriceChange(table.minPriceChange);
		TextWatcher priceTextWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				inputPrice.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(inputPrice.getmEditText(), s, precision, minPriceChange);
				calculationReturn();
				inputPrice.addTextChangedListener(this);

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
		inputPrice.addTextChangedListener(priceTextWatcher);

	}

	private void init() {
		this.contractName = getIntent().getStringExtra("contractName");
		this.side = getIntent().getStringExtra("side");
		this.avlPosition = getIntent().getStringExtra("avlPosition");
		this.avgPrice = getIntent().getStringExtra("avgPrice");
		table = ContractUsdtInfoController.getInstance().queryContrackByName(contractName);
		tvQuantityAsks.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(table)));
		tvQuantityBids.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(table)));
		tvPrice.setText(String.format("%s(%s)", getResources().getString(R.string.price), table.quoteAsset));

		sellAdapter = new MultiTypeAdapter();

		int greenColor = getResources().getColor(R.color.res_green);
		int redColor = getResources().getColor(R.color.res_red);
		boolean isRedRise = SwitchUtils.isRedRise();
		int amountTextColor = getResources().getColor(R.color.res_textColor_2);
		buyBind = new OrderBookBidsItemBinder(false, greenColor, redColor, isRedRise, amountTextColor, Constants.CONTRACT_TYPE_USDT);
		sellAdapter.register(OrderModel.class, buyBind);
		buyBind.setOnItemClick(price -> {
			if (currentPriceType == 1 || TextUtils.isEmpty(price)) {
				return;
			}
			inputPrice.setText(price);
		});


		buyAdapter = new MultiTypeAdapter();
		sellBind = new OrderBookAsksItemBinder(true, greenColor, redColor, isRedRise, amountTextColor, Constants.CONTRACT_TYPE_USDT);
		sellBind.setUsedKline(false);
		buyAdapter.register(OrderModel.class, sellBind);
		sellBind.setOnItemClick(price -> {
			if (currentPriceType == 1 || TextUtils.isEmpty(price)) {
				return;
			}
			inputPrice.setText(price);
		});

		rlyBids.setLayoutManager(new LinearLayoutManager(this));
		rlyBids.setAdapter(buyAdapter);
		rlyAsks.setLayoutManager(new LinearLayoutManager(this));
		rlyAsks.setAdapter(sellAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (tradeDialog != null && tradeDialog.isShowing()) {
			tradeDialog.dismiss();
			tradeDialog = null;
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
	}
}
