package com.june.healthmail.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.CancleGuanzhuActivity;
import com.june.healthmail.activity.FukuanActivity;
import com.june.healthmail.activity.FunctionSetupActivity;
import com.june.healthmail.activity.GuanzhuActivity;
import com.june.healthmail.activity.SijiaoLoginActivity;
import com.june.healthmail.adapter.ViewPagerAdapter;
import com.june.healthmail.http.ApiService;
import com.june.healthmail.http.HttpManager;
import com.june.healthmail.http.bean.BaseBean;
import com.june.healthmail.improve.activity.NewPingjiaActivity;
import com.june.healthmail.improve.activity.NewYuekeActivity;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.ShowProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
      case R.id.img_setup:
        intent = new Intent(getActivity(), FunctionSetupActivity.class);
        startActivity(intent);
        break;
      default:
        break;
    }
  }
}
