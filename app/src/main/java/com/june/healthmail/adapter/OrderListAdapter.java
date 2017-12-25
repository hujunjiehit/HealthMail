package com.june.healthmail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.model.HmOrder;
import com.june.healthmail.untils.Tools;

import java.util.HashMap;
import java.util.List;

/**
 * Created by june on 2017/3/12.
 */

public class OrderListAdapter extends BaseAdapter{

    private Context mContext;
    private List<HmOrder> mOrderList;
    private Callback mCallback;
    private boolean hideCheckbox = false;
  //用来记录所有ListView记录对应checkbox的状态
  public HashMap<Integer, Integer> selected;

    public OrderListAdapter(Context context,List<HmOrder> orders){
        this.mContext = context;
        this.mOrderList = orders;
        selected = new HashMap<Integer, Integer>();
    }

  public OrderListAdapter(Context context,List<HmOrder> orders, Callback callback){
    this.mContext = context;
    this.mOrderList = orders;
    this.mCallback = callback;
    selected = new HashMap<Integer, Integer>();
  }

  public HashMap<Integer, Integer> getSelected(){
    return selected;
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
            holder.cbStatus = (CheckBox) convertView.findViewById(R.id.cb_status);
            if(hideCheckbox == true) {
              //holder.cbStatus.setVisibility(View.GONE);
              convertView.findViewById(R.id.layout_status).setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HmOrder hmOrder = mOrderList.get(position);
        holder.tvNum.setText((position + 1) + "");
        holder.tvNickName.setText(hmOrder.getTrainerNick());
        holder.tvTime.setText(getServerTime(hmOrder));
        holder.tvPrice.setText(hmOrder.getHM_OrderPrice()+"");

        holder.cbStatus.setTag(position);
        if(selected.containsKey(position)) {
          holder.cbStatus.setChecked(true);
        } else {
          if(hmOrder.isSelected()) {
            holder.cbStatus.setChecked(true);
            selected.put((Integer) holder.cbStatus.getTag(),position);
          }else {
            holder.cbStatus.setChecked(false);
            selected.remove((Integer) holder.cbStatus.getTag());
          }
        }
        addListener(holder,position);

        return convertView;
    }

  private String getServerTime(HmOrder hmOrder) {
    return Tools.getCourseDay(hmOrder.getHM_ServerDate()) + "日 " + hmOrder.getHM_GBC_ServerTime();
  }

  private void addListener(ViewHolder holder, final int position) {
    holder.cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          if(!selected.containsKey(buttonView.getTag())){
            selected.put((Integer) buttonView.getTag(),position);
          }
        }else{
          selected.remove((Integer)buttonView.getTag());
        }
        if(mCallback != null) {
          mCallback.click(buttonView);
        }
      }
    });
  }

  public void hideCheckbox() {
    this.hideCheckbox = true;
  }

  static class ViewHolder {
        TextView tvNum;
        TextView tvNickName;
        TextView tvTime;
        TextView tvPrice;
        CheckBox cbStatus;
    }

  public interface Callback {
    public void click(View v);
  }
}
