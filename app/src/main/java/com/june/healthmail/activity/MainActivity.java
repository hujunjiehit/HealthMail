package com.june.healthmail.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.fragement.DiscoveryFragment;
import com.june.healthmail.fragement.FunctionListFragment;
import com.june.healthmail.fragement.ManageAcountFragment;
import com.june.healthmail.fragement.MineFragment;
import com.june.healthmail.http.bean.Notice;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.Tools;
import com.june.healthmail.view.CustomTopNoticeView;
import com.june.healthmail.view.MyTabhost;

import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity {

  private MyTabhost mTabHost;
  private String uid;
  private String userName;

  private CustomTopNoticeView mNoticeView;

  private Class[] mFragments = new Class[] { DiscoveryFragment.class,
      FunctionListFragment.class, ManageAcountFragment.class, MineFragment.class};

  private int[] mTabSelectors = new int[] {
      R.drawable.main_bottom_tab_faxian,
      R.drawable.main_bottom_tab_message,
      R.drawable.main_bottom_tab_friends,
      R.drawable.main_bottom_tab_mine
  };

  private String[] mTabSpecs = new String[]{"猫友圈","功能列表","小号管理","个人中心" };
  private static final int MY_PERMISSION_REQUEST_CODE = 10000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if(getIntent() != null){
      Log.e("test","MainActivity, uid = " + getIntent().getStringExtra("uid"));
      uid = getIntent().getStringExtra("uid");
    }
    //bmob版本更新
    //BmobUpdateAgent.update(this);

    mNoticeView = (CustomTopNoticeView) findViewById(R.id.tv_notice_view);

    mTabHost = (MyTabhost) findViewById(android.R.id.tabhost);
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
    addTab();
    mTabHost.setCurrentTab(3);
    mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
      @Override
      public void onTabChanged(String tabId) {
        final Notice notice = PreferenceHelper.getInstance().getNotice();
        if(notice != null && notice.getEnable() == 1) {
          mNoticeView.setAutoDismiss(notice.isAutoDismiss());
          if(notice.isAutoDismiss()){
            mNoticeView.setAutoDissmissDuration(notice.getAutoDissmissDuration());
          }
          if(notice.isShowIndicator()) {
            mNoticeView.showIndicator();
          }

          mNoticeView.show(notice.getContent());

          if(!TextUtils.isEmpty(notice.getUrl())) {
            mNoticeView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Tools.openTaobaoShopping(MainActivity.this,notice.getUrl());
              }
            });
          }
        }
      }
    });

    checkPermission();
  }

  private void addTab() {
    for (int i = 0; i < 4; i++) {
      View tabIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
      ImageView imageView = (ImageView) tabIndicator.findViewById(R.id.image);
      imageView.setImageResource(mTabSelectors[i]);
      TextView title = (TextView) tabIndicator.findViewById(R.id.title);
      title.setText(mTabSpecs[i]);
      mTabHost.addTab(
          mTabHost.newTabSpec(mTabSpecs[i])
              .setIndicator(tabIndicator), mFragments[i], null);
    }
  }

  /**
   * 由片段调用，获取是否已登录
   * @return
   */
  public boolean getLogined() {
    return uid != null ? true:false;
  }

  /**
   * 由片段调用，获取用户名
   *
   * @return
   */
  public String getUid() {
    return uid;
  }

  /**
   * 由片段调用，获取用户名
   *
   * @return
   */
  public String getUserName() {
    if(BmobUser.getCurrentUser() != null){
      return BmobUser.getCurrentUser().getUsername();
    }else{
      return "";
    }
  }

  public void goToFragment(int index){
    if(index <= 3){
      mTabHost.setCurrentTab(0);
    }
  }


  private void checkPermission() {
    /**
     * 第 1 步: 检查是否有相应的权限
     */
    boolean isAllGranted = checkPermissionAllGranted(
      new String[] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      }
    );

    //如果两个权限都有，正常进行
    if (isAllGranted) {

    }else {
      /**
       * 第 2 步: 请求权限
       */
      // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
      ActivityCompat.requestPermissions(
        this,
        new String[] {
          Manifest.permission.WRITE_EXTERNAL_STORAGE
        },
        MY_PERMISSION_REQUEST_CODE
      );
    }
  }

  /**
   * 检查是否拥有指定的所有权限
   */
  private boolean checkPermissionAllGranted(String[] permissions) {
    for (String permission : permissions) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        // 只要有一个权限没有被授予, 则直接返回 false
        return false;
      }
    }
    return true;
  }

  /**
   * 第 3 步: 申请权限结果返回处理
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == MY_PERMISSION_REQUEST_CODE) {
      boolean isAllGranted = true;
      // 判断是否所有的权限都已经授予了
      for (int grant : grantResults) {
        if (grant != PackageManager.PERMISSION_GRANTED) {
          isAllGranted = false;
          break;
        }
      }

      if (isAllGranted) {
        // 如果所有的权限都授予了, 则执行备份代码
      } else {
        // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
        openAppDetails();
      }
    }
  }

  /**
   * 打开 APP 的详情设置
   */
  private void openAppDetails() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("在线更新需要手机存储权限，请到 “应用信息 -> 权限” 中授予！");
    builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
      }
    });
    builder.setNegativeButton("取消", null);
    builder.show();
  }
}
