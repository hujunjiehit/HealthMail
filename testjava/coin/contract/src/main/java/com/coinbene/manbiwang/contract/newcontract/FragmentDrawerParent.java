package com.coinbene.manbiwang.contract.newcontract;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.adapter.ContractPagerAdaper;
import com.coinbene.manbiwang.contract.bean.ContractDrawerType;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FragmentDrawerParent extends CoinbeneBaseFragment {

	@BindView(R2.id.view_pager)
	ViewPager viewPager;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;

	OnDrawerListener onDrawerListener;
	private ContractViewModel contractViewModel;


	public void setOnDrawerListener(OnDrawerListener onDrawerListener) {
		this.onDrawerListener = onDrawerListener;
	}

	@Override
	public int initLayout() {
		return R.layout.fragment_drawer_parent;
	}

	@Override
	public void initView(View rootView) {
		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_1));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));
		List<ContractDrawerType> contracts = new ArrayList<>();
		contracts.add(new ContractDrawerType(Constants.CONTRACT_TYPE_USDT, String.format("%s%s", "USDT", getString(R.string.contract_title))));
		contracts.add(new ContractDrawerType(Constants.CONTRACT_TYPE_BTC, String.format("%s%s", "BTC", getString(R.string.contract_title))));
		ContractPagerAdaper contractPagerAdaper = new ContractPagerAdaper(getChildFragmentManager(), contracts);
		viewPager.setOffscreenPageLimit(contracts.size());
		viewPager.setAdapter(contractPagerAdaper);


		mTabSegment.setupWithViewPager(viewPager);
		mTabSegment.addOnTabSelectedListener(new QMUITabSelectedListener() {
			@Override
			public void onTabSelected(int index) {
				if (index == contracts.size() - 1) {
					if (onDrawerListener != null)
						onDrawerListener.onPageSelected(true);
				} else {
					if (onDrawerListener != null)
						onDrawerListener.onPageSelected(false);
				}
			}
		});

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);


		if (TradeUtils.isUsdtContract(SpUtil.getContractCionNew())) {
			viewPager.setCurrentItem(0);
		} else {
			viewPager.setCurrentItem(1);
		}


	}

	@Override
	public void setListener() {


	}


	@Override
	public void initData() {
		contractViewModel.getSymbol().observe(this, symbol -> {
			//交易对变化
			scrollTo(symbol);
		});

	}

	@Override
	public void onFragmentShow() {
	}

	public void scrollTo(String currentSymbol) {
		if (TradeUtils.isUsdtContract(currentSymbol)) {
			viewPager.setCurrentItem(0);
		} else {
			viewPager.setCurrentItem(1);
		}
	}

	@Override
	public void onFragmentHide() {
	}

	public interface OnDrawerListener<T> {
		void onPageSelected(boolean isLast);

	}
}
