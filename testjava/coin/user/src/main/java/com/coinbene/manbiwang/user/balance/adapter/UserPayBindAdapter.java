package com.coinbene.manbiwang.user.balance.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.coinbene.common.base.BaseAdapter;
import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.UserPayTypeModel;
import com.coinbene.manbiwang.user.R;


/**
 * @author ding
 * 资金费用
 */
public class UserPayBindAdapter extends BaseAdapter<UserPayTypeModel.DataBean.PaymentWayListBean> {
    private Drawable resourceBank, resourceAlipay, resourceWechat;

    private OnLineChangeListener onLineChangeListener;
    public UserPayBindAdapter() {
        super(R.layout.item_pay_bind);
    }

    public void setOnLineChangeListener(OnLineChangeListener onLineChangeListener) {
        this.onLineChangeListener = onLineChangeListener;
    }

    @Override
    protected void convert(BaseViewHolder holder, int position, UserPayTypeModel.DataBean.PaymentWayListBean item) {

        if (item.getType() == 1) {
            holder.setImageDrawable(R.id.iv_pay_img, resourceBank);
            holder.setText(R.id.tv_pay_name, item.getBankName());
            holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getUserName()) ? "" : item.getUserName());
            holder.setText(R.id.tv_acount, TextUtils.isEmpty(item.getBankAccount()) ? "" : item.getBankAccount());

        } else if (item.getType() == 2) {
            holder.setImageDrawable(R.id.iv_pay_img, resourceAlipay);
            holder.setText(R.id.tv_pay_name, R.string.alipay);
            holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getUserName()) ? "" : item.getUserName());
            holder.setText(R.id.tv_acount, TextUtils.isEmpty(item.getPayAccount()) ? "" : item.getPayAccount());

        } else if (item.getType() == 3) {
            holder.setImageDrawable(R.id.iv_pay_img, resourceWechat);
            holder.setText(R.id.tv_pay_name, R.string.wechat);
            holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getUserName()) ? "" : item.getUserName());
            holder.setText(R.id.tv_acount, TextUtils.isEmpty(item.getPayAccount()) ? "" : item.getPayAccount());
        } else {
            holder.setImageDrawable(R.id.iv_pay_img, resourceBank);
            holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getUserName()) ? "" : item.getUserName());
            holder.setText(R.id.tv_acount, TextUtils.isEmpty(item.getPayAccount()) ? "" : item.getPayAccount());
        }

        if (item.getOnline() == 0) {
            holder.setChecked(R.id.tb_bind_status, false);
        } else {
            holder.setChecked(R.id.tb_bind_status, true);
        }


        holder.getView(R.id.tb_bind_status).setOnClickListener(v -> {
            if(onLineChangeListener!=null){
                onLineChangeListener.onLineChange(item);
            }
        });
        holder.getView(R.id.tv_unbind).setOnClickListener(v -> {
            if(onLineChangeListener!=null){
                onLineChangeListener.unBind(item);
            }
        });
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resourceBank = parent.getContext().getResources().getDrawable(R.drawable.icon_bank);
        resourceAlipay = parent.getContext().getResources().getDrawable(R.drawable.icon_alipay);
        resourceWechat = parent.getContext().getResources().getDrawable(R.drawable.icon_wechat);
        return super.onCreateViewHolder(parent, viewType);
    }


    public  interface OnLineChangeListener{
        void onLineChange(UserPayTypeModel.DataBean.PaymentWayListBean item);
        void unBind(UserPayTypeModel.DataBean.PaymentWayListBean item);
    }

}
