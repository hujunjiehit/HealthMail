package com.coinbene.manbiwang.market.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.manbiwang.market.fragment.ContractTradePairFragment;
import com.coinbene.manbiwang.market.fragment.OptionalTradePairFragment;
import com.coinbene.manbiwang.market.fragment.TradePairFragment;

import java.util.List;

/**
 * Created by june
 * on 2019-08-29
 */
public class TradePairPageAdapter extends FragmentPagerAdapter {

	List<TradePairGroupBean> mGroupList;

	public TradePairPageAdapter(@NonNull FragmentManager fm, List<TradePairGroupBean> groupList) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.mGroupList = groupList;
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		if (mGroupList != null && position < mGroupList.size()) {
			switch (mGroupList.get(position).getGroupName()) {
				case TradePairGroupTable.CONTRACT_GROUP:
					//合约Fragment
					return ContractTradePairFragment.newInstance(mGroupList.get(position).getGroupName());
				case TradePairGroupTable.SELF_GROUP:
					//自选Fragment
					return OptionalTradePairFragment.newInstance(mGroupList.get(position).getGroupName());
				default:
					return TradePairFragment.newInstance(mGroupList.get(position).getGroupName());
			}

		}
		return new Fragment();
	}

	@Override
	public int getCount() {
		return mGroupList.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mGroupList.get(position).getGroupTitle();
	}
}
