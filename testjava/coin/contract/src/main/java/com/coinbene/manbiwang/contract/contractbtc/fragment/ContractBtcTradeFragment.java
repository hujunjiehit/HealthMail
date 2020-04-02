package com.coinbene.manbiwang.contract.contractbtc.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.bean.ContractBottomTab;
import com.coinbene.manbiwang.contract.bean.ContractTabType;
import com.coinbene.manbiwang.contract.contractbtc.ContractBtcFragment;
import com.coinbene.manbiwang.contract.contractbtc.activity.ClosePositionBtcActivity;
import com.coinbene.manbiwang.contract.contractbtc.activity.ContractPlanBtcActivity;
import com.coinbene.manbiwang.contract.contractbtc.adapter.ContractBtcTypePageAdapter;
import com.coinbene.manbiwang.contract.contractbtc.adapter.HoldPositionBtcAdapter;
import com.coinbene.manbiwang.contract.contractbtc.layout.ContractBottomBtcLayout;
import com.coinbene.manbiwang.contract.contractbtc.layout.ContractCenterBtcLayout;
import com.coinbene.manbiwang.contract.contractbtc.presenter.ContractBtcPresenter;
import com.coinbene.manbiwang.contract.contractbtc.widget.ContractMenu;
import com.coinbene.manbiwang.contract.contractusdt.presenter.ContractInterface;
import com.coinbene.manbiwang.contract.dialog.ContractShareDialog;
import com.coinbene.manbiwang.contract.dialog.UpdateMarginDialog;
import com.coinbene.manbiwang.contract.listener.CenterViewGroupListener;
import com.coinbene.manbiwang.contract.listener.TopViewGroupListener;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.contract.BaseContractTotalListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.view.View.VISIBLE;

public class ContractBtcTradeFragment extends CoinbeneBaseFragment implements ContractInterface.View, CenterViewGroupListener, TopViewGroupListener {

