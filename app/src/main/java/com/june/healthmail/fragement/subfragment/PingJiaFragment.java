package com.june.healthmail.fragement.subfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.june.healthmail.R;
import com.june.healthmail.fragement.BaseFragment;
import com.june.healthmail.improve.activity.NewPingjiaActivity;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by june on 2017/10/24.
 */

public class PingJiaFragment extends BaseFragment {


  private View layout;

  @Override
  public int setLayout() {
    return R.layout.fragment_pingjia;
  }

  @Override
  public void setListener() {
    mRootView.findViewById(R.id.btn_operition_pingjia).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission(NewPingjiaActivity.class);
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
