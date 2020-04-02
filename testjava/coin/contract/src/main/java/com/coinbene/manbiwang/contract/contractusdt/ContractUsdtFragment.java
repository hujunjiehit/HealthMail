package com.coinbene.manbiwang.contract.contractusdt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.contract.ContractFragment;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.bean.ContractBottomTab;
import com.coinbene.manbiwang.contract.bean.ContractTabType;
import com.coinbene.manbiwang.contract.contractusdt.adapter.ContractUsdtTypePageAdapter;
import com.coinbene.manbiwang.contract.contractusdt.fragment.CurrentPositionUsdtFragment;
import com.coinbene.manbiwang.contract.contractusdt.layout.ContractUsdtCenterLayout;
import com.coinbene.manbiwang.contract.contractusdt.layout.ContractUsdtTopLayout;
import com.coinbene.manbiwang.contract.contractusdt.widget.ContractUsdtChangePopWindow;
import com.coinbene.manbiwang.contract.contractusdt.widget.ContractUsdtMenu;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.ContractRefreshInterface;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ContractUsdtFragment extends CoinbeneBaseFragment {
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;
	@BindView(R2.id.include_top_layout)
	ContractUsdtTopLayout contractUsdtTopLayout;
	@BindView(R2.id.include_center_layout)
	ContractUsdtCenterLayout contractCenterUsdtLayout;
	@BindView(R2.id.iv_change_symbol)
	ImageView ivChangeSymbol;
	@BindView(R2.id.tv_symbol)
	TextView tvSymbol;
	@BindView(R2.id.tv_position_model)
	TextView tvPositionModel;
	@BindView(R2.id.iv_contract_tip)
	ImageView ivContractTip;
	@BindView(R2.id.iv_kline)
	ImageView ivKline;
	@BindView(R2.id.iv_more)
	ImageView ivMore;


	private List<ContractBottomTab> contractBottomTabs;
	private ContractUsdtTypePageAdapter contractUsdtTypePageAdapter;
	private String currentContractName;
	private ContractUsdtChangePopWindow contractUsdtChangePopWindow;
	private ContractUsdtMenu menu;

	public String getCurrentContractName() {
		return currentContractName;
	}


	public static ContractUsdtFragment newInstance() {

		Bundle args = new Bundle();
		ContractUsdtFragment fragment = new ContractUsdtFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public int initLayout() {
		return R.layout.fragment_contract_usdt_singer;
	}

	@Override
	public void initView(View rootView) {
	}


	@Override
	public void setListener() {
		ivChangeSymbol.setOnClickListener(v -> {

			Fragment parentFragment = getParentFragment();
			if (parentFragment instanceof ContractFragment) {
				ContractFragment contractFragment = (ContractFragment) parentFragment;
				contractFragment.openDrawer();
			}
		});
		ivKline.setOnClickListener(v -> UIBusService.getInstance().openUri(v.getContext(),
				"coinbene://ContractKline?symbol=" + currentContractName, null));

		ivMore.setOnClickListener(v -> ivRightClick());
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
//		menu.setDefaultSymbol(currentContractName);
//		menu.setDefaultLever(contractCenterUsdtLayout.getCurrentLever() + "X");
//		menu.showAtLocation(ivMore);
	}

	/**
	 * 切换交易对
	 *
	 * @param symbol
	 */
	public void changeSymbol(String symbol) {
		DLog.e("changeTab", "symbol == " + symbol + "  currentContractName = " + currentContractName);

		if (TextUtils.isEmpty(symbol)) {
			return;
		}

		if (!ContractUsdtInfoController.getInstance().checkContractIsExist(symbol)) {
			return;
		}

		DLog.e("changeTab", "changeSymbol => " + symbol);
		if (mTabSegment != null) {
			mTabSegment.selectTab(0, false, true);
		}

		SpUtil.put(getContext(), SpUtil.CONTRACT_COIN_USDT, symbol);
		currentContractName = symbol;
		tvSymbol.setText(String.format(getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(currentContractName)));

		ServiceRepo.getContractService().totalListenerUpdateSymbol(this);
	}

	@Override
	public void initData() {

		currentContractName = SpUtil.getContractCionUsdt();
		if (TextUtils.isEmpty(currentContractName) || !ContractUsdtInfoController.getInstance().checkContractIsExist(currentContractName)) {
			currentContractName = "BTC-SWAP";
		}


		tvSymbol.setText(String.format(getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(currentContractName)));
		initTabLayout();
	}

	public ContractUsdtTopLayout getContractTopLayout() {
		return contractUsdtTopLayout;
	}

	public ImageView getKchartImgview() {
		return ivKline;
	}

	public ImageView getMenuRightIv() {
		return ivMore;
	}


	private void initTabLayout() {
		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_2));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 22));
		if (contractBottomTabs == null) {
			contractBottomTabs = new ArrayList<>();
			contractBottomTabs.add(new ContractBottomTab(getContext().getString(R.string.trade_title), ContractTabType.TAB_TRADE));
			contractBottomTabs.add(new ContractBottomTab(getContext().getString(R.string.all_hold_position) + "[0]", ContractTabType.TAB_HOLD_POSITION));
			contractBottomTabs.add(new ContractBottomTab(getContext().getString(R.string.br_current_delegation) + "[0]", ContractTabType.TAB_CUR_ENTRUST));
			contractBottomTabs.add(new ContractBottomTab(getContext().getString(R.string.contract_plan), ContractTabType.TAB_PLAN_ENTRIST));
			contractBottomTabs.add(new ContractBottomTab(getContext().getString(R.string.history_entrust_label_new), ContractTabType.TAB_HIS_ENTRUST));

			contractUsdtTypePageAdapter = new ContractUsdtTypePageAdapter(getChildFragmentManager(), contractBottomTabs);
		}

		mViewPager.setOffscreenPageLimit(contractBottomTabs.size());
		mViewPager.setAdapter(contractUsdtTypePageAdapter);
		mTabSegment.setupWithViewPager(mViewPager);
		mTabSegment.setOnTabClickListener(new QMUITabSegment.OnTabClickListener() {
			@Override
			public void onTabClick(int index) {

			}
		});

		mTabSegment.addOnTabSelectedListener(new QMUITabSelectedListener() {
			@Override
			public void onTabSelected(int index) {
				Fragment fragment = getFragmentByIndex(index);
				if (fragment != null) {
					reFreshData(fragment);
				}

			}

		});
	}

	private void reFreshData(Fragment fragment) {
		if (fragment instanceof ContractRefreshInterface) {
			((ContractRefreshInterface) fragment).reFreshData();
		}
	}

	public void setUnrealizedTotal(String unrealizedTotal) {
		Fragment fragment = getFragmentByIndex(mTabSegment.getSelectedIndex());
		if (fragment instanceof CurrentPositionUsdtFragment) {
			((CurrentPositionUsdtFragment) fragment).setUnrealizedTotal(unrealizedTotal);
		}
	}


	public void updateDataSize(int total, ContractTabType tabCurEntrust) {
		switch (tabCurEntrust) {
			case TAB_HOLD_POSITION:
//				if (total > 0) {
				mTabSegment.updateTabText(1, String.format("%s[%d]", getString(R.string.all_hold_position), total));
//				} else {
//					mTabSegment.updateTabText(1, getString(R.string.hold_position));
//				}
				break;
			case TAB_CUR_ENTRUST:

				if (total <= 99) {
					mTabSegment.updateTabText(2, String.format("%s[%d]", getString(R.string.br_current_delegation), total));
				} else
					mTabSegment.updateTabText(2, String.format("%s[%s]", getString(R.string.br_current_delegation), "99+"));
				break;

		}


	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onFragmentHide() {
		ServiceRepo.getContractService().totalListenerFragmentHide();

	}

	@Override
	public void onFragmentShow() {
		SpUtil.setContractOptions("usdt");
		if (!CommonUtil.isLoginAndUnLocked()) {
			contractUsdtTopLayout.setNoUser();
			if (contractBottomTabs != null) {
				mTabSegment.updateTabText(1, contractBottomTabs.get(1).getTabName());
				mTabSegment.updateTabText(2, contractBottomTabs.get(2).getTabName());
			}
		}

		ServiceRepo.getContractService().totalListenerFragmentShow();

		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSymbol())) {
			changeSymbol(MainActivityDelegate.getInstance().getSymbol());
			MainActivityDelegate.getInstance().clearSymbol();
			if (mTabSegment != null) {
				mTabSegment.selectTab(0, false, true);
			}
		}
	}

	public void updateHoldPositionData() {
		if (mTabSegment != null && mTabSegment.getSelectedIndex() == 1) {
			Fragment fragment = getChildFragmentManager().getFragments().get(1);
			if (fragment instanceof CurrentPositionUsdtFragment) {
				((CurrentPositionUsdtFragment) fragment).getPositionlist();
			}
		}
	}


	public Fragment getFragmentByIndex(int index) {
		List<Fragment> fragments = getChildFragmentManager().getFragments();
		if (index >= fragments.size()) {
			return null;
		}
		return fragments.get(index);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ServiceRepo.getContractService().clearListener();
	}
}
