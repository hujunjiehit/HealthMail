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
import com.june.healthmail.model.AccountInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by june on 2017/3/2.
 */

public class AcountListAdapter extends BaseAdapter implements View.OnClickListener{

    private Context mContext;
    private List<AccountInfo> mAcountList;
    private Callback mCallback;

    //用来记录所有ListView记录对应checkbox的状态
    public HashMap<Integer, Integer> selected;

    public AcountListAdapter(Context mContext, List<AccountInfo> mAcountList, Callback callback) {
        this.mContext = mContext;
        this.mAcountList = mAcountList;
        this.mCallback = callback;
        selected = new HashMap<Integer, Integer>();
    }

    public HashMap<Integer, Integer> getSelected(){
        return selected;
    }

    @Override
    public int getCount() {
        return mAcountList.size();
    }

    @Override
    public AccountInfo getItem(int position) {
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
        AccountInfo acountInfo = getItem(position);
        holder.tvIndex.setText(""+acountInfo.getId());
        holder.tvPhonenumber.setText(acountInfo.getPhoneNumber());
        if(acountInfo.getNickName().equals("")){
            holder.tvNickName.setText("未知");
        }else{
            holder.tvNickName.setText(acountInfo.getNickName());
        }
        holder.cbStatus.setTag(position);
        if(selected.containsKey(position)){
            holder.cbStatus.setChecked(true);
        }else {
            if(acountInfo.getStatus() == 1){
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
                mCallback.click(buttonView);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //mCallback.click(v);
    }

    static class ViewHolder {
        TextView tvIndex;
        TextView tvPhonenumber;
        TextView tvNickName;
        CheckBox cbStatus;
    }


    public interface Callback {
        public void click(View v);
    }
}
