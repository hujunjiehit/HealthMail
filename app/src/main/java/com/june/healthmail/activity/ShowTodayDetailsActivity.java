package com.june.healthmail.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.adapter.ShowResultAdapter;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.TimeUntils;

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

  @BindView(R.id.btn_start)
  Button btnStart;
  @BindView(R.id.tv_coins_number)
  TextView tvConinsNumber;
  @BindView(R.id.tv_coins_desc)
  TextView tvConinsDesc;
  @BindView(R.id.tv_desc_coins_cost)
  TextView tvConinsCostDesc;

  @BindView(R.id.result_container)
  LinearLayout resultContainer;
  @BindView(R.id.layout_btn)
  LinearLayout btnContainer;

  @BindView(R.id.list_view)
  ListView mListView;

  private int coinsCost;
  private DBManager mDBmanager;
  private ArrayList<BmobObject> accountList = new ArrayList<>();
  private ShowResultAdapter mAdapter;

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
      tvConinsCostDesc.setText("活动期间，查看今日统计免金币");
      tvConinsNumber.setVisibility(View.GONE);
      tvConinsDesc.setVisibility(View.GONE);
    }else {
      tvConinsCostDesc.setText("查看一次今日统计消耗" + coinsCost + "金币");
      tvConinsNumber.setVisibility(View.VISIBLE);
      tvConinsDesc.setVisibility(View.VISIBLE);
    }

    mAdapter = new ShowResultAdapter(this,accountList);
    mListView.setAdapter(mAdapter);
    initData();
  }

  private void initData() {

    mDBmanager = DBManager.getInstance();
    accountList.clear();

    SQLiteDatabase db = mDBmanager.getDb();
    Cursor cursor = db.rawQuery("select * from account",null);
    if(cursor.moveToFirst()){
      do {
        AccountInfo info = new AccountInfo();
        info.setPassWord(cursor.getString(cursor.getColumnIndex("passWord")));
        info.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        info.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
        info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        info.setId(cursor.getInt(cursor.getColumnIndex("id")));
        info.setMallId(cursor.getString(cursor.getColumnIndex("mallId")));
        if(TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("lastDay")))){
          //if null, set today
          info.setLastDay(TimeUntils.getTodayStr());
          info.setPingjiaTimes(0);
          info.setYuekeTimes(0);
          DBManager.getInstance().resetPJYKTimes(info);
        }else {
          if(cursor.getString(cursor.getColumnIndex("lastDay")).equals(TimeUntils.getTodayStr())){
            //istoday
            info.setLastDay(cursor.getString(cursor.getColumnIndex("lastDay")));
            info.setPingjiaTimes(cursor.getInt(cursor.getColumnIndex("pingjiaTimes")));
            info.setYuekeTimes(cursor.getInt(cursor.getColumnIndex("yuekeTimes")));
          }else {
            //not today
            info.setLastDay(TimeUntils.getTodayStr());
            info.setPingjiaTimes(0);
            info.setYuekeTimes(0);
            DBManager.getInstance().resetPJYKTimes(info);
          }
        }
        //Log.e("test","userInfo = " + info.toString());
        accountList.add(info);
      }while(cursor.moveToNext());
    }
    cursor.close();
    mAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.btn_start)
  public void btnStart(View view){
    updateTheCoinsNumber();
    resultContainer.setVisibility(View.VISIBLE);
    btnContainer.setVisibility(View.GONE);
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
