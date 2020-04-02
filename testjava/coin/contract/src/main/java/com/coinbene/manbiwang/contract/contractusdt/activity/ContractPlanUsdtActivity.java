package com.coinbene.manbiwang.contract.contractusdt.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.MarketWebsosket;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.manbiwang.model.http.ContractAvlCloseContModel;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.websocket.WsContractQouteResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 合约止盈止损委托
 */
@SuppressLint("DefaultLocale")
public class ContractPlanUsdtActivity extends CoinbeneBaseActivity {


	@BindView(R2.id.iv_close)
	ImageView ivClose;
	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;
	@BindView(R2.id.menu_title_tv)
	TextView menuTitleTv;


	@BindView(R2.id.et_touch_price)
	PlusSubInputView etTouchPrice;
	@BindView(R2.id.et_entrust_price)
	PlusSubInputView etEntrustPrice;
	@BindView(R2.id.tv_unit_price)
	TextView tvUnitPrice;
	@BindView(R2.id.et_close_cont)
	PlusSubInputView etCloseCont;
	@BindView(R2.id.tv_avl_close_cont)
	TextView tvAvlCloseCont;
	@BindView(R2.id.tv_zero)
	TextView tvZero;
	@BindView(R2.id.tv_hundred)
	TextView tvHundred;
	@BindView(R2.id.seek_bar)
	BubbleSeekBar seekBar;
	@BindView(R2.id.tv_sure)
	TextView tvSure;
	@BindView(R2.id.tv_cur_hold)
	TextView tvCurHold;
	@BindView(R2.id.tv_expected_return)
	TextView tvExpectedReturn;
	@BindView(R2.id.tv_expected_return_value)
	TextView tvExpectedReturnValue;
	@BindView(R2.id.tv_contract_plan_tips)
	TextView tvContractPlanTips;
	@BindView(R2.id.tv_limit_price)
	TextView tvLimitPrice;
	@BindView(R2.id.tv_market_price)
	TextView tvMarketPrice;
	@BindView(R2.id.view_limit)
	View viewLimit;
	@BindView(R2.id.view_market)
	View viewMarket;

	private String side;
	private int planType;
	private String contractName;
	private String avgPrice;
	private ContractUsdtInfoTable table;
	private String avlCloseCont = "0";
	private boolean isChangeSeekBar;
	private int currentPriceType = Constants.FIXED_PRICE;

