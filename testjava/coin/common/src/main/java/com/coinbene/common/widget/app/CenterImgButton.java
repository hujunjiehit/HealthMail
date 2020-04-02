package com.coinbene.common.widget.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

public class CenterImgButton extends AppCompatButton {

    private float floattextWidth;
    private int intdrawablePadding, drawableWidth;

    public CenterImgButton(Context context) {
        super(context, null);
    }

    public CenterImgButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            if (drawableLeft != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                drawableWidth = drawableLeft.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
