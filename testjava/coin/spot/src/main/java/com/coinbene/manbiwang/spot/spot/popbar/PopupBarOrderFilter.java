package com.coinbene.manbiwang.spot.spot.popbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.widget.SupportPopupWindow;
import com.coinbene.manbiwang.spot.R;
import com.google.android.material.tabs.TabLayout;
import com.lzy.okgo.OkGo;


/**
 * 交易页面，选择币种的页面
 * 这个页面废弃不用,当做例子参考使用
 * @deprecated
 * @author xiangdong.meng
 */
public class PopupBarOrderFilter implements View.OnClickListener {
    private View mAnchor;
    private SupportPopupWindow mPopupWindow;
    private Context mContext;
    private PopupBarListener actionListener;
    private int index = 0;
    boolean mDismissed = false;

    public PopupBarOrderFilter(View anchor) {
        mAnchor = anchor;
        mContext = mAnchor.getContext();
        mPopupWindow = new SupportPopupWindow(anchor);
        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);// true，才能popupWindow消失

        LinearLayout mContentView = new LinearLayout(mContext);
        mContentView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mContentView.setBackgroundColor(mContext.getResources().getColor(
                android.R.color.transparent));
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setContentView(mContentView);
        mPopupWindow.setOnDismissListener(() -> dismissQuickActionBar());
    }

    private Context context;

    public void showBelowAnchor(Context context) {
        this.context = context;
        int yoff = DensityUtil.dip2px( 15);
        mPopupWindow.showAsDropDown(mAnchor, 0, yoff);
        //    在某个控件下方弹出
//        mPopupWindow.showAtLocation(mAnchor,Gravity.LEFT,0, StatusBarCompat.getStatusBarHeight(context)+mAnchor.getHeight());
        initView();
    }

    private FrameLayout layout;
    private TabLayout mTabLayout;
    private LinearLayout mPanel;
    private View mBg;
    private static final int BG_VIEW_ID = 10;
    private static final int ALPHA_DURATION = 300;

    private void initView() {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = ((Activity) context).getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
        FrameLayout parent = new FrameLayout(context);
        parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mBg = new View(context);
        mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mBg.setBackgroundColor(Color.argb(105, 0, 0, 0));
        mBg.setId(BG_VIEW_ID);
        mBg.setOnClickListener(this);

        mPanel = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.spot_popup_pair_layout, null);

        layout = mPanel.findViewById(R.id.body_layout);
        mTabLayout = mPanel.findViewById(R.id.tab_layout_trade);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, context.getResources()
                .getDisplayMetrics().heightPixels / 10 * 4);
        params.gravity = Gravity.TOP;
        mPanel.setLayoutParams(params);

        parent.addView(mBg);
        parent.addView(mPanel);
        ((ViewGroup) mPopupWindow.getContentView()).addView(parent);
    }

    public void dismissQuickActionBar() {
        if (mDismissed) {
            return;
        }
        mDismissed = true;

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
            OkGo.getInstance().cancelTag(this);
        }
        if (mPanel != null) {
            mPanel = null;
        }
        // 点击outSide，不走onDismiss，写到这里，页面消失必走。
        if (actionListener != null)
            actionListener.onDismiss();
    }

    void onItemClick(Object object) {
        if (actionListener != null)
            actionListener.onItemClick(object);
    }

    public void onDismiss() {
        dismissQuickActionBar();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == BG_VIEW_ID) {
            dismissQuickActionBar();
        }
    }

    public interface PopupBarListener {

        // 页面消失
        void onDismiss();

        void onItemClick(Object object);

    }
}