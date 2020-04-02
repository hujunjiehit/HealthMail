package com.coinbene.manbiwang.kline.widget;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.github.mikephil.coinbene.components.MarkerView;
import com.github.mikephil.coinbene.data.Entry;
import com.github.mikephil.coinbene.highlight.Highlight;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by loro on 2017/2/8.
 */
public class MyBottomMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView markerTv;
    private String timeStr;
    SimpleDateFormat sdf;

    public MyBottomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        markerTv = (TextView) findViewById(R.id.marker_tv);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public void setData(KLineBean kLineBean) {
        if (kLineBean == null || TextUtils.isEmpty(kLineBean.time)) {
            timeStr = "";
            return;
        }
        try {
            Date date = new Date(Long.valueOf(kLineBean.time));
            timeStr = sdf.format(date);
        } catch (Exception ex) {
            timeStr = "";
        }
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerTv.setText(timeStr);
    }

}
