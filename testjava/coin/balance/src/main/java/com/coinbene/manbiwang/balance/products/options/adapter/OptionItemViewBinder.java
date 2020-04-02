package com.coinbene.manbiwang.balance.products.options.adapter;

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
import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.OptionsTotalInfoModel;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 我的资产页面
 *
 * @author mxd on 2018/6/14.
 */

public class OptionItemViewBinder extends ItemViewBinder<OptionsTotalInfoModel.DataBean.ListBean, OptionItemViewBinder.ViewHolder> {

    private String symbol;
    private boolean hideValue;

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
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull OptionsTotalInfoModel.DataBean.ListBean balanceModel) {
        hideValue =	AssetManager.getInstance().isHideValue();

        holder.coinNameTv.setText(balanceModel.getAssetName());
        holder.avaTv.setVisibility(View.GONE);

        if (hideValue) {
            holder.totalTv.setText("*****");
        } else {
            holder.totalTv.setText(balanceModel.getAmount());//获取当前的币的估值，不是对应的btc估值
        }
        if (hideValue) {
            holder.valueTv.setText("*****");
        } else {
            String currentSym = TextUtils.isEmpty(symbol) ? "" : symbol;
            holder.valueTv.setText(new StringBuilder().append(StringUtils.getCnyReplace(currentSym)).append(balanceModel.getLocalPreestimate()).toString());
        }
        String urlpath = Constants.BASE_IMG_URL + balanceModel.getAssetName().trim() + ".png";

        GlideUtils.loadImageViewLoad(holder.coinIcon.getContext(), urlpath, holder.coinIcon, R.drawable.coin_default_icon, R.drawable.coin_default_icon);
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
