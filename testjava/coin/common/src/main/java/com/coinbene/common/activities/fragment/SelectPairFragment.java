package com.coinbene.common.activities.fragment;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.activities.fragment.adapter.TradePairGroupBean;
import com.coinbene.common.activities.fragment.adapter.TradePairPageAdapter;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.utils.ToastUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-07
 */
public class SelectPairFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.tab_trade_group)
	QMUITabSegment mTabSegment;
	@BindView(R2.id.trade_pair_viewpager)
	ViewPager mViewPager;

	/**
	 * 交易组
	 */
	private List<TradePairGroupTable> tradePairGroupList;

	private List<TradePairGroupBean> mGroupBeanList;

	private TradePairPageAdapter mTradePairPageAdapter;

	private String fenmuAsset;

	private int currentIndex = 0;

	public static SelectPairFragment newInstance(String fenmuAsset) {

		Bundle args = new Bundle();
		args.putString("fenmuAsset", fenmuAsset);
		SelectPairFragment fragment = new SelectPairFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.common_fragment_select_tradepair;
	}

	@Override
	public void initView(View rootView) {
		//配置TabSegment
		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_2));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			fenmuAsset = bundle.getString("fenmuAsset");
		}

		getTradePairList();
	}

	@Override
	public void onFragmentHide() {

	}

	@Override
	public void onFragmentShow() {

	}


	/**
	 * 获取交易组
	 */
	private void getTradePairList() {

		tradePairGroupList = TradePairGroupController.getInstance().getTradePairGroupsFilterContrack();

		if (tradePairGroupList == null || tradePairGroupList.size() == 0) {
			ToastUtil.show(R.string.tradePair_list_null);
			return;
		}

		if (mGroupBeanList == null) {
			mGroupBeanList = new ArrayList<>();
		}

		//遍历交易组列表
		for(int i = 0; i < tradePairGroupList.size(); i++) {
			TradePairGroupTable item = tradePairGroupList.get(i);
			TradePairGroupBean tradePairGroupBean = new TradePairGroupBean();
			if (item.groupName.equals(TradePairGroupTable.SELF_GROUP)) {
				tradePairGroupBean.setGroupTitle(getString(R.string.self));
			} else {
				tradePairGroupBean.setGroupTitle(item.groupName);
			}
			tradePairGroupBean.setGroupName(item.groupName);
			tradePairGroupBean.setGroupId(String.valueOf(i));
			mGroupBeanList.add(tradePairGroupBean);

			if (!TextUtils.isEmpty(fenmuAsset) && fenmuAsset.toLowerCase().equals(item.groupName.toLowerCase())) {
				currentIndex = i;
			}
		}

		mTradePairPageAdapter = new TradePairPageAdapter(getChildFragmentManager(), mGroupBeanList);
		mViewPager.setOffscreenPageLimit(mGroupBeanList.size());
		mViewPager.setAdapter(mTradePairPageAdapter);
		mTabSegment.setupWithViewPager(mViewPager);

		//设置默认选中的分组
		mTabSegment.selectTab(currentIndex);
	}
}
