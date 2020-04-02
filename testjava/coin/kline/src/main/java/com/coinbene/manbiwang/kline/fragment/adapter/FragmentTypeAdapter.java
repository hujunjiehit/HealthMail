package com.coinbene.manbiwang.kline.fragment.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.coinbene.manbiwang.kline.fragment.CoinInfoFragment;
import com.coinbene.manbiwang.kline.fragment.KlineOrderListFragment;
import com.coinbene.manbiwang.kline.fragment.KlineTradeDetailFragment;

import java.util.List;

/**
 * Created by june
 * on 2019-11-20
 */
public class FragmentTypeAdapter extends FragmentPagerAdapter {

	List<FragmentItem> list;

	public FragmentTypeAdapter(FragmentManager fm, List<FragmentItem> list) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.list = list;
	}

	@Override
	public Fragment getItem(int position) {
		switch (list.get(position).getType()) {
			case ORDERLIST:
				return KlineOrderListFragment.newInstance();
			case TRADEDETAIL:
				return KlineTradeDetailFragment.newInstance();
			case COININFO:
				return CoinInfoFragment.newInstance();
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
}
