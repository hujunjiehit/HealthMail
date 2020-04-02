package com.coinbene.manbiwang.contract.contractusdt.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.contract.bean.ContractBottomTab;
import com.coinbene.manbiwang.contract.contractusdt.fragment.ContractPlanUsdtFragment;
import com.coinbene.manbiwang.contract.contractusdt.fragment.ContractUsdtTradeFragment;
import com.coinbene.manbiwang.contract.contractusdt.fragment.CurrentPositionUsdtFragment;
import com.coinbene.manbiwang.service.ServiceRepo;

import java.util.List;

public class ContractUsdtTypePageAdapter extends FragmentPagerAdapter {

	private List<ContractBottomTab> contractBottomTabs;

	public ContractUsdtTypePageAdapter(FragmentManager fm, List<ContractBottomTab> contractBottomTabs) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.contractBottomTabs = contractBottomTabs;
	}

	@Override
	public Fragment getItem(int position) {
		switch (contractBottomTabs.get(position).getTabType()) {
			case TAB_TRADE:
				//交易
				return ContractUsdtTradeFragment.newInstance();

			case TAB_HOLD_POSITION:
				//持仓
				return CurrentPositionUsdtFragment.newInstance();

			case TAB_CUR_ENTRUST:
				//当前委托
				return ServiceRepo.getRecordService().getContractCurrentOrderFragment(Constants.CONTRACT_TYPE_USDT);

			case TAB_HIS_ENTRUST:
				//历史委托
				return ServiceRepo.getRecordService().getContractHistoryOrderFragment(Constants.CONTRACT_TYPE_USDT);

			case TAB_PLAN_ENTRIST:
				//计划委托
				return ContractPlanUsdtFragment.newInstance();

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