	public static void startMe(Context context, int planType, String contractName, String side, String avgPrice) {
		Intent intent = new Intent(context, ContractPlanUsdtActivity.class);
		intent.putExtra("planType", planType);
		intent.putExtra("contractName", contractName);
		intent.putExtra("side", side);
		intent.putExtra("avgPrice", avgPrice);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_contract_plan;
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
		menuBack.setOnClickListener(this);
		side = getIntent().getStringExtra("side");
		planType = getIntent().getIntExtra("planType", 0);
		contractName = getIntent().getStringExtra("contractName");
		avgPrice = getIntent().getStringExtra("avgPrice");
		table = ContractUsdtInfoController.getInstance().queryContrackByName(contractName);
		etCloseCont.setHint(String.format("%s(%s)", getString(R.string.close_cont), TradeUtils.getContractUsdtUnit(table)));
		etEntrustPrice.setHint(String.format("%s(%s)", getString(R.string.price), table.quoteAsset));


		etCloseCont.setMinPriceChange(TradeUtils.getContractUsdtMultiplier(table));
		etEntrustPrice.setMinPriceChange(table.minPriceChange);
		etTouchPrice.setMinPriceChange(table.minPriceChange);
		etEntrustPrice.setOnPlusSubListener(new PlusSubInputView.OnPlusSubListener() {
			@Override
			public void onPlus() {
				setLastPrice(etEntrustPrice);

			}

			@Override
			public void onSub() {
				setLastPrice(etEntrustPrice);
			}
		});
		etTouchPrice.setOnPlusSubListener(new PlusSubInputView.OnPlusSubListener() {
			@Override
			public void onPlus() {
				setLastPrice(etTouchPrice);

			}

			@Override
			public void onSub() {
				setLastPrice(etTouchPrice);
			}
		});
		tvLimitPrice.setOnClickListener(v -> {
			if (currentPriceType == Constants.FIXED_PRICE) {
				return;
			}
			etEntrustPrice.setHint(String.format("%s(%s)", getString(R.string.entrust_price_new), table.quoteAsset));
			etEntrustPrice.setEnablePlusSub(true);
			etEntrustPrice.setEnabled(true);

			currentPriceType = Constants.FIXED_PRICE;
			tvLimitPrice.setTextColor(getResources().getColor(R.color.res_blue));
			tvMarketPrice.setTextColor(getResources().getColor(R.color.res_textColor_1));
			viewLimit.setVisibility(View.INVISIBLE);
			viewMarket.setVisibility(View.VISIBLE);

			tvExpectedReturn.setVisibility(View.VISIBLE);
			tvExpectedReturnValue.setVisibility(View.VISIBLE);
		});

		tvMarketPrice.setOnClickListener(v -> {
			if (currentPriceType == Constants.MARKET_PRICE) {
				return;
			}
			etEntrustPrice.setHint(String.format("%s(%s)", getString(R.string.market_price), table.quoteAsset));
			etEntrustPrice.setEnablePlusSub(false);
			etEntrustPrice.setEnabled(false);
			currentPriceType = Constants.MARKET_PRICE;
			tvLimitPrice.setTextColor(getResources().getColor(R.color.res_textColor_1));
			tvMarketPrice.setTextColor(getResources().getColor(R.color.res_blue));
			viewLimit.setVisibility(View.VISIBLE);
			viewMarket.setVisibility(View.INVISIBLE);
			tvExpectedReturn.setVisibility(View.GONE);
			tvExpectedReturnValue.setVisibility(View.GONE);
		});


		TextWatcher watcherTouch = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etTouchPrice.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(etTouchPrice.getmEditText(), s, table.precision, table.minPriceChange);
				etTouchPrice.addTextChangedListener(this);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
		etTouchPrice.addTextChangedListener(watcherTouch);

		TextWatcher watcherEntrust = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etEntrustPrice.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(etEntrustPrice.getmEditText(), s, table.precision, table.minPriceChange);
				calculationReturn();
				etEntrustPrice.addTextChangedListener(this);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
		etEntrustPrice.addTextChangedListener(watcherEntrust);
		TextWatcher watcherCont = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (table != null) {
					if (TradeUtils.getContractUsdtPrecision(table) == 0) {
						etCloseCont.setInputType(InputType.TYPE_CLASS_NUMBER);
					} else {
						etCloseCont.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
						PrecisionUtils.setPrecision(etCloseCont.getmEditText(), s, TradeUtils.getContractUsdtPrecision(table));
					}

				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!isChangeSeekBar) {
					changeToSeekBar();
				}
				calculationReturn();
			}
		};
		etCloseCont.addTextChangedListener(watcherCont);


		seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

			@Override
			public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
				if (tvCurHold != null) {
					tvCurHold.setText(String.format("%s%d%%", getString(R.string.cur_position), progress));
					if (fromUser) {
						isChangeSeekBar = true;
						changeToCloseCont(progress);
						isChangeSeekBar = false;
					}
				}
			}

			@Override
			public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

			}

			@Override
			public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

			}
		});
		tvMarketPrice.performClick();
	}

	private void setLastPrice(PlusSubInputView plusSubInputView) {
		WsMarketData wsMarketData = NewMarketWebsocket.getInstance().getContractMarketMap().get(table.name);
		if (wsMarketData == null) {
			return;
		}
		plusSubInputView.setText(wsMarketData.getLastPrice());
	}

	private void changeToCloseCont(int progress) {
		if (BigDecimalUtils.compareZero(avlCloseCont) <= 0) {
			return;
		}
		BigDecimal bdAvl = new BigDecimal(avlCloseCont);
		BigDecimal bdPg = new BigDecimal(progress);
		BigDecimal onHundred = new BigDecimal(100);
		etCloseCont.setText(bdPg.divide(onHundred).multiply(bdAvl).setScale(TradeUtils.getContractUsdtPrecision(table), BigDecimal.ROUND_DOWN).toPlainString());
		etCloseCont.setSelection(etCloseCont.getText().length());
	}

	private void changeToSeekBar() {
		if (BigDecimalUtils.compareZero(avlCloseCont) <= 0) {
			seekBar.setProgress(0);
			return;
		}

		int persent = BigDecimalUtils.divideHalfUp(etCloseCont.getText(), avlCloseCont, 2);
		if (persent > 100 || persent < 0) {
			persent = 100;
		}
		seekBar.setProgress(persent);
	}


	private void calculationReturn() {
		if (currentPriceType == Constants.MARKET_PRICE) {
			return;
		}

		if (BigDecimalUtils.isEmptyOrZero(etEntrustPrice.getText())) {
			tvExpectedReturnValue.setText("-- USDT");
			return;
		}
		if (BigDecimalUtils.isEmptyOrZero(etCloseCont.getText())) {
			tvExpectedReturnValue.setText("-- USDT");
			return;
		}
		if (BigDecimalUtils.compareZero(avlCloseCont) <= 0) {
			tvExpectedReturnValue.setText("-- USDT");
			return;
		}

//		if (contractName.contains("BTC")) {
//			tvExpectedReturnValue.setText(String.format("%s BTC", CalculationUtils.calculateBtcExpectedReturn(
//					side,
//					avgPrice,
//					etEntrustPrice.getText().toString(),
//					etCloseCont.getText().toString())));
//		} else {
		tvExpectedReturnValue.setText(String.format("%s USDT", CalculationUtils.calculateEthExpectedReturnUsdtContract(
				side, avgPrice,
				etEntrustPrice.getText(),
				etCloseCont.getText(), table.multiplier,
				2)));
//		}
	}


	private void getData() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format(getString(R.string.forever_no_delivery),table.baseAsset));
		stringBuilder.append(side.equals("long") ? getString(R.string.long_order) : getString(R.string.short_order));
		stringBuilder.append(planType == 0 ? " " + getString(R.string.target_profit) : " " + getString(R.string.stop_loss));
		menuTitleTv.setText(stringBuilder.toString());

		/**
		 * 止盈：做多仓位需≥开仓均价，做空仓位需≤开仓均价。止损：做多仓位≤开仓均价，做空仓位≥开仓均价
		 */
		if (planType == 0) {//止盈
			if (side.equals("long")) {
				tvUnitPrice.setText(String.format("%s%s%s", "≥", avgPrice + " ", getString(R.string.open_unit_price)));
			} else {
				tvUnitPrice.setText(String.format("%s%s%s", "≤", avgPrice + " ", getString(R.string.open_unit_price)));
			}
			etTouchPrice.setHint(String.format("%s(%s)", getString(R.string.stop_profit_price), table.quoteAsset));
		} else {
			if (side.equals("long")) {
				tvUnitPrice.setText(String.format("%s%s%s", "≤", avgPrice + " ", getString(R.string.open_unit_price)));
			} else {
				tvUnitPrice.setText(String.format("%s%s%s", "≥", avgPrice + " ", getString(R.string.open_unit_price)));
			}
			etTouchPrice.setHint(String.format("%s(%s)", getString(R.string.stop_loss_price), table.quoteAsset));
		}


		getAvlCloseCont();
	}


	private void getAvlCloseCont() {
		HttpParams params = new HttpParams();
		params.put("symbol", contractName);
		params.put("planType", planType == 0 ? "takeProfit" : "stopLoss");
		params.put("direction", side.equals("long") ? "closeLong" : "closeShort");

		OkGo.<ContractAvlCloseContModel>get(Constants.CONTRACT_PLAN_QUANTITY_USDT).params(params).tag(this).execute(new DialogCallback<ContractAvlCloseContModel>(ContractPlanUsdtActivity.this) {
			@Override
			public void onSuc(Response<ContractAvlCloseContModel> response) {
				if (response.body().getData() != null) {
					avlCloseCont = response.body().getData().getQuantity();
					avlCloseCont = TradeUtils.getContractUsdtUnitValue(avlCloseCont, table);
					etCloseCont.setMax(avlCloseCont);
					tvAvlCloseCont.setText(String.format("%s %s%s", getString(R.string.avl_close_cont), avlCloseCont, TradeUtils.getContractUsdtUnit(table)));
				}
			}

			@Override
			public void onE(Response<ContractAvlCloseContModel> response) {

			}

		});
	}


	private void init() {
		tvContractPlanTips.setBackground(ResourceProvider.getRectShape(4, getResources().getColor(R.color.res_blue_10)));
		tvSure.setOnClickListener(this);
		seekBar.setProgress(0);
		tvCurHold.setText(String.format("%s0%%", getString(R.string.cur_position)));

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_sure) {
			submit();
		} else if (id == R.id.menu_back) {
			KeyboardUtils.hideKeyboard(etCloseCont);
			finish();
		}
	}

	private void submit() {
		if (BigDecimalUtils.isEmptyOrZero(etTouchPrice.getText())) {
			ToastUtil.show(R.string.please_input_touch_price);
			return;
		}
		if (BigDecimalUtils.isEmptyOrZero(etEntrustPrice.getText()) && currentPriceType == Constants.FIXED_PRICE) {
			ToastUtil.show(R.string.please_input_entrust_price);
			return;
		}
		if (BigDecimalUtils.isEmptyOrZero(etCloseCont.getText())) {
			ToastUtil.show(R.string.please_input_close_cont);
			return;
		}
		if (BigDecimalUtils.compareZero(avlCloseCont) <= 0) {
			ToastUtil.show(R.string.not_close_position);
			return;
		}

		if (currentPriceType == Constants.FIXED_PRICE) {
			if (planType == 0) {//止盈
				if (side.equals("long")) {
					if (BigDecimalUtils.isLessThan(etEntrustPrice.getText(), avgPrice)) {
						ToastUtil.show(R.string.entrust_price_than_avg_price);
						return;
					}

				} else {
					if (BigDecimalUtils.isGreaterThan(etEntrustPrice.getText(), avgPrice)) {
						ToastUtil.show(R.string.entrust_price_less_avg_price);
						return;
					}
				}
			} else {

				if (side.equals("long")) {
					if (BigDecimalUtils.isGreaterThan(etEntrustPrice.getText(), avgPrice)) {
						ToastUtil.show(R.string.entrust_price_less_avg_price);
						return;
					}

				} else {
					if (BigDecimalUtils.isLessThan(etEntrustPrice.getText(), avgPrice)) {
						ToastUtil.show(R.string.entrust_price_than_avg_price);
						return;
					}
				}
			}
		}
		if (!TradeUtils.checkContractUsdtInput(etCloseCont.getText(), table)) {
			ToastUtil.show(String.format("%s%s", getString(R.string.usdt_contract_min_place_order), table.multiplier));
			return;
		}


		planCloseOrder();

	}


	private void planCloseOrder() {
		HttpParams params = new HttpParams();
		params.put("symbol", contractName);
		params.put("orderType", currentPriceType == Constants.FIXED_PRICE ? "planLimit" : "planMarket");//订单类型，planLimit，计划限价  planMarket计划市价
		params.put("triggerPrice", etTouchPrice.getText());
		params.put("orderPrice", currentPriceType == Constants.FIXED_PRICE ? etEntrustPrice.getText() : null);
		params.put("quantity", TradeUtils.getContractUsdtPlaceOrderValue(etCloseCont.getText(), table));
		params.put("planType", planType == 0 ? "takeProfit" : "stopLoss");
		params.put("direction", side.equals("long") ? "closeLong" : "closeShort");

		OkGo.<BaseRes>post(Constants.CONTRACT_PLAN_CLOSE_ORDER_USDT).params(params).tag(this).execute(new DialogCallback<BaseRes>(ContractPlanUsdtActivity.this) {
			@Override
			public void onSuc(Response<BaseRes> response) {

				ToastUtil.show(R.string.entrustSuc);
				finish();
			}

			@Override
			public void onE(Response<BaseRes> response) {
				finish();
			}

		});
	}


}