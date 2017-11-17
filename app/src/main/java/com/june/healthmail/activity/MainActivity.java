package com.june.healthmail.activity;

import android.os.Bundle;
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
}
