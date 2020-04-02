package com.coinbene.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.coinbene.common.R;
import com.coinbene.common.utils.DensityUtil;

public class ItemDivider extends RecyclerView.ItemDecoration {

    private Paint paint;
    private int height;
    private Context mContext;

    public ItemDivider(Context context) {
        mContext = context;
        init();
    }

    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(mContext.getResources().getColor(R.color.res_item_divider));
        height = DensityUtil.dip2px( 0.4f);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);

        if (position == state.getItemCount() - 1) {
            outRect.set(height, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, height);
        }


    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {

            View child = parent.getChildAt(i);

            float left = parent.getPaddingLeft();

            float top = child.getBottom();

            float right = parent.getRight() - parent.getPaddingRight();

            float bottom = child.getBottom() + height;

            c.drawRect(left, top, right, bottom, paint);

        }

    }
}
