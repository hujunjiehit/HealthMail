package com.coinbene.manbiwang.contract.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.manbiwang.contract.newcontract.ContractDrawerLayoutFragment;
import com.coinbene.manbiwang.contract.bean.ContractDrawerType;

import java.util.List;

public class ContractPagerAdaper extends FragmentPagerAdapter {
	List<ContractDrawerType> stringList;


	public ContractPagerAdaper(@NonNull FragmentManager fm, List<ContractDrawerType> groupList) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.stringList = groupList;
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		return ContractDrawerLayoutFragment.newInstance(stringList.get(position).getType());
	}

	@Override
	public int getCount() {
		return stringList.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return stringList.get(position).getContactName();
	}
}