	@BindView(R2.id.nest_scroll_view)
	NestedScrollView nestScrolView;
	@BindView(R2.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;

	@BindView(R2.id.include_center_layout)
	ContractCenterBtcLayout contractCenterBtcLayout;
	@BindView(R2.id.include_bottom_layout)
	ContractBottomBtcLayout contractBottomBtcLayout;

	@BindView(R2.id.tv_unrealized_total)
	TextView tvUnrealizedTotal;
	@BindView(R2.id.tv_unrealized_total_value)
	TextView tvUnrealizedTotalValue;
	@BindView(R2.id.layout_unrealized)
	ConstraintLayout layoutUnrealized;

	private ContractShareDialog contractShareDialog;
	private ContractInterface.Presenter presenter;

	private String currentContrackName;
	private int curDisplayType = Constants.DISPLAY_ORDER_LIST;
	private UpdateMarginDialog dialog;
	private ArrayList<ContractBottomTab> contractBottomTabs;
	private ContractBtcTypePageAdapter contractBtcTypePageAdapter;

	public void setCurrentContrackName(String currentContrackName) {
		this.currentContrackName = currentContrackName;
	}

	public static ContractBtcTradeFragment newInstance() {

		Bundle args = new Bundle();

		ContractBtcTradeFragment fragment = new ContractBtcTradeFragment();
		fragment.setArguments(args);
		return fragment;
	}


	/**
	 * 切换合约
	 *
	 * @param contractName
	 * @param tabIndex     0开仓  1平仓
	 * @param direction    持仓列表可平方向   （当点击持仓列表平仓的时候需要填充）
	 */
	public void changeContrack(String contractName, int tabIndex, String direction) {
		contractCenterBtcLayout.changeTabToClosePosition(tabIndex);
		if (TextUtils.isEmpty(contractName)) {
			return;
		}
		SpUtil.put(getContext(), SpUtil.CONTRACT_COIN, contractName);
		nestScrolView.scrollTo(0, 0);
		if (currentContrackName.equals(contractName)) {
			//如果是相同的合约  则直接填充   否则等到切换合约后通过boolean值看是否需要填充
			if (!TextUtils.isEmpty(direction)) {
				contractCenterBtcLayout.setAvlCloseAccount(direction);
			}
			return;
		}
		if (presenter != null) {
			//先取消上次的订阅  然后再赋值给当前的合约名称  再订阅当前的订阅

			NewContractBtcWebsocket.getInstance().changeSymbol(contractName);

			this.currentContrackName = contractName;
			setContractName(contractName, direction);

			//切换合约刷新当前账户信息
			presenter.refreshAccountInfo();
			NewMarketWebsocket.getInstance().pullMarketData();
		}
	}


	@Override
	public int initLayout() {
		return R.layout.fragment_contract_btc_trade;
	}

	@Override
	public void initView(View rootView) {
		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
	}

	@Override
	public void setListener() {
		swipeRefreshLayout.setOnRefreshListener(() -> {
			reFreshData();
			swipeRefreshLayout.postDelayed(this::stopRefresh, 500);
		});
		contractCenterBtcLayout.setCenterViewGroupListener(this);
		getContractBtcFragment().getContractBtcTopLayout().setTopViewGroupListener(this);
		contractBottomBtcLayout.setClickHoldPostion(new HoldPositionBtcAdapter.ClickHoldPostionListener() {
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
//                if (dialog == null) {
				dialog = new UpdateMarginDialog(getActivity());
//                }

				dialog.setSymbol(data.getSymbol());

				dialog.setPositionId(data.getId());

				dialog.show();

			}
		});


		getContractBtcFragment().getKchartImgview().setOnClickListener(v -> {
//
			//BTC 合约K线
			UIBusService.getInstance().openUri(v.getContext(),
					"coinbene://ContractKline?symbol=" + currentContrackName, null);
		});

		getContractBtcFragment().getMenuRightIv().setOnClickListener(v -> {
//            ContractGuideActivity.startMe(v.getContext());
			ContractMenu menu = new ContractMenu(getContext());
			menu.setDefaultSymbol(currentContrackName);
			menu.setDefaultLever(contractCenterBtcLayout.getCurrentLever() + "X");
			menu.showAtLocation(getContractBtcFragment().getMenuRightIv());
		});

//		getContractBtcFragment().getMingLottieView().setOnClickListener(v -> UIBusService.getInstance().openUri(getActivity(), UrlUtil.getContractMiningUrl(), null));
	}


	public ContractBtcFragment getContractBtcFragment() {
		Fragment fragment = getParentFragment();
//		if (fragment instanceof ContractBtcFragment) {
		return ((ContractBtcFragment) fragment);
//		}
//		return null;
	}


	@Override
	public void initData() {
		getContractBtcFragment().registerTotalListener(new BaseContractTotalListener() {
			@Override
			public void updateSymbol(String symbol) {
				//第一次初始化的时候 presenter == null
				if (presenter == null) {
					currentContrackName = symbol;
					presenter = new ContractBtcPresenter(ContractBtcTradeFragment.this, currentContrackName);
					contractCenterBtcLayout.setUserLogin();

					setContractName(currentContrackName, null);
					if (contractCenterBtcLayout != null) {
						contractCenterBtcLayout.changeTabToClosePosition(Constants.INDEX_OPEN);
					}
				} else {
					changeContrack(symbol, contractCenterBtcLayout.getCurrentIndex(), null);
				}
			}


			@Override
			public void parentFragmentShow() {
				if (presenter != null) {
					presenter.getFundingTime();
					presenter.subAll();
				}

			}

			@Override
			public void parentFramentHide() {
				if (presenter != null) {
					presenter.onPause();
					presenter.unsubAll();
				}
			}

		});
	}

