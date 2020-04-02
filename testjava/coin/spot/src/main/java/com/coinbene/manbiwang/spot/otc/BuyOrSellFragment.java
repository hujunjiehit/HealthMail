package com.coinbene.manbiwang.spot.otc;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.OtcConfigController;
import com.coinbene.common.database.OtcPayTypeTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.MessageDialogBuilder;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.wrapper.LoadMoreWrapper;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.OrderDetailModel;
import com.coinbene.manbiwang.model.http.OtcAdListModel;
import com.coinbene.manbiwang.model.http.UserPayTypeModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.otc.adapter.OtcBuySellItemBinder;
import com.coinbene.manbiwang.spot.otc.dialog.OtcBuySellChangeListener;
import com.coinbene.manbiwang.spot.otc.dialog.OtcPayOrderListener;
import com.coinbene.manbiwang.spot.otc.dialog.OtcPlaceOrderDialog;
import com.coinbene.manbiwang.spot.otc.dialog.OtcUserNotPayDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * OTC 页面
 */
public class BuyOrSellFragment extends BaseFragment {

	private Unbinder mUnbinder;
	@BindView(R2.id.rl_list_buy)
	RecyclerView rl_list_buy;
	@BindView(R2.id.empty_layout)
	LinearLayout empty_layout;
	@BindView(R2.id.swipe_refresh_layout)
	SwipeRefreshLayout swipe_refresh_layout;

	private LoadMoreWrapper loadMoreWrapper;
	private OtcBuySellItemBinder buyAdapter;
	private boolean isEnd = false;
	private int current_page = 1;
	private int pageSize = 100;
	private int type = 2;//默认买入
	private String curCoinName = "";
	private OtcPlaceOrderDialog otcPlaceOrderDialog;
	private boolean isLooperStart = false;
	private Timer timer;
	private static final long PERIOD_TIME = 5000;//5秒刷新一次

	public OtcPlaceOrderDialog getOtcPlaceOrderDialog() {
		return otcPlaceOrderDialog;
	}

	private int getLayoutId() {
		return R.layout.spot_fragment_otc_buy;
	}

