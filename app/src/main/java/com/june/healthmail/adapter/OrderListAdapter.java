package com.june.healthmail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.model.HmOrder;

import java.util.List;

/**
 * Created by june on 2017/3/12.
 */

public class OrderListAdapter extends BaseAdapter{

    private Context mContext;
    private List<HmOrder> mOrderList;

    public OrderListAdapter(Context context,List<HmOrder> orders){
        this.mContext = context;
        this.mOrderList = orders;
    }

    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_order,parent,false);
            holder = new ViewHolder();
            holder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_course_time);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HmOrder hmOrder = mOrderList.get(position);
        holder.tvNum.setText((position + 1) + "");
        holder.tvNickName.setText(hmOrder.getTrainerNick());
        holder.tvTime.setText(hmOrder.getHM_GBC_ServerTime());
        holder.tvPrice.setText(hmOrder.getHM_OrderPrice()+"");
        return convertView;
    }

    static class ViewHolder {
        TextView tvNum;
        TextView tvNickName;
        TextView tvTime;
        TextView tvPrice;
    }
}
