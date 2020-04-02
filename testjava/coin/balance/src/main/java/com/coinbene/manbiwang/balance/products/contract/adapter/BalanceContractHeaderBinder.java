package com.coinbene.manbiwang.balance.products.contract.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.manbiwang.model.http.ContractTotalInfoModel;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 我的资产页面--头部
 *
 * @author mxd on 2019/3/18.
 */

public class BalanceContractHeaderBinder extends ItemViewBinder<ContractTotalInfoModel.DataBean, BalanceContractHeaderBinder.ViewHolder> {

    private Context mContext;
    private boolean hideValue;
    private int contractType;

    public void setContractType(int contractType) {
        this.contractType = contractType;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.balance_contract_header, parent, false);
        mContext = root.getContext();
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ContractTotalInfoModel.DataBean balanceModel) {
        hideValue =	AssetManager.getInstance().isHideValue();

        holder.currentBalanceLayout.setVisibility(View.VISIBLE);
        holder.tipOrderTv.setVisibility(View.VISIBLE);
        holder.lineView.setVisibility(View.VISIBLE);

        if (hideValue) {
            holder.tvAccountBalance.setText("*****");
            holder.tvAccountBalanceLocal.setText("*****");

            holder.first_value_tv.setText("*****");
            holder.second_value_tv.setText("*****");
            holder.third_value_tv.setText("*****");
            holder.fourth_value_tv.setText("*****");
        } else {
            if (balanceModel == null) {
                holder.first_value_tv.setText("--");
                holder.second_value_tv.setText("--");
                holder.third_value_tv.setText("--");
                holder.fourth_value_tv.setText("--");
                return;
            }

            holder.tvAccountBalance.setText(String.format("%s BTC",balanceModel.getBtcTotalPreestimate()));
            holder.tvAccountBalanceLocal.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(balanceModel.getCurrencySymbol()), balanceModel.getLocalTotalPreestimate()));

            //账户余额
            holder.first_value_tv.setText(String.format("%s %s", balanceModel.getAccountInfo().getBalance(), balanceModel.getAccountInfo().getSymbol()));

            //未实现盈亏
            holder.second_value_tv.setText(String.format("%s %s", balanceModel.getAccountInfo().getUnrealisedPnl(), balanceModel.getAccountInfo().getSymbol()));

            //净值--当前资产
            holder.third_value_tv.setText(String.format("%s %s", balanceModel.getAccountInfo().getMarginBalance(), balanceModel.getAccountInfo().getSymbol()));

            //可用余额
            holder.fourth_value_tv.setText(String.format("%s %s", balanceModel.getAccountInfo().getAvailableBalance(), balanceModel.getAccountInfo().getSymbol()));

            if (TextUtils.isEmpty(balanceModel.getAccountInfo().getUnrealisedPnl())) {
                return;
            }

            String unRealised = TextUtils.isEmpty(balanceModel.getAccountInfo().getUnrealisedPnl()) ? "0" : balanceModel.getAccountInfo().getUnrealisedPnl();
            float unRealisedF = Float.valueOf(unRealised);
            boolean isRedRise = SpUtil.isRedRise();
            if (unRealisedF == 0) {
                holder.second_value_tv.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));
            } else {
                TextView second_value_tv = holder.second_value_tv;
                if (isRedRise) {
                    if (unRealised.contains("-")) {
                        second_value_tv.setTextColor(mContext.getResources().getColor(R.color.res_green));
                    } else {
                        second_value_tv.setTextColor(mContext.getResources().getColor(R.color.res_red));
                    }
                } else {
                    if (unRealised.contains("-")) {
                        second_value_tv.setTextColor(mContext.getResources().getColor(R.color.res_red));
                    } else {
                        second_value_tv.setTextColor(mContext.getResources().getColor(R.color.res_green));
                    }
                }
            }
        }

        if (contractType == Constants.CONTRACT_TYPE_BTC) {
            holder.tvContractRecord.setText(R.string.res_btc_contract_record);
        } else if (contractType == Constants.CONTRACT_TYPE_USDT){
            holder.tvContractRecord.setText(R.string.res_usdt_contract_record);
        }
        holder.tvContractRecord.setOnClickListener(v ->
            ServiceRepo.getRecordService().gotoRecord(v.getContext(),
                    contractType == Constants.CONTRACT_TYPE_USDT ? RecordType.USDT_CONTRACT : RecordType.BTC_CONTRACT));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.first_value_tv)
        TextView first_value_tv;
        @BindView(R2.id.second_value_tv)
        TextView second_value_tv;
        @BindView(R2.id.third_value_tv)
        TextView third_value_tv;
        @BindView(R2.id.fourth_value_tv)
        TextView fourth_value_tv;

        @BindView(R2.id.current_balance_layout)
        View currentBalanceLayout;
        @BindView(R2.id.tip_order_tv)
        TextView tipOrderTv;
        @BindView(R2.id.line_view)
        View lineView;

        @BindView(R2.id.tv_account_balance)
        TextView tvAccountBalance;
        @BindView(R2.id.tv_account_balance_local)
        TextView tvAccountBalanceLocal;

        @BindView(R2.id.tv_contract_record)
        TextView tvContractRecord;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
