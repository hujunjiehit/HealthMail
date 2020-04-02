package com.coinbene.manbiwang.contract.newcontract.bottom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.activity.ClosePositionBtcActivity;
import com.coinbene.manbiwang.contract.contractbtc.activity.ContractPlanBtcActivity;
import com.coinbene.manbiwang.contract.contractusdt.activity.ClosePositionUsdtActivity;
import com.coinbene.manbiwang.contract.contractusdt.activity.ContractPlanUsdtActivity;
import com.coinbene.manbiwang.contract.dialog.ContractShareDialog;
import com.coinbene.manbiwang.contract.dialog.UpdateMarginDialog;
import com.coinbene.manbiwang.contract.newcontract.ContractViewModel;
import com.coinbene.manbiwang.contract.newcontract.bottom.adapter.CurrentPositionAdapter;
import com.coinbene.manbiwang.contract.newcontract.closeposition.QuickClosePositionBoard;
import com.coinbene.manbiwang.model.contract.PriceParamsModel;
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
 * 全部持仓 Fragment
 */
public class CurrentPositionFragment extends CoinbeneBaseFragment {

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


	private CurrentPositionAdapter mAdapter;

	private ContractShareDialog contractShareDialog;
	private UpdateMarginDialog updateMarginDialog;

	BaseUserEventListener userEventListener;

	private ContractViewModel contractViewModel;
	private Observer<PriceParamsModel> paramsModelObserver;
	private Observer<Integer> unitTypeObserver;
	private Observer<String> symbolObserver;

	private ContractType contractType = ContractType.USDT;
	private String symbol;

	private QuickClosePositionBoard mQuickClosePositionBoard;

	public static CurrentPositionFragment newInstance() {
		Bundle args = new Bundle();
		CurrentPositionFragment fragment = new CurrentPositionFragment();
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
		mAdapter = new CurrentPositionAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
	}

	@Override
	public void setListener() {
		mAdapter.setClickHoldPostionListener(new CurrentPositionAdapter.ClickHoldPostionListener() {
			@Override
			public void clickShare(ContractPositionListModel.DataBean item) {
				if (TextUtils.isEmpty(item.getSymbol())) {
					return;
				}
				if (contractShareDialog == null) {
					contractShareDialog = new ContractShareDialog(getContext());
				}
				contractShareDialog.setOpenAveragePrice(item.getAvgPrice());
				contractShareDialog.setSymbol(item.getSymbol());
				contractShareDialog.setLever(item.getSide(), item.getLeverage());
				contractShareDialog.setAmountOfReturn(item.getUnrealisedPnl());
				contractShareDialog.setAsset(TradeUtils.getContractType(item.getSymbol()) == ContractType.USDT ? "USDT" : "BTC");
				contractShareDialog.setReturnRate(item.getRoe());
				contractShareDialog.setOwnerActivity(getActivity());
				if (NewMarketWebsocket.getInstance().getContractMarketMap() != null && NewMarketWebsocket.getInstance().getContractMarketMap().get(item.getSymbol()) != null) {
					contractShareDialog.setLatestPrice(NewMarketWebsocket.getInstance().getContractMarketMap().get(item.getSymbol()).getMarkPrice());
				}
				contractShareDialog.show();
			}

			@Override
			public void clickTargetProfit(ContractPositionListModel.DataBean item, int planType) {
				//点击止盈
				switch (contractType) {
					case USDT:
						ContractPlanUsdtActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
						break;
					case BTC:
						ContractPlanBtcActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
						break;
				}
			}

			@Override
			public void clickStopLoss(ContractPositionListModel.DataBean item, int planType) {
				//点击止损
				switch (contractType) {
					case USDT:
						ContractPlanUsdtActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
						break;
					case BTC:
						ContractPlanBtcActivity.startMe(getContext(), planType, item.getSymbol(), item.getSide(), item.getAvgPrice());
						break;
				}
			}

			@Override
			public void clickClosePosition(ContractPositionListModel.DataBean item) {
				//点击平仓
				switch (contractType) {
					case USDT:
						ClosePositionUsdtActivity.startMe(getContext(), item.getSymbol(), item.getSide(), item.getAvailableQuantity(), item.getAvgPrice());
						break;
					case BTC:
						ClosePositionBtcActivity.startMe(getContext(), item.getSymbol(), item.getSide(), item.getAvailableQuantity(), item.getAvgPrice());
						break;
				}
			}

			@Override
			public void clickQuickClosePosition(ContractPositionListModel.DataBean item) {
				//点击闪电平仓
				if (mQuickClosePositionBoard == null) {
					mQuickClosePositionBoard = new QuickClosePositionBoard(getActivity());
				}
				mQuickClosePositionBoard.initParams(item.getSymbol(), item.getSide(), item.getAvailableQuantity());
				mQuickClosePositionBoard.show();
			}

			@Override
			public void updateMargin(ContractPositionListModel.DataBean data) {
				if (updateMarginDialog == null) {
					updateMarginDialog = new UpdateMarginDialog(getActivity());
				}
				updateMarginDialog.setSymbol(data.getSymbol());
				updateMarginDialog.setContractType(contractType);
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

		mSwipeRefresh.setEnabled(false);
	}

	@Override
	public void initData() {
	}

	@Override
	public void onFragmentHide() {
		unregisterUserEvent();

		contractViewModel.getSymbol().removeObserver(symbolObserver);
		contractViewModel.getUnitType().removeObserver(unitTypeObserver);
		contractViewModel.getPriceParamsModel().removeObserver(paramsModelObserver);

		if (mQuickClosePositionBoard != null && mQuickClosePositionBoard.isShowing()) {
			mQuickClosePositionBoard.dismiss();
		}
	}

	@Override
	public void onFragmentShow() {

		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onPositionsChanged() {
					getPositionListImmediately();
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

						getPositionListImmediately();
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

		//标记价格变化刷新持仓
		if (paramsModelObserver == null) {
			paramsModelObserver = priceParamsModel -> {
				getPositionlist();
			};
		}
		contractViewModel.getPriceParamsModel().observe(this, paramsModelObserver);

		getPositionlist();
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

	@AddFlowControl(timeInterval = 3000)
	public void getPositionlist() {
		getPositionListImmediately();
	}

	public void getPositionListImmediately() {
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

		String url = contractType == ContractType.USDT ? Constants.ACCOUNT_POSITION_LIST_USDT : Constants.ACCOUNT_POSITION_LIST;
		OkGo.<ContractPositionListModel>get(url).tag(this).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
			@Override
			public void onSuc(Response<ContractPositionListModel> response) {
				if (response.body().getData() != null) {
					setAdapterData(mAdapter, response.body().getData());
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
