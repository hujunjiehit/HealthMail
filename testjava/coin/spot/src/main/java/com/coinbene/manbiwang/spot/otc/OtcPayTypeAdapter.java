package com.coinbene.manbiwang.spot.otc;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.OtcAdListModel;
import com.coinbene.manbiwang.spot.R;

import java.util.List;

/**
 */
public class OtcPayTypeAdapter extends RecyclerView.Adapter<OtcPayTypeAdapter.MyHolder> {

    private Drawable resourceBank, resourceAlipay, resourceWechat;

    private List<OtcAdListModel.DataBean.ListBean.PayType> lists;

    public void setLists(List<OtcAdListModel.DataBean.ListBean.PayType> lists) {
        this.lists = lists;
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_otc_pay_type_item, parent, false);
        resourceBank = parent.getContext().getResources().getDrawable(R.drawable.icon_bank);
        resourceAlipay = parent.getContext().getResources().getDrawable(R.drawable.icon_alipay);
        resourceWechat = parent.getContext().getResources().getDrawable(R.drawable.icon_wechat);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
//        holder.ivBank.setImageDrawable(null);
        if (lists.get(position).getPayTypeId() == 1) {
            holder.ivBank.setImageDrawable(resourceBank);
        } else if (lists.get(position).getPayTypeId() == 2) {
            holder.ivBank.setImageDrawable(resourceAlipay);
        } else if (lists.get(position).getPayTypeId() == 3) {
            holder.ivBank.setImageDrawable(resourceWechat);
        } else {
            holder.ivBank.setImageDrawable(resourceBank);
        }
    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView ivBank;

        MyHolder(View itemView) {
            super(itemView);
            ivBank = itemView.findViewById(R.id.iv_bank);
        }
    }

    public interface OnItemClickLisenter {
        void onItemClick(String selectStr, int position);
    }

}
