package com.coinbene.manbiwang.spot.otc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.widget.tablayout.SlidingTabLayout;
import com.coinbene.manbiwang.spot.R;

import java.util.ArrayList;
import java.util.List;

public class ShopsOrderFragment extends BaseFragment {
    private SlidingTabLayout tabLayout;
    private ViewPager mViewpager;
    private String[] strings;
    private List<Fragment> fragments;

    private int getLayoutId() {
        return R.layout.spot_fragment_shops_order_status;
    }

    public static ShopsOrderFragment newInstance(int orderDirection) {
        Bundle args = new Bundle();
        args.putInt("order_direction", orderDirection);
        ShopsOrderFragment fragment = new ShopsOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        mViewpager = view.findViewById(R.id.view_pager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        Bundle bundle = this.getArguments();
        // 0 卖出订单   1 买入订单   2  主动成交
        int orderDirection = bundle.getInt("order_direction", 0);
        fragments = new ArrayList<>();
        switch (orderDirection) {

            case 0://
                strings = new String[]{getString(R.string.order_all), getString(R.string.wait_receivables),
                        getString(R.string.wait_sure), getString(R.string.order_completed),
                        getString(R.string.order_cancelled), getString(R.string.order_freezing)};
                int[] status1 = {0, 1, 2, 3, 4, 6};// 0,全部、待收款、待确认、已完成、已取消、冻结中
                for (int i = 0; i < strings.length; i++) {
                    ShopsOrderStatuFragment orderStatuFragment = ShopsOrderStatuFragment.newInstance(2, status1[i]);
                    fragments.add(orderStatuFragment);
                }

                break;
            case 1://
                strings = new String[]{getString(R.string.order_all), getString(R.string.wait_payment),
                        getString(R.string.paid), getString(R.string.order_completed),
                        getString(R.string.order_cancelled), getString(R.string.order_freezing)};
                int[] status2 = {0, 1, 2, 3, 4, 6};// 0,全部1 待付款 2 已付款 3 已完成 4 已取消6 被冻结
                for (int i = 0; i < strings.length; i++) {
                    ShopsOrderStatuFragment orderStatuFragment = ShopsOrderStatuFragment.newInstance(1, status2[i]);
                    fragments.add(orderStatuFragment);
                }
                break;
            default:
        }

        mViewpager.setOffscreenPageLimit(strings.length);
        mViewpager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {


            @Override
            public Fragment getItem(int position) {
                if (position < fragments.size()) {
                    return fragments.get(position);
                }
                return null;
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return strings[position];
            }
        });
        mViewpager.setCurrentItem(0);
        tabLayout.setViewPager(mViewpager);
    }
}
