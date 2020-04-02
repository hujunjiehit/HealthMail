package com.coinbene.manbiwang.contract.contractbtc.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.activity.ClosePositionBtcActivity;
import com.coinbene.manbiwang.contract.contractbtc.activity.ContractPlanBtcActivity;
import com.coinbene.manbiwang.contract.contractbtc.adapter.HoldPositionBtcAdapter;
import com.coinbene.manbiwang.contract.dialog.ContractShareDialog;
import com.coinbene.manbiwang.contract.dialog.UpdateMarginDialog;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

import static android.view.View.VISIBLE;

/**
 * Created by june
 * on 2019-09-09
 * <p>
 * 当前持仓 Fragment
 */
public class CurrentPositionBtcFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	@BindView(R2.id.tv_unrealized_total)
	TextView tvUnrealizedTotal;
	@BindView(R2.id.tv_unrealized_total_value)
	TextView tvUnrealizedTotalValue;
	@BindView(R2.id.layout_unrealized)
	ConstraintLayout layoutUnrealized;
	@BindView(R2.id.cl_view)
	ConstraintLayout clView;


	private HoldPositionBtcAdapter mAdapter;

	private ContractShareDialog contractShareDialog;
	private UpdateMarginDialog updateMarginDialog;

	UsereventWebsocket.UserEventListener userEventListener;

	public static CurrentPositionBtcFragment newInstance() {

		Bundle args = new Bundle();

		CurrentPositionBtcFragment fragment = new CurrentPositionBtcFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.fragment_current_position;
	}

	@Override
	public void initView(View rootView) {
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mAdapter = new HoldPositionBtcAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();


	}

	@Override
	public void setListener() {
		mAdapter.setClickHoldPostionListener(new HoldPositionBtcAdapter.ClickHoldPostionListener() {
			@Override
			public void clickShare(ContractPositionListModel.DataBean item) {
				if (TextUtils.isEmpty(item.getSymbol())) {
					return;
				}
				if (contractShareDialog == null) {
					contractShareDialog = new ContractShareDialog(getContext());
					contractShareDialog.setOwnerActivity(getActivity());
				}
				contractShareDialog.setOpenAveragePrice(item.getAvgPrice());
				contractShareDialog.setSymbol(item.getSymbol());
				contractShareDialog.setLever(item.getSide(), item.getLeverage());
				contractShareDialog.setAmountOfReturn(item.getUnrealisedPnl());
				contractShareDialog.setAsset("BTC");
				contractShareDialog.setReturnRate(item.getRoe());
				contractShareDialog.setOwnerActivity(getActivity());
				if (NewContractBtcWebsocket.getInstance().getTickerData() != null) {
					contractShareDialog.setLatestPrice(NewContractBtcWebsocket.getInstance().getTickerData().getMarkPrice());
				}
				contractShareDialog.show();
			}

			@Override
			public void clickTargetProfit(ContractPositionListModel.DataBean item, int planType) {
				ContractPlanBtcActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
			}

			@Override
			public void clickStopLoss(ContractPositionListModel.DataBean item, int planType) {
				ContractPlanBtcActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
			}

			@Override
			public void clickClosePosition(ContractPositionListModel.DataBean item) {
				ClosePositionBtcActivity.startMe(getContext(), item.getSymbol(), item.getSide(), item.getAvailableQuantity(), item.getAvgPrice());
			}

			@Override
			public void updateMargin(ContractPositionListModel.DataBean data) {
//				if (updateMarginDialog == null) {
				updateMarginDialog = new UpdateMarginDialog(getActivity());
//				}
				updateMarginDialog.setSymbol(data.getSymbol());
				updateMarginDialog.setPositionId(data.getId());
				updateMarginDialog.show();
			}
		});

		mSwipeRefresh.setOnRefreshListener(() -> {
			getPositionlist();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});

		mSwipeRefresh.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
			@Override
			public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
				if (mRecyclerView == null) {
					return false;
				}
				LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
				return linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0;

			}
		});


	}

	@Override
	public void initData() {

	}

	@Override
	public void onFragmentHide() {
		UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.BTC, userEventListener);
	}

	@Override
	public void onFragmentShow() {
		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onPositionsChanged() {
					getPositionlist();
				}
			};
		}
		UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.BTC, userEventListener);
		getPositionlist();
	}

	@AddFlowControl
	public void getPositionlist() {
		if (ServiceRepo.getUserService().getUserStatus() == UserStatus.UN_LOGIN) {
			layoutUnrealized.setVisibility(View.GONE);
			if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
				mSwipeRefresh.setRefreshing(false);
			}
			if (mAdapter.getEmptyView() == null) {
				mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
			}
			mAdapter.setNewData(null);
			return;
		}

		OkGo.<ContractPositionListModel>get(Constants.ACCOUNT_POSITION_LIST).tag(this).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
			@Override
			public void onSuc(Response<ContractPositionListModel> response) {
				if (response.body().getData() != null) {
					setAdapterData(mAdapter, response.body().getData());
//					calculateUnrealizedTotal(response.body().getData());
				}
			}

			@Override
			public void onE(Response<ContractPositionListModel> response) {
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
				if (mAdapter.getEmptyView() == null) {
					mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
				}
			}
		});
	}


	public void setUnrealizedTotal(String unrealizedTotal) {
		if (BigDecimalUtils.isEmptyOrZero(unrealizedTotal) && mAdapter.getData().size() == 0) {
			layoutUnrealized.setVisibility(View.GONE);
		} else {
			layoutUnrealized.setVisibility(VISIBLE);
//			String totalUnrealizedProfitLoss = TradeUtils.getTotalUnrealizedProfitLoss(listData);
			tvUnrealizedTotalValue.setText(unrealizedTotal);
			if (SwitchUtils.isRedRise()) {
				tvUnrealizedTotalValue.setTextColor(getResources().getColor(unrealizedTotal.contains("-") ? R.color.res_green : R.color.res_red));
			} else
				tvUnrealizedTotalValue.setTextColor(getResources().getColor(unrealizedTotal.contains("-") ? R.color.res_red : R.color.res_green));
		}
	}
}
