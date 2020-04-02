package com.coinbene.manbiwang.contract.newcontract.bottom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.newcontract.ContractViewModel;
import com.coinbene.manbiwang.contract.newcontract.bottom.adapter.CurrentOrderAdapter;
import com.coinbene.manbiwang.model.http.ContractPlaceOrderModel;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.ContractRefreshInterface;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-09
 * <p>
 * 合约当前委托 Fragment
 */
public class CurrentOrderFragment extends CoinbeneBaseFragment implements ContractRefreshInterface {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private CurrentOrderAdapter mAdapter;

	private int pageNum = 1;
	private int pageSize = 10;

	BaseUserEventListener userEventListener;
	private ContractViewModel contractViewModel;
	private Observer<Integer> unitTypeObserver;
	private Observer<String> symbolObserver;

	private ContractType contractType = ContractType.USDT;
	private String symbol;

	public static CurrentOrderFragment newInstance() {
		Bundle args = new Bundle();
		CurrentOrderFragment fragment = new CurrentOrderFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.contract_fragment_current_order;
	}

	@Override
	public void initView(View rootView) {
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mAdapter = new CurrentOrderAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
	}

	@Override
	public void setListener() {
		mAdapter.setOnLoadMoreListener(() -> {
			pageNum++;
			getCurrentOrderImmediately();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getCurrentOrder();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});
		mSwipeRefresh.setEnabled(false);

		mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			CurrentDelegationModel.DataBean.ListBean item = (CurrentDelegationModel.DataBean.ListBean) adapter.getItem(position);
			cancelOrder(item.getOrderId(), position);
		});
	}

	@Override
	public void initData() {
	}



	@Override
	public void reFreshData(){
		mRecyclerView.scrollToPosition(0);
		pageNum = 1;
		getCurrentOrder();
	}

	@Override
	public void onFragmentHide() {
		unregisterUserEvent();

		contractViewModel.getSymbol().removeObserver(symbolObserver);
		contractViewModel.getUnitType().removeObserver(unitTypeObserver);
	}

	@Override
	public void onFragmentShow() {
		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onCurorderChanged() {
					pageNum = 1;
					getCurrentOrder();
				}
			};
		}
		registerUserEvent();

		if (symbolObserver == null) {
			symbolObserver = new Observer<String>() {
				@Override
				public void onChanged(String s) {
					symbol = s;
					if (isShowing){
						unregisterUserEvent();

						contractType = TradeUtils.getContractType(symbol);

						registerUserEvent();

						pageNum = 1;
						getCurrentOrderImmediately();
					} else {
						contractType = TradeUtils.getContractType(symbol);
					}
				}
			};
		}
		contractViewModel.getSymbol().observe(this, symbolObserver);

		//单位切换刷新UI
		if (unitTypeObserver == null) {
			unitTypeObserver = new Observer<Integer>() {
				@Override
				public void onChanged(Integer integer) {
					if (mAdapter != null && isShowing) {
						mAdapter.notifyDataSetChanged();
					}
				}
			};
		}
		contractViewModel.getUnitType().observe(this, unitTypeObserver);
	}

	private void registerUserEvent() {
		switch (contractType) {
			case USDT:
				UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.USDT, userEventListener);
				break;
			case BTC:
				UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.BTC, userEventListener);
				break;
		}
	}

	private void unregisterUserEvent() {
		switch (contractType) {
			case USDT:
				UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.USDT, userEventListener);
				break;
			case BTC:
				UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.BTC, userEventListener);
				break;
		}
	}

	@AddFlowControl
	public void getCurrentOrder() {
		getCurrentOrderImmediately();
	}

	private void getCurrentOrderImmediately() {
		if (ServiceRepo.getUserService().getUserStatus() == UserStatus.UN_LOGIN) {
			if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
				mSwipeRefresh.setRefreshing(false);
			}
			if (mAdapter.getEmptyView() == null) {
				mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
			}
			pageNum = 1;
			mAdapter.setNewData(null);
			return;
		}

		HttpParams params = new HttpParams();
		params.put("pageNum", pageNum);
		params.put("pageSize", pageSize);

		String url = contractType == ContractType.USDT ? Constants.CONTRACT_CURRENT_DELEGATION_USDT : Constants.CONTRACT_CURRENT_DELEGATION;
		OkGo.<CurrentDelegationModel>get(url).params(params).tag(this).execute(new NewJsonSubCallBack<CurrentDelegationModel>() {
			@Override
			public void onSuc(Response<CurrentDelegationModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					setAdapterData(mAdapter, response.body().getData().getList(), pageNum, pageSize);
				}
			}

			@Override
			public void onE(Response<CurrentDelegationModel> response) {

			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
			}
		});
	}

	/**
	 * @param orderId 订单ID
	 *                撤单接口
	 */
//	@AddFlowControl(strategy = FlowControlStrategy.throttleFirst, timeInterval = 1000)
	public void cancelOrder(String orderId, int position) {

		String url = contractType == ContractType.USDT ? Constants.TRADE_CANCEL_ORDER_USDT : Constants.TRADE_CANCEL_ORDER;
		OkGo.<ContractPlaceOrderModel>post(url).tag(this).params("orderId", orderId).execute(new DialogCallback<ContractPlaceOrderModel>(getActivity()) {
			@Override
			public void onSuc(Response<ContractPlaceOrderModel> response) {
				ToastUtil.show(R.string.cancel_entrust_success);
				if (position < mAdapter.getData().size()) {
					if (mAdapter.getEmptyView() == null) {
						mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
					}
					DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appCancelOrder,
							new DataCollectionModel(orderId, DataCollectionModel.CONTRACT,
									(response.getRawResponse().receivedResponseAtMillis()-response.getRawResponse().sentRequestAtMillis()) + ""));

					removeByOrderId(orderId);
				}
			}

			@Override
			public void onE(Response<ContractPlaceOrderModel> response) {

			}

			@Override
			public boolean getCancelAble() {
				return false;
			}
		});

	}

	private void removeByOrderId(String orderId) {
		if (TextUtils.isEmpty(orderId) || mAdapter.getData().size() == 0) {
			return;
		}
		for (int i = 0; i < mAdapter.getData().size(); i++) {
			if (orderId.equals(mAdapter.getData().get(i).getOrderId())) {
				mAdapter.remove(i);
				return;
			}
		}
	}
}
