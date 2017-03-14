package com.june.healthmail.fragement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.LoginActivity;
import com.june.healthmail.activity.MainActivity;
import com.june.healthmail.activity.SuperRootActivity;
import com.june.healthmail.activity.WebViewActivity;
import com.june.healthmail.model.MessageDetails;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
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
  private TextView mTvCoinsNumber;
  private RelativeLayout ivGetHelp;

  private UserInfo userInfo;

  private TextView tvGoToTaobao;
  private ImageView ivUserIcon;

  private static final int HANDLER_THE_MESSAGES = 1;

  private ArrayList<MessageDetails> messageList = new ArrayList<>();
  private int messageIndex = 0;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case HANDLER_THE_MESSAGES:
          Log.d("test","开始处理消息,messgaeIndex = " + messageIndex);
          if(messageIndex < messageList.size()){
            dealTheMessage(messageList.get(messageIndex));
          }else {
            Log.d("test","消息处理完毕，更新用户信息");
            userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if(e==null){
                  Log.e("test","更新用户信息成功");
                  setUserDetails();
                }else{
                  Log.e("test","更新用户信息失败");
                }
              }
            });
          }
          break;
        default:
          break;
      }
    }
  };

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

  @Override
  public void onResume() {
    super.onResume();
    //更新金币信息
    if(userInfo != null){
      userInfo = BmobUser.getCurrentUser(UserInfo.class);
      setUserDetails();
    }
  }

  private void initView() {
    mViewNotLogined = layout.findViewById(R.id.layout_not_logined);
    mViewLogined = layout.findViewById(R.id.layout_logined);
    mTvUid = (TextView) layout.findViewById(R.id.tv_uid);
    mImgUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
    mTvUserType = (TextView) layout.findViewById(R.id.tv_user_type);
    mTvAllowDays = (TextView) layout.findViewById(R.id.tv_allow_days);
    mTvCoinsNumber = (TextView) layout.findViewById(R.id.tv_coins_number);
    tvGoToTaobao = (TextView) layout.findViewById(R.id.tv_go_to_taobao);
    ivUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
    ivGetHelp = (RelativeLayout) layout.findViewById(R.id.iv_get_help);
  }

  private void setOnListener() {
    layout.findViewById(R.id.tv_log_out).setOnClickListener(this);
    layout.findViewById(R.id.btn_unbind_device).setOnClickListener(this);
    layout.findViewById(R.id.btn_check_update).setOnClickListener(this);
    tvGoToTaobao.setOnClickListener(this);
    ivUserIcon.setOnClickListener(this);
    ivGetHelp.setOnClickListener(this);
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
      getMessagesFromServer();
    } else {
      mViewNotLogined.setVisibility(View.VISIBLE);
      mViewLogined.setVisibility(View.GONE);
    }
  }

  private void setUserDetails() {
    mTvCoinsNumber.setText("金币余额：" + userInfo.getCoinsNumber());
    if (userInfo.getUserType() == 0) {
      //普通用户
      mTvUserType.setText("普通用户");
      mTvAllowDays.setText("暂无授权，请联系软件作者购买");
      tvGoToTaobao.setVisibility(View.VISIBLE);
    }else if (userInfo.getUserType() == 1){
      //月卡用户
      mTvUserType.setText("月卡用户");
      getServerTime();
      tvGoToTaobao.setVisibility(View.VISIBLE);
    } else if (userInfo.getUserType() == 2){
      //永久用户
      mTvUserType.setText("永久用户");
      mTvAllowDays.setVisibility(View.GONE);
      tvGoToTaobao.setVisibility(View.GONE);
    } else if(userInfo.getUserType() == 1){
      //过期用户
      mTvUserType.setText("过期用户");
      mTvAllowDays.setText("授权已过期，请联系软件作者续费");
      tvGoToTaobao.setVisibility(View.VISIBLE);
    }else if(userInfo.getUserType() == 99){
      //管理员用户
      mTvUserType.setText("管理员用户");
      mTvAllowDays.setVisibility(View.GONE);
      tvGoToTaobao.setVisibility(View.GONE);
    } else if(userInfo.getUserType() == 100){
      //超级管理员用户
      mTvUserType.setText("超级管理员用户");
      mTvAllowDays.setVisibility(View.GONE);
      tvGoToTaobao.setVisibility(View.GONE);
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
        if(userInfo != null && (userInfo.getUserType() == 99 || userInfo.getUserType() == 100)){
          Intent intent = new Intent(getActivity(),SuperRootActivity.class);
          startActivity(intent);
        }
        break;
      case R.id.iv_get_help: // 金币帮助问号
        showGethelpDialog();
        break;
      default:
        break;
    }
  }

  private void showGethelpDialog() {
    AlertDialog dialog = new AlertDialog.Builder(getActivity())
            .setTitle("金币获得途径")
            .setMessage(" 1. 首次注册赠送100金币\n\n 2. 注册时填写邀请人手机号，双方各额外获得88金币\n\n 3. 邀请的用户开通月卡授权，获得588金币\n\n"+
            " 4. 邀请的用户开通永久授权，获得1888金币\n\n 5. 参加群内活动获得金币")
            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            }).create();
    dialog.show();
  }

  private void getMessagesFromServer() {
    BmobQuery<MessageDetails> query = new BmobQuery<MessageDetails>();
    query.addWhereEqualTo("username",userInfo.getUsername());
    query.addWhereEqualTo("status",1);
    query.findObjects(new FindListener<MessageDetails>() {
      @Override
      public void done(List<MessageDetails> object, BmobException e) {
        if(e==null){
          Log.d("test","查询消息成功：共"+object.size()+"条数据。");
          messageList.clear();
          for(MessageDetails message:object){
            messageList.add(message);
          }
          messageIndex = 0;
          mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
        }else{
          Log.i("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
        }
      }
    });
  }


  private void dealTheMessage(final MessageDetails messageDetails) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setCancelable(false);
    int messageType = messageDetails.getType();
      if(messageType == 0 || messageType == 1 || messageType == 2 || messageType == 3 || messageType == 4){
        //金币入账消息
          builder.setTitle("金币入账通知");
          if(messageType == 1 || messageType == 2 || messageType == 3){
            builder.setMessage(messageDetails.getReasons() + "\n被邀请人账号：" + messageDetails.getRelatedUserName());
          }else {
            builder.setMessage(messageDetails.getReasons());
          }
          builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    messageDetails.setStatus(0);
                    messageDetails.update(messageDetails.getObjectId(), new UpdateListener() {
                      @Override
                      public void done(BmobException e) {
                        if(e == null) {
                          Log.d("test","消息处理成功，开始处理下一条消息");
                          if(userInfo.getCoinsNumber() == null){
                            userInfo.setCoinsNumber(messageDetails.getScore());
                          }else {
                            userInfo.setCoinsNumber(userInfo.getCoinsNumber() + messageDetails.getScore());
                          }
                          messageIndex++;
                          mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
                        }else {
                          Log.e("test","消息处理失败："+e.getMessage()+","+e.getErrorCode());
                        }
                      }
                    });
                  }
                });
          builder.create().show();
      }else if(messageType == 5 || messageType == 6 ){
        //授权变动消息
        builder.setTitle("授权变动通知");
        if(messageType == 5){
          builder.setMessage("月卡授权开通成功，本次开通了" + messageDetails.getScore() + "天授权");
          if(userInfo.getUserType() == 1){
            //试用时间未过期
            userInfo.setAllowDays(userInfo.getAllowDays() + messageDetails.getScore());
          }else {
            userInfo.setUserType(1);
            userInfo.setAllowDays(messageDetails.getScore());
          }
        }else if(messageType == 6){
          builder.setMessage("永久授权开通成功");
          userInfo.setUserType(2);
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            messageDetails.setStatus(0);
            messageDetails.update(messageDetails.getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if(e == null) {
                  Log.d("test","消息处理成功，开始处理下一条消息");
                  messageIndex++;
                  mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
                }else {
                  Log.e("test","消息处理失败："+e.getMessage()+","+e.getErrorCode());
                }
              }
            });
          }
        });
        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
          @Override
          public void done(BmobException e) {
            if(e==null){
              Log.e("test","更新用户信息成功");
              builder.create().show();
            }else{
              Log.e("test","更新用户信息失败");
            }
          }
        });
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