	public static BuyOrSellFragment newInstance(int type, String coinName) {
		Bundle args = new Bundle();
		args.putInt("type", type);
		args.putString("coinName", coinName);
		BuyOrSellFragment fragment = new BuyOrSellFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutId(), container, false);
		mUnbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initAdapter();
		initData();
	}

	public void setCurCoinName(String curCoinName) {
		this.curCoinName = curCoinName;
	}

	private void initData() {

		IntentFilter filterIntent = new IntentFilter();
		filterIntent.addAction(Constants.ORDER_LSIT_REFRESH);
		LocalBroadcastManager.getInstance(CBRepository.getContext()).registerReceiver(broadcastReceiver, filterIntent);

		assert getArguments() != null;
		type = getArguments().getInt("type");
		curCoinName = getArguments().getString("coinName");
		swipe_refresh_layout.setOnRefreshListener(() -> {

			//如果otc配置信息存在  则直接刷新广告列表 负责刷新otc配置信息后再刷新列表
			if (OtcConfigController.getInstance().checkAssetExist()) {
				refreshData();
			} else {
				reFreshOtcConfig();
			}
			swipe_refresh_layout.postDelayed(() -> {
				if (swipe_refresh_layout != null)
					swipe_refresh_layout.setRefreshing(false);
			}, 500);

		});
		OtcFragment otcFragment = (OtcFragment) getParentFragment();
		if (otcFragment != null)
			otcFragment.addChangeListener(changeListener);

		refreshData();
	}


	public void refreshData() {
		current_page = 1;
		getAdList();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (otcPlaceOrderDialog != null && otcPlaceOrderDialog.isShowing()) {
			otcPlaceOrderDialog.dismiss();
		}
	}

	private OtcPayOrderListener payOrderListener = new OtcPayOrderListener() {
		@Override
		public void payOrder(String adId, String usdtNum, int buyorsell) {
//            payOrderSub(adId,usdtNum,buyorsell);
		}

		@Override
		public void onClickBuyOrSell(OtcAdListModel.DataBean.ListBean bean) {
			//当用户是卖的时候  需要取用户绑定的支付方式和商家绑定的支付方式  取 重叠值
			if (!checkInput_IsRight(bean.getAdType())) {
				return;
			}

			if (bean.getAdType() == 1) {
				getUserPayType(bean);
			} else {
				if (otcPlaceOrderDialog == null) {
					otcPlaceOrderDialog = new OtcPlaceOrderDialog(getActivity());
					otcPlaceOrderDialog.setPayOrderLisenter((adId, price, buyorsell, payType) -> payOrderSub(adId, price, buyorsell, payType));
				}
				otcPlaceOrderDialog.show();
				otcPlaceOrderDialog.setData(bean, null);
			}
		}
	};

	private OtcBuySellChangeListener changeListener = () -> {
		if (getUserVisibleHint()) {
			refreshData();
		}
	};

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.ORDER_LSIT_REFRESH)) {
				if (getUserVisibleHint()) {
					refreshData();
				}
			}
		}
	};


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		//第一次的时候这个方法先执行  导致view为null，所有第一次 不执行他  之后都执行他
		if (isVisibleToUser && empty_layout != null) {
			refreshData();
		}


	}


	private void initAdapter() {
		//buy
		buyAdapter = new OtcBuySellItemBinder();
		rl_list_buy.setLayoutManager(new LinearLayoutManager(getActivity()));
		loadMoreWrapper = new LoadMoreWrapper(buyAdapter);
		rl_list_buy.setAdapter(loadMoreWrapper);
		buyAdapter.setLoadAdapter(loadMoreWrapper);
		swipe_refresh_layout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
		buyAdapter.setPayOrderListener(payOrderListener);
	}

	public void doRequestLooper() {
		if (isLooperStart) {
			return;
		}
		isLooperStart = true;
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				getAdList();
			}
		};
		timer.schedule(timerTask, 0, PERIOD_TIME);
	}


	public void stopRequestLooper() {
		if (!isLooperStart) {
			return;
		}
		isLooperStart = false;
		if (timer != null) {
			timer.cancel();
		}
	}

	private void getAdList() {
		if (TextUtils.isEmpty(curCoinName)) {
			if (empty_layout != null && getActivity() != null) {
				getActivity().runOnUiThread(() -> {
					empty_layout.setVisibility(View.VISIBLE);
					rl_list_buy.setVisibility(View.GONE);
					setRefresh();
				});

			}
			return;
		}
		HttpParams params = new HttpParams();
		params.put("asset", curCoinName);
		params.put("flag", type);// 针对商家  1买入   2卖出
		params.put("curPage", current_page);
		OtcFragment parentFragment = (OtcFragment) getParentFragment();
		if (parentFragment != null) {
			params.put("accountType", parentFragment.getAccountType());
			params.put("minCny", parentFragment.getPriceRange()[0]);
			params.put("maxCny", parentFragment.getPriceRange()[1]);
			params.put("currency", parentFragment.getCurCurrency());
		} else {
			params.put("accountType", Constants.PAY_TYPE_ALL);
			params.put("minCny", Constants.PRICE_TYPE_ALL[0]);
			params.put("maxCny", Constants.PRICE_TYPE_ALL[1]);
			params.put("currency", "CNY");
		}
		params.put("pageSize", pageSize);
		OkGo.<OtcAdListModel>post(Constants.GET_OTC_AD_LIST).params(params).tag(this).execute(new NewJsonSubCallBack<OtcAdListModel>() {
			@Override
			public void onSuc(Response<OtcAdListModel> response) {
				if (getActivity() != null && getActivity().isDestroyed()) {
					return;
				}
				if (BuyOrSellFragment.this.isDetached() || empty_layout == null) {
					return;
				}
				OtcAdListModel t = response.body();
				if (t != null && t.getData() != null && t.getData().getList() != null && t.getData().getList().size() > 0) {
					empty_layout.setVisibility(View.GONE);
					rl_list_buy.setVisibility(View.VISIBLE);
					List<OtcAdListModel.DataBean.ListBean> list = t.getData().getList();
					isEnd = true;
					loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
					//做了一步处理
					List<OtcAdListModel.DataBean.ListBean> orginData = t.getData().getList();
					for (int i = 0; i < orginData.size(); i++) {
						OtcAdListModel.DataBean.ListBean dataBean = orginData.get(i);
						BigDecimal usdt = new BigDecimal(dataBean.getStock());
						dataBean.setStock(PrecisionUtils.getRoundDown(usdt.toPlainString(), Constants.usdtPrecision));
						if (otcPlaceOrderDialog != null && otcPlaceOrderDialog.isShowing() && otcPlaceOrderDialog.getAdItem() != null) {
							if (dataBean.getAdId().equals(otcPlaceOrderDialog.getAdItem().getAdId()) && !dataBean.getPrice().equals(otcPlaceOrderDialog.getAdItem().getPrice())) {
								otcPlaceOrderDialog.setPriceChange(dataBean);
							}
						}

					}

					buyAdapter.setItems(orginData);
					loadMoreWrapper.notifyDataSetChanged();
				} else {
					if (current_page == 1) {
						empty_layout.setVisibility(View.VISIBLE);
						rl_list_buy.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void onE(Response<OtcAdListModel> response) {
			}

			@Override
			public void onFinish() {
				super.onFinish();
				setRefresh();
			}
		});
	}

	public void setIsRedRise(boolean isRedRise) {
		if (buyAdapter != null)
			buyAdapter.setRedRise(isRedRise);
	}

	private boolean isOnlyBindPayInfo = false;

	private void payOrderSub(String adId, String cny, int adType, int payType) {

		HttpParams params = new HttpParams();
		params.put("placeMoney", cny);
		params.put("productId", adId);
		params.put("productType", adType);// 针对商家  1买入   2卖出
		params.put("payId", payType);// 针对商家  1买入   2卖出
		params.put("accountType", 1);//固定的  现货账户
		OkGo.<OrderDetailModel>post(Constants.OTC_PAY_ORDER).tag(this).params(params).execute(new NewDialogJsonCallback<OrderDetailModel>(getActivity()) {
			@Override
			public void onSuc(Response<OrderDetailModel> response) {
				if (response.body() != null && response.body().getData() != null && response.body().getData().getOrder() != null && response.body().getData().getOrder().getOrderId() != null)
					OtcOrderDetailActivity.startMe(getActivity(), response.body().getData().getOrder().getOrderId(), 1);//这个地方不需要
				ToastUtil.show(getString(R.string.order_succes));
				if (otcPlaceOrderDialog != null && otcPlaceOrderDialog.isShowing())
					otcPlaceOrderDialog.dismiss();
			}

			@Override
			public void dealServerError(Response<OrderDetailModel> response) {
				//特殊处理后端返回的错误信息
				if (response.body() != null && !TextUtils.isEmpty(((BaseRes) response.body()).message)) {
					int code;
					code = response.body().code;
					String message = response.body().message;

					StringBuilder dialogContent = new StringBuilder();
					if (code == Constants.OTC_NOT_AUTH || code == Constants.OTC_NOT_BIND_PHONE) {
						dialogContent.append(message);
						isOnlyBindPayInfo = false;
					} else if (code == Constants.OTC_NOT_BIND_PAY_INFO) {//自定义错误提示,没用后端返回的
						dialogContent.append(getResources().getString(R.string.tips_pay_bind_info));
						isOnlyBindPayInfo = true;
					}
					if (dialogContent.toString().length() > 0) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setMessage(dialogContent.toString());
						dialog.setPositiveButton(R.string.go_setting, (dialog16, which) -> {
							if (isOnlyBindPayInfo) {
								ARouter.getInstance().build(RouteHub.User.settingBalanceActivity).navigation(getContext());
							} else {
								ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(getContext());
							}
							dialog16.dismiss();
						});
						dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog13, which) -> dialog13.dismiss());
						dialog.show();
					} else {
						if (!response.body().isExpired()) {
							ToastUtil.show(message);
						}
					}
				}
			}

			@Override
			public void onE(Response<OrderDetailModel> response) {
			}

		});
	}

	//    /**
