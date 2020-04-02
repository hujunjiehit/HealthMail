package com.coinbene.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public class SupportPopupWindow extends PopupWindow {

    public SupportPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public SupportPopupWindow(View contentView) {
        super(contentView);
    }

    public SupportPopupWindow(Context context) {
        super(context);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom + QMUIDisplayHelper.getStatusBarHeight(anchor.getContext());
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom - yoff +
            QMUIDisplayHelper.getStatusBarHeight(anchor.getContext());
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

}
