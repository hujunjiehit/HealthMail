package com.coinbene.manbiwang.balance.activity.spot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.coinbene.manbiwang.model.http.TransferAccountListModel;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提现地址管理的item
 */

public class TransferAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TransferAccountListModel.DataBean> dataList;
    private TransferAddressAdapter.ItemListener itemListener;

    public TransferAddressAdapter() {
    }

    public void setItems(List<TransferAccountListModel.DataBean> items) {
        this.dataList = items;
    }

    public void setListener(TransferAddressAdapter.ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transfer_account_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TransferAccountListModel.DataBean item = dataList.get(position);

        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
//        GlideUtils.newInstance().loadNetImageCoinIcon(item.url, recyclerViewHolder.coinIconImg);
        recyclerViewHolder.coinNameTv.setText(item.getLabel());
        recyclerViewHolder.addressTv.setText(item.getTargetId());
        recyclerViewHolder.rootLayout.setOnClickListener(v -> {
            if (itemListener != null) {
                itemListener.itemClick(item);
            }
        });
    }
    public interface ItemListener {
//        void delete(WithDrawAddressModel.DataBean.ListBean item);

        void itemClick(TransferAccountListModel.DataBean item);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.coin_name_tv)
        TextView coinNameTv;
        @BindView(R2.id.address_tv)
        TextView addressTv;

        @BindView(R2.id.root_layout)
        View rootLayout;

        RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
