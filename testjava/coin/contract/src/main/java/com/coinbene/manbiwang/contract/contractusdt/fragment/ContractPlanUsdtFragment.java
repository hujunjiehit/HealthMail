package com.coinbene.manbiwang.contract.contractusdt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractusdt.adapter.ContractUsdtPlanAdapter;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.ContractPlanModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.contract.BaseContractTotalListener;
import com.coinbene.manbiwang.service.record.ContractRefreshInterface;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-09
 */
public class ContractPlanUsdtFragment extends CoinbeneBaseFragment implements ContractRefreshInterface {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private ContractUsdtPlanAdapter mAdapter;

	private int pageNum = 1;
	private int pageSize = 10;

	BaseUserEventListener userEventListener;

	public static ContractPlanUsdtFragment newInstance() {

		Bundle args = new Bundle();

		ContractPlanUsdtFragment fragment = new ContractPlanUsdtFragment();
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

		mAdapter = new ContractUsdtPlanAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();
	}

	@Override
	public void setListener() {
		mAdapter.setOnLoadMoreListener(() -> {
			pageNum++;
			getPlanOrderList();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getPlanOrderList();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});

		mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
			@Override
			public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
				ContractPlanModel.DataBean.ListBean item = (ContractPlanModel.DataBean.ListBean) adapter.getItem(position);
				cancelPlanOrder(item.getId());
			}
		});
		ServiceRepo.getContractService().registerTotalListener(this, new BaseContractTotalListener() {
			@Override
			public void updateContractUnit() {
				mAdapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public void initData() {
		reFreshData();
	}


	@Override
	public void reFreshData() {
		mRecyclerView.scrollToPosition(0);
		pageNum = 1;
		getPlanOrderList();

	}
	@Override
	public void onFragmentHide() {
		UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.USDT, userEventListener);
	}

	@Override
	public void onFragmentShow() {
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

		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onPlanorderChanged() {
					pageNum = 1;
					getPlanOrderList();
				}
			};
		}
		UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.USDT, userEventListener);
	}

	@AddFlowControl
	public void getPlanOrderList() {

		if (!CommonUtil.isLoginAndUnLocked()) {
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
		OkGo.<ContractPlanModel>get(Constants.CONTRACT_PLAN_ORDER_LIST_USDT).params(params).tag(this).execute(new NewJsonSubCallBack<ContractPlanModel>() {
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

		OkGo.<BaseRes>post(Constants.CONTRACT_CANCEL_PLAN_ORDER_USDT).params(params).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
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
