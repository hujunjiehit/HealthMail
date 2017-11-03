package com.june.healthmail.fragement.subfragment;

import android.view.View;

import com.june.healthmail.R;
import com.june.healthmail.fragement.BaseFragment;
import com.june.healthmail.improve.activity.NewFukuanActivity;

/**
 * Created by june on 2017/10/24.
 */

public class PayAssistFragment extends BaseFragment {


  @Override
  public int setLayout() {
    return R.layout.fragment_pay;
  }

  @Override
  public void setListener() {
    mRootView.findViewById(R.id.btn_operition_pay).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(NewFukuanActivity.class);
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
