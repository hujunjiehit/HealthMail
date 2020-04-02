package com.coinbene.manbiwang.record.contractrecord.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.model.http.HistoryDelegationModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.contractrecord.OrderDetailBtcActivity;
import com.coinbene.manbiwang.record.contractrecord.adapter.HistoryOrderBtcAdapter;
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
 *
 * 合约历史委托 Fragment
 */
public class HistoryOrderBtcFragment extends CoinbeneBaseFragment implements ContractRefreshInterface {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private int pageNum = 1;
	private int pageSize = 10;

	private HistoryOrderBtcAdapter mAdapter;

	BaseUserEventListener userEventListener;

	public static HistoryOrderBtcFragment newInstance() {

		Bundle args = new Bundle();
		HistoryOrderBtcFragment fragment = new HistoryOrderBtcFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.record_fragment_history_order;
	}

	@Override
	public void initView(View rootView) {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mAdapter = new HistoryOrderBtcAdapter();
		mAdapter.setOnItemClickLisenter(item -> OrderDetailBtcActivity.startMe(getContext(), item));
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
			getHistoryDelegation();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getHistoryDelegation();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
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
		getHistoryDelegation();
	}
	@Override
	public void onFragmentHide() {
		UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.BTC, userEventListener);
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
				public void onHisorderChanged() {
					pageNum = 1;
					getHistoryDelegation();
				}
			};
		}
		UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.BTC, userEventListener);
	}

	private void getHistoryDelegation() {
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

		OkGo.<HistoryDelegationModel>get(Constants.CONTRACT_HISTORY_DELEGATION).params(params).tag(this).execute(new NewJsonSubCallBack<HistoryDelegationModel>() {

			@Override
			public void onSuc(Response<HistoryDelegationModel> response) {

				if (response.body() != null && response.body().getData() != null) {
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
