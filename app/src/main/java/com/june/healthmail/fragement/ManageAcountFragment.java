package com.june.healthmail.fragement;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.june.healthmail.R;
import com.june.healthmail.adapter.AcountListAdapter;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.view.TopbarOperatePop;

import java.util.ArrayList;

/**
 * 小号管理fragment
 */
public class ManageAcountFragment extends Fragment implements View.OnClickListener, AcountListAdapter.Callback, AdapterView.OnItemClickListener {

  private View layout;

  private ListView mListView;
  private ArrayList<AccountInfo> accountList = new ArrayList<>();
  private AcountListAdapter mAdapter;
  private ImageView ivAddButton;

  private TopbarOperatePop operateBarPop;
  private DBManager mDBmanager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_manage_acount, container, false);
    initView();
    setOnListener();
    initData();
    return layout;
  }

  private void initView() {
    mListView = (ListView) layout.findViewById(R.id.list_view);
    mAdapter = new AcountListAdapter(getActivity(),accountList,this);
    mListView.setAdapter(mAdapter);

    ivAddButton = (ImageView) layout.findViewById(R.id.iv_add_btn);
  }

  private void setOnListener() {
    ivAddButton.setOnClickListener(this);
    mListView.setOnItemClickListener(this);
  }

  private void initData() {

    mDBmanager = DBManager.getInstance(getActivity());
    accountList.clear();

//    AccountInfo info1 = new AccountInfo();
//    info1.setNickName("胡军杰");
//    info1.setPassWord("111456");
//    info1.setPhoneNumber("13027909110");
//    info1.setStatus(0);
//    //acountList.add(info1);
//
//    AccountInfo info2 = new AccountInfo();
//    info2.setNickName("左佩");
//    info2.setPassWord("222456");
//    info2.setPhoneNumber("15278734567");
//    info2.setStatus(1);
//    //acountList.add(info2);

    SQLiteDatabase db = DBManager.getInstance(getActivity()).getDb();
    Cursor cursor = db.rawQuery("select * from account",null);
    if(cursor.moveToFirst()){
      do {
        AccountInfo info = new AccountInfo();
        info.setPassWord(cursor.getString(cursor.getColumnIndex("passWord")));
        info.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        info.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
        info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        info.setId(cursor.getInt(cursor.getColumnIndex("id")));
        accountList.add(info);
      }while(cursor.moveToNext());
    }
    mAdapter.notifyDataSetChanged();

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
    if(v.getId() == R.id.iv_add_btn){
        if (operateBarPop != null && operateBarPop.isShowing()){
          operateBarPop.dismiss();
        } else {
          operateBarPop =  new TopbarOperatePop(getActivity(),accountList,mAdapter);
          operateBarPop.showAsDropDown(ivAddButton,0,0);
        }
    }
  }

  @Override
  public void click(View v) {
//    Toast.makeText(getActivity(), "listview的内部的按钮被点击了！，位置是-->" + (Integer) v.getTag()
//            + ",内容是-->" + accountList.get((Integer) v.getTag()), Toast.LENGTH_SHORT).show();
    AccountInfo info = accountList.get((Integer) v.getTag());
    if (mAdapter.getSelected().containsKey((Integer) v.getTag()) && info.getStatus() != 1) {
      Log.d("test","set status checked, phone = " + info.getPhoneNumber());
      info.setStatus(1);
      mDBmanager.setStatus(info,1);
    } else if (!mAdapter.getSelected().containsKey((Integer) v.getTag()) && info.getStatus() != 0){
      Log.d("test","set status unchecked, phone = " + info.getPhoneNumber());
      info.setStatus(0);
      mDBmanager.setStatus(info,0);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  }
}
