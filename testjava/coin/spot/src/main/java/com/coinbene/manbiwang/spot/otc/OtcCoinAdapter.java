package com.coinbene.manbiwang.spot.otc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.database.OtcAssetListTable;
import com.coinbene.manbiwang.spot.R;

import java.util.List;

/**
 *
 */
public class OtcCoinAdapter extends RecyclerView.Adapter<OtcCoinAdapter.MyHolder> {


    private List<OtcAssetListTable> lists;
    private int selectPosition;
    private OnItemClickLisenter onItemClickLisenter;
    private int selectColor, defaultColor;

    public void setLists(List<OtcAssetListTable> lists, int selectPosition) {
        this.lists = lists;
        this.selectPosition = selectPosition;
        notifyDataSetChanged();

    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_item_otc_coin, parent, false);
        selectColor = parent.getResources().getColor(R.color.   res_blue);
        defaultColor = parent.getResources().getColor(R.color.res_textColor_2);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tvCoin.setText(lists.get(position).asset);
        if (position == selectPosition) {
            holder.indicator.setVisibility(View.VISIBLE);
            holder.tvCoin.setTextColor(selectColor);
        } else {
            holder.indicator.setVisibility(View.GONE);
            holder.tvCoin.setTextColor(defaultColor);
        }
        if (onItemClickLisenter != null) {
            holder.clRoot.setOnClickListener(v -> onItemClickLisenter.onItemClick(lists.get(position).asset, position));
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
        private View indicator;

        MyHolder(View itemView) {
            super(itemView);
            tvCoin = itemView.findViewById(R.id.tv_coin);
            clRoot = itemView.findViewById(R.id.cl_root);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }

    public interface OnItemClickLisenter {
        void onItemClick(String coinName, int position);
    }

}
