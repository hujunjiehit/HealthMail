package com.june.healthmail.fragement.subfragment;

import android.view.View;

import com.june.healthmail.R;
import com.june.healthmail.activity.SijiaoLoginActivity;
import com.june.healthmail.fragement.BaseFragment;

/**
 * Created by june on 2017/10/24.
 */

public class FaKeFragment extends BaseFragment {


  private View layout;

  @Override
  public int setLayout() {
    return R.layout.fragment_fake;
  }

  @Override
  public void setListener() {
    mRootView.findViewById(R.id.btn_operition_fake).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(SijiaoLoginActivity.class, 1);
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
