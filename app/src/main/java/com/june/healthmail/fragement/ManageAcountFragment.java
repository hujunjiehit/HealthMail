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
import android.widget.ImageView;
import android.widget.ListView;

import com.june.healthmail.R;
import com.june.healthmail.adapter.AcountListAdapter;
import com.june.healthmail.model.AcountInfo;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.view.TopbarOperatePop;

import java.util.ArrayList;

/**
 * 小号管理fragment
 */
public class ManageAcountFragment extends Fragment implements View.OnClickListener{

  private View layout;

  private ListView mListView;
  private ArrayList<AcountInfo> acountList = new ArrayList<>();
  private AcountListAdapter mAdapter;
  private ImageView ivAddButton;

  private TopbarOperatePop operateBarPop;

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
    mAdapter = new AcountListAdapter(getActivity(),acountList);
    mListView.setAdapter(mAdapter);

    ivAddButton = (ImageView) layout.findViewById(R.id.iv_add_btn);
  }

  private void setOnListener() {
    ivAddButton.setOnClickListener(this);
  }

  private void initData() {

    acountList.clear();

    AcountInfo info1 = new AcountInfo();
    info1.setNickName("胡军杰");
    info1.setPassWord("111456");
    info1.setPhoneNumber("13027909110");
    info1.setStatus(0);
    //acountList.add(info1);

    AcountInfo info2 = new AcountInfo();
    info2.setNickName("左佩");
    info2.setPassWord("222456");
    info2.setPhoneNumber("15278734567");
    info2.setStatus(1);
    //acountList.add(info2);

    SQLiteDatabase db = DBManager.getInstance(getActivity()).getDb();
    db.execSQL("insert into account (phoneNumber,passWord,nickName,status) values (?,?,?,?)",
            new String[]{info1.getPhoneNumber(),info1.getPassWord(),info1.getNickName(),
                    info1.getStatus()+""});

    db.execSQL("insert into account (phoneNumber,passWord,nickName,status) values (?,?,?,?)",
            new String[]{info2.getPhoneNumber(),info2.getPassWord(),info2.getNickName(),
                    info2.getStatus()+""});

    Cursor cursor = db.rawQuery("select * from account",null);
    if(cursor.moveToFirst()){
      do {
        AcountInfo info = new AcountInfo();
        info.setPassWord(cursor.getString(cursor.getColumnIndex("passWord")));
        info.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        info.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
        info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        info.setId(cursor.getInt(cursor.getColumnIndex("id")));

        Log.e("test","id = " + info.getId());
        acountList.add(info);
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
          operateBarPop =  new TopbarOperatePop(getActivity());
          operateBarPop.showAsDropDown(ivAddButton,0,0);
        }
    }
  }
}
