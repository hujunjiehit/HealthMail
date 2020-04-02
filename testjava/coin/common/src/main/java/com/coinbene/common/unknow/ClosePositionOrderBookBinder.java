package com.coinbene.common.unknow;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.manbiwang.model.http.OrderModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 */

public class ClosePositionOrderBookBinder extends ItemViewBinder<OrderModel, ClosePositionOrderBookBinder.ViewHolder> {

    private int greenColor, redColor;
    private boolean isRedRise;
    private boolean isBuy;//是不是买  买盘  text位置不一样
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ClosePositionOrderBookBinder(boolean isBuy, int greenColor, int redColor, boolean isRedRise) {
        this.isBuy = isBuy;
        this.greenColor = greenColor;
        this.redColor = redColor;
        this.isRedRise = isRedRise;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_close_position_orderbook, parent, false);
        return new ViewHolder(root, this);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OrderModel item) {
        holder.amount_tv.setText(TextUtils.isEmpty(item.cnt) ? "--" : item.cnt);
        holder.priceTv.setText(TextUtils.isEmpty(item.price) ? "--" : item.price);
        if (isBuy) {
            holder.priceTv.setTextColor(isRedRise ? redColor : greenColor);
        } else {
            holder.priceTv.setTextColor(isRedRise ? greenColor : redColor);
        }
        holder.relativeLayout.setOnClickListener(v -> {
            if(onItemClick!=null){
                onItemClick.onItemClick(item.price);
            }
        });

    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.price_tv)
        TextView priceTv;
        @BindView(R2.id.amount_tv)
        TextView amount_tv;
        @BindView(R2.id.rl_root)
        RelativeLayout relativeLayout;
        ViewHolder(View view, ClosePositionOrderBookBinder binder) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClick{
        void onItemClick(String price);
    }
}
