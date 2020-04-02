package com.coinbene.manbiwang.contract.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 减仓队列layout
 */
public class SubteactPositionLayout extends LinearLayout {
    @BindView(R2.id.iv_lever1)
    ImageView ivLever1;
    @BindView(R2.id.iv_lever2)
    ImageView ivLever2;
    @BindView(R2.id.iv_lever3)
    ImageView ivLever3;
    @BindView(R2.id.iv_lever4)
    ImageView ivLever4;
    @BindView(R2.id.iv_lever5)
    ImageView ivLever5;

    private Drawable backDrawable, defautDrawable;

    public SubteactPositionLayout(Context context) {
        super(context);
        inflaterLayout(context);
    }

    private void inflaterLayout(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.layout_subtract_position_queue, null);
        addView(myView);
    }

    public SubteactPositionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflaterLayout(context);
    }

    public SubteactPositionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflaterLayout(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        defautDrawable = getResources().getDrawable(R.drawable.bg_substrack_positon);
        backDrawable = getResources().getDrawable(R.drawable.btn_blue_center_shape);
    }

    /**
     * @param lever 持仓队列
     */
    public void setLever(int lever) {
        if (lever == 0) {
            ivLever1.setBackground(defautDrawable);
            ivLever2.setBackground(defautDrawable);
            ivLever3.setBackground(defautDrawable);
            ivLever4.setBackground(defautDrawable);
            ivLever5.setBackground(defautDrawable);
        } else if (lever < 20) {
            ivLever1.setBackground(backDrawable);
            ivLever2.setBackground(defautDrawable);
            ivLever3.setBackground(defautDrawable);
            ivLever4.setBackground(defautDrawable);
            ivLever5.setBackground(defautDrawable);
        } else if (lever < 40) {
            ivLever1.setBackground(backDrawable);
            ivLever2.setBackground(backDrawable);
            ivLever3.setBackground(defautDrawable);
            ivLever4.setBackground(defautDrawable);
            ivLever5.setBackground(defautDrawable);
        } else if (lever < 60) {
            ivLever1.setBackground(backDrawable);
            ivLever2.setBackground(backDrawable);
            ivLever3.setBackground(backDrawable);
            ivLever4.setBackground(defautDrawable);
            ivLever5.setBackground(defautDrawable);
        } else if (lever < 80) {
            ivLever1.setBackground(backDrawable);
            ivLever2.setBackground(backDrawable);
            ivLever3.setBackground(backDrawable);
            ivLever4.setBackground(backDrawable);
            ivLever5.setBackground(defautDrawable);
        } else {
            ivLever1.setBackground(backDrawable);
            ivLever2.setBackground(backDrawable);
            ivLever3.setBackground(backDrawable);
            ivLever4.setBackground(backDrawable);
            ivLever5.setBackground(backDrawable);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
