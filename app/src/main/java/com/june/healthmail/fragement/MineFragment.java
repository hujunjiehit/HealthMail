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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.june.healthmail.untils.Installation;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;
import com.june.healthmail.untils.TimeUntils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
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
  private RelativeLayout ivGetHelpAddPingjia;
  private RelativeLayout ivGetHelpAddYueke;

  private UserInfo userInfo;

  private TextView tvGoToBuyConins;
  private TextView tvGoToTaobao;
  private ImageView ivUserIcon;
  private ShowProgress showProgress;

  private TextView tvYuekeTimes;
  private TextView tvPingjiaTimes;
  private Button btnAddYuekeTimes;
  private Button btnAddPingjiaTimes;

  private static final int HANDLER_THE_MESSAGES = 1;
  private static final int UPDATE_THE_TIMES = 2;
  private static final int UPDATE_USER_INFO = 3;

  private ArrayList<MessageDetails> messageList = new ArrayList<>();
  private int messageIndex = 0;

  private boolean success1 = false;
  private boolean success2 = false;
  private PreferenceHelper sph;

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
            userInfo.setAppVersion(CommonUntils.getVersionInt(getActivity()));
            if(TextUtils.isEmpty(userInfo.getInstallId())){
              userInfo.setInstallId(Installation.id(getActivity()));
            }
            success1 = true;
            userInfo.setBindMac("testmac");
            Log.e("test","success1 ");
            if(success1 && success2) {
              mHandler.sendEmptyMessage(UPDATE_USER_INFO);
            }
          }
          break;
        case UPDATE_THE_TIMES:
          Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
              if (e == null) {
                String serverDay = TimeUntils.transForDate1(new Integer(String.valueOf(aLong)));
                Log.e("test"," serverDay = " + serverDay);
                Log.e("test"," lastDay = " + userInfo.getLastDay());
                int x = 1;
                if (userInfo.getUserType() >= 3) {
                  //高级永久用户
                  x = 3;
                }

                if(TextUtils.isEmpty(userInfo.getLastDay()) || serverDay.equals(userInfo.getLastDay())) {
                  Log.e("test","null or today");
                  if(TextUtils.isEmpty(userInfo.getLastDay())) {
                    userInfo.setLastDay(serverDay);
                    userInfo.setYuekeTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                    userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  }
                  PreferenceHelper.getInstance().setRemainYuekeTimes(userInfo.getYuekeTimes());
                  PreferenceHelper.getInstance().setRemainPingjiaTimes(userInfo.getPingjiaTimes());
                } else {
                  Log.e("test","not today");
                  userInfo.setLastDay(serverDay);
                  userInfo.setYuekeTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  PreferenceHelper.getInstance().setRemainYuekeTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  PreferenceHelper.getInstance().setRemainPingjiaTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                }
                success2 = true;
                Log.e("test","success2 ");
                if(success1 && success2) {
                  mHandler.sendEmptyMessage(UPDATE_USER_INFO);
                }
              }
            }
          });
