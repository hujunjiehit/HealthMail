package com.coinbene.manbiwang.kline.widget;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.coinbene.manbiwang.kline.R;
import com.github.mikephil.coinbene.components.MarkerView;
import com.github.mikephil.coinbene.data.Entry;
import com.github.mikephil.coinbene.highlight.Highlight;

/**
 * Created by loro on 2017/2/8.
 */
public class MyHMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private ImageView markerTv;
    public MyHMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        markerTv = (ImageView) findViewById(R.id.marker_tv);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
    }

    public void setTvWidth(int width){
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) markerTv.getLayoutParams();
        params.width=width;
        markerTv.setLayoutParams(params);
    }
}
