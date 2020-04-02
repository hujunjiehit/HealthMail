package com.coinbene.manbiwang.contract.contractusdt.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractusdt.activity.ClosePositionUsdtActivity;
import com.coinbene.manbiwang.contract.contractusdt.activity.ContractPlanUsdtActivity;
import com.coinbene.manbiwang.contract.contractusdt.adapter.HoldPositionUsdtAdapter;
import com.coinbene.manbiwang.contract.dialog.ContractShareDialog;
import com.coinbene.manbiwang.contract.dialog.UpdateMarginDialog;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.contract.BaseContractTotalListener;
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
public class CurrentPositionUsdtFragment extends CoinbeneBaseFragment {

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


	private HoldPositionUsdtAdapter mAdapter;

	private ContractShareDialog contractShareDialog;
	private UpdateMarginDialog updateMarginDialog;

	BaseUserEventListener userEventListener;

	public static CurrentPositionUsdtFragment newInstance() {

		Bundle args = new Bundle();

		CurrentPositionUsdtFragment fragment = new CurrentPositionUsdtFragment();
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
		mAdapter = new HoldPositionUsdtAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();


	}

	@Override
	public void setListener() {
		mAdapter.setClickHoldPostionListener(new HoldPositionUsdtAdapter.ClickHoldPostionListener() {
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
				contractShareDialog.setAsset("USDT");
				contractShareDialog.setReturnRate(item.getRoe());
				contractShareDialog.setOwnerActivity(getActivity());
				if (NewContractUsdtWebsocket.getInstance().getTickerData() != null) {
					contractShareDialog.setLatestPrice(NewContractUsdtWebsocket.getInstance().getTickerData().getMarkPrice());
				}
				contractShareDialog.show();
			}

			@Override
			public void clickTargetProfit(ContractPositionListModel.DataBean item, int planType) {
				ContractPlanUsdtActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
			}

			@Override
			public void clickStopLoss(ContractPositionListModel.DataBean item, int planType) {
				ContractPlanUsdtActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
			}

			@Override
			public void clickClosePosition(ContractPositionListModel.DataBean item) {
				ClosePositionUsdtActivity.startMe(getContext(), item.getSymbol(), item.getSide(), item.getAvailableQuantity(), item.getAvgPrice());
			}

			@Override
			public void updateMargin(ContractPositionListModel.DataBean data) {
				if (updateMarginDialog == null) {
					updateMarginDialog = new UpdateMarginDialog(getActivity());
				}
				updateMarginDialog.setSymbol(data.getSymbol());
				updateMarginDialog.setPositionId(data.getId());
				updateMarginDialog.show();
			}
		});

		mSwipeRefresh.setOnRefreshListener(() -> {
			getPositionlist();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});
		mSwipeRefresh.setOnChildScrollUpCallback((parent, child) -> {
			if (mRecyclerView == null) {
				return false;
			}
			LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
			return linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0;

		});

		ServiceRepo.getContractService().registerTotalListener(getParentFragment(), new BaseContractTotalListener() {
			@Override
			public void updateContractUnit() {
				mAdapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public void initData() {

	}

	@Override
	public void onFragmentHide() {
		UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.USDT, userEventListener);
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
		UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.USDT, userEventListener);
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
		OkGo.<ContractPositionListModel>get(Constants.ACCOUNT_POSITION_LIST_USDT).tag(this).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
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
			}
		});
	}


	public void setUnrealizedTotal(String unrealizedTotal) {
		if (BigDecimalUtils.isEmptyOrZero(unrealizedTotal) && mAdapter.getData().size() == 0) {
			layoutUnrealized.setVisibility(View.GONE);
		} else {
			layoutUnrealized.setVisibility(VISIBLE);
//			String totalUnrealizedProfitLoss = TradeUtils.getTotalUnrealizedProfitLoss(unrealizedTotal);
			tvUnrealizedTotalValue.setText(unrealizedTotal);
			if (SwitchUtils.isRedRise()) {
				tvUnrealizedTotalValue.setTextColor(getResources().getColor(unrealizedTotal.contains("-") ? R.color.res_green : R.color.res_red));
			} else
				tvUnrealizedTotalValue.setTextColor(getResources().getColor(unrealizedTotal.contains("-") ? R.color.res_red : R.color.res_green));
		}
	}
}
