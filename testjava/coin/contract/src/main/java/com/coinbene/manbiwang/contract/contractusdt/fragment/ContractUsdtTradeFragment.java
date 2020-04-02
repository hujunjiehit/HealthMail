package com.coinbene.manbiwang.contract.contractusdt.fragment;

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
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.bean.ContractTabType;
import com.coinbene.manbiwang.contract.contractusdt.ContractUsdtFragment;
import com.coinbene.manbiwang.contract.contractusdt.activity.ClosePositionUsdtActivity;
import com.coinbene.manbiwang.contract.contractusdt.activity.ContractPlanUsdtActivity;
import com.coinbene.manbiwang.contract.contractusdt.adapter.HoldPositionUsdtAdapter;
import com.coinbene.manbiwang.contract.contractusdt.layout.ContractUsdtBottomLayout;
import com.coinbene.manbiwang.contract.contractusdt.layout.ContractUsdtCenterLayout;
import com.coinbene.manbiwang.contract.contractusdt.presenter.ContractInterface;
import com.coinbene.manbiwang.contract.contractusdt.presenter.ContractUsdtPresenter;
import com.coinbene.manbiwang.contract.contractusdt.widget.ContractUsdtMenu;
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

public class ContractUsdtTradeFragment extends CoinbeneBaseFragment implements ContractInterface.View, CenterViewGroupListener, TopViewGroupListener {

	@BindView(R2.id.nest_scroll_view)
	NestedScrollView nestScrolView;
	@BindView(R2.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;

	@BindView(R2.id.include_center_layout)
	ContractUsdtCenterLayout contractCenterUsdtLayout;
	@BindView(R2.id.include_bottom_layout)
	ContractUsdtBottomLayout contractBottomBtcLayout;

	@BindView(R2.id.tv_unrealized_total)
	TextView tvUnrealizedTotal;
	@BindView(R2.id.tv_unrealized_total_value)
	TextView tvUnrealizedTotalValue;
	@BindView(R2.id.layout_unrealized)
	ConstraintLayout layoutUnrealized;

	private ContractShareDialog contractShareDialog;
	private ContractInterface.Presenter presenter;

	private String currentContrackName;
	//	private Map<String, WsContractQouteResponse.DataBean> contrackMap;
	private int curDisplayType = Constants.DISPLAY_ORDER_LIST;
	private UpdateMarginDialog dialog;
//	private ArrayList<ContractBottomTab> contractTabs;
//	private ContractBtcTypePageAdapter contractBtcTypePageAdapter;
	private ContractUsdtMenu menu;
	private ContractUsdtInfoTable table;

	public void setCurrentContrackName(String currentContrackName) {
		this.currentContrackName = currentContrackName;
	}

	public static ContractUsdtTradeFragment newInstance() {

		Bundle args = new Bundle();

		ContractUsdtTradeFragment fragment = new ContractUsdtTradeFragment();
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
		contractCenterUsdtLayout.changeTabToClosePosition(tabIndex);
		if (TextUtils.isEmpty(contractName)) {
			return;
		}
		SpUtil.put(getContext(), SpUtil.CONTRACT_COIN, contractName);
		nestScrolView.scrollTo(0, 0);
		if (currentContrackName.equals(contractName)) {
			//如果是相同的合约  则直接填充   否则等到切换合约后通过boolean值看是否需要填充
			if (!TextUtils.isEmpty(direction)) {
				contractCenterUsdtLayout.setAvlCloseAccount(direction);
			}
			return;
		}
		if (presenter != null) {
			//先取消上次的订阅  然后再赋值给当前的合约名称  再订阅当前的订阅

			NewContractUsdtWebsocket.getInstance().changeSymbol(contractName);

			this.currentContrackName = contractName;
			setContractName(contractName, direction);

			//切换合约刷新当前账户信息
			presenter.refreshAccountInfo();
		}
	}


	@Override
	public int initLayout() {
		return R.layout.fragment_contract_usdt_trade;
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
		contractCenterUsdtLayout.setCenterViewGroupListener(this);
		getParentContractFragment().getContractTopLayout().setTopViewGroupListener(this);
		contractBottomBtcLayout.setClickHoldPostion(new HoldPositionUsdtAdapter.ClickHoldPostionListener() {
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
//                if (dialog == null) {
				dialog = new UpdateMarginDialog(getActivity());
//                }

				dialog.setSymbol(data.getSymbol());

				dialog.setPositionId(data.getId());

				dialog.show();

			}
		});


		getParentContractFragment().getKchartImgview().setOnClickListener(v -> {
//
			//USDT 合约K线
			UIBusService.getInstance().openUri(v.getContext(),
					"coinbene://ContractKline?symbol=" + currentContrackName, null);
		});

		getParentContractFragment().getMenuRightIv().setOnClickListener(v -> ivRightClick());

//		getParentContractFragment().getMingLottieView().setOnClickListener(v -> UIBusService.getInstance().openUri(getActivity(), UrlUtil.getContractMiningUrl(), null));
	}

