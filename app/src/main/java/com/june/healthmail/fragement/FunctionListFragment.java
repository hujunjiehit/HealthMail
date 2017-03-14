package com.june.healthmail.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.FukuanActivity;
import com.june.healthmail.activity.FunctionSetupActivity;
import com.june.healthmail.activity.PingjiaActivity;
import com.june.healthmail.activity.YuekeActivity;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bjhujunjie on 2016/9/21.
 */
public class FunctionListFragment extends Fragment implements View.OnClickListener{

  private View layout;

  private Button btnPingjia;
  private Button btnYueke;
  private Button btnFukuan;
  private ImageView imgSetup;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_function_list, container, false);
    initView();
    setOnListener();
//    initLogin();
    return layout;
  }

  private void initView() {
    btnPingjia = (Button) layout.findViewById(R.id.btn_operition_pingjia);
    btnYueke = (Button) layout.findViewById(R.id.btn_operition_yueke);
    btnFukuan = (Button) layout.findViewById(R.id.btn_operition_fukuan);
    imgSetup = (ImageView) layout.findViewById(R.id.img_setup);
  }


  private void setOnListener() {
    btnPingjia.setOnClickListener(this);
    btnYueke.setOnClickListener(this);
    btnFukuan.setOnClickListener(this);
    imgSetup.setOnClickListener(this);
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
    Intent intent = null;
      switch (v.getId()){
        case R.id.btn_operition_pingjia:
          if(CommonUntils.hasPermission()){
            intent = new Intent(getActivity(),PingjiaActivity.class);
            startActivity(intent);
          }else {
            Toast.makeText(getActivity(),"当前用户暂无授权，请联系软件作者购买授权",Toast.LENGTH_SHORT).show();
          }
          break;
        case R.id.btn_operition_yueke:
          if(CommonUntils.hasPermission()){
            Intent it = new Intent(getActivity(),YuekeActivity.class);
            startActivity(it);
          }else {
            Toast.makeText(getActivity(),"当前用户暂无授权，请联系软件作者购买授权",Toast.LENGTH_SHORT).show();
          }
          break;
        case R.id.img_setup:
          intent = new Intent(getActivity(),FunctionSetupActivity.class);
          startActivity(intent);
          break;
        case R.id.btn_operition_fukuan:
          intent = new Intent(getActivity(),FukuanActivity.class);
          startActivity(intent);
          break;
        default:
          break;
      }
  }
}