	private void setContractName(String contrackName, String direction) {

		ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(contrackName);
		if (table == null) {
			return;
		}
		if (!TextUtils.isEmpty(direction)) {
			contractCenterBtcLayout.setNeedFillAvlCloseAccount(true, direction);
		}

		contractCenterBtcLayout.setInputPrecision(table.precision, table.minPriceChange);
		contractCenterBtcLayout.setNeedChangePrice(true);
		contractCenterBtcLayout.setGetUserUseLever(false);
		if (!ServiceRepo.getUserService().isLogin()) {
			contractCenterBtcLayout.setLever(0);
		}
		contractCenterBtcLayout.setAmountUnit(table.quoteAsset);
		contractCenterBtcLayout.clearFirstAndNewPrice();
		contractCenterBtcLayout.setContractInfoTable(table);
		presenter.setContractName(contrackName);

	}

	@Override
	public void onFragmentHide() {

		contractCenterBtcLayout.setNeedChangePrice(true);
	}

	@Override
	public void onFragmentShow() {

		contractCenterBtcLayout.setRedRase(SwitchUtils.isRedRise());
		contractBottomBtcLayout.setRedRase(SwitchUtils.isRedRise());
		contractCenterBtcLayout.setUserLogin();
		if (CommonUtil.isLoginAndUnLocked()) {
			reFreshData();

		} else {
//			contractTopLayout.setNoUser();
			layoutUnrealized.setVisibility(View.GONE);
			contractBottomBtcLayout.setNoUser();
			contractCenterBtcLayout.setNoUser();
		}

	}

	@Override
	public void clickOrderBook() {
		this.curDisplayType = Constants.DISPLAY_ORDER_LIST;
		contractCenterBtcLayout.setOrderType(curDisplayType);
		presenter.setOrderType(curDisplayType);
	}

	@Override
	public void clickOrderDetail() {
		this.curDisplayType = Constants.DISPLAY_ORDER_DETAIL;
		contractCenterBtcLayout.setOrderType(curDisplayType);
		presenter.setOrderType(Constants.DISPLAY_ORDER_DETAIL);
	}

	@Override
	public void clickPlaceOrder(int currentTradeDirection, int fixPriceType, int curHighLeverEntrust, int lever, String price, String account, String contractName,String profitPrice,String lossPrice) {
		presenter.placeOrder(currentTradeDirection, fixPriceType, curHighLeverEntrust, lever, price, account, getContractBtcFragment().getContractBtcTopLayout().getMarginMode(), contractName, profitPrice, lossPrice, getActivity());
	}

	@Override
	public void openContract() {
		presenter.agreeProtocol("btcContract_protocol", "1");
	}

	@Override
	public void agreeProtocol() {
		presenter.agreeProtocol("btcContract_protocol", "1");
	}


