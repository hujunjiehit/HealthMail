package com.june.healthmail.fragement;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.ShowTodayDetailsActivity;
import com.june.healthmail.adapter.AcountListAdapter;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.TimeUntils;
import com.june.healthmail.view.LeftTopbarOperatePop;
import com.june.healthmail.view.RightTopbarOperatePop;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * 小号管理fragment
 */
public class ManageAcountFragment extends Fragment implements View.OnClickListener, AcountListAdapter.Callback, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

  private View layout;

  private ListView mListView;
  private ArrayList<BmobObject> accountList = new ArrayList<>();
  private AcountListAdapter mAdapter;
  private ImageView ivAddButton;
  private ImageView ivCloudOpButton;
  private Button btnShowTodayDetails;

  private RightTopbarOperatePop rightOperateBarPop;
  private LeftTopbarOperatePop leftOperateBarPop;
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

  @Override
  public void onResume() {
    super.onResume();
    initData();
  }

  private void initView() {
    mListView = (ListView) layout.findViewById(R.id.list_view);
    mAdapter = new AcountListAdapter(getActivity(),accountList,this);
    mListView.setAdapter(mAdapter);

    ivAddButton = (ImageView) layout.findViewById(R.id.iv_add_btn);
    ivCloudOpButton = (ImageView) layout.findViewById(R.id.iv_cloud_op);
    btnShowTodayDetails = (Button) layout.findViewById(R.id.show_today_details);
    btnShowTodayDetails.setVisibility(View.GONE);
  }

  private void setOnListener() {
    ivAddButton.setOnClickListener(this);
    ivCloudOpButton.setOnClickListener(this);
    mListView.setOnItemClickListener(this);
    mListView.setOnItemLongClickListener(this);
    btnShowTodayDetails.setOnClickListener(this);
  }

  private void initData() {

    mDBmanager = DBManager.getInstance();
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

    SQLiteDatabase db = DBManager.getInstance().getDb();
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
          }
        }
        //Log.e("test","userInfo = " + info.toString());
        accountList.add(info);
      }while(cursor.moveToNext());
    }
    cursor.close();
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
        if (rightOperateBarPop != null && rightOperateBarPop.isShowing()){
          rightOperateBarPop.dismiss();
        } else {
          rightOperateBarPop =  new RightTopbarOperatePop(getActivity(),accountList,mAdapter);
          rightOperateBarPop.showAsDropDown(ivAddButton,0,0);
        }
    } else if (v.getId() == R.id.iv_cloud_op) {
      if (leftOperateBarPop != null && leftOperateBarPop.isShowing()){
        leftOperateBarPop.dismiss();
      } else {
        leftOperateBarPop =  new LeftTopbarOperatePop(getActivity(),accountList,mAdapter);
        leftOperateBarPop.showAsDropDown(ivCloudOpButton,0,0);
      }
    }else if(v.getId() == R.id.show_today_details){
      Intent it = new Intent(getActivity(), ShowTodayDetailsActivity.class);
      startActivity(it);
    }
  }

  @Override
  public void click(View v) {
//    Toast.makeText(getActivity(), "listview的内部的按钮被点击了！，位置是-->" + (Integer) v.getTag()
//            + ",内容是-->" + accountList.get((Integer) v.getTag()), Toast.LENGTH_SHORT).show();
    AccountInfo info = (AccountInfo) accountList.get((Integer) v.getTag());
    if (mAdapter.getSelected().containsKey((Integer) v.getTag()) && (info.getStatus() == 0 || info.getStatus() == -2)) {
      Log.d("test","set status checked, phone = " + info.getPhoneNumber());
      info.setStatus(1);
      mDBmanager.setStatus(info,1);
    } else if (!mAdapter.getSelected().containsKey((Integer) v.getTag()) && info.getStatus() == 1){
      Log.d("test","set status unchecked, phone = " + info.getPhoneNumber());
      info.setStatus(0);
      mDBmanager.setStatus(info,0);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    showEditAccountDialog((AccountInfo) accountList.get(position),position);
  }

  private void showEditAccountDialog(final AccountInfo accountInfo,final int position) {
    View diaog_view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_edit_account_layout,null);
    final EditText edit_text_phonenumber = (EditText) diaog_view.findViewById(R.id.edit_text_phonenumber);
    final EditText edit_text_pwd = (EditText) diaog_view.findViewById(R.id.edit_text_pwd);
    edit_text_phonenumber.setInputType(InputType.TYPE_CLASS_NUMBER);

    edit_text_phonenumber.setText(accountInfo.getPhoneNumber());
    edit_text_pwd.setText(accountInfo.getPassWord());

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("修改账号密码");
    builder.setView(diaog_view);
    builder.setNegativeButton("取消修改", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String phonenumber = edit_text_phonenumber.getText().toString().trim();
        String pwd = edit_text_pwd.getText().toString().trim();
        if(TextUtils.isEmpty(phonenumber)){
          Toast.makeText(getActivity(),"账号不能为空",Toast.LENGTH_LONG).show();
          return;
        }
        if(TextUtils.isEmpty(pwd)){
          Toast.makeText(getActivity(),"密码不能为空",Toast.LENGTH_LONG).show();
          return;
        }
        AccountInfo accountInfo = (AccountInfo) accountList.get(position);
        accountInfo.setPhoneNumber(phonenumber);
        accountInfo.setPassWord(pwd);
        accountInfo.setStatus(1);
        DBManager.getInstance().updateAccountInfo(accountInfo.getId(),phonenumber,pwd);
        mAdapter.notifyDataSetChanged();
        dialog.dismiss();
      }
    });
    builder.create().show();
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    showDeleteAccountDialog((AccountInfo) accountList.get(position),position);
    return true;
  }

  private void showDeleteAccountDialog(final AccountInfo accountInfo, final int position) {
    View diaog_view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_edit_account_layout,null);
    final EditText edit_text_phonenumber = (EditText) diaog_view.findViewById(R.id.edit_text_phonenumber);
    final EditText edit_text_pwd = (EditText) diaog_view.findViewById(R.id.edit_text_pwd);
    edit_text_phonenumber.setInputType(InputType.TYPE_CLASS_NUMBER);
    edit_text_phonenumber.setText(accountInfo.getPhoneNumber());
    edit_text_pwd.setText(accountInfo.getPassWord());

    edit_text_phonenumber.setEnabled(false);
    edit_text_pwd.setEnabled(false);
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("删除小号--" + (position + 1));
    builder.setView(diaog_view);
    builder.setNegativeButton("取消删除", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        accountList.remove(position);
        mAdapter.notifyDataSetChanged();
        DBManager.getInstance().deleteAccountInfo(accountInfo.getPhoneNumber());
        Toast.makeText(getActivity(),"小号" + (position + 1) + "删除成功",Toast.LENGTH_LONG).show();
        dialog.dismiss();
      }
    });
    builder.create().show();
  }
}
