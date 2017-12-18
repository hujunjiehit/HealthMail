package com.june.healthmail.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.june.healthmail.activity.CoinsDetailActivity;
import com.june.healthmail.activity.LoginActivity;
import com.june.healthmail.activity.MainActivity;
import com.june.healthmail.activity.ProxyPersonActivity;
import com.june.healthmail.activity.SuperRootActivity;
import com.june.healthmail.activity.WebViewActivity;
import com.june.healthmail.http.ApiService;
import com.june.healthmail.http.HttpManager;
import com.june.healthmail.http.bean.GetConfigsBean;
import com.june.healthmail.model.MessageDetails;
import com.june.healthmail.model.ProxyInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.Installation;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;
import com.june.healthmail.untils.TimeUntils;
import com.june.healthmail.untils.Tools;
import com.june.healthmail.view.CircleImageView;
import com.june.healthmail.view.CustomSettingItem;
import com.tencent.bugly.beta.Beta;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

  @BindView(R.id.img_head)
  CircleImageView mImgHead;
  @BindView(R.id.tv_user_name)
  TextView mTvUserName;
  @BindView(R.id.layout_my_count)
  RelativeLayout mLayoutMyCount;
  @BindView(R.id.setting_user_type)
  CustomSettingItem mSettingUserType;
  @BindView(R.id.setting_left_time)
  CustomSettingItem mSettingLeftTime;
  @BindView(R.id.setting_coins_number)
  CustomSettingItem mSettingCoinsNumber;
  @BindView(R.id.setting_left_yueke_count)
  CustomSettingItem mSettingLeftYuekeCount;
  @BindView(R.id.setting_left_pingjia_count)
  CustomSettingItem mSettingLeftPingjiaCount;
  @BindView(R.id.setting_qq_group)
  CustomSettingItem mSettingQqGroup;
  @BindView(R.id.setting_current_version)
  CustomSettingItem mSettingCurrentVersion;
  @BindView(R.id.btn_logout)
  Button mBtnLogout;
  @BindView(R.id.div_left_time)
  View mDivLeftTime;
  @BindView(R.id.div_max_number)
  View mDivMaxNumber;
  @BindView(R.id.setting_max_number)
  CustomSettingItem mSettingMaxNumber;
  private Unbinder mUnbinder;


  private View layout;
  private View mViewLogined;
  private TextView mTvUid;
  private TextView mTvQQGroup;
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
  private static final int UPDATE_QQ_GROUP = 4;

  private ArrayList<MessageDetails> messageList = new ArrayList<>();
  private int messageIndex = 0;

  private boolean success1 = false;
  private boolean success2 = false;
  private PreferenceHelper sph;
  private Retrofit mRetrofit;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case HANDLER_THE_MESSAGES:
          Log.d("test", "开始处理消息,messgaeIndex = " + messageIndex);
          if (messageIndex < messageList.size()) {
            dealTheMessage(messageList.get(messageIndex));
          } else {
            Log.d("test", "消息处理完毕，更新用户信息");
            if (getActivity() != null) {
              userInfo.setAppVersion(CommonUntils.getVersionInt(getActivity()));
              if (TextUtils.isEmpty(userInfo.getInstallId())) {
                userInfo.setInstallId(Installation.id(getActivity()));
              }
              success1 = true;
              userInfo.setBindMac("testmac");
              Log.e("test", "success1 ");
              if (success1 && success2) {
                mHandler.sendEmptyMessage(UPDATE_USER_INFO);
              }
            }
          }
          break;
        case UPDATE_THE_TIMES:
          Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
              if (e == null) {
                String serverDay = TimeUntils.transForDate1(new Integer(String.valueOf(aLong)));
                Log.e("test", " serverDay = " + serverDay);
                Log.e("test", " lastDay = " + userInfo.getLastDay());
                int x = 1;
                if (userInfo.getXTimes() != null) {
                  x = userInfo.getXTimes();
                } else {
                  if (userInfo.getUserType() >= 3) {
                    //高级永久用户
                    x = 3;
                  }

                  if (userInfo.getAutoPay() != null && userInfo.getAutoPay() == 2) {
                    x = 5;
                  }
                }
                if (TextUtils.isEmpty(userInfo.getLastDay()) || serverDay.equals(userInfo.getLastDay())) {
                  Log.e("test", "null or today");
                  if (TextUtils.isEmpty(userInfo.getLastDay())) {
                    userInfo.setLastDay(serverDay);
                    userInfo.setYuekeTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                    userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  }
                  PreferenceHelper.getInstance().setRemainYuekeTimes(userInfo.getYuekeTimes());
                  PreferenceHelper.getInstance().setRemainPingjiaTimes(userInfo.getPingjiaTimes());
                } else {
                  Log.e("test", "not today");
                  userInfo.setLastDay(serverDay);
                  userInfo.setYuekeTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  PreferenceHelper.getInstance().setRemainYuekeTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                  PreferenceHelper.getInstance().setRemainPingjiaTimes(PreferenceHelper.getInstance().getFreeTimesPerday() * x);
                }
                success2 = true;
                Log.e("test", "success2 ");
                if (success1 && success2) {
                  mHandler.sendEmptyMessage(UPDATE_USER_INFO);
                }
              }
            }
          });
          break;
        case UPDATE_USER_INFO:
          userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
              if (e == null) {
                Log.e("test", "更新用户信息成功");
                setUserDetails();
                mSettingLeftYuekeCount.setSubText(PreferenceHelper.getInstance().getRemainYuekeTimes() + "次");
                mSettingLeftPingjiaCount.setSubText(PreferenceHelper.getInstance().getRemainPingjiaTimes() + "次");
                if (PreferenceHelper.getInstance().getAutoJump() == 1) {
                  //有活动
                  postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      if (getActivity() != null) {
                        ((MainActivity) getActivity()).goToFragment(0);
                      }
                    }
                  }, 100);
                }
              } else {
                Log.e("test", "更新用户信息失败");
              }
            }
          });
          break;
        case UPDATE_QQ_GROUP:
          String str = msg.obj.toString();
          if (str.contains("::")) {
            String[] array = str.split("::");
            mSettingQqGroup.setSubText(array[1]);
          } else {
            mSettingQqGroup.setSubText(str);
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
    mUnbinder = ButterKnife.bind(this, layout);
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
    if (userInfo != null) {
      userInfo = BmobUser.getCurrentUser(UserInfo.class);
      setUserDetails();
      mSettingLeftYuekeCount.setSubText(PreferenceHelper.getInstance().getRemainYuekeTimes() + "次");
      mSettingLeftPingjiaCount.setSubText(PreferenceHelper.getInstance().getRemainPingjiaTimes() + "次");
    }
  }

  private void initView() {
//    mViewLogined = layout.findViewById(R.id.layout_logined);
//    mTvUid = (TextView) layout.findViewById(R.id.tv_uid);
//    mTvQQGroup = (TextView) layout.findViewById(R.id.tv_qq_group);
//    mImgUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
//    mTvUserType = (TextView) layout.findViewById(R.id.tv_user_type);
//    mTvAllowDays = (TextView) layout.findViewById(R.id.tv_allow_days);
//    mTvCoinsNumber = (TextView) layout.findViewById(R.id.tv_coins_number);
//    tvGoToTaobao = (TextView) layout.findViewById(R.id.tv_go_to_taobao);
//    tvGoToBuyConins = (TextView) layout.findViewById(R.id.tv_go_to_buy_coins);
//    ivUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
//    ivGetHelp = (RelativeLayout) layout.findViewById(R.id.iv_get_help);
//    ivGetHelpAddPingjia = (RelativeLayout) layout.findViewById(R.id.iv_get_help_add_pigjia);
//    ivGetHelpAddYueke = (RelativeLayout) layout.findViewById(R.id.iv_get_help_add_yueke);
//    tvYuekeTimes = (TextView) layout.findViewById(R.id.tv_yueke_times);
//    tvPingjiaTimes = (TextView) layout.findViewById(R.id.tv_pingjia_times);
//    btnAddPingjiaTimes = (Button) layout.findViewById(R.id.btn_add_pingjia_times);
//    btnAddYuekeTimes = (Button) layout.findViewById(R.id.btn_add_yueke_times);
  }

  private void setOnListener() {
//    layout.findViewById(R.id.tv_log_out).setOnClickListener(this);
//    tvGoToTaobao.setOnClickListener(this);
//    tvGoToBuyConins.setOnClickListener(this);
//    ivUserIcon.setOnClickListener(this);
//    ivGetHelp.setOnClickListener(this);
//    btnAddPingjiaTimes.setOnClickListener(this);
//    btnAddYuekeTimes.setOnClickListener(this);
//    ivGetHelpAddPingjia.setOnClickListener(this);
//    ivGetHelpAddYueke.setOnClickListener(this);
    mBtnLogout.setOnClickListener(this);
    mImgHead.setOnClickListener(this);
    mSettingCoinsNumber.setOnClickListener(this);
    mSettingLeftYuekeCount.setOnClickListener(this);
    mSettingLeftPingjiaCount.setOnClickListener(this);
    mSettingCurrentVersion.setOnClickListener(this);
    mSettingMaxNumber.setOnClickListener(this);
  }

  /**
   * 初始化登录信息
   */
  private void initLogin() {
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    userName = userInfo.getUsername();
    mTvUserName.setText(userName);

    setUserDetails();
    getMessagesFromServer();
  }

  private void setUserDetails() {

    mSettingCoinsNumber.setSubText(userInfo.getCoinsNumber() + "");

    mSettingUserType.setSubText(Tools.getUserTypeDsec(userInfo.getUserType()));
    mSettingLeftTime.setSubText(getDesc());
    if(userInfo.getMaxNumber() != null) {
      mSettingMaxNumber.setVisibility(View.VISIBLE);
      mDivMaxNumber.setVisibility(View.VISIBLE);
      mSettingMaxNumber.setSubText(Tools.getMaxNumber(userInfo));
    }else {
      mSettingMaxNumber.setVisibility(View.GONE);
      mDivMaxNumber.setVisibility(View.GONE);
    }

    //qq交流群
    if (userInfo.getUserType() == 0) {
      mSettingQqGroup.setSubText("");
    } else {
      if (TextUtils.isEmpty(userInfo.getProxyPerson()) && userInfo.getUserType() != 98) {
        mSettingQqGroup.setSubText(PreferenceHelper.getInstance().getQQGroup());
      } else {
        BmobQuery<ProxyInfo> query = new BmobQuery<ProxyInfo>();
        query.addWhereEqualTo("userName", userInfo.getProxyPerson());
        query.findObjects(new FindListener<ProxyInfo>() {
          @Override
          public void done(List<ProxyInfo> list, BmobException e) {
            if (e == null) {
              if (list.size() == 0) {
                mSettingQqGroup.setSubText(PreferenceHelper.getInstance().getQQGroup());
              } else {
                Message msg = mHandler.obtainMessage(UPDATE_QQ_GROUP);
                msg.obj = list.get(0).getDesc();
                msg.sendToTarget();
              }
            } else {
              toast("获取代理人信息异常,请退出页面重进，错误信息" + e.getMessage());
            }
          }
        });
      }
    }

    //当前版本
    mSettingCurrentVersion.setSubText(CommonUntils.getVersion(getActivity()));
  }

  private String getDesc() {
    String result = "";
    if (userInfo.getUserType() == 0) {
      mSettingLeftTime.setVisibility(View.VISIBLE);
      mDivLeftTime.setVisibility(View.VISIBLE);
      result = "暂无授权，请联系软件作者购买";
    } else if (userInfo.getUserType() == 1) {
      mSettingLeftTime.setVisibility(View.VISIBLE);
      mDivLeftTime.setVisibility(View.VISIBLE);
      getServerTime();
    } else if (userInfo.getUserType() == -1) {
      mSettingLeftTime.setVisibility(View.VISIBLE);
      mDivLeftTime.setVisibility(View.VISIBLE);
      result = "授权已过期，请联系软件作者续费";
    } else {
      mSettingLeftTime.setVisibility(View.GONE);
      mDivLeftTime.setVisibility(View.GONE);
    }
    return result;
  }

  private void getServerTime() {
    Bmob.getServerTime(new QueryListener<Long>() {
      @Override
      public void done(Long aLong, BmobException e) {
        if (e == null) {
          if (userInfo.getBeginTime() == null || userInfo.getBeginTime() == 0) {
            //如果没有记录beginTime,那么写入当前服务器时间
            Log.e("test", "beginTime is null,update it");
            mSettingLeftTime.setSubText(userInfo.getAllowDays() + "天");

            BmobUser bmobUser = BmobUser.getCurrentUser();
            userInfo.setBeginTime(aLong);
            if (TextUtils.isEmpty(userInfo.getInstallId())) {
              userInfo.setInstallId(Installation.id(getActivity()));
            }
            userInfo.setAppVersion(CommonUntils.getVersionInt(getActivity()));
            userInfo.update(bmobUser.getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if (e == null) {
                  Log.e("test", "更新beginTime成功");
                } else {
                  Log.e("test", "更新beginTime失败");
                }
              }
            });
          } else {
            Log.e("test", "beginTime = " + userInfo.getBeginTime());
            Log.e("test", "serverTime = " + aLong);
            double usedHours = (aLong - userInfo.getBeginTime()) / 3600; //授权已经使用的小时数
            Log.e("test", "usedHours = " + usedHours);
            if (usedHours < userInfo.getAllowDays() * 24) {
              String leftTimeDesc = getLeftTimeDesc(userInfo.getAllowDays() * 24 - (int) usedHours);
              mSettingLeftTime.setSubText(leftTimeDesc);
            } else {
              //用户授权已过期，更新用户信息
              mSettingUserType.setSubText("过期用户");
              mSettingLeftTime.setSubText("授权已过期，请联系软件作者续费");

              BmobUser bmobUser = BmobUser.getCurrentUser();
              userInfo.setBeginTime((long) 0);
              userInfo.setUserType(-1);
              userInfo.setAppVersion(CommonUntils.getVersionInt(getActivity()));
              if (TextUtils.isEmpty(userInfo.getInstallId())) {
                userInfo.setInstallId(Installation.id(getActivity()));
              }
              userInfo.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                  if (e == null) {
                    Log.e("test", "更新用户信息成功");
                  } else {
                    Log.e("test", "更新用户信息失败");
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
    Log.e("test", "leftHours = " + leftHours);
    StringBuilder sb = new StringBuilder();
    if (leftHours >= 24) {
      sb.append(leftHours / 24 + "天");
    }
    sb.append(leftHours % 24 + "小时");
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
  public void onDestroy() {
    super.onDestroy();
    if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
      mUnbinder.unbind();
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_logout: // 注销登录
        BmobUser.logOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        break;
      case R.id.img_head:
        if (userInfo != null && (userInfo.getUserType() == 99 || userInfo.getUserType() == 100)) {
          Intent intent = new Intent(getActivity(), SuperRootActivity.class);
          startActivity(intent);
        } else if (userInfo != null && (userInfo.getUserType() == 98)) {
          Intent intent = new Intent(getActivity(), ProxyPersonActivity.class);
          startActivity(intent);
        }
        break;
      case R.id.setting_current_version:
        Beta.checkUpgrade();
        break;
      case R.id.setting_coins_number:
        startActivity(new Intent(getActivity(), CoinsDetailActivity.class));
        break;
      case R.id.setting_max_number:
        //startActivity(new Intent(getActivity(), CoinsDetailActivity.class));
        break;
      case R.id.setting_left_yueke_count:
        showAddYuekeDialog();
        break;
      case R.id.setting_left_pingjia_count:
        showAddPingjiaDialog();
        break;
      default:

        break;
    }
//    switch (v.getId()) {
//      case R.id.tv_log_out: // 注销登录
//        BmobUser.logOut();
//        startActivity(new Intent(getActivity(), LoginActivity.class));
//        getActivity().finish();
//        break;
//      case R.id.tv_go_to_taobao: // 点击购买授权链接
//        if (userInfo.getUserType() == 2) {
//          openTaobaoShopping(PreferenceHelper.getInstance().getUpdateLevelUrl());
//        } else {
//          openTaobaoShopping(PreferenceHelper.getInstance().getBuyAuthUrl());
//        }
//        break;
//      case R.id.tv_go_to_buy_coins: // 点击购买金币链接
//        openTaobaoShopping(PreferenceHelper.getInstance().getBuyCoinsUrl());
//        break;
//      case R.id.user_icon: // 点击用户头像，拉起超级用户配置管理界面
//        if (userInfo != null && (userInfo.getUserType() == 99 || userInfo.getUserType() == 100)) {
//          Intent intent = new Intent(getActivity(), SuperRootActivity.class);
//          startActivity(intent);
//        } else if (userInfo != null && (userInfo.getUserType() == 98)) {
//          Intent intent = new Intent(getActivity(), ProxyPersonActivity.class);
//          startActivity(intent);
//        }
//        break;
//      case R.id.iv_get_help: // 金币帮助问号
//        showGethelpDialog();
//        break;
//      case R.id.iv_get_help_add_pigjia: // 金币帮助问号
//        showAddPingjiaDialog();
//        break;
//      case R.id.iv_get_help_add_yueke: // 金币帮助问号
//        showAddYuekeDialog();
//        break;
//      case R.id.btn_add_pingjia_times: //充值评价次数
//        showAddPingjiaTimesDialog();
//        break;
//      case R.id.btn_add_yueke_times:  //充值约课次数
//        showAddYuekeTimesDialog();
//        break;
//      default:
//        break;
//    }
  }

  private void showAddYuekeDialog() {
    AlertDialog dialog = new AlertDialog.Builder(getActivity())
      .setTitle("约课次数说明")
      .setMessage("1.每成功约一节课消耗一次约课次数\n\n2. 每天的免费约课次数过了晚上十二点，重新登陆之后，自动恢复\n\n3. 免费次数用完的，如果还想继续使用，需要充值约课次数\n\n4.消耗一个金币可以充值10次约课次数")
      .setNegativeButton("充值次数", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          showAddYuekeTimesDialog();
        }
      })
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
      .setNegativeButton("充值次数", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          showAddPingjiaTimesDialog();
        }
      })
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
      .setMessage(" 1. 首次注册赠送100金币\n\n 2. 注册时填写邀请人手机号,额外获得88金币\n\n 3. 邀请的用户开通月卡授权，获得588金币\n\n" +
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
    query.addWhereEqualTo("username", userInfo.getUsername());
    query.addWhereEqualTo("status", 1);
    query.findObjects(new FindListener<MessageDetails>() {
      @Override
      public void done(List<MessageDetails> object, BmobException e) {
        if (e == null) {
          Log.d("test", "查询消息成功：共" + object.size() + "条数据。");
          messageList.clear();
          for (MessageDetails message : object) {
            messageList.add(message);
          }
          messageIndex = 0;
          mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
        } else {
          Log.i("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
        }
      }
    });
  }


  private void dealTheMessage(final MessageDetails messageDetails) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setCancelable(false);
    int messageType = messageDetails.getType();
    if (messageType == 0 || messageType == 1 || messageType == 2 || messageType == 3 || messageType == 4) {
      //金币入账消息
      builder.setTitle("金币入账通知");
      if (messageType == 1 || messageType == 2 || messageType == 3) {
        if (messageDetails.getReasons().contains("邀请人")) {
          builder.setMessage(messageDetails.getReasons() + "\n被邀请人账号：" + messageDetails.getRelatedUserName());
        } else {
          builder.setMessage(messageDetails.getReasons());
        }
      } else {
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
              if (e == null) {
                Log.d("test", "消息处理成功，开始处理下一条消息");
                if (userInfo.getCoinsNumber() == null) {
                  userInfo.setCoinsNumber(messageDetails.getScore());
                } else {
                  userInfo.setCoinsNumber(userInfo.getCoinsNumber() + messageDetails.getScore());
                }
                messageIndex++;
                mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
              } else {
                Log.e("test", "消息处理失败：" + e.getMessage() + "," + e.getErrorCode());
              }
            }
          });
        }
      });
      builder.create().show();
    } else if (messageType == 5 || messageType == 6 || messageType == 7 || messageType == 8) {
      //授权变动消息

      //试用过的用户无法再次开通试用
      if (messageDetails.getScore() == 1) {
        if (userInfo.getAllowDays() > 0) {
          //一个人最多试用两天
          toast("一个用户最多开通一次试用");
          messageDetails.setStatus(0);
          messageDetails.update(messageDetails.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
              if (e == null) {
                Log.e("test", "消息更新成功");
                messageIndex++;
                mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
              } else {
                Log.e("test", "消息处理失败：" + e.getMessage() + "," + e.getErrorCode());
              }
            }
          });
          return;
        }
      }

      builder.setTitle("授权变动通知");
      if (messageType == 5) {
        if (messageDetails.getScore() == 1) {
          builder.setMessage("试用授权开通成功");
        } else {
          builder.setMessage("月卡授权开通成功，本次开通了" + messageDetails.getScore() + "天授权");
        }
        if (messageDetails.getReasons().contains("代理人")) {
          userInfo.setProxyPerson(messageDetails.getReasons().split("::")[1]);
        }
        if (userInfo.getUserType() == 1) {
          //试用时间未过期
          userInfo.setAllowDays(userInfo.getAllowDays() + messageDetails.getScore());
        } else if (userInfo.getUserType() == 0 || userInfo.getUserType() == -1) {
          userInfo.setUserType(1);
          userInfo.setAllowDays(messageDetails.getScore());
        }
      } else if (messageType == 6) {
        builder.setMessage("永久授权开通成功");
        if (messageDetails.getReasons().contains("代理人")) {
          userInfo.setProxyPerson(messageDetails.getReasons().split("::")[1]);
        }
        userInfo.setUserType(2);
      } else if (messageType == 7) {
        builder.setMessage("成功升级高级永久");
        if (messageDetails.getReasons().contains("代理人")) {
          userInfo.setProxyPerson(messageDetails.getReasons().split("::")[1]);
        }
        userInfo.setUserType(3);
        userInfo.setLastDay("");
      } else if (messageType == 8) {
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
              if (e == null) {
                Log.e("test", "消息更新成功");
                userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
                  @Override
                  public void done(BmobException e) {
                    if (e == null) {
                      Log.d("test", "用户信息更新成功，开始处理下一条消息");
                      messageIndex++;
                      mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
                    } else {
                      Log.e("test", "更新用户信息失败");
                    }
                  }
                });
              } else {
                Log.e("test", "消息处理失败：" + e.getMessage() + "," + e.getErrorCode());
              }
            }
          });
        }
      });
      builder.create().show();
    } else if(messageType == 9){
      //辅助功能授权
      builder.setTitle("辅助功能授权通知");
      builder.setMessage("辅助功能授权成功，本次开通了" + messageDetails.getScore() + "天授权");
      if(userInfo.getAutoPay() != null && userInfo.getAutoPay() == 1){
        //时间未过期
        userInfo.setPayDays(userInfo.getPayDays() + messageDetails.getScore());
      }else {
        userInfo.setAutoPay(1);
        userInfo.setPayDays(messageDetails.getScore());
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
    }else if (messageType == 99) {
      //系统公告消息
      builder.setTitle("增加最大约课人数");
      builder.setMessage("用户增加最大约课人数,本次成功增加了" + messageDetails.getScore() + "人");
      builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          messageDetails.setStatus(0);
          messageDetails.update(messageDetails.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
              if (e == null) {
                Log.e("test", "消息更新成功");
                if(userInfo.getMaxNumber() != null) {
                  userInfo.setMaxNumber(userInfo.getMaxNumber() + messageDetails.getScore());
                }else {
                  userInfo.setMaxNumber(50 + messageDetails.getScore());
                }
                userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
                  @Override
                  public void done(BmobException e) {
                    if (e == null) {
                      Log.d("test", "用户信息更新成功，开始处理下一条消息");
                      messageIndex++;
                      mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
                    } else {
                      Log.e("test", "更新用户信息失败");
                    }
                  }
                });
              } else {
                Log.e("test", "消息处理失败：" + e.getMessage() + "," + e.getErrorCode());
              }
            }
          });
        }
      });
      builder.create().show();
    } else {
      //未知消息类型，不处理，继续下一个
      messageIndex++;
      mHandler.sendEmptyMessage(HANDLER_THE_MESSAGES);
    }
  }


  private void getConfigs() {

    if (mRetrofit == null) {
      mRetrofit = HttpManager.getInstance().getRetrofit();
    }
    mRetrofit.create(ApiService.class).getConfigs().enqueue(new Callback<GetConfigsBean>() {
      @Override
      public void onResponse(Call<GetConfigsBean> call, Response<GetConfigsBean> response) {
        GetConfigsBean bean = response.body();
        PreferenceHelper.getInstance().setBuyAuthUrl(bean.getBuyAuthUrl());
        PreferenceHelper.getInstance().setBuyConisUrl(bean.getBuyCoinsUrl());
        PreferenceHelper.getInstance().setCoinsCostForPost(Integer.parseInt(bean.getPostCoinsCost()));
        PreferenceHelper.getInstance().setCoinsCostForSpecialFunction(Integer.parseInt(bean.getSpecialCoinsCost()));
        PreferenceHelper.getInstance().setFreeTimesPerday(Integer.parseInt(bean.getFreeTimesPerDay()));
        PreferenceHelper.getInstance().setUpdateLevelUrl(bean.getUpdateLevelUrl());
        PreferenceHelper.getInstance().setPayCost(Integer.parseInt(bean.getPayCoinsCost()));
        PreferenceHelper.getInstance().setHasActivity(Integer.parseInt(bean.getActivityOrNot()));
        PreferenceHelper.getInstance().setQQGroup(bean.getQqGroup());
        PreferenceHelper.getInstance().setNotification(bean.getNotification());
        PreferenceHelper.getInstance().setAutoJump(Integer.parseInt(bean.getJumpOrNot()));
        PreferenceHelper.getInstance().setMinConfigTime(Integer.parseInt(bean.getMinConfigTime()));
        PreferenceHelper.getInstance().setEnableGiveCoins(bean.getEnableGiveCoins());
        PreferenceHelper.getInstance().setMaxSetupCourses(bean.getMaxCourses());
        PreferenceHelper.getInstance().setNotice(bean.getNotice());
        mHandler.sendEmptyMessage(UPDATE_THE_TIMES);
      }

      @Override
      public void onFailure(Call<GetConfigsBean> call, Throwable t) {

      }
    });
  }

  private void openTaobaoShopping(final String url) {
    Intent intent = new Intent();
    if (CommonUntils.checkPackage(getActivity(), "com.taobao.taobao")) {
      Log.e("test", "taobao is not installed");
      intent.setAction("android.intent.action.VIEW");
      Uri uri = Uri.parse(url);
      intent.setData(uri);
      startActivity(intent);
    } else {
      intent.putExtra("url", url);
      intent.setClass(getActivity(), WebViewActivity.class);
      startActivity(intent);
    }
  }

  private void showAddPingjiaTimesDialog() {
    View diaog_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_times, null);
    final EditText et_input_times = (EditText) diaog_view.findViewById(R.id.et_input_add_times);
    final TextView tv_show_cost_coins = (TextView) diaog_view.findViewById(R.id.tv_cost_coins);
    et_input_times.setInputType(InputType.TYPE_CLASS_NUMBER);
    et_input_times.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
          tv_show_cost_coins.setText((Integer.valueOf(s.toString()) - 1) / 10 + 1 + "个金币");
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
        if (TextUtils.isEmpty(et_input_times.getText().toString().trim())) {
          toast("充值次数不能为空！");
          return;
        }
        final int number, cost;
        number = Integer.valueOf(et_input_times.getText().toString().trim());
        cost = (number - 1) / 10 + 1;
        Log.e("test", "充值消耗金币数量：" + cost);
        Log.e("test", "用户当前金币数量:" + userInfo.getCoinsNumber());
        if (userInfo.getCoinsNumber() < cost) {
          toast("金币余额不足，请先购买金币！");
          return;
        }
        userInfo.setCoinsNumber(userInfo.getCoinsNumber() - cost);
        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
          @Override
          public void done(BmobException e) {
            if (e == null) {
              Log.e("test", "扣除" + cost + "个金币");
              toast("扣除" + cost + "个金币");
              sph.setRemainPingjiaTimes(sph.getRemainPingjiaTimes() + number);
              userInfo.setPingjiaTimes(sph.getRemainPingjiaTimes());
              mHandler.sendEmptyMessage(UPDATE_USER_INFO);
            } else {
              Log.e("test", "金币扣除失败");
            }
          }
        });
      }
    });
    builder.create().show();
  }

  private void showAddYuekeTimesDialog() {
    View diaog_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_times, null);
    final EditText et_input_times = (EditText) diaog_view.findViewById(R.id.et_input_add_times);
    final TextView tv_show_cost_coins = (TextView) diaog_view.findViewById(R.id.tv_cost_coins);
    et_input_times.setInputType(InputType.TYPE_CLASS_NUMBER);
    et_input_times.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
          tv_show_cost_coins.setText((Integer.valueOf(s.toString()) - 1) / 10 + 1 + "个金币");
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
        if (TextUtils.isEmpty(et_input_times.getText().toString().trim())) {
          toast("充值次数不能为空！");
          return;
        }
        final int number, cost;
        number = Integer.valueOf(et_input_times.getText().toString().trim());
        cost = (number - 1) / 10 + 1;
        Log.e("test", "充值消耗金币数量：" + cost);
        Log.e("test", "用户当前金币数量:" + userInfo.getCoinsNumber());
        if (userInfo.getCoinsNumber() < cost) {
          toast("金币余额不足，请先购买金币！");
          return;
        }
        userInfo.setCoinsNumber(userInfo.getCoinsNumber() - cost);
        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
          @Override
          public void done(BmobException e) {
            if (e == null) {
              Log.e("test", "扣除" + cost + "个金币");
              toast("扣除" + cost + "个金币");
              sph.setRemainYuekeTimes(sph.getRemainYuekeTimes() + number);
              userInfo.setYuekeTimes(sph.getRemainYuekeTimes());
              mHandler.sendEmptyMessage(UPDATE_USER_INFO);
            } else {
              Log.e("test", "金币扣除失败");
            }
          }
        });
      }
    });
    builder.create().show();
  }

  private void toast(String str) {
    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
  }
}
