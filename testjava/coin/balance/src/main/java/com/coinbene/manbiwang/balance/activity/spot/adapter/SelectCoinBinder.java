package com.coinbene.manbiwang.balance.activity.spot.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.coinbene.common.Constants;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.SelectCoinActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 */

public class SelectCoinBinder extends ItemViewBinder<BalanceInfoTable, SelectCoinBinder.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private int type;

    public SelectCoinBinder(int type) {
        this.type = type;
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.select_coin_item, parent, false);
        return new ViewHolder(root);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BalanceInfoTable item) {
        String name = item.localAssetName;
        holder.nameTv.setText(item.asset);
        if (!TextUtils.isEmpty(name)) {
            holder.coinNameTv.setText("(" + name.trim() + ")");
            holder.coinNameTv.setVisibility(View.VISIBLE);
        } else {
            holder.coinNameTv.setVisibility(View.GONE);
        }

        String urlpath = Constants.BASE_IMG_URL + item.asset + ".png";
        GlideUtils.loadImageViewLoad(holder.iconImg.getContext(), urlpath, holder.iconImg, R.drawable.coin_default_icon, R.drawable.coin_default_icon);

        holder.rootLayout.setOnClickListener(v -> {
            if (this.onItemClickListener != null) {
                this.onItemClickListener.onItemClick(item);
            }
        });

        if (type == SelectCoinActivity.FROM_DEPOSIT) {
            if (item.deposit) {
                holder.statusTv.setVisibility(View.GONE);
            } else {
                holder.statusTv.setVisibility(View.VISIBLE);
            }
        } else if (type == SelectCoinActivity.FROM_WITHDRAW) {
            if (item.withdraw) {
                holder.statusTv.setVisibility(View.GONE);
            } else {
                //不能提现
                holder.statusTv.setVisibility(View.VISIBLE);
            }
        } else {
            holder.statusTv.setVisibility(View.GONE);
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.root_layout)
        LinearLayout rootLayout;
        @BindView(R2.id.name_tv)
        TextView nameTv;

        @BindView(R2.id.coin_name_tv)
        TextView coinNameTv;
        @BindView(R2.id.icon_img)
        ImageView iconImg;

        @BindView(R2.id.status_tv)
        TextView statusTv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BalanceInfoTable item);
    }
}
