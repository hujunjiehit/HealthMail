package com.june.healthmail.fragement.subfragment;

import android.view.View;

import com.june.healthmail.R;
import com.june.healthmail.activity.FukuanActivity;
import com.june.healthmail.fragement.BaseFragment;
import com.june.healthmail.improve.activity.NewYuekeActivity;

/**
 * Created by june on 2017/10/24.
 */

public class FuKuanFragment extends BaseFragment {


  private View layout;

  @Override
  public int setLayout() {
    return R.layout.fragment_fukuan;
  }

  @Override
  public void setListener() {
    mRootView.findViewById(R.id.btn_operition_fukuan).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(FukuanActivity.class);
      }
    });

  }

  @Override
  public void initData() {

  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
