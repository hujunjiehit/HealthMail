package com.june.healthmail.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.adapter.ShowResultAdapter1;
import com.june.healthmail.adapter.ShowResultAdapter2;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/7/30.
 */

public class ShowTodayDetailsActivity extends BaseActivity {
  private UserInfo userInfo;

  @BindView(R.id.btn_start_1)
  Button btnStart1;
  @BindView(R.id.btn_start_2)
  Button btnStart2;
  @BindView(R.id.tv_coins_number)
  TextView tvConinsNumber;
  @BindView(R.id.tv_coins_desc)
  TextView tvConinsDesc;
  @BindView(R.id.tv_desc_coins_cost_1)
  TextView tvConinsCostDesc1;
  @BindView(R.id.tv_desc_coins_cost_2)
  TextView tvConinsCostDesc2;

  @BindView(R.id.result_container_1)
  LinearLayout resultContainer1;
  @BindView(R.id.result_container_2)
  LinearLayout resultContainer2;
  @BindView(R.id.layout_btn)
  LinearLayout btnContainer;

  @BindView(R.id.list_view_1)
  ListView mListView1;
  @BindView(R.id.list_view_2)
  ListView mListView2;

  private int coinsCost;
  private ArrayList<BmobObject> accountList = new ArrayList<>();
  private ShowResultAdapter1 mAdapter1;
  private ShowResultAdapter2 mAdapter2;

  private ShowProgress showProgress;

  public static final int LOAD_DATA_FINISHED = 999;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case LOAD_DATA_FINISHED:
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          mAdapter1.notifyDataSetChanged();
          mAdapter2.notifyDataSetChanged();
          break;
        default:
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(!CommonUntils.hasPermission()){
      Toast.makeText(this,"当前用户无授权，无法进入本页面",Toast.LENGTH_SHORT).show();
      finish();
    }
    setContentView(R.layout.activity_show_today_detail);
    ButterKnife.bind(this);
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    tvConinsNumber.setText(userInfo.getCoinsNumber() + "");
    Log.e("test","userInfo.getCoinsNumber() = " + userInfo.getCoinsNumber());
    coinsCost = PreferenceHelper.getInstance().getCoinsCostForSpecialFunction();
    if(coinsCost == 0){
      tvConinsCostDesc1.setText("活动期间，查看统计免金币");
      tvConinsCostDesc2.setText("活动期间，查看统计免金币");
      tvConinsNumber.setVisibility(View.GONE);
      tvConinsDesc.setVisibility(View.GONE);
    }else {
      tvConinsCostDesc1.setText("查看一次统计消耗" + coinsCost + "金币");
      tvConinsCostDesc2.setText("查看一次统计消耗" + coinsCost + "金币");
      tvConinsNumber.setVisibility(View.VISIBLE);
      tvConinsDesc.setVisibility(View.VISIBLE);
    }

    mAdapter1 = new ShowResultAdapter1(this,accountList);
    mAdapter2 = new ShowResultAdapter2(this,accountList);
    mListView1.setAdapter(mAdapter1);
    mListView2.setAdapter(mAdapter2);
    initData();
  }

  private void initData() {
    showProgress = new ShowProgress(this);
    if(showProgress != null && !showProgress.isShowing()){
      showProgress.setMessage("请稍后...");
      showProgress.show();
    }
    accountList.clear();
    new Thread(new Runnable() {
      @Override
      public void run() {
        for(AccountInfo accountInfo:CommonUntils.loadAccountInfo()) {
          accountList.add(accountInfo);
        }
        mHandler.obtainMessage(LOAD_DATA_FINISHED).sendToTarget();
      }
    }).start();
  }

  @OnClick(R.id.btn_start_1)
  public void btnStart1(View view){
    updateTheCoinsNumber();
    resultContainer1.setVisibility(View.VISIBLE);
    resultContainer2.setVisibility(View.GONE);
    btnContainer.setVisibility(View.GONE);
  }

  @OnClick(R.id.btn_start_2)
  public void btnStart2(View view){
    updateTheCoinsNumber();
    resultContainer1.setVisibility(View.GONE);
    resultContainer2.setVisibility(View.VISIBLE);
    btnContainer.setVisibility(View.GONE);
  }

  @Override
  public void onBackPressed() {
    if (resultContainer1.getVisibility() == View.VISIBLE || resultContainer2.getVisibility() == View.VISIBLE) {
      resultContainer1.setVisibility(View.GONE);
      resultContainer2.setVisibility(View.GONE);
      btnContainer.setVisibility(View.VISIBLE);
    } else {
      super.onBackPressed();
    }
  }

  @OnClick(R.id.img_back)
  public void btnImageBack(View view){
    finish();
  }


  private void updateTheCoinsNumber(){
    if(coinsCost > 0) {
      userInfo.setCoinsNumber(userInfo.getCoinsNumber() - coinsCost);
      userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
        @Override
        public void done(BmobException e) {
          if(e == null){
            Log.e("test","updateTheCoinsNumber 更新金币成功");
            toast("金币余额-" + coinsCost);
          }
        }
      });
    }
  }
}
