package com.coinbene.manbiwang.contract.contractbtc.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.Constants;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.layout.SubteactPositionLayout;

import java.util.List;

/**
 *
 */

public class HoldPositionBtcAdapter extends BaseQuickAdapter<ContractPositionListModel.DataBean, BaseViewHolder> {

    private ClickHoldPostionListener clickHoldPostionListener;

    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    int viewType;
    private List<ContractPositionListModel.DataBean> mList;

    private String symbleStr, longStr, shortStr;

    private boolean isRedRaise;
    private int greenDrawable, redDrawable;
    private int greeColor, redColor;
    private Context mContext;

    private SpannableString spanString;
    private ForegroundColorSpan colorSpan;


    public HoldPositionBtcAdapter() {
        super(R.layout.item_hold_position_btc);
    }

    public void setClickHoldPostionListener(ClickHoldPostionListener clickHoldPostionListener) {
        this.clickHoldPostionListener = clickHoldPostionListener;
    }

    public void setRedRaise(boolean redRaise) {
        isRedRaise = redRaise;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         mContext = parent.getContext();
        colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.res_blue));
        symbleStr = parent.getResources().getString(R.string.forever_no_delivery);
        longStr = parent.getResources().getString(R.string.side_long);
        shortStr = parent.getResources().getString(R.string.side_short);

        greenDrawable = R.drawable.bg_green_sharp;
        redDrawable = R.drawable.bg_red_sharp;
        greeColor = parent.getResources().getColor(R.color.res_green);
        redColor = parent.getResources().getColor(R.color.res_red);
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder hd, ContractPositionListModel.DataBean item) {
        isRedRaise = SwitchUtils.isRedRise();
        hd.setText(R.id.tv_contrack_name,String.format(symbleStr, item.getSymbol()));
        if (item.getSide().equals("long")) {//多
            hd.setText(R.id.current_delegation_tag,String.format(longStr, item.getLeverage()));
            hd.setBackgroundRes(R.id.current_delegation_tag,isRedRaise ? redDrawable : greenDrawable);

        } else {//空
            hd.setText(R.id.current_delegation_tag,String.format(shortStr, item.getLeverage()));
            hd.setBackgroundRes(R.id.current_delegation_tag,isRedRaise ? greenDrawable : redDrawable);
        }
        if (BigDecimalUtils.lessThanToZero(item.getUnrealisedPnl())) {
            hd.setTextColor(R.id.tv_unrealized_profit_loss_value,isRedRaise ? greeColor : redColor);
        } else {
            hd.setTextColor(R.id.tv_unrealized_profit_loss_value,isRedRaise ? redColor : greeColor);
        }
        if (BigDecimalUtils.lessThanToZero(item.getRealisedPnl())) {
            hd.setTextColor(R.id.tv_realized_profit_loss_value,isRedRaise ? greeColor : redColor);
        } else {
            hd.setTextColor(R.id.tv_realized_profit_loss_value,isRedRaise ? redColor : greeColor);
        }
        if (BigDecimalUtils.lessThanToZero(item.getRoe())) {
            hd.setTextColor(R.id.tv_return_percent_value,isRedRaise ? greeColor : redColor);
        } else {
            hd.setTextColor(R.id.tv_return_percent_value,isRedRaise ? redColor : greeColor);
        }
        hd.setText(R.id.tv_total_hold_value,String.format("%s/%s", item.getAvailableQuantity(), item.getQuantity()));
        hd.setText(R.id.tv_entrust_price_value,item.getAvgPrice());
        hd.setText(R.id.tv_force_price_value,item.getLiquidationPrice());
        hd.setText(R.id.tv_unrealized_profit_loss_value,item.getUnrealisedPnl());
        hd.setText(R.id.tv_realized_profit_loss_value,item.getRealisedPnl());
        hd.setText(R.id.tv_usage_bond_value,item.getPositionMargin());
        hd.setText(R.id.tv_return_percent_value,BigDecimalUtils.toPercentage(item.getRoe()));
        ((SubteactPositionLayout)hd.getView(R.id.tv_substrack_queue_value)).setLever(item.getWeight());


        if (item.getMarginMode().equals(Constants.MODE_FIXED)) {
            spanString = new SpannableString("+฿ " + item.getPositionMargin());
            spanString.setSpan(colorSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            hd.setText(R.id.tv_usage_bond_value,spanString);
            hd.getView(R.id.tv_usage_bond_value).setOnClickListener(v -> clickHoldPostionListener.updateMargin(item));
        } else {
            hd.setText(R.id.tv_usage_bond_value,item.getPositionMargin());
            hd.getView(R.id.tv_usage_bond_value).setOnClickListener(null);
        }

        hd.getView(R.id.iv_share).setOnClickListener(v -> {
            if (clickHoldPostionListener != null) {
                clickHoldPostionListener.clickShare(item);
            }
        });
        hd.getView(R.id.tv_close_hold).setOnClickListener(v -> {
            if (clickHoldPostionListener != null) {
                clickHoldPostionListener.clickClosePosition(item);
            }
        });
        hd.getView(R.id.tv_target_profit).setOnClickListener(v -> {
            if (clickHoldPostionListener != null) {
                clickHoldPostionListener.clickTargetProfit(item, 0);
            }
        });
        hd.getView(R.id.tv_stop_loss).setOnClickListener(v -> {
            if (clickHoldPostionListener != null) {
                clickHoldPostionListener.clickStopLoss(item, 1);
            }
        });

    }




    public interface ClickHoldPostionListener {
        void clickShare(ContractPositionListModel.DataBean item);

        void clickTargetProfit(ContractPositionListModel.DataBean item, int planType);

        void clickStopLoss(ContractPositionListModel.DataBean item, int planType);

        void clickClosePosition(ContractPositionListModel.DataBean item);

        /**
         * 修改保证金
         */
        void updateMargin(ContractPositionListModel.DataBean data);
    }
}
