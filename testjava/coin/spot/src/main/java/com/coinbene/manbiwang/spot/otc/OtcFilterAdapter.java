package com.coinbene.manbiwang.spot.otc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.utils.DensityUtil;
import com.coinbene.manbiwang.spot.R;

import java.util.List;

/**
 *
 */
public class OtcFilterAdapter extends RecyclerView.Adapter<OtcFilterAdapter.MyHolder> {


    private List<String> lists;
    private int selectPosition;
    private OnItemClickLisenter onItemClickLisenter;
    private int selectBack, defaultBack;
    private int selectColor, defaultColor;

    public void setLists(List<String> lists, int selectPosition) {
        this.lists = lists;
        this.selectPosition = selectPosition;
        notifyDataSetChanged();

    }


    public int getSelectPosition() {
        return selectPosition;
    }

    public String getSelectStr() {
        return lists == null || lists.size() == 0 ? "" : lists.get(selectPosition);
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_item_otc_filter, parent, false);
        selectBack = R.drawable.res_shape_botton_selected;
        defaultBack = R.drawable.res_shape_botton_normal;
        selectColor = parent.getResources().getColor(R.color.res_blue);
        defaultColor = parent.getResources().getColor(R.color.res_textColor_3);
        MyHolder holder = new MyHolder(itemView);
        ViewGroup.LayoutParams params = holder.clRoot.getLayoutParams();
        params.height = DensityUtil.dip2px(32);
        params.width = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(60)) / 4;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tvCoin.setText(lists.get(position));
        if (position == selectPosition) {
            holder.clRoot.setBackgroundResource(selectBack);
            holder.tvCoin.setTextColor(selectColor);
        } else {
            holder.clRoot.setBackgroundResource(defaultBack);
            holder.tvCoin.setTextColor(defaultColor);
        }
        if (onItemClickLisenter != null) {
            holder.clRoot.setOnClickListener(v -> onItemClickLisenter.onItemClick(lists.get(position), position));
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void setOnItemClickLisenter(OnItemClickLisenter onItemClickLisenter) {
        this.onItemClickLisenter = onItemClickLisenter;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tvCoin;
        private View clRoot;

        MyHolder(View itemView) {
            super(itemView);
            tvCoin = itemView.findViewById(R.id.tv_coin);
            clRoot = itemView.findViewById(R.id.cl_root);
        }
    }

    public interface OnItemClickLisenter {
        void onItemClick(String coinName, int position);
    }

}
