package com.coinbene.common.widget;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 */
public class ContentSpan extends ClickableSpan {
    private int color = 0;
    private View.OnClickListener onClickListener;


    public ContentSpan(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ContentSpan(View.OnClickListener onClickListener, int color) {
        this.color = color;
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View widget) {
        if (this.onClickListener != null) {
            this.onClickListener.onClick(widget);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        if (color != 0) {
            ds.setColor(color);
        } else {
            ds.setARGB(255, 0, 0, 0);
        }
    }

    @Override
    public CharacterStyle getUnderlying() {
        return super.getUnderlying();
    }
}
