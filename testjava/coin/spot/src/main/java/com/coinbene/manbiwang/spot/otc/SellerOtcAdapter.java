package com.coinbene.manbiwang.spot.otc;

import com.coinbene.common.base.BaseAdapter;
import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.SellerAdModel;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.spot.R;

/**
 * @author ding
 * 商家广告管理适配器
 */
public class SellerOtcAdapter extends BaseAdapter<SellerAdModel.DataBean.ListBean> {

    static final int TYPE_BUY = 0;
    static final int TYPE_SELL = 1;

    public SellerOtcAdapter() {
        super(R.layout.spot_item_seller_order);
    }

    @Override
    protected void convert(BaseViewHolder holder, int position, SellerAdModel.DataBean.ListBean item) {


        holder.setText(R.id.ad_coin_type, item.getAsset());
        holder.setText(R.id.ad_order_status, setOrderStatus(item.getReleaseType(), holder));
        holder.setText(R.id.ad_price, item.getPrice());
        holder.setText(R.id.ad_limit, String.format("%s%s%s",item.getMinOrder(),"-",item.getMaxOrder()));
        holder.setText(R.id.ad_vol, item.getStock());
        holder.setText(R.id.ad_type, setAdType(item.getAdType(), holder));
        holder.setText(R.id.textView39, mContext.getResources().getString(R.string.order_unit_price)+"/"+item.getCurrency());
        holder.setText(R.id.textView40, mContext.getResources().getString(R.string.limit)+"/"+item.getCurrency());



        holder.itemView.setOnClickListener(v -> {
            AdDetailActivity.startMe(mContext, item);

        });
    }


    /**
     * @param status
     * @return 商家广告状态
     */
    private String setOrderStatus(String status, BaseViewHolder holder) {
        if (status.equals("1")) {
            holder.setTextColor(R.id.ad_order_status, "#3b7bfd");
            return mContext.getString(R.string.ad_status_running);
        } else {
            holder.setTextColor(R.id.ad_order_status,R.color.res_textColor_1);
            return mContext.getString(R.string.ad_status_invalid);
        }
    }

    /**
     * @param type
     * @return 返回买入/卖出类型
     */
    private String setAdType(int type, BaseViewHolder holder) {
        if (type == 1) {
            holder.setTextColor(R.id.ad_type, SwitchUtils.isRedRise()?
                    R.color.res_red:
                    R.color.res_green);
            return mContext.getString(R.string.histr_filter_direction_buy);
        } else {
            holder.setTextColor(R.id.ad_type, SwitchUtils.isRedRise()?
                    R.color.res_green:
                    R.color.res_red);
            return mContext.getString(R.string.histr_filter_direction_sell);
        }

    }


}
