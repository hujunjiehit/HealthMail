package com.coinbene.common.widget.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coinbene.common.R;

/**
 * @author huyong
 */
public class CopyPopWindow extends PopupWindow implements View.OnClickListener {
    private int popupHeight;
    private int popupWidth;
    private TextView tvCopy;
    public onClickPopLisener onClickLisener;
    private String copyStr;

    public void setCopyStr(String copyStr) {
        this.copyStr = copyStr;
    }

    public CopyPopWindow(Context context) {
        initPopupWindow();
        View view = View.inflate(context, R.layout.commom_pop_copy, null);
        setContentView(view);
        //设置popwindow的宽高，这个数字是多少就设置多少dp，注意单位是dp
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();
        initView(view);
    }

    private void initView(View view) {
        tvCopy = view.findViewById(R.id.tv_copy);
        tvCopy.setOnClickListener(this);
    }

    private void initPopupWindow() {
//        setAnimationStyle(R.style.popwindowAnim);//设置动画
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 显示在view 上方popupWindow
     */
    public void showPopupWindow(View v) {
        if (!this.isShowing()) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            this.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        //自己设置点击事件。。。

        if (v.getId() == R.id.tv_copy) {
            if (onClickLisener != null) {
                onClickLisener.onPopClick(copyStr);
            }
        }
        dismiss();
    }

    public CopyPopWindow.onClickPopLisener getOnClickLisener() {
        return onClickLisener;
    }

    public void setOnClickPopLisener(CopyPopWindow.onClickPopLisener onClickLisener) {
        this.onClickLisener = onClickLisener;
    }

    public interface onClickPopLisener {
        public void onPopClick(String string);
    }
}