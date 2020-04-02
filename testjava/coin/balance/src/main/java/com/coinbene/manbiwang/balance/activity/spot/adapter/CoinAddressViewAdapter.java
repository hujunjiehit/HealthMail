package com.coinbene.manbiwang.balance.activity.spot.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.WithDrawAddressModel;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.CoinAddressActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提现地址管理的item
 */

public class CoinAddressViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WithDrawAddressModel.DataBean.ListBean> dataList;
    private CoinAddressActivity.ItemListener itemListener;
    private boolean hideDelete;

    public CoinAddressViewAdapter(boolean hideDelete) {
        this.hideDelete = hideDelete;
    }

    public void setItems(List<WithDrawAddressModel.DataBean.ListBean> items) {
        this.dataList = items;
    }

    public void removeId(int id) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId() == id) {
                dataList.remove(i);
            }
        }
    }

    public void setListener(CoinAddressActivity.ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin_address_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WithDrawAddressModel.DataBean.ListBean item = dataList.get(position);

        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
//        GlideUtils.newInstance().loadNetImageCoinIcon(item.url, recyclerViewHolder.coinIconImg);
        recyclerViewHolder.coinNameTv.setText(item.getAsset());
        recyclerViewHolder.remarkTv.setText(String.format("(%s%s)", recyclerViewHolder.remarkTv.getResources().getString(R.string.my_coin_address), item.getLabel()));
        recyclerViewHolder.addressTv.setText(item.getAddress());

        if(TextUtils.isEmpty(item.getProtocolName())){
            recyclerViewHolder.tvChainName.setText("");
            recyclerViewHolder.tvChainName.setVisibility(View.GONE);
        }else{
            recyclerViewHolder.tvChainName.setText(item.getProtocolName());
            recyclerViewHolder.tvChainName.setVisibility(View.VISIBLE);
        }

        if (hideDelete) {
            recyclerViewHolder.deleteTv.setVisibility(View.GONE);
            recyclerViewHolder.deleteTv.setOnClickListener(null);
        } else {
            recyclerViewHolder.deleteTv.setVisibility(View.VISIBLE);
            recyclerViewHolder.deleteTv.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.delete(item);
                }
            });
        }
        recyclerViewHolder.rootLayout.setOnClickListener(v -> {
            if (itemListener != null) {
                itemListener.itemClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.coin_name_tv)
        TextView coinNameTv;
        @BindView(R2.id.remark_tv)
        TextView remarkTv;
        @BindView(R2.id.delete_tv)
        TextView deleteTv;
        @BindView(R2.id.address_tv)
        TextView addressTv;
        @BindView(R2.id.tv_chain_name)
        TextView tvChainName;

        @BindView(R2.id.root_layout)
        View rootLayout;

        RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
