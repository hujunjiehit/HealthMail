package com.coinbene.manbiwang.balance.activity.spot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.app.RecyclerItemDecoration;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.adapter.BalanceDetailMarketBinder;
import com.coinbene.manbiwang.model.http.CoinTotalInfoModel;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;
import com.coinbene.manbiwang.service.RouteHub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author huyong
 * 现货资产详情
 */
public class BalanceDetailActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.iv_close)
	ImageView ivClose;
	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;
	@BindView(R2.id.menu_title_tv)
	TextView menuTitleTv;
	@BindView(R2.id.menu_right_tv)
	TextView menuRightTv;
	@BindView(R2.id.search_img)
	ImageView searchImg;
	@BindView(R2.id.menu_line_view)
	View menuLineView;
	@BindView(R2.id.iv_coin_icon)
	ImageView ivCoinIcon;
	@BindView(R2.id.tv_coin_name)
	TextView tvCoinName;
	@BindView(R2.id.tv_coin_local_name)
	TextView tvCoinLocalName;
	@BindView(R2.id.tv_total_price)
	TextView tvTotalPrice;
	@BindView(R2.id.tv_total_price_value)
	TextView tvTotalPriceValue;
	@BindView(R2.id.tv_available_price)
	TextView tvAvailablePrice;
	@BindView(R2.id.tv_available_price_value)
	TextView tvAvailablePriceValue;
	@BindView(R2.id.tv_frozen_price)
	TextView tvFrozenPrice;
	@BindView(R2.id.tv_frozen_price_value)
	TextView tvFrozenPriceValue;
	@BindView(R2.id.tv_valuation_btc)
	TextView tvValuationBtc;
	@BindView(R2.id.tv_valuation_btc_value)
	TextView tvValuationBtcValue;
	@BindView(R2.id.tv_valuation_local)
	TextView tvValuationLocal;
	@BindView(R2.id.tv_valuation_local_value)
	TextView tvValuationLocalValue;
	@BindView(R2.id.tv_go_to_trade)
	TextView tvGoToTrade;
	@BindView(R2.id.rl_market)
	RecyclerView rlMarket;
	public static final int REQUEST_CODE = 200;
	@BindView(R2.id.rl_coin_info)
	RelativeLayout rlCoinInfo;
	@BindView(R2.id.tv_deposit)
	TextView tvDeposit;
	@BindView(R2.id.tv_withdraw)
	TextView tvWithdraw;
	@BindView(R2.id.tv_transfer)
	TextView tvTransfer;

	private CoinTotalInfoModel.DataBean.ListBean bean;
	private MultiTypeAdapter mContentAdapter;
	private HashMap<String, TradePairMarketRes.DataBean> mapTradePair;
	private Set<String> tradePairSet;

	private String symbol;

	private NewMarketWebsocket.MarketDataListener listener;
	private List<WsMarketData> tradePairList;

	public static void startMe(Activity context, CoinTotalInfoModel.DataBean.ListBean bean, String symbol) {
		Intent intent = new Intent(context, BalanceDetailActivity.class);
		intent.putExtra("bean", bean);
		intent.putExtra("symbol", symbol);
		context.startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_balance_detail;
	}

	@Override
	public void initView() {
		init();
		getData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (listener == null) {
			listener = dataMap -> {
				if (tradePairList == null || tradePairList.size() == 0) {
					return;
				}
				synchronized (tradePairList) {
					for (int i = 0; i < tradePairList.size(); i++) {
						WsMarketData bean = tradePairList.get(i);
						if (dataMap.get(bean.getSymbol()) == null) {
							continue;
						}
						TradeUtils.getMarketDataFromMap(bean, dataMap);
					}
				}
				setData();
			};
		}
		NewMarketWebsocket.getInstance().registerMarketDataListener(listener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(listener);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.menu_back) {
			finish();
		} else if (id == R.id.tv_transfer) {
			//AccountTransferActivity.startActivity(this);
		}
	}

	private void getData() {
		boolean isRedRise = SwitchUtils.isRedRise();
		if (isRedRise) {
			tvDeposit.setBackground(getDrawable(R.drawable.small_btn_red_bg));
			tvWithdraw.setBackground(getDrawable(R.drawable.small_btn_green_bg));
		} else {
			tvDeposit.setBackground(getDrawable(R.drawable.small_btn_green_bg));
			tvWithdraw.setBackground(getDrawable(R.drawable.small_btn_red_bg));
		}

		menuTitleTv.setText(R.string.balance_detail);
		bean = (CoinTotalInfoModel.DataBean.ListBean) getIntent().getSerializableExtra("bean");
		symbol = getIntent().getStringExtra("symbol");
		if (bean == null || bean.getAsset() == null) {
			ToastUtil.show(R.string.toast_data_transfer_error);
			finish();
			return;
		}
		String urlpath = Constants.BASE_IMG_URL + bean.getAsset() + ".png";
		GlideUtils.loadImageViewLoad(this, urlpath, ivCoinIcon, R.drawable.coin_default_icon, R.drawable.coin_default_icon);

		tvCoinName.setText(bean.getAsset());
		if (bean.getAsset().contains(Constants.BRL)) {
			tvWithdraw.setVisibility(View.GONE);
			tvDeposit.setVisibility(View.GONE);
		}

//		if (SwitchUtils.isOpenContract_Asset() || SwitchUtils.isOpenOPT_Asset()) {
//			if (bean.getAsset().toUpperCase().equals("BTC")) {
//				tvTransfer.setVisibility(View.VISIBLE);
//			} else {
//				tvTransfer.setVisibility(View.GONE);
//			}
//		} else {
//			tvTransfer.setVisibility(View.GONE);
//		}
// 		tvTransfer.setOnClickListener(this);

		//资产详情页面不再显示划转按钮
		tvTransfer.setVisibility(View.GONE);

		tvCoinLocalName.setText(bean.getLocalAssetName());
		tvTotalPriceValue.setText(bean.getTotalBalance());
		tvAvailablePriceValue.setText(bean.getAvailableBalance());
		tvFrozenPriceValue.setText(bean.getFrozenBalance());
		tvValuationBtcValue.setText(bean.getPreestimateBTC());
		tvValuationLocal.setText(new StringBuilder().append(getString(R.string.valuation)).append("(").append(StringUtils.getCnyReplace(symbol)).append(")").toString());

		tvValuationLocalValue.setText(bean.getLocalPreestimate());

		mContentAdapter = new MultiTypeAdapter();
		mContentAdapter.register(WsMarketData.class, new BalanceDetailMarketBinder(BalanceDetailActivity.this));

		List<TradePairInfoTable> tradePairInfoTables = TradePairInfoController.getInstance().queryDataByBalanceName(bean.getAsset()+"/");//带/，这样保证分子都是一样的
		rlMarket.setAdapter(mContentAdapter);
		if (tradePairInfoTables != null && tradePairInfoTables.size() > 0) {
			mapTradePair = new HashMap<>();
			for (int i = 0; i < tradePairInfoTables.size(); i++) {
				TradePairInfoTable pairInfoTable = tradePairInfoTables.get(i);
				if (pairInfoTable == null) {
					continue;
				}
				TradePairMarketRes.DataBean bean = new TradePairMarketRes.DataBean();
				bean.setEnglishName(pairInfoTable.englishBaseAsset);
				bean.setChineseName(pairInfoTable.localBaseAsset);
				bean.setName(pairInfoTable.tradePairName);
				bean.setPrecision(pairInfoTable.pricePrecision);
				bean.setPairID(pairInfoTable.tradePair);
				mapTradePair.put(pairInfoTable.tradePair, bean);
			}

			if (tradePairSet == null) {
				tradePairSet = new HashSet<>();
			}
			tradePairSet.clear();
			Iterator<TradePairInfoTable> iterator = tradePairInfoTables.iterator();
			while (iterator.hasNext()) {
				TradePairInfoTable tradePairInfoTable = iterator.next();
				if (tradePairSet.contains(tradePairInfoTable.tradePair)) {
					iterator.remove();
				} else {
					tradePairSet.add(tradePairInfoTable.tradePair);
				}
			}

			if (tradePairList == null) {
				tradePairList = Collections.synchronizedList(new ArrayList<>());
			}
			for (int i = 0; i < tradePairInfoTables.size(); i++) {
				WsMarketData bean = new WsMarketData();
				bean.setEnglishName(tradePairInfoTables.get(i).englishBaseAsset);
				bean.setChineseName(tradePairInfoTables.get(i).localBaseAsset);
				bean.setTradePairName(tradePairInfoTables.get(i).tradePairName);
				bean.setPrecision(tradePairInfoTables.get(i).pricePrecision);
				bean.setSymbol(tradePairInfoTables.get(i).tradePair);
				tradePairList.add(bean);
			}
			NewMarketWebsocket.getInstance().pullMarketData();
		} else {
			tvGoToTrade.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void setData() {
		runOnUiThread(() -> {
			mContentAdapter.setItems(tradePairList);
			mContentAdapter.notifyDataSetChanged();
		});


	}

	private void init() {
		GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
		rlMarket.addItemDecoration(new RecyclerItemDecoration(DensityUtil.dip2px(10), 2));
		rlMarket.setLayoutManager(layoutManager);
		menuBack.setOnClickListener(this);
		tvDeposit.setOnClickListener(v -> {
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (userTable == null) {
				return;
			}

			if (TextUtils.isEmpty(userTable.phone) && !String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage(getString(R.string.dialog_recharge_content));
				dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> {
					ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(this);
					dialog1.dismiss();
				});
				dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog12, which) -> dialog12.dismiss());
				dialog.show();
				return;
			} else if (TextUtils.isEmpty(userTable.phone) && !userTable.deposit) {
				//如果手机号不为空，则不检查这部分;手机号未空，并且设置了deposit=false，才检查
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage(R.string.restricted_deposit);
				dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> dialog1.dismiss());
				dialog.show();
				return;
			}

			String str;
			BalanceInfoTable infoTable = BalanceController.getInstance().findByAsset(bean.getAsset());
			if (infoTable == null) {
				ToastUtil.show(R.string.query_fail);
				return;
			}
			if (!infoTable.deposit) {
				if (TextUtils.isEmpty(infoTable.banDepositReason)) {
					str = this.getResources().getString(R.string.cannot_deposit_tips);
					str = String.format(str, infoTable.asset);
				} else {
					//由于增加多原因 app端没有配  或者忘记配了  直接返回banDepositReason
					str = infoTable.banDepositReason + getString(R.string.cannot_deposit_tips_new);
				}
				ToastUtil.show(str);
				return;
			}
			DepositActivity.startMe(BalanceDetailActivity.this, infoTable.asset);
		});
		tvWithdraw.setOnClickListener(v -> {

			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (userTable == null) {
				return;
			}

			StringBuilder dialogContent = new StringBuilder();
			if (TextUtils.isEmpty(userTable.phone)) {
				boolean isOnlyPin = false;
				if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
					dialogContent.append(getString(R.string.dialog_email_verify));
				}
				if (!userTable.googleBind) {
					dialogContent.append("\n").append(getString(R.string.dialog_bind_google_verify));
				}
				if (!userTable.pinSetting) {
					//如果含有其他字段，表示需要先进入安全中心；否则直接进入资产设置
					isOnlyPin = dialogContent.length() <= 0;
					dialogContent.append("\n").append(getString(R.string.dialog_cap_modify_verify));
				}

				if (!TextUtils.isEmpty(dialogContent)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setTitle(getString(R.string.dialog_withdraw_title));
					dialog.setMessage(dialogContent);
					boolean finalIsOnlyPin = isOnlyPin;
					dialog.setPositiveButton(getString(R.string.btn_ok), (dialog16, which) -> {
						if (finalIsOnlyPin) {
							ARouter.getInstance().build(RouteHub.User.settingBalanceActivity).navigation(this);
						} else {
							ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(this);
						}
						dialog16.dismiss();
					});
					dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog13, which) -> dialog13.dismiss());
					dialog.show();
					return;
				}
			} else {
				if (!userTable.pinSetting) {
					dialogContent.append("\n").append(getString(R.string.dialog_cap_pwd_label));

					AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setMessage(dialogContent.toString());
					dialog.setPositiveButton(getString(R.string.btn_ok), (dialog14, which) -> {
						ARouter.getInstance().build(RouteHub.User.settingBalanceActivity).navigation(this);
						dialog14.dismiss();
					});
					dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog15, which) -> dialog15.dismiss());
					dialog.show();

					return;
				}
			}

			//提现被限制
			if (!userTable.withdraw) {
				//换绑手机号 24小时限制提币
				if (!TextUtils.isEmpty(userTable.withdrawBanReason) && Constants.CHANGE_PHONE.equals(userTable.withdrawBanReason)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(BalanceDetailActivity.this);
					dialog.setMessage(R.string.twentyFour_dont_withdray);
					dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> {
						dialog1.dismiss();
					});
					dialog.show();
					return;
				} else {
					AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setMessage(R.string.restricted_withdrawing);
					dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> {
						dialog1.dismiss();
					});
					dialog.show();
					return;
				}
			}

			BalanceInfoTable infoTable = BalanceController.getInstance().findByAsset(bean.getAsset());
			if (infoTable == null) {
				ToastUtil.show(R.string.query_fail);
				return;
			}

//			if (!infoTable.withdraw) {
//				String str = "";
//				if (TextUtils.isEmpty(infoTable.banWithdrawReason)) {
//					str = this.getResources().getString(R.string.cannot_withdraw_tips);
//					str = String.format(str, infoTable.asset);
//				} else {
//					str = infoTable.banWithdrawReason + getString(R.string.cannot_withdraw_tips_new);
//				}
//				ToastUtil.show(str);
//				return;
//			}

			WithDrawActivity.startMe(BalanceDetailActivity.this, infoTable.asset);
		});

	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

}
