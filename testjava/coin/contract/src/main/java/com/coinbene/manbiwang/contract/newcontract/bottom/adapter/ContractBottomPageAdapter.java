package com.coinbene.manbiwang.contract.newcontract.bottom.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.manbiwang.contract.bean.ContractBottomTab;
import com.coinbene.manbiwang.contract.newcontract.bottom.fragment.ContractPlanFragment;
import com.coinbene.manbiwang.contract.newcontract.bottom.fragment.CurrentOrderFragment;
import com.coinbene.manbiwang.contract.newcontract.bottom.fragment.CurrentPositionFragment;
import com.coinbene.manbiwang.contract.newcontract.bottom.fragment.HistoryOrderFragment;

import java.util.List;

public class ContractBottomPageAdapter extends FragmentPagerAdapter {

	private List<ContractBottomTab> contractBottomTabs;

	public ContractBottomPageAdapter(FragmentManager fm, List<ContractBottomTab> contractBottomTabs) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.contractBottomTabs = contractBottomTabs;
	}

	@Override
	public Fragment getItem(int position) {
		switch (contractBottomTabs.get(position).getTabType()) {
			case TAB_TRADE:
				//交易
				//return ContractBtcTradeFragment.newInstance();

			case TAB_HOLD_POSITION:
				//持仓
				return CurrentPositionFragment.newInstance();

			case TAB_CUR_ENTRUST:
				//当前委托
				return CurrentOrderFragment.newInstance();

			case TAB_HIS_ENTRUST:
				//历史委托
				return HistoryOrderFragment.newInstance();

			case TAB_PLAN_ENTRIST:
				//计划委托--止盈止损
				return ContractPlanFragment.newInstance();

			default:
				break;
		}
		return new Fragment();
	}

	@Override
	public int getCount() {
		return contractBottomTabs.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return contractBottomTabs.get(position).getTabName();
	}

}
