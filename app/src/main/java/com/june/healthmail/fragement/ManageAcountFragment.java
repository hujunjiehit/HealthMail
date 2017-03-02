package com.june.healthmail.fragement;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.june.healthmail.R;

/**
 * 小号管理fragment
 */
public class ManageAcountFragment extends Fragment implements View.OnClickListener{

  private View layout;
  private ListView mListView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_manage_acount, container, false);
    initView();
//    setOnListener();
//    initLogin();
    return layout;
  }

  private void initView() {
    mListView = (ListView) layout.findViewById(R.id.list_view);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    // 将layout从父组件中移除
    ViewGroup parent = (ViewGroup) layout.getParent();
    parent.removeView(layout);
  }

  @Override
  public void onClick(View v) {

  }
}
