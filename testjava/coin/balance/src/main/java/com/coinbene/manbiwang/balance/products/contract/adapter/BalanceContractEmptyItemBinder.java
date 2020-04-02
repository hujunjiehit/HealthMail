package com.coinbene.manbiwang.balance.products.contract.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.FutureItemEmptyModel;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 合约资产--->下面的每个项目
 *
 * @author mxd on 2019/3/18.
 */

public class BalanceContractEmptyItemBinder extends ItemViewBinder<FutureItemEmptyModel, BalanceContractEmptyItemBinder.ViewHolder> {
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.balance_item_empty, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull FutureItemEmptyModel emptyModel) {
            viewHolder.emptyLayout.setVisibility(View.VISIBLE);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.empty_layout)
        LinearLayout emptyLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
