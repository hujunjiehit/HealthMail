package com.coinbene.manbiwang.record.coinrecord.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.manbiwang.model.http.DepositListModel;
import com.coinbene.manbiwang.model.http.DispatchRecordBean;
import com.coinbene.manbiwang.model.http.TransferListModel;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.coinrecord.DispatchCoinActivity;
import com.coinbene.manbiwang.record.coinrecord.RecordDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity.ORDER_TYPE_DEPOSIT;
import static com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity.ORDER_TYPE_DISPATCH;
import static com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity.ORDER_TYPE_TRANSFER;
import static com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity.ORDER_TYPE_WITHDRAW;

/**
 * --tab
 *
 * @author ding
 */

public class HistoryRechargeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DepositListModel.DataBean.ListBean> dataList;
    private List<TransferListModel.DataBean.ListBean> tranferList;
    private List<DispatchRecordBean.DataBean.ListBean> dispatchList;

    private String orderType;

    public void setItems(List<DepositListModel.DataBean.ListBean> items, List<TransferListModel.DataBean.ListBean> tranferList, String orderType) {
        this.orderType = orderType;
        if (!orderType.equals(ORDER_TYPE_TRANSFER)) {
            this.dataList = items;
        } else {
            this.tranferList = tranferList;
        }

    }

    public void setItems(List<DispatchRecordBean.DataBean.ListBean> items, String orderType) {
        this.orderType = orderType;

        if (orderType.equals(ORDER_TYPE_DISPATCH)) {
            this.dispatchList = items;
        }

    }


    public void appendItems(List<DepositListModel.DataBean.ListBean> items, List<TransferListModel.DataBean.ListBean> tranferGetList, String orderType) {
        if (!orderType.equals(ORDER_TYPE_TRANSFER)) {
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            if (items == null) {
                items = new ArrayList<>();
            }
            this.dataList.addAll(items);
        } else {
            if (tranferList == null) {
                tranferList = new ArrayList<>();
            }
            if (tranferGetList == null) {
                tranferGetList = new ArrayList<>();
            }
            this.tranferList.addAll(tranferGetList);
        }
    }

    public void appendItems(List<DispatchRecordBean.DataBean.ListBean> items, String orderType) {
        if (orderType.equals(ORDER_TYPE_DISPATCH)) {
            if (dispatchList == null) {
                dispatchList = new ArrayList<>();
            }
            if (items != null) {
                this.dispatchList.addAll(items);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (orderType.equals(ORDER_TYPE_TRANSFER)) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.record_his_transfer_list_item, parent, false);
            return new RecyclerViewHolder_Transfer(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.record_his_recharge_draw_list_item, parent, false);
            return new RecyclerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (orderType.equals(ORDER_TYPE_DEPOSIT) || orderType.equals(ORDER_TYPE_WITHDRAW)) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            DepositListModel.DataBean.ListBean model = dataList.get(position);

            viewHolder.coinNameTv.setText(model.getAsset());
            viewHolder.timeTvView.setText(model.getOrderTime());
            viewHolder.numTv.setText(model.getAmount());
            viewHolder.status_tv.setVisibility(View.VISIBLE);

            if (orderType.equals(ORDER_TYPE_DEPOSIT)) {//充值
                if (model.getStatus().equals("1")) {//正在处理
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_1_doing));
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_red));
                } else if (model.getStatus().equals("2")) {//已经入账
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    String str = viewHolder.status_tv.getResources().getString(R.string.orderstatus_2_in);
                    viewHolder.status_tv.setText(str);
                }
            } else if (orderType.equals(ORDER_TYPE_WITHDRAW)) {//提现   1处理中2待审核3交易已发出4已完成5已撤单6审核失败 7缺少tag
                if (model.getStatus().equals("1")) {//正在处理
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_1_doing));
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_red));
                } else if (model.getStatus().equals("2")) {//待审核
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_daishenhe));
                } else if (model.getStatus().equals("3")) {
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_4_submit));
                } else if (model.getStatus().equals("4")) {
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_2_out));
                } else if (model.getStatus().equals("5")) {
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_5));
                } else if (model.getStatus().equals("6")) {
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_6));
                } else if (model.getStatus().equals("7")) {
                    viewHolder.status_tv.setTextColor(viewHolder.status_tv.getResources().getColor(R.color.res_textColor_3));
                    viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_7));
                }
            }


            viewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordDetailActivity.startMe(v.getContext(), model, orderType);
                }
            });
        } else if (orderType.equals(ORDER_TYPE_TRANSFER)) {//转账
            RecyclerViewHolder_Transfer viewHolder = (RecyclerViewHolder_Transfer) holder;
            TransferListModel.DataBean.ListBean model = tranferList.get(position);
            viewHolder.coinNameTv.setText(model.getAsset());
            viewHolder.timeTvView.setText(model.getTransferTime());

            boolean isRedRise = SwitchUtils.isRedRise();

            if (model.getTransferType() == 1) {//转出  2转入
                viewHolder.accoutTv.setText(new StringBuilder().append(viewHolder.accoutTv.getResources().getString(R.string.to)).append("  ").append(model.getNostro()).toString());
                viewHolder.numTv.setText(new StringBuilder().append("-").append(model.getAmount()).toString());
                if (isRedRise) {
                    viewHolder.leftColorTv.setBackgroundColor(viewHolder.leftColorTv.getResources().getColor(R.color.res_green));
                } else {
                    viewHolder.leftColorTv.setBackgroundColor(viewHolder.leftColorTv.getResources().getColor(R.color.res_red));
                }
//                viewHolder.orderTypeView.setText(viewHolder.orderTypeView.getResources().getString(R.string.transferOut));
            } else {
                viewHolder.numTv.setText(model.getAmount());
                if (LanguageHelper.isChinese(CBRepository.getContext())) {//中文 from在后  英文在前   产品说是语法问题
                    viewHolder.accoutTv.setText(new StringBuilder().append(model.getNostro()).append("  ").append(viewHolder.accoutTv.getResources().getString(R.string.from)).toString());
                } else {
                    viewHolder.accoutTv.setText(new StringBuilder().append(viewHolder.accoutTv.getResources().getString(R.string.from)).append("  ").append(model.getNostro()).toString());
                }
                if (isRedRise) {
                    viewHolder.leftColorTv.setBackgroundColor(viewHolder.leftColorTv.getResources().getColor(R.color.res_red));
                } else {
                    viewHolder.leftColorTv.setBackgroundColor(viewHolder.leftColorTv.getResources().getColor(R.color.res_green));
                }
//                viewHolder.orderTypeView.setText(viewHolder.orderTypeView.getResources().getString(R.string.transferIn));
            }
        } else if (orderType.equals(ORDER_TYPE_DISPATCH)) {
            //分发记录
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            DispatchRecordBean.DataBean.ListBean dispatch = dispatchList.get(position);

            //设置数据
            viewHolder.coinNameTv.setText(dispatch.getAsset());
            viewHolder.timeTvView.setText(dispatch.getCreateTime());
            viewHolder.numTv.setText(dispatch.getAmount());
            viewHolder.status_tv.setVisibility(View.VISIBLE);
            viewHolder.status_tv.setText(viewHolder.status_tv.getResources().getString(R.string.orderstatus_2_in));

            viewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();

                    String urlpath = Constants.BASE_IMG_URL + dispatch.getAsset() + ".png";
                    bundle.putString("dispatchNum", dispatch.getAmount());
                    bundle.putString("dispatchRemark", dispatch.getRemark());
                    bundle.putString("dispatchDate", dispatch.getUpdateTime());
                    bundle.putString("dispatchName", dispatch.getAsset());
                    bundle.putString("dispatchIcon", urlpath);

                    DispatchCoinActivity.startMe(v.getContext(), bundle);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (orderType == null) {
            return 0;
        } else if (orderType.equals(ORDER_TYPE_TRANSFER)) {
            return tranferList == null ? 0 : tranferList.size();
        } else if (orderType.equals(ORDER_TYPE_DEPOSIT) || orderType.equals(ORDER_TYPE_WITHDRAW)) {
            return dataList == null ? 0 : dataList.size();
        } else if (orderType.equals(ORDER_TYPE_DISPATCH)) {
            return dispatchList == null ? 0 : dispatchList.size();
        }

        return 0;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.left_top_tv)
        TextView coinNameTv;
        @BindView(R2.id.status_tv)
        TextView status_tv;
        @BindView(R2.id.left_num_tv)
        TextView numTv;
        @BindView(R2.id.time_tv)
        TextView timeTvView;
        @BindView(R2.id.root_layout)
        View rootLayout;

        RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class RecyclerViewHolder_Transfer extends RecyclerView.ViewHolder {
        @BindView(R2.id.left_color_tv)
        TextView leftColorTv;
        @BindView(R2.id.account_tv)
        TextView accoutTv;

        @BindView(R2.id.left_top_tv)
        TextView coinNameTv;
        @BindView(R2.id.left_num_tv)
        TextView numTv;

        @BindView(R2.id.time_tv)
        TextView timeTvView;

        RecyclerViewHolder_Transfer(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
