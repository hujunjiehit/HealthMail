package com.coinbene.manbiwang.record.orderrecord.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.HisOrderModel;
import com.coinbene.manbiwang.model.http.HistoryItemModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author mxd on 2018/7/30.
 */

public class HistoryDetailItemBinder extends ItemViewBinder<HisOrderModel.DataBean.TradeRecordVoBean, HistoryDetailItemBinder.HeaderViewHolder> {


    @NonNull
    @Override
    protected HeaderViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.record_history_detail_item, parent, false);
        return new HeaderViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull HeaderViewHolder holder, @NonNull HisOrderModel.DataBean.TradeRecordVoBean item) {
        holder.timeValueTv.setText(item.getTradeTime());
        holder.countValueTv.setText(item.getQuantity());//成交量

        holder.price_valueTv.setText(item.getPrice());//成交价格

//        if (TextUtils.isEmpty(item.feeByConi) || item.feeByConi.equals("-1")) {
//            holder.fee_valueTv.setText(item.fee + " " + item.quoteAsset);
//        } else {
//            holder.fee_valueTv.setText(item.feeByConi + " " + "CONI");
//        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R2.id.header_layout)
//        RelativeLayout headerLayout;

        @BindView(R2.id.time_tv_value)
        TextView timeValueTv;
        @BindView(R2.id.volu_tv_value)
        TextView countValueTv;
        @BindView(R2.id.price_tv_value)
        TextView price_valueTv;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

