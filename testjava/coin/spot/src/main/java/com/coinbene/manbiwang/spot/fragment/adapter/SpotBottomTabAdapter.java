package com.coinbene.manbiwang.spot.fragment.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.manbiwang.spot.fragment.CurrentOrderFiveFragment;
import com.coinbene.manbiwang.spot.fragment.HighLeverOrderFiveFragment;
import com.coinbene.manbiwang.spot.fragment.HistoryOrderFiveFragment;
import com.coinbene.manbiwang.spot.fragment.bean.SpotBottomTabItem;

import java.util.List;

/**
 * Created by june
 * on 2019-11-27
 */
public class SpotBottomTabAdapter extends FragmentPagerAdapter {

	List<SpotBottomTabItem> list;

	public SpotBottomTabAdapter(FragmentManager fm, List<SpotBottomTabItem> list) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.list = list;
	}

	@Override
	public Fragment getItem(int position) {
		switch (list.get(position).getType()) {
			case CURRENT_ORDER:
				return CurrentOrderFiveFragment.newInstance(list.get(position).getAccountType());
			case HISTORY_ORDER:
				return HistoryOrderFiveFragment.newInstance(list.get(position).getAccountType());
			case HIGH_LEVEL_ORDER:
				return HighLeverOrderFiveFragment.newInstance(list.get(position).getAccountType());
		}
		return new Fragment();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return list.get(position).getTitle();
	}

	public interface SpotBottomInterface {
		String getTradePair();

		void changeTradePair(String tradePair);

		boolean getHideOtherCoin();

		void setHideOtherCoin(boolean hideOtherCoin);
	}
}
