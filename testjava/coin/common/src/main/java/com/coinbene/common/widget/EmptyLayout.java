package com.coinbene.common.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.coinbene.common.R;

/**
 * ding
 * 2019-07-15
 * com.coinbene.common.widget
 */
public class EmptyLayout extends LinearLayout {
    public EmptyLayout(Context context) {
        super(context);
        init();
    }

    public void init() {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.common_empty_layout, null);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(view,layoutParams);
    }

}
