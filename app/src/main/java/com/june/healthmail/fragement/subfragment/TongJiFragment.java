package com.june.healthmail.fragement.subfragment;

import android.view.View;

import com.june.healthmail.R;
import com.june.healthmail.activity.SijiaoLoginActivity;
import com.june.healthmail.fragement.BaseFragment;

/**
 * Created by june on 2017/12/2.
 */

public class TongJiFragment extends BaseFragment {
  @Override
  public int setLayout() {
    return R.layout.fragment_tongji;
  }

  @Override
  public void setListener() {
    mRootView.findViewById(R.id.btn_operition_tongji).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(SijiaoLoginActivity.class, 2);
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
