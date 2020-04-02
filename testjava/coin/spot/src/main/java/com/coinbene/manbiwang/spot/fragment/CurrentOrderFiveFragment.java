package com.coinbene.manbiwang.spot.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.fragment.adapter.CurrentOrderFiveAdapter;
import com.coinbene.manbiwang.spot.fragment.adapter.SpotBottomTabAdapter;
import com.coinbene.manbiwang.spot.fragment.bean.AccountType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


/**
 * 交易页面当前委托--展示五条记录
 */
public class CurrentOrderFiveFragment extends CoinbeneBaseFragment {


	@BindView(R2.id.cb_hide_other_coin)
	CheckBox mCbHideOtherCoin;
	@BindView(R2.id.layout_hide_other_coin)
	LinearLayout mLayoutHideOtherCoin;
	@BindView(R2.id.tv_cancel_all_order)
	TextView mTvCancelAllOrder;
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;

	private CurrentOrderFiveAdapter mAdapter;

	private View footerView;

	private AccountType accountType;

	private BaseUserEventListener userEventListener;

	public static CurrentOrderFiveFragment newInstance(AccountType accountType) {
		Bundle args = new Bundle();
		args.putSerializable("accountType", accountType);
		CurrentOrderFiveFragment fragment = new CurrentOrderFiveFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public int initLayout() {
		return R.layout.spot_current_order_five_fragment;
	}


	@Override
	public void initView(View rootView) {
		footerView = getLayoutInflater().inflate(R.layout.commom_footer_look_all, null);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mAdapter = new CurrentOrderFiveAdapter();
		mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_base_empty, null));
		mAdapter.bindToRecyclerView(mRecyclerView);
	}

	@Override
	public void setListener() {
		mLayoutHideOtherCoin.setOnClickListener(v -> {
			mCbHideOtherCoin.setChecked(!mCbHideOtherCoin.isChecked());
			setHideOtherCoin(mCbHideOtherCoin.isChecked());
			getCurrentOrderList();
		});

		mTvCancelAllOrder.setOnClickListener(v -> cancalAllOrder());

		footerView.setOnClickListener(v -> {
				switch (accountType) {
					case SPOT:
						ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.SPOT_CURRENT_ORDER);
						break;
					case MARGIN:
						ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.MARGIN_CURRENT_ORDER);
						break;
				}
			}
		);

		mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			CurOrderListModel .DataBean.ListBean item = (CurOrderListModel.DataBean.ListBean) adapter.getItem(position);
			if (view.getId() == R.id.cancel_tv) {
				//撤销当前订单
				doCancelOrder(item.getOrderId());
			} else if (view.getId() == R.id.ll_root) {
				//切换交易对
				if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
					((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).changeTradePair(item.baseAsset + "/" + item.quoteAsset);
				}
			}
		});
	}

	@Override
	public void initData() {
		accountType = (AccountType) getArguments().getSerializable("accountType");
	}


	@Override
	public void onFragmentShow() {
		mCbHideOtherCoin.setChecked(getHideOtherCoin());

		if (ServiceRepo.getUserService().getUserStatus() == UserStatus.LOGIN) {
			mTvCancelAllOrder.setVisibility(View.VISIBLE);
		} else {
			mTvCancelAllOrder.setVisibility(View.GONE);
			mAdapter.setNewData(null);
		}

		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onCurorderChanged() {
					getCurrentOrderList();
				}
			};
		}

		if (accountType == AccountType.MARGIN) {
			UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.MARGIN, userEventListener);
		} else {
			UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.SPOT, userEventListener);
		}
		getCurrentOrderList();
	}

	@Override
	public void onFragmentHide() {
		if (accountType == AccountType.MARGIN) {
			UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.MARGIN, userEventListener);
		} else {
			UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.SPOT, userEventListener);
		}
	}

//	@AddFlowControl(strategy = FlowControlStrategy.throttleFirst,timeInterval = 200)
	@NeedLogin
	private void getCurrentOrderList() {


		HttpParams params = new HttpParams();
		params.put("pageNum", 1);
		params.put("pageSize", 10);
		if (accountType == AccountType.MARGIN) {
			params.put("accountType", "margin");
		}
		if (mCbHideOtherCoin.isChecked()) {
			params.put("symbol", getSymbol());
		}

		OkGo.<CurOrderListModel>get(Constants.USER_CURORDER_LIST).params(params).tag(this).execute(new NewJsonSubCallBack<CurOrderListModel>() {
			@Override
			public void onSuc(Response<CurOrderListModel> response) {
				if (response.body() != null && response.body().data != null) {
					if (response.body().data.list != null && response.body().data.list.size() > 5) {
						//超过五个显示footerView
						if (mAdapter.getFooterLayoutCount() == 0) {
							mAdapter.addFooterView(footerView);
						}
						setAdapterData(mAdapter, response.body().data.list.subList(0, 5));
					} else {
						mAdapter.removeAllFooterView();
						setAdapterData(mAdapter, response.body().data.list);
					}
				} else {
					mAdapter.removeAllFooterView();
					setAdapterData(mAdapter, null);
				}
			}

			@Override
			public void onE(Response<CurOrderListModel> response) {
			}
		});
	}


	public void doCancelOrder(String orderId) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("orderId", orderId);
		long startTime = System.currentTimeMillis();
		OkGo.<BaseRes>post(Constants.TRADE_ORDER_CANCEL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes t = response.body();
				if (t.isSuccess()) {
					DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appCancelOrder,
							new DataCollectionModel(orderId, DataCollectionModel.SPOT,
									(response.getRawResponse().receivedResponseAtMillis()-response.getRawResponse().sentRequestAtMillis())+""));

					ToastUtil.show(R.string.cancel_entrust_success);

					getCurrentOrderList();
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}


	private void cancalAllOrder() {
		if (mAdapter == null || mAdapter.getData() == null || mAdapter.getData().size() == 0) {
				return;
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setMessage(mCbHideOtherCoin.isChecked() ? String.format(getString(R.string.sure_cancel_all), getSymbol()) : String.format(getString(R.string.sure_cancel_all), ""));
		dialog.setPositiveButton(getString(R.string.btn_ok), (dialog12, which) -> {
			doCancelAllOrder();
			dialog12.dismiss();
		});
		dialog.setNegativeButton(getContext().getString(R.string.btn_cancel), (dialog16, which) -> dialog16.dismiss());
		dialog.show();
	}



	public void doCancelAllOrder() {
		HttpParams params = new HttpParams();

		if (accountType == AccountType.MARGIN) {
			params.put("accountType", "margin");
		} else if (accountType == AccountType.SPOT) {
			params.put("accountType", "spot");
		}

		if (mCbHideOtherCoin.isChecked()) {
			String symbol = getSymbol();
			if (!TextUtils.isEmpty(symbol) && symbol.contains("/")) {
				symbol = symbol.replace("/", "");
			}
			params.put("tradePair", symbol);
		}

		OkGo.<BaseRes>post(Constants.TRADE_ORDER_CANCEL_ALL).params(params).tag(this).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes t = response.body();
				if (t.isSuccess()) {
					ToastUtil.show(R.string.cancel_success);
					getCurrentOrderList();
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	private String getSymbol() {
		if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
			return ((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).getTradePair();
		}
		return "";
	}


	private boolean getHideOtherCoin() {
		if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
			return ((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).getHideOtherCoin();
		}
		return false;
	}


	private void setHideOtherCoin(boolean hideOtherCoin) {
		if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
			((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).setHideOtherCoin(hideOtherCoin);
		}
	}
}
