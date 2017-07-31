package com.june.healthmail.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.fragement.DiscoveryFragment;
import com.june.healthmail.fragement.ManageAcountFragment;
import com.june.healthmail.fragement.FunctionListFragment;
import com.june.healthmail.fragement.MineFragment;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends FragmentActivity {

  private FragmentTabHost mTabHost;
  private String uid;
  private String userName;

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

    mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
    addTab();
    mTabHost.setCurrentTab(3);
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
