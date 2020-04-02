package com.coinbene.manbiwang;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.app.TabType;

import java.util.List;

/**
 * Created by june
 * on 2019-08-15
 * 资产账户的pageAdapter
 */
public class TabFragmentPageAdapter extends FragmentPagerAdapter {

	List<TabType> list;

	public TabFragmentPageAdapter(FragmentManager fm, List<TabType> list) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.list = list;
	}

	@Override
	public Fragment getItem(int position) {
		switch (list.get(position)) {
			case HOME:
				return ServiceRepo.getHomeService().getHomeFragment();
			case MARKET:
				return ServiceRepo.getMarketService().getMarketFargment();
			case SPOT:
				return ServiceRepo.getSpotService().getSpotFragment();
			case CONTRACT:
				return  ServiceRepo.getContractService().getContractFragment();
			case BALANCE:
				return ServiceRepo.getBalanceService().getBalanceFragment();
		}
		return ServiceRepo.getHomeService().getHomeFragment();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public int getTabPosition(TabType tabType) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == tabType) {
				return i;
			}
		}
		return 0;
	}
}
