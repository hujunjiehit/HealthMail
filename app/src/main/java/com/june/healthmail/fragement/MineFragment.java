package com.june.healthmail.fragement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.june.healthmail.activity.FunctionSetupActivity;
import com.june.healthmail.activity.LoginActivity;
import com.june.healthmail.activity.MainActivity;
import com.june.healthmail.activity.SuperRootActivity;
import com.june.healthmail.activity.WebViewActivity;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

public class MineFragment extends Fragment implements View.OnClickListener{

  private View layout;
  private View mViewNotLogined;
  private View mViewLogined;
  private TextView mTvUid;
  private ImageView mImgUserIcon;
  private String uid;
  private String userName;
  private String icon;

  private TextView mTvUserType;
  private TextView mTvAllowDays;

  private UserInfo userInfo;

  private TextView tvGoToTaobao;
  private ImageView ivUserIcon;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_mine, container, false);
    initView();
    setOnListener();
    initLogin();
    return layout;
  }

  private void initView() {
    mViewNotLogined = layout.findViewById(R.id.layout_not_logined);
    mViewLogined = layout.findViewById(R.id.layout_logined);
    mTvUid = (TextView) layout.findViewById(R.id.tv_uid);
    mImgUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
    mTvUserType = (TextView) layout.findViewById(R.id.tv_user_type);
    mTvAllowDays = (TextView) layout.findViewById(R.id.tv_allow_days);
    tvGoToTaobao = (TextView) layout.findViewById(R.id.tv_go_to_taobao);
    ivUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
  }

  private void setOnListener() {
    layout.findViewById(R.id.tv_log_out).setOnClickListener(this);
    layout.findViewById(R.id.btn_unbind_device).setOnClickListener(this);
    layout.findViewById(R.id.btn_check_update).setOnClickListener(this);
    tvGoToTaobao.setOnClickListener(this);
    ivUserIcon.setOnClickListener(this);
  }

  /**
   * 初始化登录信息
   */
  private void initLogin() {
    MainActivity activity = (MainActivity) getActivity();
    boolean isLogined = activity.getLogined();
    if (isLogined) {
      // 读取登录类型
      SharedPreferences sp = activity.getSharedPreferences("login_type",
          Context.MODE_PRIVATE);
      int type = sp.getInt("login_type", 0);
      switch (type) {
        case 1: // 通过Bmob登录
          break;
        case 2: // 通过微博登录
          //icon = activity.getIcon();
          //UILUtils.displayImage(getActivity(), icon, mImgUserIcon);
          break;
        default:
          break;
      }
      userInfo = BmobUser.getCurrentUser(UserInfo.class);
      userName = activity.getUserName();
      mViewNotLogined.setVisibility(View.GONE);
      mViewLogined.setVisibility(View.VISIBLE);
      mTvUid.setText(userName);
      setUserDetails();
    } else {
      mViewNotLogined.setVisibility(View.VISIBLE);
      mViewLogined.setVisibility(View.GONE);
    }
  }

  private void setUserDetails() {
    if (userInfo.getUserType() == 0) {
      //普通用户
      mTvUserType.setText("普通用户");
      mTvAllowDays.setText("暂无授权，请联系软件作者购买");
      tvGoToTaobao.setVisibility(View.VISIBLE);
    }else if (userInfo.getUserType() == 1){
      //月卡用户
      mTvUserType.setText("月卡用户");
      getServerTime();
      mTvAllowDays.setVisibility(View.INVISIBLE);
      //mTvAllowDays.setText("剩余授权时间：" + allowTime);
      tvGoToTaobao.setVisibility(View.VISIBLE);
    } else if (userInfo.getUserType() == 2){
      //永久用户
      mTvUserType.setText("永久用户");
      mTvAllowDays.setVisibility(View.GONE);
      tvGoToTaobao.setVisibility(View.GONE);
    } else {
      //过期用户
      mTvUserType.setText("过期用户");
      mTvAllowDays.setText("授权已过期，请联系软件作者续费");
      tvGoToTaobao.setVisibility(View.VISIBLE);
    }
  }

  private void getServerTime() {
    Bmob.getServerTime(new QueryListener<Long>() {
      @Override
      public void done(Long aLong, BmobException e) {
        if(e == null){
          if(userInfo.getBeginTime() == null || userInfo.getBeginTime() == 0){
            //如果没有记录beginTime,那么写入当前服务器时间
             Log.e("test","beginTime is null,update it");
            mTvAllowDays.setVisibility(View.VISIBLE);
            mTvAllowDays.setText("剩余授权时间：" + userInfo.getAllowDays() + "天");
            BmobUser bmobUser = BmobUser.getCurrentUser();
            userInfo.setBeginTime(aLong);
            userInfo.update(bmobUser.getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if(e==null){
                  Log.e("test","更新beginTime成功");
                }else{
                  Log.e("test","更新beginTime失败");
                }
              }
            });
          }else{
            mTvAllowDays.setVisibility(View.VISIBLE);
            Log.e("test","beginTime = " + userInfo.getBeginTime());
            Log.e("test","serverTime = " + aLong);
            double usedHours = (aLong - userInfo.getBeginTime())/3600; //授权已经使用的小时数
            Log.e("test","usedHours = " + usedHours);
            if(usedHours < userInfo.getAllowDays()*24){
              String leftTimeDesc = getLeftTimeDesc(userInfo.getAllowDays()*24 - (int)usedHours);
              mTvAllowDays.setText("剩余授权时间：" + leftTimeDesc);
            }else{
              //用户授权已过期，更新用户信息
              mTvUserType.setText("过期用户");
              mTvAllowDays.setText("授权已过期，请联系软件作者续费");

              BmobUser bmobUser = BmobUser.getCurrentUser();
              userInfo.setBeginTime((long) 0);
              userInfo.setUserType(-1);
              userInfo.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                  if(e==null){
                    Log.e("test","更新用户信息成功");
                  }else{
                    Log.e("test","更新用户信息失败");
                  }
                }
              });
            }
          }
        }
      }
    });
  }

  private String getLeftTimeDesc(int leftHours) {
    Log.e("test","leftHours = " + leftHours);
    StringBuilder sb = new StringBuilder();
    if(leftHours >= 24) {
      sb.append(leftHours/24+"天");
    }
    sb.append(leftHours%24 + "小时");
    return sb.toString();
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
    switch (v.getId()) {
      case R.id.tv_log_out: // 注销登录
        BmobUser.logOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        break;
      case R.id.btn_unbind_device: //解除设备绑定
        if(userInfo != null) {
          AlertDialog dialog = new AlertDialog.Builder(getActivity())
              .setTitle("重要提示")
              .setMessage("每个帐号可以解除三次设备绑定，当前剩余解绑次数：" + userInfo.getUnbindTimes() + "\n\n是否确定解绑?")
              .setNegativeButton("取消解绑", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .setPositiveButton("确定解绑", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  Log.e("test", "用户选择确定解绑");
                  if(userInfo.getUnbindTimes() > 0){
                      userInfo.setUnbindTimes(userInfo.getUnbindTimes() - 1);
                      userInfo.setBindMac("");
                      userInfo.setBindDesc("");
                      BmobUser bmobUser = BmobUser.getCurrentUser();
                      userInfo.update(bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                          if(e==null){
                              Log.e("test","解绑成功");
                              BmobUser.logOut();
                              startActivity(new Intent(getActivity(), LoginActivity.class));
                              getActivity().finish();
                          }else{
                            Log.e("test","解绑失败，请重试");
                            Toast.makeText(getActivity(),"解绑失败，请重试",Toast.LENGTH_LONG).show();
                          }
                        }
                      });
                  }else {
                    Toast.makeText(getActivity(),"解绑失败，当前帐号无剩余解绑次数",Toast.LENGTH_LONG).show();
                  }
                }
              }).create();
          dialog.show();
        }
        break;
      case R.id.btn_check_update: // 点击检查更新按钮
        Toast.makeText(getActivity(), CommonUntils.getVersion(getActivity()),Toast.LENGTH_LONG).show();
        BmobUpdateAgent.forceUpdate(getActivity());
        break;
      case R.id.tv_go_to_taobao: // 点击购买链接
        openTaobaoShopping();
        break;
      case R.id.user_icon: // 点击用户头像，拉起超级用户配置管理界面
        if(userInfo != null && (userInfo.getUsername().equals("13027909110") || userInfo.getUsername().equals("18002570032"))){
          Intent intent = new Intent(getActivity(),SuperRootActivity.class);
          startActivity(intent);
        }
        break;
      default:
        break;
    }
  }

  private void openTaobaoShopping() {
    String url = "https://item.taobao.com/item.htm?spm=a230r.1.14.21.2l6ruV&id=540430775263";
    Intent intent = new Intent();
    if (CommonUntils.checkPackage(getActivity(),"com.taobao.taobao")){
      Log.e("test","taobao is not installed");
      intent.setAction("android.intent.action.VIEW");
      Uri uri = Uri.parse(url);
      intent.setData(uri);
      startActivity(intent);
    } else {
      intent.putExtra("url",url);
      intent.setClass(getActivity(),WebViewActivity.class);
      startActivity(intent);
    }
  }
}
