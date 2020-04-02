package com.coinbene.manbiwang.contract.newcontract.bottom.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.newcontract.ContractViewModel;
import com.coinbene.manbiwang.contract.newcontract.bottom.adapter.HistoryOrderAdapter;
import com.coinbene.manbiwang.model.http.HistoryDelegationModel;
import com.coinbene.manbiwang.service.RouteHub;
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
 * 合约历史委托 Fragment
 */
public class HistoryOrderFragment extends CoinbeneBaseFragment implements ContractRefreshInterface {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private int pageNum = 1;
	private int pageSize = 10;

	private HistoryOrderAdapter mAdapter;

	BaseUserEventListener userEventListener;
	private ContractViewModel contractViewModel;
	private Observer<Integer> unitTypeObserver;
	private Observer<String> symbolObserver;

	private ContractType contractType = ContractType.USDT;
	private String symbol;

	public static HistoryOrderFragment newInstance() {
		Bundle args = new Bundle();
		HistoryOrderFragment fragment = new HistoryOrderFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.contract_fragment_history_order;
	}

	@Override
	public void initView(View rootView) {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mAdapter = new HistoryOrderAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
	}

	@Override
	public void setListener() {
		mAdapter.setOnItemClickLisenter(item -> {
			Bundle bundle = new Bundle();
			bundle.putSerializable("bean",item);
			ARouter.getInstance().build(contractType == ContractType.USDT ? RouteHub.Record.contractUsdtOrderDetail : RouteHub.Record.contractBtcOrderDetail).with(bundle).navigation(getContext());

		});

		mAdapter.setOnLoadMoreListener(() -> {
			pageNum++;
			getHistoryDelegationImmediately();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getHistoryDelegation();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});
		mSwipeRefresh.setEnabled(false);
	}

	@Override
	public void initData() {
		//reFreshData();
	}

	@Override
	public void onFragmentHide() {
		unregisterUserEvent();

		contractViewModel.getSymbol().removeObserver(symbolObserver);
		contractViewModel.getUnitType().removeObserver(unitTypeObserver);
	}

	@Override
	public void reFreshData() {
		mRecyclerView.scrollToPosition(0);
		pageNum = 1;
		getHistoryDelegation();
	}

	@Override
	public void onFragmentShow() {
		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onHisorderChanged() {
					pageNum = 1;
					getHistoryDelegation();
				}
			};
		}
		registerUserEvent();

		if (symbolObserver == null) {
			symbolObserver = new Observer<String>() {
				@Override
				public void onChanged(String s) {
					symbol = s;
					if (isShowing) {
						unregisterUserEvent();

						contractType = TradeUtils.getContractType(symbol);

						registerUserEvent();

						pageNum = 1;
						getHistoryDelegationImmediately();
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
	private void getHistoryDelegation() {
		getHistoryDelegationImmediately();
	}


	private void getHistoryDelegationImmediately() {
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

		String url = contractType == ContractType.USDT ? Constants.CONTRACT_HISTORY_DELEGATION_USDT : Constants.CONTRACT_HISTORY_DELEGATION;
		OkGo.<HistoryDelegationModel>get(url).params(params).tag(this).execute(new NewJsonSubCallBack<HistoryDelegationModel>() {
			@Override
			public void onSuc(Response<HistoryDelegationModel> response) {
				if (response.body() != null && response.body().getData() != null && response.body().getData().getList() != null) {

					setAdapterData(mAdapter, response.body().getData().getList(), pageNum, pageSize);
				}
			}

			@Override
			public void onE(Response<HistoryDelegationModel> response) {

			}

			@Override
			public void onFinish() {
				if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
			}
		});
	}
}