//          tvYuekeTimes.setText(PreferenceHelper.getInstance().getFreeTimesPerday() + "次");
//          tvPingjiaTimes.setText(PreferenceHelper.getInstance().getFreeTimesPerday() + "次");
          break;
        case UPDATE_USER_INFO:
          userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
              if(e==null){
                Log.e("test","更新用户信息成功");
                setUserDetails();
                tvYuekeTimes.setText(PreferenceHelper.getInstance().getRemainYuekeTimes() + "次");
                tvPingjiaTimes.setText(PreferenceHelper.getInstance().getRemainPingjiaTimes() + "次");
              }else{
                Log.e("test","更新用户信息失败");
              }
            }
          });
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
    sph = PreferenceHelper.getInstance();
    initView();
    setOnListener();
    initLogin();
    getConfigs();
    return layout;
  }

  @Override
  public void onResume() {
    super.onResume();
    //更新金币信息
    if(userInfo != null){
      userInfo = BmobUser.getCurrentUser(UserInfo.class);
      setUserDetails();
      tvYuekeTimes.setText(PreferenceHelper.getInstance().getRemainYuekeTimes() + "次");
      tvPingjiaTimes.setText(PreferenceHelper.getInstance().getRemainPingjiaTimes() + "次");
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
    tvGoToBuyConins = (TextView) layout.findViewById(R.id.tv_go_to_buy_coins);
    ivUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
    ivGetHelp = (RelativeLayout) layout.findViewById(R.id.iv_get_help);
    ivGetHelpAddPingjia = (RelativeLayout) layout.findViewById(R.id.iv_get_help_add_pigjia);
    ivGetHelpAddYueke = (RelativeLayout) layout.findViewById(R.id.iv_get_help_add_yueke);
    tvYuekeTimes = (TextView) layout.findViewById(R.id.tv_yueke_times);
    tvPingjiaTimes = (TextView) layout.findViewById(R.id.tv_pingjia_times);
    btnAddPingjiaTimes = (Button) layout.findViewById(R.id.btn_add_pingjia_times);
    btnAddYuekeTimes = (Button) layout.findViewById(R.id.btn_add_yueke_times);
  }

  private void setOnListener() {
    layout.findViewById(R.id.tv_log_out).setOnClickListener(this);
    tvGoToTaobao.setOnClickListener(this);
    tvGoToBuyConins.setOnClickListener(this);
    ivUserIcon.setOnClickListener(this);
    ivGetHelp.setOnClickListener(this);
    btnAddPingjiaTimes.setOnClickListener(this);
    btnAddYuekeTimes.setOnClickListener(this);
    ivGetHelpAddPingjia.setOnClickListener(this);
    ivGetHelpAddYueke.setOnClickListener(this);

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
    } else if (userInfo.getUserType() == 2) {
      //永久用户
      mTvUserType.setText("永久用户");
      mTvAllowDays.setVisibility(View.GONE);
      tvGoToTaobao.setText("点击升级高级永久");
      tvGoToTaobao.setVisibility(View.VISIBLE);
    } else if (userInfo.getUserType() == 3) {
      //高级永久用户
      mTvUserType.setText("高级永久用户");
      mTvAllowDays.setVisibility(View.GONE);
      tvGoToTaobao.setVisibility(View.GONE);
    } else if(userInfo.getUserType() == -1){
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
            if(TextUtils.isEmpty(userInfo.getInstallId())){
              userInfo.setInstallId(Installation.id(getActivity()));
            }
            userInfo.setAppVersion(CommonUntils.getVersionInt(getActivity()));
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
              userInfo.setAppVersion(CommonUntils.getVersionInt(getActivity()));
              if(TextUtils.isEmpty(userInfo.getInstallId())){
                userInfo.setInstallId(Installation.id(getActivity()));
              }
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
      case R.id.tv_go_to_taobao: // 点击购买授权链接
        openTaobaoShopping(PreferenceHelper.getInstance().getBuyAuthUrl());
        break;
      case R.id.tv_go_to_buy_coins: // 点击购买金币链接
        openTaobaoShopping(PreferenceHelper.getInstance().getBuyCoinsUrl());
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
      case R.id.iv_get_help_add_pigjia: // 金币帮助问号
        showAddPingjiaDialog();
        break;
      case R.id.iv_get_help_add_yueke: // 金币帮助问号
        showAddYuekeDialog();
        break;
      case R.id.btn_add_pingjia_times: //充值评价次数
        showAddPingjiaTimesDialog();
        break;
      case R.id.btn_add_yueke_times:  //充值约课次数
        showAddYuekeTimesDialog();
        break;
      default:
        break;
    }
  }

  private void showAddYuekeDialog() {
    AlertDialog dialog = new AlertDialog.Builder(getActivity())
        .setTitle("约课次数说明")
        .setMessage("1.每成功约一节课消耗一次约课次数\n\n2. 每天的免费约课次数过了晚上十二点，重新登陆之后，自动恢复\n\n3. 免费次数用完的，如果还想继续使用，需要充值约课次数\n\n4.消耗一个金币可以充值10次约课次数")
        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).create();
    dialog.show();
  }

  private void showAddPingjiaDialog() {
    AlertDialog dialog = new AlertDialog.Builder(getActivity())
        .setTitle("评价次数说明")
        .setMessage("1.每成功评价一节课消耗一次评价次数\n\n2. 每天的免费评价次数过了晚上十二点，重新登陆之后，自动恢复\n\n3. 免费次数用完的，如果还想继续使用，需要充值评价次数\n\n4.消耗一个金币可以充值10次评价次数")
        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).create();
    dialog.show();
  }

  private void showGethelpDialog() {
    AlertDialog dialog = new AlertDialog.Builder(getActivity())
            .setTitle("金币获得途径")
            .setMessage(" 1. 首次注册赠送100金币\n\n 2. 注册时填写邀请人手机号,额外获得88金币\n\n 3. 邀请的用户开通月卡授权，获得588金币\n\n"+
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
      }else if(messageType == 5 || messageType == 6 || messageType == 7 || messageType == 8){
        //授权变动消息
        builder.setTitle("授权变动通知");
        if(messageType == 5){
          if(messageDetails.getScore() == 1){
            builder.setMessage("试用授权开通成功");
          }else{
            builder.setMessage("月卡授权开通成功，本次开通了" + messageDetails.getScore() + "天授权");
          }
          if(userInfo.getUserType() == 1){
            //试用时间未过期
            userInfo.setAllowDays(userInfo.getAllowDays() + messageDetails.getScore());
          }else if(userInfo.getUserType() == 0 || userInfo.getUserType() == -1){
            userInfo.setUserType(1);
            userInfo.setAllowDays(messageDetails.getScore());
          }
        }else if(messageType == 6){
          builder.setMessage("永久授权开通成功");
          userInfo.setUserType(2);
        }else if(messageType == 7){
          builder.setMessage("成功升级高级永久");
          userInfo.setUserType(3);
          userInfo.setLastDay("");
        }else if(messageType == 8){
          builder.setMessage("成功开通付款永久");
          userInfo.setPayStatus(1);
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
                  Log.e("test","消息更新成功");
                  userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                      if(e==null){
                        Log.d("test","用户信息更新成功，开始处理下一条消息");
                        messageIndex++;
                        mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
                      }else{
                        Log.e("test","更新用户信息失败");
                      }
                    }
                  });
                }else {
                  Log.e("test","消息处理失败："+e.getMessage()+","+e.getErrorCode());
                }
              }
            });
          }
        });
        builder.create().show();
      } else if(messageType == 99){
        //系统公告消息
        builder.setTitle("系统公告");
        builder.setMessage(messageDetails.getReasons());
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            messageDetails.setStatus(0);
            messageDetails.update(messageDetails.getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if(e == null) {
                  Log.e("test","消息更新成功");
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
      }else {
        //未知消息类型，不处理，继续下一个
        messageIndex++;
        mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
      }
  }

  private void getConfigs() {
    String cloudCodeName = "getConfigs";
    JSONObject job = new JSONObject();
    try {
      job.put("action","getConfigs");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    //创建云端逻辑
    AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
    cloudCode.callEndpoint(cloudCodeName, job, new CloudCodeListener() {
      @Override
      public void done(Object o, BmobException e) {
        if(e == null){
          Log.e("test","云端逻辑调用成功：" + o.toString());
          String [] arrays = o.toString().split("::");
          PreferenceHelper.getInstance().setBuyAuthUrl(arrays[0]);
          PreferenceHelper.getInstance().setBuyConisUrl(arrays[1]);
          PreferenceHelper.getInstance().setCoinsCostForPost(Integer.parseInt(arrays[2]));
          PreferenceHelper.getInstance().setCoinsCostForPostWithPicture(Integer.parseInt(arrays[3]));
          PreferenceHelper.getInstance().setFreeTimesPerday(Integer.parseInt(arrays[4]));
          mHandler.sendEmptyMessage(UPDATE_THE_TIMES);
        }else {
          Log.e("test","云端逻辑调用异常：" + e.toString());
        }
      }
    });
  }

  private void openTaobaoShopping(final String url){
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

  private void showAddPingjiaTimesDialog(){
    View diaog_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_times,null);
    final EditText et_input_times = (EditText) diaog_view.findViewById(R.id.et_input_add_times);
    final TextView tv_show_cost_coins = (TextView) diaog_view.findViewById(R.id.tv_cost_coins);
    et_input_times.setInputType(InputType.TYPE_CLASS_NUMBER);
    et_input_times.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(s)) {
          tv_show_cost_coins.setText((Integer.valueOf(s.toString()) - 1)/10 + 1 + "个金币");
        } else {
          tv_show_cost_coins.setText("");
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("充值评价次数");
    builder.setView(diaog_view);
    builder.setNegativeButton("取消充值", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("确定充值", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(et_input_times.getText().toString().trim())){
          toast("充值次数不能为空！");
          return;
        }
        final int number,cost;
        number = Integer.valueOf(et_input_times.getText().toString().trim());
        cost = (number - 1)/10 + 1;
        Log.e("test", "充值消耗金币数量：" + cost);
        Log.e("test","用户当前金币数量:" + userInfo.getCoinsNumber());
        if(userInfo.getCoinsNumber() < cost) {
          toast("金币余额不足，请先购买金币！");
          return;
        }
        userInfo.setCoinsNumber(userInfo.getCoinsNumber() - cost);
        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
          @Override
          public void done(BmobException e) {
            if(e==null){
              Log.e("test", "扣除" + cost + "个金币");
              toast("扣除" + cost + "个金币");
              sph.setRemainPingjiaTimes(sph.getRemainPingjiaTimes() + number);
              userInfo.setPingjiaTimes(sph.getRemainPingjiaTimes());
              mHandler.sendEmptyMessage(UPDATE_USER_INFO);
            }else{
              Log.e("test","金币扣除失败");
            }
          }
        });
      }
    });
    builder.create().show();
  }

  private void showAddYuekeTimesDialog(){
    View diaog_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_times,null);
    final EditText et_input_times = (EditText) diaog_view.findViewById(R.id.et_input_add_times);
    final TextView tv_show_cost_coins = (TextView) diaog_view.findViewById(R.id.tv_cost_coins);
    et_input_times.setInputType(InputType.TYPE_CLASS_NUMBER);
    et_input_times.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(s)) {
          tv_show_cost_coins.setText((Integer.valueOf(s.toString()) - 1)/10 + 1 + "个金币");
        } else {
          tv_show_cost_coins.setText("");
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("充值约课次数");
    builder.setView(diaog_view);
    builder.setNegativeButton("取消充值", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("确定充值", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(et_input_times.getText().toString().trim())){
          toast("充值次数不能为空！");
          return;
        }
        final int number,cost;
        number = Integer.valueOf(et_input_times.getText().toString().trim());
        cost = (number - 1)/10 + 1;
        Log.e("test", "充值消耗金币数量：" + cost);
        Log.e("test","用户当前金币数量:" + userInfo.getCoinsNumber());
        if(userInfo.getCoinsNumber() < cost) {
          toast("金币余额不足，请先购买金币！");
          return;
        }
        userInfo.setCoinsNumber(userInfo.getCoinsNumber() - cost);
        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
          @Override
          public void done(BmobException e) {
            if(e==null){
              Log.e("test", "扣除" + cost + "个金币");
              toast("扣除" + cost + "个金币");
              sph.setRemainYuekeTimes(sph.getRemainYuekeTimes() + number);
              userInfo.setYuekeTimes(sph.getRemainYuekeTimes());
              mHandler.sendEmptyMessage(UPDATE_USER_INFO);
            }else{
              Log.e("test","金币扣除失败");
            }
          }
        });
      }
    });
    builder.create().show();
  }

  private void toast(String str){
    Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
  }
}
