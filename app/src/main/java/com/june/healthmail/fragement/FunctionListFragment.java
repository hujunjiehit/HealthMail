package com.june.healthmail.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.CancleGuanzhuActivity;
import com.june.healthmail.activity.FukuanActivity;
import com.june.healthmail.activity.FunctionSetupActivity;
import com.june.healthmail.activity.GuanzhuActivity;
import com.june.healthmail.activity.PingjiaActivity;
import com.june.healthmail.activity.PostCourseActivity;
import com.june.healthmail.activity.PostCourseDetailActivity;
import com.june.healthmail.activity.YuekeActivity;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.ShowProgress;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
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
  private Button btnPostCourse;
  private ImageView imgSetup;
  private TextView tvGuanzhu;
  private UserInfo userInfo;
  private ShowProgress showProgress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_function_list, container, false);
    initView();
    setOnListener();
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
//    initLogin();
    return layout;
  }

  private void initView() {
    btnPingjia = (Button) layout.findViewById(R.id.btn_operition_pingjia);
    btnYueke = (Button) layout.findViewById(R.id.btn_operition_yueke);
    btnFukuan = (Button) layout.findViewById(R.id.btn_operition_fukuan);
    btnPostCourse = (Button) layout.findViewById(R.id.btn_operition_post_course);
    imgSetup = (ImageView) layout.findViewById(R.id.img_setup);
    tvGuanzhu = (TextView) layout.findViewById(R.id.tv_guanzhu);
  }


  private void setOnListener() {
    btnPingjia.setOnClickListener(this);
    btnYueke.setOnClickListener(this);
    btnFukuan.setOnClickListener(this);
    btnPostCourse.setOnClickListener(this);
    imgSetup.setOnClickListener(this);
    tvGuanzhu.setOnClickListener(this);
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
//          if(CommonUntils.hasPermission()){
//            intent = new Intent(getActivity(),PingjiaActivity.class);
//            startActivity(intent);
//          }else {
//            Toast.makeText(getActivity(),"当前用户暂无授权，请联系软件作者购买授权",Toast.LENGTH_SHORT).show();
//          }
          checkPermission(PingjiaActivity.class);
          break;
        case R.id.btn_operition_yueke:
          checkPermission(YuekeActivity.class);
//          if(CommonUntils.hasPermission()){
//            Intent it = new Intent(getActivity(),YuekeActivity.class);
//            startActivity(it);
//          }else {
//            Toast.makeText(getActivity(),"当前用户暂无授权，请联系软件作者购买授权",Toast.LENGTH_SHORT).show();
//          }
          break;
        case R.id.img_setup:
          intent = new Intent(getActivity(),FunctionSetupActivity.class);
          startActivity(intent);
          break;
        case R.id.tv_guanzhu:
          AlertDialog dialog = new AlertDialog.Builder(getActivity())
              .setTitle("功能选择")
              .setMessage("请选择是要关注还是取消关注？")
              .setNegativeButton("取消关注", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  checkPermission(CancleGuanzhuActivity.class);
                }
              })
              .setPositiveButton("一键关注", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  checkPermission(GuanzhuActivity.class);
                }
              })
              .create();
          dialog.show();
          //checkPermission(PostCourseDetailActivity.class);
          break;
        case R.id.btn_operition_fukuan:
          checkPermission(FukuanActivity.class);
//          intent = new Intent(getActivity(),FukuanActivity.class);
//          startActivity(intent);
          break;
        case R.id.btn_operition_post_course:
          checkPermission(PostCourseActivity.class);
//          intent = new Intent(getActivity(),FukuanActivity.class);
//          startActivity(intent);
          break;
        default:
          break;
      }
  }

  private void checkPermission(final Class cls){
    String cloudCodeName = "getPermission";
    JSONObject job = new JSONObject();
    if(showProgress == null){
      showProgress = new ShowProgress(getActivity());
    }
    if(!showProgress.isShowing()){
      showProgress.setMessage("正在验证当前用户权限，请稍后...");
      showProgress.show();
    }
    try {
      job.put("objectId",userInfo.getObjectId());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    //创建云端逻辑
    AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
    cloudCode.callEndpoint(cloudCodeName, job, new CloudCodeListener() {
      @Override
      public void done(Object o, BmobException e) {
        if(showProgress != null && showProgress.isShowing()){
          showProgress.dismiss();
        }
        if(e == null){
          Log.e("test","云端逻辑调用成功：" + o.toString());
          if(o.toString().equals("true")){
            Intent it = new Intent(getActivity(),cls);
            startActivity(it);
          }else {
            Toast.makeText(getActivity(),o.toString(), Toast.LENGTH_LONG).show();
          }
        }else {
//          Toast.makeText(getActivity(),"权限验证异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
//          Intent it = new Intent(getActivity(),cls);
//          it.putExtra("exception",true);
//          startActivity(it);
        }
      }
    });
  }
}
