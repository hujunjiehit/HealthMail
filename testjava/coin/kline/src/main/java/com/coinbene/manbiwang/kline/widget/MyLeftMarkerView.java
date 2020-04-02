package com.coinbene.manbiwang.kline.widget;

import android.content.Context;
import android.widget.TextView;

import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.github.mikephil.coinbene.components.MarkerView;
import com.github.mikephil.coinbene.data.Entry;
import com.github.mikephil.coinbene.highlight.Highlight;

import java.text.DecimalFormat;

/**
 * Created by loro on 2017/2/8.
 */
public class MyLeftMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView markerTv;
    private DecimalFormat mFormat;
    private KLineBean kLineBean;

    public MyLeftMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mFormat = new DecimalFormat("#0.00");
        markerTv = (TextView) findViewById(R.id.marker_tv);
        markerTv.setTextSize(10);
    }

    public void setData(KLineBean kLineBean) {
        this.kLineBean = kLineBean;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (kLineBean == null) {
            return;
        }
        markerTv.setText(BigDecimalUtils.parseENum(kLineBean.close));
    }
}
