package com.june.healthmail.fragement.subfragment;

import android.view.View;

import com.june.healthmail.R;
import com.june.healthmail.activity.CancleGuanzhuActivity;
import com.june.healthmail.activity.GuanzhuActivity;
import com.june.healthmail.fragement.BaseFragment;

/**
 * Created by june on 2017/10/24.
 */

public class GuanzhuFragment extends BaseFragment {


  private View layout;

  @Override
  public int setLayout() {
    return R.layout.fragment_guanzhu;
  }

  @Override
  public void setListener() {
    mRootView.findViewById(R.id.btn_guanzhu).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(GuanzhuActivity.class);
      }
    });

    mRootView.findViewById(R.id.btn_cancel_guanzhu).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(CancleGuanzhuActivity.class);
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
