package com.coinbene.manbiwang.spot.otc.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.widget.SupportPopupWindow;
import com.coinbene.manbiwang.spot.R;


/**
 * @author xiangdong.meng
 */
public class OtcPricePopWindowNew implements View.OnClickListener {
    private View mAnchor;
    private SupportPopupWindow mPopupWindow;
    boolean mDismissed = false;
    private LinearLayout mPanel;
    private static final int BG_VIEW_ID = 10;
    private OtcSelectListener selectTypeListener;
    private View mBg;

    private TextView tv_all, tv_five_down, tv_five_up, tv_ten, tv_tween;
    private int[] priceRange = Constants.PRICE_TYPE_ALL;

    public OtcPricePopWindowNew(View anchor) {
        mAnchor = anchor;
        mPopupWindow = new SupportPopupWindow(anchor);
        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);// true，才能popupWindow消失

        LinearLayout mContentView = new LinearLayout(mAnchor.getContext());
        mContentView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mContentView.setBackgroundColor(mAnchor.getContext().getResources().getColor(
                android.R.color.transparent));

        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);

        mPopupWindow.setContentView(mContentView);
        mPopupWindow.setOnDismissListener(() -> dismissQuickActionBar());
        initView();
    }

    private void initView() {
        InputMethodManager imm = (InputMethodManager) mAnchor.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (mAnchor != null) {
                imm.hideSoftInputFromWindow(mAnchor.getWindowToken(), 0);
            }
        }
        FrameLayout parent = new FrameLayout(mAnchor.getContext());
        parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        mBg = new View(mAnchor.getContext());
        mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mBg.setBackgroundColor(Color.argb(105, 0, 0, 0));
        mBg.setId(BG_VIEW_ID);

        mPanel = (LinearLayout) LayoutInflater.from(mAnchor.getContext()).inflate(
                R.layout.spot_otc_price_popview, null);

        tv_all = mPanel.findViewById(R.id.tv_all);
        tv_all.setOnClickListener(this);
        tv_five_down = mPanel.findViewById(R.id.tv_five_down);
        tv_five_down.setOnClickListener(this);
        tv_five_up = mPanel.findViewById(R.id.tv_five_up);
        tv_five_up.setOnClickListener(this);
        tv_ten = mPanel.findViewById(R.id.tv_ten);
        tv_ten.setOnClickListener(this);
        tv_tween = mPanel.findViewById(R.id.tv_tween);
        tv_tween.setOnClickListener(this);

        parent.addView(mBg);
        parent.addView(mPanel);

        mBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismiss();
            }
        });

        ((ViewGroup) mPopupWindow.getContentView()).addView(parent);
    }

    public void showBelowAnchor(Context context) {
        mDismissed = false;
        int yoff = DensityUtil.dip2px( 0);
        mPopupWindow.showAsDropDown(mAnchor, 0, yoff);
    }

    @Override
    public void onClick(View v) {
        setDefoatColor();
        String text = ((TextView) v).getText().toString();
        TextView textTxt = ((TextView) v);
        if (v.getId() == R.id.tv_all) {
            priceRange = Constants.PRICE_TYPE_ALL;
        } else if (v.getId() == R.id.tv_five_down) {
            priceRange = Constants.PRICE_TYPE_UNDER_TEN;
        } else if (v.getId() == R.id.tv_five_up) {
            priceRange = Constants.PRICE_TYPE_UP_FIFTY;
        } else if (v.getId() == R.id.tv_ten) {
            priceRange = Constants.PAY_TYPE_UP_ONE_HUNDRED_THOUSAND;
        } else if (v.getId() == R.id.tv_tween) {
            priceRange = Constants.PAY_TYPE_UP_TWO_HUNDRED_THOUSAND;
        }
        if (selectTypeListener != null) {
            textTxt.setTextColor(textTxt.getResources().getColor(R.color.res_blue));
        }
        onDismiss();
    }

    private void setDefoatColor() {
        tv_all.setTextColor(mAnchor.getContext().getResources().getColor(R.color.res_textColor_3));
        tv_five_down.setTextColor(mAnchor.getContext().getResources().getColor(R.color.res_textColor_3));
        tv_five_up.setTextColor(mAnchor.getContext().getResources().getColor(R.color.res_textColor_3));
        tv_ten.setTextColor(mAnchor.getContext().getResources().getColor(R.color.res_textColor_3));
        tv_tween.setTextColor(mAnchor.getContext().getResources().getColor(R.color.res_textColor_3));
    }

    public void setTypeChangeListener(OtcSelectListener selectTimeListener) {
        this.selectTypeListener = selectTimeListener;
    }

    public void onDismiss() {
        dismissQuickActionBar();
    }

    private void dismissQuickActionBar() {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public SupportPopupWindow getPopupWindow() {
        return mPopupWindow;
    }
}