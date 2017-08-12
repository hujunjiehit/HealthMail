package com.june.healthmail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.model.UserInfo;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/8/12.
 */

public class ProxyUserAdapter extends BaseAdapter {

  private Context mContext;
  private List<UserInfo> mUserList;

  public ProxyUserAdapter(Context mContext, List<UserInfo> mUserList) {
    this.mContext = mContext;
    this.mUserList = mUserList;
  }

  @Override
  public int getCount() {
    return mUserList.size();
  }

  @Override
  public BmobObject getItem(int position) {
    return mUserList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if(convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_proxy_detail,parent,false);
      holder = new ViewHolder();
      holder.tvIndex = (TextView) convertView.findViewById(R.id.tv_index);
      holder.tvPhonenumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
      holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
      holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    UserInfo userInfo = (UserInfo) getItem(position);
    holder.tvIndex.setText(""+ (position + 1));
    holder.tvPhonenumber.setText(userInfo.getUsername());
    holder.tvStatus.setText(getUserTypeDsec(userInfo.getUserType()));
    holder.tvCreateTime.setText(getTime(userInfo.getCreatedAt()));
    return convertView;
  }

  static class ViewHolder {
    TextView tvIndex;
    TextView tvPhonenumber;
    TextView tvStatus;
    TextView tvCreateTime;
  }

  public String getUserTypeDsec(Integer userType) {
    String typeDesc = "";
    if(userType == 0){
      typeDesc = "注册状态";
    }else if(userType == 1){
      typeDesc = "试用状态";
    }else if(userType == 2) {
      typeDesc = "普通永久";
    }else if(userType == 3) {
      typeDesc = "高级永久";
    }else if(userType == 98){
      typeDesc = "总代理";
    }else if(userType == 99) {
      typeDesc = "管理员用户";
    }else if(userType == 100) {
      typeDesc = "超级管理员用户";
    }else {
      typeDesc = "过期状态";
    }
    return typeDesc;
  }

  private String getTime(String createdAt) {
    String[] array = createdAt.split(" ");
    String date = array[0];
    String time = array[1];
    return date.split("-")[1] + "-" + date.split("-")[2] + " "
        + time.split(":")[0] + ":" + time.split(":")[1];
  }
}
