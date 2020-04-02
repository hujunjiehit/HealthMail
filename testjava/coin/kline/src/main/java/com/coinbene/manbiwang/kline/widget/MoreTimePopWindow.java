package com.coinbene.manbiwang.kline.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coinbene.common.utils.DensityUtil;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.listener.SelectTimeListener;

/**
 * Created by mxd
 */
public class MoreTimePopWindow implements View.OnClickListener {
    private PopupWindow mPopupWindow;
    private View mAnchor;
    private View mContentView;
    private SelectTimeListener selectTimeListener;

    public MoreTimePopWindow(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.more_time_popview, null);
        mPopupWindow = new PopupWindow(mContentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
    }

    public void show(View anchor) {
        mAnchor = anchor;

        int xoff = DensityUtil.dip2px( 0);
        int yoff = DensityUtil.dip2px(0);

        mPopupWindow.showAsDropDown(mAnchor, xoff, yoff);
        mContentView.findViewById(R.id.one_min_tv).setOnClickListener(this);
        mContentView.findViewById(R.id.five_min_tv).setOnClickListener(this);
        mContentView.findViewById(R.id.thirty_min_tv).setOnClickListener(this);
        mContentView.findViewById(R.id.one_week_tv).setOnClickListener(this);
        mContentView.findViewById(R.id.one_month_tv).setOnClickListener(this);
    }

    public PopupWindow getmPopupWindow() {
        return mPopupWindow;
    }

    public void setmPopupWindow(PopupWindow mPopupWindow) {
        this.mPopupWindow = mPopupWindow;
    }

    @Override
    public void onClick(View v) {
        String text = ((TextView) v).getText().toString();
        if (selectTimeListener != null) {
            selectTimeListener.selectTimeType(v.getId(), text);
        }
        mPopupWindow.dismiss();
    }

    public void setTypeChangeListener(SelectTimeListener selectTimeListener) {
        this.selectTimeListener = selectTimeListener;
    }
}
