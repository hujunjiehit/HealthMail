package com.june.healthmail.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.june.healthmail.R;
import com.june.healthmail.improve.activity.NewFukuanActivity;
import com.june.healthmail.adapter.ViewPagerAdapter;

import butterknife.BindView;

/**
 * Created by bjhujunjie on 2016/9/21.
 */
public class FunctionListFragment extends BaseFragment implements View.OnClickListener {


  @BindView(R.id.img_setup)
  ImageView mImgSetup;

  @BindView(R.id.tab_layout)
  TabLayout mTabLayout;
  @BindView(R.id.view_pager)
  ViewPager mViewPager;
  private ViewPagerAdapter adapter;



  @Override
  public int setLayout() {
    return R.layout.fragment_function_list;
  }

  @Override
  public void setListener() {
    mImgSetup.setOnClickListener(this);
  }

  @Override
  public void initData() {
    adapter = new ViewPagerAdapter(getChildFragmentManager());
    mViewPager.setAdapter(adapter);
    mTabLayout.setupWithViewPager(mViewPager);
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onClick(View v) {
    Intent intent = null;
      switch (v.getId()) {
        case R.id.btn_operition_fukuan:
          checkPermission(NewFukuanActivity.class);
          break;
      }
  }
}
