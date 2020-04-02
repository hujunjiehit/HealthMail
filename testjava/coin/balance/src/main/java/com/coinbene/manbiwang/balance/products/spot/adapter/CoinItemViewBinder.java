package com.coinbene.manbiwang.balance.products.spot.adapter;

import android.app.Activity;
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
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.CoinTotalInfoModel;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.BalanceDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 我的资产页面
 *
 * @author mxd on 2018/6/14.
 */

public class CoinItemViewBinder extends ItemViewBinder<CoinTotalInfoModel.DataBean.ListBean, CoinItemViewBinder.ViewHolder> {

    private String symbol;

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.myself_asset_list_item, parent, false);
        return new ViewHolder(root);
    }

    public void setCurrencySymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull CoinTotalInfoModel.DataBean.ListBean balanceModel) {
        holder.coinNameTv.setText(balanceModel.getAsset());
        holder.coinName2Tv.setText(!TextUtils.isEmpty(balanceModel.getLocalAssetName()) ? new StringBuilder().append("(").append(balanceModel.getLocalAssetName()).append(")").toString() : "");
        balanceModel.setValueHide(AssetManager.getInstance().isHideValue());
        if (balanceModel.isValueHide()) {
            holder.avaTv.setText("*****");
        } else {
            holder.avaTv.setText(balanceModel.getAvailableBalance());
        }
        if (balanceModel.isValueHide()) {
            holder.totalTv.setText("*****");
        } else {
            holder.totalTv.setText(balanceModel.getTotalBalance());
        }
        if (balanceModel.isValueHide()) {
            holder.valueTv.setText("*****");
        } else {
            String currentSym = TextUtils.isEmpty(symbol) ? "" : symbol;
            holder.valueTv.setText(new StringBuilder().append(StringUtils.getCnyReplace(currentSym)).append(balanceModel.getLocalPreestimate()).toString());
        }

        //币种icon直接请求线上环境的
        String urlpath = Constants.BASE_IMG_URL + balanceModel.getAsset() + ".png";

        GlideUtils.loadImageViewLoad(holder.coinIcon.getContext(), urlpath, holder.coinIcon, R.drawable.coin_default_icon, R.drawable.coin_default_icon);

        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostPointHandler.postClickData(PostPointHandler.balance_balance_item);
                BalanceDetailActivity.startMe((Activity) v.getContext(), balanceModel, symbol);
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.ll_root)
        LinearLayout llRoot;
        @BindView(R2.id.coin_icon)
        ImageView coinIcon;
        @BindView(R2.id.top_left_tv)
        TextView coinNameTv;
        @BindView(R2.id.top_left_2_tv)
        TextView coinName2Tv;

        @BindView(R2.id.top_right_tv)
        TextView avaTv;

        @BindView(R2.id.bottom_left_tv)
        TextView valueTv;
        @BindView(R2.id.bottom_right_tv)
        TextView totalTv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
