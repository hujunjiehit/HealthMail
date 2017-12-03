package com.june.healthmail.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.june.healthmail.fragement.subfragment.FaKeFragment;
import com.june.healthmail.fragement.subfragment.FragmentInfo;
import com.june.healthmail.fragement.subfragment.FuKuanFragment;
import com.june.healthmail.fragement.subfragment.GuanzhuFragment;
import com.june.healthmail.fragement.subfragment.SpecialFragment;
import com.june.healthmail.fragement.subfragment.TongJiFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by june on 2017/10/24.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

  private List<FragmentInfo> mFragments = new ArrayList<>();

  public ViewPagerAdapter(FragmentManager fm) {
    super(fm);
    initFragments();
  }

  private void initFragments() {
    //mFragments.add(new FragmentInfo("约课",YueKeFragment.class));
    //mFragments.add(new FragmentInfo("评价",PingJiaFragment.class));
    mFragments.add(new FragmentInfo("付款",FuKuanFragment.class));
    mFragments.add(new FragmentInfo("发课",FaKeFragment.class));
    mFragments.add(new FragmentInfo("统计",TongJiFragment.class));
    mFragments.add(new FragmentInfo("特殊",SpecialFragment.class));
    mFragments.add(new FragmentInfo("关注/收藏",GuanzhuFragment.class));
  }

  @Override
  public Fragment getItem(int position) {
    try {
      return (Fragment) mFragments.get(position).getFragment().newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public int getCount() {
    return mFragments.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return mFragments.get(position).getTitle();
  }
}
