package com.june.healthmail.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.model.AcountInfo;

import java.util.List;

/**
 * Created by june on 2017/3/2.
 */

public class AcountListAdapter extends BaseAdapter{

    private Context mContext;
    private List<AcountInfo> mAcountList;

    public AcountListAdapter(Context mContext, List<AcountInfo> mAcountList) {
        this.mContext = mContext;
        this.mAcountList = mAcountList;
    }

    @Override
    public int getCount() {
        return mAcountList.size();
    }

    @Override
    public AcountInfo getItem(int position) {
        return mAcountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_acount_info,parent,false);
            holder = new ViewHolder();
            holder.tvIndex = (TextView) convertView.findViewById(R.id.tv_index);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
            holder.tvPhonenumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            holder.cbStatus = (CheckBox) convertView.findViewById(R.id.cb_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AcountInfo acountInfo = getItem(position);
        Log.e("test","id = " + acountInfo.getId());
        holder.tvIndex.setText(""+acountInfo.getId());
        holder.tvPhonenumber.setText(acountInfo.getPhoneNumber());
        holder.tvNickName.setText(acountInfo.getNickName());
        if(acountInfo.getStatus() == 0){
            holder.cbStatus.setChecked(false);
        } else {
            holder.cbStatus.setChecked(true);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvIndex;
        TextView tvPhonenumber;
        TextView tvNickName;
        CheckBox cbStatus;
    }
}
