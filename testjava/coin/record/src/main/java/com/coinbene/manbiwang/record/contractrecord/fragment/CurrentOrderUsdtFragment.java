package com.coinbene.manbiwang.record.contractrecord.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.model.http.ContractPlaceOrderModel;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.contractrecord.adapter.CurrentOrderUsdtAdapter;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.contract.BaseContractTotalListener;
import com.coinbene.manbiwang.service.record.ContractRefreshInterface;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-09
 * <p>
 * 合约当前委托 Fragment
 */
public class CurrentOrderUsdtFragment extends CoinbeneBaseFragment implements ContractRefreshInterface {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private CurrentOrderUsdtAdapter mAdapter;

	private int pageNum = 1;
	private int pageSize = 10;
	private int count;

	BaseUserEventListener userEventListener;

	public static CurrentOrderUsdtFragment newInstance() {
		Bundle args = new Bundle();
		CurrentOrderUsdtFragment fragment = new CurrentOrderUsdtFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.record_fragment_current_order;
	}

	@Override
	public void initView(View rootView) {
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mAdapter = new CurrentOrderUsdtAdapter();
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
			getCurrentDelegation();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getCurrentDelegation();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});

		mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			CurrentDelegationModel.DataBean.ListBean item = (CurrentDelegationModel.DataBean.ListBean) adapter.getItem(position);
			cancelOrder(item.getOrderId(), position);
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
	public void reFreshData(){
		mRecyclerView.scrollToPosition(0);
		pageNum = 1;
		getCurrentDelegation();
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
				public void onCurorderChanged() {
					pageNum = 1;
					getCurrentDelegation();
				}
			};
		}
		UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.USDT, userEventListener);

	}

	private Set<String> sets = new HashSet<>();

	public void getCurrentDelegation() {
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


		OkGo.<CurrentDelegationModel>get(Constants.CONTRACT_CURRENT_DELEGATION_USDT).params(params).tag(this).execute(new NewJsonSubCallBack<CurrentDelegationModel>() {
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

//		DLog.e("orderId", "cancelOrderRequest = " + orderId);
//		String lastId = orderId;
		long startTime = System.currentTimeMillis();
		OkGo.<ContractPlaceOrderModel>post(Constants.TRADE_CANCEL_ORDER_USDT).tag(this).params("orderId", orderId).execute(new DialogCallback<ContractPlaceOrderModel>(getActivity()) {
			@Override
			public void onSuc(Response<ContractPlaceOrderModel> response) {
				ToastUtil.show(R.string.cancel_entrust_success);

				if (position < mAdapter.getData().size()) {
					if (mAdapter.getEmptyView() == null) {
						mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
					}
					DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appCancelOrder,
							new DataCollectionModel(orderId, DataCollectionModel.CONTRACT,
									(response.getRawResponse().receivedResponseAtMillis()-response.getRawResponse().sentRequestAtMillis())+""));

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
