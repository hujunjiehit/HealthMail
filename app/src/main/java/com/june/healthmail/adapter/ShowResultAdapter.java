package com.june.healthmail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.june.healthmail.R;
import com.june.healthmail.model.AccountInfo;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/7/30.
 */

public class ShowResultAdapter extends BaseAdapter {
  private Context mContext;
  private List<BmobObject> mAcountList;

  public ShowResultAdapter(Context mContext, List<BmobObject> mAcountList) {
    this.mContext = mContext;
    this.mAcountList = mAcountList;
  }

  @Override
  public int getCount() {
    return mAcountList.size();
  }

  @Override
  public BmobObject getItem(int position) {
    return mAcountList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if(convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_show_result,parent,false);
      holder = new ViewHolder();
      holder.tvIndex = (TextView) convertView.findViewById(R.id.tv_index);
      holder.tvPhonenumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
      holder.tvYuekeTimes = (TextView) convertView.findViewById(R.id.tv_yueke_times);
      holder.tvPingjiaTimes = (TextView) convertView.findViewById(R.id.tv_pingjia_times);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    AccountInfo acountInfo = (AccountInfo) getItem(position);
    holder.tvIndex.setText(""+ (position + 1));
    holder.tvPhonenumber.setText(acountInfo.getPhoneNumber());
    holder.tvYuekeTimes.setText(acountInfo.getYuekeTimes() + "");
    holder.tvPingjiaTimes.setText(acountInfo.getPingjiaTimes() + "");
    return convertView;
  }

  static class ViewHolder {
    TextView tvIndex;
    TextView tvPhonenumber;
    TextView tvYuekeTimes;
    TextView tvPingjiaTimes;
  }
}
