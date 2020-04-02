package com.coinbene.manbiwang.contract.contractbtc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.bean.ContractBottomTab;
import com.coinbene.manbiwang.contract.bean.ContractTabType;
import com.coinbene.manbiwang.contract.contractbtc.adapter.ContractBtcTypePageAdapter;
import com.coinbene.manbiwang.contract.contractbtc.fragment.CurrentPositionBtcFragment;
import com.coinbene.manbiwang.contract.contractbtc.layout.ContractBtcTopLayout;
import com.coinbene.manbiwang.contract.contractbtc.widget.ContractBtcChangePopWindow;
import com.coinbene.manbiwang.service.contract.ContractChangePopWindow;
import com.coinbene.manbiwang.service.contract.ContractTotalListener;
import com.coinbene.manbiwang.service.record.ContractRefreshInterface;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ContractBtcFragment extends CoinbeneBaseFragment {
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;
	@BindView(R2.id.rl_title)
	RelativeLayout rlTitle;
	@BindView(R2.id.tv_contrack_name)
	TextView tvContrackName;
	@BindView(R2.id.trade_pair_layout)
	LinearLayout tradePairLayout;
	@BindView(R2.id.include_top_layout)
	ContractBtcTopLayout contractBtcTopLayout;
	@BindView(R2.id.kchart_imgview)
	ImageView kchartImgview;
	@BindView(R2.id.menu_right_iv)
	ImageView menuRightIv;
	@BindView(R2.id.mining_lottie_view)
	LottieAnimationView mMingLottieView;


	private List<ContractBottomTab> contractBottomTabs;
	private ContractBtcTypePageAdapter contractBtcTypePageAdapter;
	private String currentContractName;
	private ContractBtcChangePopWindow contractBtcChangePopWindow;
	private List<ContractTotalListener> totalListener = new ArrayList<>();


	/**
	 * 注册合约总监听   暂时放了合约名字
	 *
	 * @param listener
	 */
	public void registerTotalListener(ContractTotalListener listener) {
		if (totalListener == null)
			totalListener = new ArrayList<>();

		if (!totalListener.contains(listener)) {
			totalListener.add(listener);
			listener.updateSymbol(currentContractName);
			//由于第一次注册的时候 fragment本身还没有注册进来  不会调用parent 的 show方法  所以第一次注册 把show方法传给当前注册的fragment
			listener.parentFragmentShow();
		}
	}

	public static ContractBtcFragment newInstance() {

		Bundle args = new Bundle();
		ContractBtcFragment fragment = new ContractBtcFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public int initLayout() {
		return R.layout.fragment_contract_btc_singer;
	}

	@Override
	public void initView(View rootView) {
	}

	@Override
	public void setListener() {
		tradePairLayout.setOnClickListener(v -> {
			contractBtcChangePopWindow = new ContractBtcChangePopWindow(tradePairLayout, false);
			contractBtcChangePopWindow.showBelowAnchor();
			contractBtcChangePopWindow.setOnItemClickContrctListener(new ContractChangePopWindow.OnItemClickContractListener<WsMarketData>() {
				@Override
				public void onItemClickContract(WsMarketData item) {
					contractBtcChangePopWindow.getPopupWindow().dismiss();
					changeSymbol(item.getSymbol());
				}
			});
		});
	}

	/**
	 * 切换交易对
	 *
	 * @param symbol
	 */
	public void changeSymbol(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return;
		}
		if (!ContractInfoController.getInstance().checkContractIsExist(symbol)) {
			return;
		}
		if (mTabSegment != null) {
			mTabSegment.selectTab(0, false, true);
		}
		SpUtil.put(getContext(), SpUtil.CONTRACT_COIN, symbol);
		currentContractName = symbol;
		tvContrackName.setText(String.format(getString(R.string.forever_no_delivery), currentContractName));
		if (totalListener != null) {
			for (ContractTotalListener listener : totalListener) {
				listener.updateSymbol(symbol);
			}
		}
	}

	@Override
	public void initData() {


		currentContractName = SpUtil.get(CBRepository.getContext(), SpUtil.CONTRACT_COIN, "BTCUSDT");
		if (TextUtils.isEmpty(currentContractName) || !ContractInfoController.getInstance().checkContractIsExist(currentContractName)) {
			currentContractName = "BTCUSDT";
		}
		tvContrackName.setText(String.format(getString(R.string.forever_no_delivery), currentContractName));
		initTabLayout();
	}

	public ContractBtcTopLayout getContractBtcTopLayout() {
		return contractBtcTopLayout;
	}

	public ImageView getKchartImgview() {
		return kchartImgview;
	}

	public ImageView getMenuRightIv() {
		return menuRightIv;
	}

//	public LottieAnimationView getMingLottieView() {
//		return mMingLottieView;
//	}


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
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.trade_title), ContractTabType.TAB_TRADE));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.all_hold_position) + "[0]", ContractTabType.TAB_HOLD_POSITION));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.br_current_delegation) + "[0]", ContractTabType.TAB_CUR_ENTRUST));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.contract_plan), ContractTabType.TAB_PLAN_ENTRIST));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.history_entrust_label_new), ContractTabType.TAB_HIS_ENTRUST));
			contractBtcTypePageAdapter = new ContractBtcTypePageAdapter(getChildFragmentManager(), contractBottomTabs);
		}

		mViewPager.setOffscreenPageLimit(contractBottomTabs.size());
		mViewPager.setAdapter(contractBtcTypePageAdapter);
		mTabSegment.setupWithViewPager(mViewPager);

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

	public Fragment getFragmentByIndex(int index) {

		List<Fragment> fragments = getChildFragmentManager().getFragments();
		if (index >= fragments.size()) {
			return null;
		}
		return fragments.get(index);
	}


	private void reFreshData(Fragment fragment) {
		if (fragment instanceof ContractRefreshInterface) {
			((ContractRefreshInterface) fragment).reFreshData();
		}
	}

	public void  setUnrealizedTotal(String unrealizedTotal){
		Fragment fragment = getFragmentByIndex(mTabSegment.getSelectedIndex());
		if(fragment instanceof  CurrentPositionBtcFragment){
			((CurrentPositionBtcFragment)fragment).setUnrealizedTotal(unrealizedTotal);
		}
	}

	/**
	 * 更新持仓和当前委托  的 数量
	 *
	 * @param total
	 * @param tabCurEntrust
	 */
	public void updateHoldPosition(int total, ContractTabType tabCurEntrust) {
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
		for (ContractTotalListener listener : totalListener) {
			listener.parentFramentHide();
		}
	}

	@Override
	public void onFragmentShow() {
		SpUtil.setContractOptions("btc");
		if (!CommonUtil.isLoginAndUnLocked()) {
			contractBtcTopLayout.setNoUser();
			if (contractBottomTabs != null) {
				mTabSegment.updateTabText(1, contractBottomTabs.get(1).getTabName());
				mTabSegment.updateTabText(2, contractBottomTabs.get(2).getTabName());
			}
		}

		if (totalListener != null) {
			for (ContractTotalListener listener : totalListener) {
				listener.parentFragmentShow();
			}
		}

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
			if (fragment instanceof CurrentPositionBtcFragment) {
				((CurrentPositionBtcFragment) fragment).getPositionlist();
			}
		}
	}
}