	@Override
	public void setPresenter(ContractInterface.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setOrderListData(List<OrderModel> buyList, List<OrderModel> sellList, WsMarketData tickerData, String contrackName) {
		if (currentContrackName.equals(contrackName)) {
			contractCenterBtcLayout.setOrderListData(buyList, sellList, tickerData);
		}
	}

	@Override
	public void setOrderDetail(List<WsTradeList> tradeDetailList, String contrackName) {
		if (currentContrackName == null) {
			return;
		}
		if (currentContrackName.equals(contrackName)) {
			contractCenterBtcLayout.setOrderDetailData(tradeDetailList);
		}
	}

	@Override
	public void setSettlement(long time) {
		contractCenterBtcLayout.initTimer(time);
	}

	@Override
	public void setQuoData(WsMarketData tickerData) {
		contractCenterBtcLayout.post(() -> setQuoteData(tickerData));
	}
	
	private synchronized void setQuoteData(WsMarketData tickerData) {
		if (tickerData == null || contractCenterBtcLayout == null) {
			return;
		}
		contractCenterBtcLayout.setF8(tickerData.getFundingRate());
	}

	@Override
	public void setCurLeverData(int lever) {
		if (contractCenterBtcLayout != null)
			contractCenterBtcLayout.setLever(lever);
	}

	@Override
	public void setContractAccountInfo(ContractAccountInfoModel.DataBean dataBean) {
		if (contractCenterBtcLayout == null || dataBean == null) {
			return;
		}

		getContractBtcFragment().getContractBtcTopLayout().setData(dataBean);
		getContractBtcFragment().setUnrealizedTotal(dataBean.getUnrealisedPnl());
		contractCenterBtcLayout.post(() -> {
			contractCenterBtcLayout.setUserData(dataBean.getAvailableBalance(), dataBean.getLongQuantity(), dataBean.getShortQuantity());
			contractCenterBtcLayout.calculationAvlOpen();
		});

	}

	@Override
	public void setPisitionListData(List<ContractPositionListModel.DataBean> listData) {
		if (contractBottomBtcLayout == null) {
			return;
		}
		contractBottomBtcLayout.setHoldPositionData(listData);

		calculateUnrealizedTotal(listData);


	}

	@Override
	public void setAllPisitionListData(List<ContractPositionListModel.DataBean> listData) {
		//更新持仓数量
		getContractBtcFragment().updateHoldPosition(listData.size(), ContractTabType.TAB_HOLD_POSITION);

		getContractBtcFragment().updateHoldPositionData();
	}

	private void calculateUnrealizedTotal(List<ContractPositionListModel.DataBean> listData) {
		if (listData == null || listData.size() == 0) {
			layoutUnrealized.setVisibility(View.GONE);
		} else {
			layoutUnrealized.setVisibility(VISIBLE);
			tvUnrealizedTotal.setText(listData.get(0).getSymbol() + getResources().getString(R.string.unrealized_profit_loss_total));
			String totalUnrealizedProfitLoss = TradeUtils.getTotalUnrealizedProfitLoss(listData);
			tvUnrealizedTotalValue.setText(totalUnrealizedProfitLoss);
			if (SwitchUtils.isRedRise()) {
				tvUnrealizedTotalValue.setTextColor(getResources().getColor(totalUnrealizedProfitLoss.contains("-") ? R.color.res_green : R.color.res_red));
			} else
				tvUnrealizedTotalValue.setTextColor(getResources().getColor(totalUnrealizedProfitLoss.contains("-") ? R.color.res_red : R.color.res_green));
		}
	}

	@Override
	public void setCurrentOrderData(List<CurrentDelegationModel.DataBean.ListBean> listData, int total) {
		//更新当前委托数量
		if (contractBottomBtcLayout == null || TextUtils.isEmpty(currentContrackName)) {
			return;
		}
		int orderCount = 0;
		for (CurrentDelegationModel.DataBean.ListBean data : listData) {
			if (currentContrackName.equals(data.getSymbol())){
				orderCount++;
			}
		}
		contractCenterBtcLayout.setCurrentOrderNumber(orderCount);
		getContractBtcFragment().updateHoldPosition(total, ContractTabType.TAB_CUR_ENTRUST);
	}

	@Override
	public void placeOrderSucces() {
		contractCenterBtcLayout.clearInputText();
	}

	/**
	 * @param mode 设置当前保证金模式
	 */
	@Override
	public void setMarginMode(String mode, String modeSetting) {
		if (getContractBtcFragment().getContractBtcTopLayout() != null)
			getContractBtcFragment().getContractBtcTopLayout().setMarginMode(mode, modeSetting);
	}

	@Override
	public void updateModeSuccess() {
		if (getContractBtcFragment().getContractBtcTopLayout() != null)
			getContractBtcFragment().getContractBtcTopLayout().updateModeSuccess();
	}


	/**
	 * 刷新用户相关的数据
	 */
	private void reFreshData() {
		if (CommonUtil.isLoginAndUnLocked()) {
			presenter.getCurLeverage();
			presenter.getContractAccountInfo();
			presenter.getPositionlist();
			presenter.getCurrentOrderList();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		presenter.onDestory();
	}


	private void stopRefresh() {
		if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
			swipeRefreshLayout.setRefreshing(false);
		}
	}


	@Override
	public void updatePositonMode(String mode) {
		presenter.setPositionMode(currentContrackName, mode);
	}

}