	private void ivRightClick() {
//		if (menu == null) {
//			menu = new ContractUsdtMenu(getContext());
//			menu.setOnMenuClickListener(() -> {
//				SelectorDialog selectorDialog = DialogManager.getSelectorDialog(getContext());
//				List<String> datas = new ArrayList<>();
//				datas.add(getString(R.string.number));
//				datas.add(table.baseAsset);
//				selectorDialog.setDatas(datas);
//				selectorDialog.setDefaultPosition(SpUtil.getContractUsdtUnitSwitch());
//				selectorDialog.show();
//				selectorDialog.setSelectListener(new SelectorDialog.SelectListener() {
//					@Override
//					public void onItemSelected(Object data, int positon) {
//						//选择的和之前保存的一样
//						if (SpUtil.getContractUsdtUnitSwitch() == positon) {
//							return;
//						}
//						//如果用户是登录状态则通过接口更新  否则本地刷新保存
//						if (ServiceRepo.getUserService().isLogin())
//							presenter.agreeProtocol("usdtContract_tradeUnit", String.valueOf(positon));
//						else {
//							SpUtil.setContractUsdtUnitSwitch(Integer.valueOf(positon));
//							ServiceRepo.getContractService().totalListenerUpdateUnit();
//							NewContractUsdtWebsocket.getInstance().pullOrderListData();
//							NewContractUsdtWebsocket.getInstance().pullTradeDetailData();
//						}
//					}
//				});
//			});
//		}
//		menu.setDefaultSymbol(currentContrackName);
//		menu.setDefaultLever(contractCenterUsdtLayout.getCurrentLever() + "X");
//		menu.showAtLocation(getParentContractFragment().getMenuRightIv());
	}


	public ContractUsdtFragment getParentContractFragment() {
		Fragment fragment = getParentFragment();
		if (fragment instanceof ContractUsdtFragment) {
			return ((ContractUsdtFragment) fragment);
		}
		return null;
	}


