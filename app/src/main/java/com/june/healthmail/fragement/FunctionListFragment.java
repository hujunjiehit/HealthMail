package com.june.healthmail.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.PingjiaActivity;
import com.june.healthmail.activity.YuekeActivity;
import com.june.healthmail.untils.CommonUntils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

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
  }


  private void setOnListener() {
    btnPingjia.setOnClickListener(this);
    btnYueke.setOnClickListener(this);
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
      switch (v.getId()){
        case R.id.btn_operition_pingjia:
          Toast.makeText(getActivity(),"进入评价页面",Toast.LENGTH_SHORT).show();

          Intent intent = new Intent(getActivity(),PingjiaActivity.class);
          startActivity(intent);

          new Thread(new Runnable() {
            @Override
            public void run() {
              Response response = null;
              try {
                OkHttpClient mOkHttpClient = new OkHttpClient();
                FormBody body = new FormBody.Builder()
                    .add("data","{\"userPassword\":\"0a4532a389a4a0553a6ea9eb4b1ef17c\",\"grantType\":\"app_credential\",\"userName\":\"13064529726\",\"thirdLoginType\":\"0\"}")
                    .build();

                Request.Builder builder  = new Request.Builder().url("http://ssl.healthmall.cn/data/app/token/accessToken.do").post(body);
                //builder.addHeader(key,value);  //将请求头以键值对形式添加，可添加多个请求头
                builder.addHeader("User-Agent", CommonUntils.getUserAgent(getActivity())); //必须
                builder.addHeader("appId","101"); //必须

                builder.addHeader("deviceId","android_" + CommonUntils.getLocalMacAddressFromIp(getActivity())); //非必须
                builder.addHeader("deviceType","1"); //非必须

                builder.addHeader("timeStamp",String.valueOf(System.currentTimeMillis()));  //必须

                builder.addHeader("versionCode","67"); //非必须
                builder.addHeader("versionName","2.5.2.1"); //非必须

                Request request = builder.build();
                response = mOkHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                  Log.e("test","body:"+response.body().toString());
                  response.close();
                } else {
                  throw new IOException("Unexpected code " + response);
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          });

          break;
        case R.id.btn_operition_yueke:
          Toast.makeText(getActivity(),"进入约课页面",Toast.LENGTH_SHORT).show();
          //CommonUntils.getLocalMacAddressFromIp(getActivity());
          Intent it = new Intent(getActivity(),YuekeActivity.class);
          startActivity(it);
          break;
        default:
          break;
      }
  }
}
