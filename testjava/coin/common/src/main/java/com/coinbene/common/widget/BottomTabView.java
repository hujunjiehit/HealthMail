package com.coinbene.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.R;
import com.coinbene.common.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class BottomTabView extends RelativeLayout {
    @BindView(R2.id.item_imgview)
    ImageView mIconIv;
    @BindView(R2.id.item_label_txt)
    TextView mTextTv;

    public BottomTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setText(String textRes) {
        mTextTv.setText(textRes);
    }
    public void setTextColor(int color){
        mTextTv.setTextColor(color);
    }

    public void setIcon(int iconRes) {
        mIconIv.setImageResource(iconRes);
    }

}
