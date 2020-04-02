package com.coinbene.manbiwang.record.optionrecord.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseAdapter;
import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.OptionsRecordMode;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.manbiwang.record.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OptionRecordAdapter extends BaseAdapter<OptionsRecordMode.DataBean.ListBean> {

    private SimpleDateFormat dateFormat;
    private final Date date;
    private final StringBuffer stringBuffer;

    public OptionRecordAdapter() {
        super(R.layout.record_item_option_record);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        date = new Date();
        stringBuffer = new StringBuffer();
    }

    @Override
    protected void convert(BaseViewHolder holder, int position, OptionsRecordMode.DataBean.ListBean item) {

        date.setTime(item.getSettlementTime());

        if (!TextUtils.isEmpty(stringBuffer.toString())) {
            stringBuffer.setLength(0);
        }

        stringBuffer.append(Constants.BASE_IMG_URL);
        stringBuffer.append(item.getAsset());
        stringBuffer.append(".png");

        //账户名称
        holder.setText(R.id.option_record_name, item.getAsset());

        //时间
        holder.setText(R.id.option_record_time, dateFormat.format(date));

        //标的资产
        holder.setText(R.id.option_record_underlying, item.getUnderlying());

        //投资
        holder.setText(R.id.option_record_investment, item.getPrice());

        //收益
        holder.setText(R.id.option_record_profit, item.getProfit());

        //价值
        holder.setText(R.id.option_record_value, item.getFinalValue());

        //Icon
        ImageView view = holder.getView(R.id.option_coin_icon);
        GlideUtils.loadImageViewLoad(mContext, stringBuffer.toString(), view, R.drawable.coin_default_icon, R.drawable.coin_default_icon);

    }

}
