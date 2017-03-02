package com.june.healthmail.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.june.healthmail.R;

/**
 * Created by bjhujunjie on 2016/9/21.
 */
public class MessageFragment extends Fragment implements View.OnClickListener{

  private View layout;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_message, container, false);
//    initView();
//    setOnListener();
//    initLogin();
    return layout;
  }

  @Override
  public void onClick(View v) {

  }
}
