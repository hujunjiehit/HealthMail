package com.coinbene.manbiwang.contract.newcontract.bottom.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.enumtype.ContractType;
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
import com.coinbene.manbiwang.contract.newcontract.bottom.adapter.ContractPlanAdapter;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.ContractPlanModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.ContractRefreshInterface;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * 计划委托 -- 止盈止损 fragment
 */
public class ContractPlanFragment extends CoinbeneBaseFragment implements ContractRefreshInterface {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private ContractPlanAdapter mAdapter;

	private int pageNum = 1;
	private int pageSize = 10;

	private BaseUserEventListener userEventListener;
	private ContractViewModel contractViewModel;
	private Observer<Integer> unitTypeObserver;
	private Observer<String> symbolObserver;

	private ContractType contractType = ContractType.USDT;
	private String symbol;

	public static ContractPlanFragment newInstance() {
		Bundle args = new Bundle();
		ContractPlanFragment fragment = new ContractPlanFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.fragment_contract_plan;
	}

	@Override
	public void initView(View rootView) {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mAdapter = new ContractPlanAdapter();
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
			getPlanOrderListImmediately();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getPlanOrderList();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});
		mSwipeRefresh.setEnabled(false);

		mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
			@Override
			public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
				ContractPlanModel.DataBean.ListBean item = (ContractPlanModel.DataBean.ListBean) adapter.getItem(position);
				cancelPlanOrder(item.getId());
			}
		});
	}

	@Override
	public void initData() {
		//reFreshData();
	}


	@Override
	public void reFreshData() {
		mRecyclerView.scrollToPosition(0);
		pageNum = 1;
		getPlanOrderList();
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
				public void onPlanorderChanged() {
					pageNum = 1;
					getPlanOrderListImmediately();
				}
			};
		}
		registerUserEvent();

		if (symbolObserver == null) {
			symbolObserver = s -> {
				symbol = s;
				if (isShowing){
					unregisterUserEvent();

					contractType = TradeUtils.getContractType(symbol);

					registerUserEvent();

					pageNum = 1;
					getPlanOrderListImmediately();
				} else {
					contractType = TradeUtils.getContractType(symbol);
				}
			};
		}
		contractViewModel.getSymbol().observe(this, symbolObserver);

		//单位切换刷新UI
		if (unitTypeObserver == null) {
			unitTypeObserver = integer -> {
				if (mAdapter != null && isShowing) {
					mAdapter.notifyDataSetChanged();
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
	public void getPlanOrderList() {
		getPlanOrderListImmediately();
	}

	private void getPlanOrderListImmediately(){
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
		String url = contractType == ContractType.USDT ? Constants.CONTRACT_PLAN_ORDER_LIST_USDT : Constants.CONTRACT_PLAN_ORDER_LIST;
		OkGo.<ContractPlanModel>get(url).params(params).tag(this).execute(new NewJsonSubCallBack<ContractPlanModel>() {
			@Override
			public void onSuc(Response<ContractPlanModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					setAdapterData(mAdapter, response.body().getData().getList(), pageNum, pageSize);
				}
			}

			@Override
			public void onE(Response<ContractPlanModel> response) {

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

	public void cancelPlanOrder(String planId) {
		HttpParams params = new HttpParams();
		params.put("planId", planId);
		String url = contractType == ContractType.USDT ? Constants.CONTRACT_CANCEL_PLAN_ORDER_USDT : Constants.CONTRACT_CANCEL_PLAN_ORDER;
		OkGo.<BaseRes>post(url).params(params).tag(this).execute(new DialogCallback<BaseRes>(getActivity()) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.canceled_order);

				//重新刷新第一页数据
				pageNum = 1;
				getPlanOrderList();
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}


}
