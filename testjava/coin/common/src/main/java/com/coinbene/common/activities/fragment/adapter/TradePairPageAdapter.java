package com.coinbene.common.activities.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.common.activities.fragment.TradePairListFragment;

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
		return TradePairListFragment.newInstance(mGroupList.get(position).getGroupName());
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