	@Override
	public void initData() {

		ServiceRepo.getContractService().registerTotalListener(getParentContractFragment(), new BaseContractTotalListener() {
			@Override
			public void updateSymbol(String symbol) {
				//第一次初始化的时候 presenter == null
				if (presenter == null) {
					currentContrackName = symbol;
					presenter = new ContractUsdtPresenter(ContractUsdtTradeFragment.this, currentContrackName);
					contractCenterUsdtLayout.setUserLogin();
					setContractName(currentContrackName, null);
					if (contractCenterUsdtLayout != null) {
						contractCenterUsdtLayout.changeTabToClosePosition(Constants.INDEX_OPEN);
					}
				} else {
					changeContrack(symbol, contractCenterUsdtLayout.getCurrentIndex(), null);
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

			@Override
			public void updateContractUnit() {
				if (contractCenterUsdtLayout != null) {
					contractCenterUsdtLayout.updataContractUnit();
					contractBottomBtcLayout.updataContractUnit();
					if(presenter!=null){
						presenter.getContractAccountInfo();
					}

				}

			}
		});
	}

	private void setContractName(String contrackName, String direction) {

		table = ContractUsdtInfoController.getInstance().queryContrackByName(contrackName);
		if (table == null) {
			return;
		}
		if (!TextUtils.isEmpty(direction)) {
			contractCenterUsdtLayout.setNeedFillAvlCloseAccount(true, direction);
		}

		contractCenterUsdtLayout.setInputPrecision(table.precision, table.minPriceChange);
		contractCenterUsdtLayout.setNeedChangePrice(true);
		contractCenterUsdtLayout.setGetUserUseLever(false);
		if (!CommonUtil.isLoginAndUnLocked()) {
			contractCenterUsdtLayout.setLever(0);
		}
		contractCenterUsdtLayout.setContractInfoTable(table);
		contractCenterUsdtLayout.setAmountUnit(table.quoteAsset);
		contractCenterUsdtLayout.clearFirstAndNewPrice();
		presenter.setContractName(contrackName);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onFragmentHide() {
		contractCenterUsdtLayout.setNeedChangePrice(true);
	}

	@Override
	public void onFragmentShow() {

		contractCenterUsdtLayout.setRedRase(SwitchUtils.isRedRise());

		contractBottomBtcLayout.setRedRase(SwitchUtils.isRedRise());
		contractCenterUsdtLayout.setUserLogin();
		if (CommonUtil.isLoginAndUnLocked()) {
			reFreshData();
			contractCenterUsdtLayout.updataContractUnit();
		} else {
//			contractTopLayout.setNoUser();
			layoutUnrealized.setVisibility(View.GONE);
			contractBottomBtcLayout.setNoUser();
			contractCenterUsdtLayout.setNoUser();
		}

	}

	@Override
	public void clickOrderBook() {
		this.curDisplayType = Constants.DISPLAY_ORDER_LIST;
		contractCenterUsdtLayout.setOrderType(curDisplayType);
		presenter.setOrderType(curDisplayType);
	}

	@Override
	public void clickOrderDetail() {
		this.curDisplayType = Constants.DISPLAY_ORDER_DETAIL;
		contractCenterUsdtLayout.setOrderType(curDisplayType);
		presenter.setOrderType(Constants.DISPLAY_ORDER_DETAIL);
	}

	@Override
	public void clickPlaceOrder(int currentTradeDirection, int fixPriceType, int curHighLeverEntrust, int lever, String price, String account, String contractName,String profitPrice,String lossPrice) {
		presenter.placeOrder(currentTradeDirection, fixPriceType, curHighLeverEntrust, lever, price, account, getParentContractFragment().getContractTopLayout().getMarginMode(), contractName, profitPrice, lossPrice, getActivity());
	}

	@Override
	public void openContract() {
		presenter.agreeProtocol("usdtContract_protocol", "1");
	}

	@Override
	public void agreeProtocol() {
		presenter.agreeProtocol("usdtContract_protocol", "1");
	}


	@Override
	public void setPresenter(ContractInterface.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setOrderListData(List<OrderModel> buyList, List<OrderModel> sellList, WsMarketData tickerData, String contractName) {
		if (currentContrackName.equals(contractName)) {
			contractCenterUsdtLayout.setOrderListData(buyList, sellList, tickerData, contractName);
		}
	}

	@Override
	public void setOrderDetail(List<WsTradeList> tradeDetailList, String contractName) {
		if (currentContrackName == null) {
			return;
		}
		if (currentContrackName.equals(contractName)) {
			contractCenterUsdtLayout.setOrderDetailData(tradeDetailList, contractName);
		}
	}

	@Override
	public void setSettlement(long time) {
		if(contractCenterUsdtLayout==null){
			return;
		}
		contractCenterUsdtLayout.initTimer(time);
	}

	@Override
	public void setQuoData(WsMarketData tickerData) {
		if(contractCenterUsdtLayout==null){
			return;
		}
		contractCenterUsdtLayout.post(() -> setQuoteData(tickerData));

	}


	private synchronized void setQuoteData(WsMarketData tickerData) {
		if (tickerData == null || contractCenterUsdtLayout == null) {
			return;
		}
		contractCenterUsdtLayout.setF8(tickerData.getFundingRate());
	}

	@Override
	public void setCurLeverData(int lever) {
		if (contractCenterUsdtLayout != null)
			contractCenterUsdtLayout.setLever(lever);
	}

	@Override
	public void setContractAccountInfo(ContractAccountInfoModel.DataBean dataBean) {
		if (contractCenterUsdtLayout == null) {
			return;
		}

		getParentContractFragment().getContractTopLayout().setData(dataBean);
		getParentContractFragment().setUnrealizedTotal(dataBean.getUnrealisedPnl());
		contractCenterUsdtLayout.post(() -> {
			contractCenterUsdtLayout.setUserData(dataBean.getAvailableBalance(), dataBean.getLongQuantity(), dataBean.getShortQuantity());
			contractCenterUsdtLayout.calculationAvlOpen();
		});

	}

	@Override
	public void setPisitionListData(List<ContractPositionListModel.DataBean> listData) {
		if (contractBottomBtcLayout == null) {
			return;
		}
		contractBottomBtcLayout.setHoldPositionData(listData);

		calculateUnrealizedTotal(listData);

//		contractBottomBtcLayout.setHoldPositionSize(listData);

	}

	@Override
	public void setAllPisitionListData(List<ContractPositionListModel.DataBean> listData) {
		//更新全部持仓数量
		getParentContractFragment().updateDataSize(listData.size(), ContractTabType.TAB_HOLD_POSITION);

		getParentContractFragment().updateHoldPositionData();
	}

	private void calculateUnrealizedTotal(List<ContractPositionListModel.DataBean> listData) {
		if (listData == null || listData.size() == 0) {
			layoutUnrealized.setVisibility(View.GONE);
		} else {
			layoutUnrealized.setVisibility(VISIBLE);
			tvUnrealizedTotal.setText(String.format(getContext().getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(currentContrackName)) + getResources().getString(R.string.unrealized_profit_loss_total));
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
		contractCenterUsdtLayout.setCurrentOrderNumber(orderCount);
		getParentContractFragment().updateDataSize(total, ContractTabType.TAB_CUR_ENTRUST);
	}

	@Override
	public void placeOrderSucces() {
		contractCenterUsdtLayout.clearInputText();
	}

	/**
	 * @param mode 设置当前保证金模式
	 */
	@Override
	public void setMarginMode(String mode, String modeSetting) {
		if (getParentContractFragment().getContractTopLayout() != null)
			getParentContractFragment().getContractTopLayout().setMarginMode(mode, modeSetting);
	}

	@Override
	public void updateModeSuccess() {
		if (getParentContractFragment().getContractTopLayout() != null)
			getParentContractFragment().getContractTopLayout().updateModeSuccess();
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

