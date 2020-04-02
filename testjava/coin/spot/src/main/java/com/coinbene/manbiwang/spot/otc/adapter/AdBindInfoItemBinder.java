package com.coinbene.manbiwang.spot.otc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.ReleaseAdInfoModel;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 我的资产页面
 *
 * @author mxd on 2019/4/26.
 */

public class AdBindInfoItemBinder extends ItemViewBinder<ReleaseAdInfoModel.OtcPaymentWay, AdBindInfoItemBinder.ViewHolder> {

    public static int TYPE_BANK = 1;
    public static int TYPE_WEIXIN = 3;
    public static int TYPE_ALIPAY = 2;

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.spot_adbindinfo_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ReleaseAdInfoModel.OtcPaymentWay bindInfo) {
        if (bindInfo.getType() == TYPE_BANK) {
            holder.bind_info_tv.setText(bindInfo.getBankName());
            holder.bind_info_value_tv.setText(bindInfo.getBankAccount());
        } else if (bindInfo.getType() == TYPE_WEIXIN) {
            holder.bind_info_tv.setText(holder.bind_info_tv.getResources().getString(R.string.wechat));
            holder.bind_info_value_tv.setText(bindInfo.getPayAccount());
        } else if (bindInfo.getType() == TYPE_ALIPAY) {
            holder.bind_info_tv.setText(holder.bind_info_tv.getResources().getString(R.string.alipay));
            holder.bind_info_value_tv.setText(bindInfo.getPayAccount());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.bind_info_tv)
        TextView bind_info_tv;
        @BindView(R2.id.bind_info_value_tv)
        TextView bind_info_value_tv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