//     * 检查用户的信息是否合法;true，合法
//     *
//     * @param adType
//     * @return
//     */
	boolean onlyBindPayInfo = false;

	private boolean checkInput_IsRight(int adType) {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null) {
			return false;
		}
		int count_line = 0;
		StringBuilder dialogContent = new StringBuilder();
		if (TextUtils.isEmpty(userTable.phone)) {//邮箱用户
			if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
				count_line++;
				dialogContent.append(Objects.requireNonNull(getActivity()).getString(R.string.dialog_email_verify));
			}
			if (dialogContent.toString().length() > 0) {
				dialogContent.append("\n");
			}
			count_line++;
			dialogContent.append(Objects.requireNonNull(getActivity()).getString(R.string.dialog_bing_phone));
		}
		boolean isKyc = userTable.kyc;
		if (!isKyc) {
			if (dialogContent.toString().length() > 0) {
				dialogContent.append("\n");
			}
			count_line++;
			dialogContent.append(getString(R.string.dialog_real_name_auth));
		}

		if (adType == 1) {//商家：1买入，2卖出；用户：1卖出，2 买入
			//法币卖出前提：--> 绑定支付信息
			boolean otcBinded = userTable.payment;
			if (!otcBinded) {
				if (dialogContent.toString().length() > 0) {
					dialogContent.append("\n");
				}
				count_line++;
				if (dialogContent.length() == 0) {
					onlyBindPayInfo = true;
				}
				dialogContent.append(getString(R.string.pay_bind_info));
			}
		}

		if (dialogContent.toString().length() > 0) {
			MessageDialogBuilder builder = DialogManager.getMessageDialogBuilder(getContext());
			builder.setPositiveButton(R.string.go_setting);
			builder.setNegativeButton(R.string.btn_cancel);

			if (count_line > 1) {
				builder.setTitle(R.string.dialog_withdraw_title);
				builder.setMessage(dialogContent.toString());
			} else {
				builder.setMessage(getString(R.string.please) + dialogContent.toString());
			}

			builder.showDialog();

			builder.setListener(new DialogListener() {
				@Override
				public void clickNegative() {

				}

				@Override
				public void clickPositive() {
					if (onlyBindPayInfo) {
						ARouter.getInstance().build(RouteHub.User.settingBalanceActivity).navigation(getContext());
					} else {
						ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(getContext());
					}
				}
			});

			return false;
		}
		return true;
	}

	private void setRefresh() {
		if (swipe_refresh_layout != null && swipe_refresh_layout.isRefreshing()) {
			swipe_refresh_layout.setRefreshing(false);
		}
	}


	public void getUserPayType(OtcAdListModel.DataBean.ListBean bean) {

		OkGo.<UserPayTypeModel>get(Constants.OTC_GET_USER_PAY_TYPE).tag(this).execute(new NewJsonSubCallBack<UserPayTypeModel>() {
			@Override
			public void onSuc(Response<UserPayTypeModel> response) {
				if (response.body().getData() == null || response.body().getData().getPaymentWayList() == null || response.body().getData().getPaymentWayList().size() == 0) {
					OtcUserNotPayDialog dialog = new OtcUserNotPayDialog(getContext());
					dialog.show();
					dialog.setData(bean);
				} else {
					if (bean.getPayTypes() != null && bean.getPayTypes().size() > 0) {
						List<OtcPayTypeTable> list = new ArrayList<>();

						for (OtcAdListModel.DataBean.ListBean.PayType shopsPay : bean.getPayTypes()) {
							for (UserPayTypeModel.DataBean.PaymentWayListBean userPay : response.body().getData().getPaymentWayList()) {
								if (shopsPay.getPayTypeId() == userPay.getType() && userPay.getOnline() == 1) {
									OtcPayTypeTable table = new OtcPayTypeTable();
									table.payTypeId = shopsPay.getPayTypeId();
									table.payTypeName = shopsPay.getPayTypeName();
									table.payId = userPay.getId();
									table.bankName = userPay.getBankName();
									list.add(table);
								}
							}
						}
						if (list.size() > 0) {
							if (otcPlaceOrderDialog == null) {
								otcPlaceOrderDialog = new OtcPlaceOrderDialog(getContext());
								otcPlaceOrderDialog.setPayOrderLisenter((adId, price, buyorsell, payType) -> payOrderSub(adId, price, buyorsell, payType));
							}
							otcPlaceOrderDialog.show();
							otcPlaceOrderDialog.setData(bean, list);
						} else {
							OtcUserNotPayDialog dialog = new OtcUserNotPayDialog(getContext());
							dialog.show();
							dialog.setData(bean);
						}

					}


				}
			}

			@Override
			public void onE(Response<UserPayTypeModel> response) {
			}

		});

	}

	public void cancelHttp() {
		OkGo.getInstance().cancelTag(this);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mUnbinder != null) {
			mUnbinder.unbind();
		}
		OkGo.getInstance().cancelTag(this);
		LocalBroadcastManager.getInstance(CBRepository.getContext()).unregisterReceiver(broadcastReceiver);
	}

	private void reFreshOtcConfig() {
		OtcFragment fragment = (OtcFragment) getParentFragment();
		if (fragment != null) {
			fragment.getOtcConfig();
		}
	}
}
