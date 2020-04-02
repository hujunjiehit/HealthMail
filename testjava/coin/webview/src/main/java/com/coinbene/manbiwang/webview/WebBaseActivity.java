package com.coinbene.manbiwang.webview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.widget.CoinbeneShareParam;
import com.coinbene.common.widget.CustomDialog;
import com.coinbene.common.widget.ShareDialog;

public abstract class WebBaseActivity extends CoinbeneBaseActivity implements View.OnClickListener{

    private String actionUrl;

    private ProgressDialog progress;
    private boolean displayProgress = false;
    private ShareDialog shareDialog;

    private View leftView;

    private View rightView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);  //腾讯x5内核。这个对宿主没什么影响，建议声明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        initProgress(this);
    }

    View.OnClickListener onRightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!TextUtils.isEmpty(actionUrl)) {
                UIBusService.getInstance().openUri(WebBaseActivity.this, actionUrl, null);
            }
        }
    };

    @Override
    public void setListener() {
        if (mTopBar != null) {
            mTopBar.removeAllLeftViews();
            leftView = mTopBar.addLeftImageButton(R.drawable.icon_back_close, R.id.res_title_left_image);
            leftView.setOnClickListener(v -> onBack());
        }
    }


    @Override
    public boolean needLock() {
        return false;
    }


    protected void setWebTitle(@StringRes int resId) {
        if (mTopBar != null) {
            mTopBar.setTitle(resId);
        }
    }

    protected void setWebTitle(String title) {
        if (mTopBar != null) {
            mTopBar.setTitle(title);
        }
    }


    protected void setRightText(@StringRes int resId) {
        setRightText(getResources().getString(resId));
    }

    protected void setRightText(String text) {
        if (mTopBar != null) {
            if (rightView != null) {
                mTopBar.removeAllRightViews();
            }
            rightView = mTopBar.addRightTextButton(text, R.id.res_title_right_text);
            rightView.setOnClickListener(onRightClick);
        }
    }

    protected void setRightImage(@DrawableRes int drawableId) {
        if (mTopBar != null) {
            rightView = mTopBar.addRightImageButton(drawableId, R.id.res_title_right_layout);
            rightView.setOnClickListener(onRightClick);
        }
    }

    protected void setRightImage() {
        setRightImage(R.drawable.icon_share_grey);
    }

    protected void setActionUrl(String url) {
        actionUrl = url;
    }

    protected void hideTitleBar(){
        if (mTopBar!=null){
            mTopBar.setVisibility(View.GONE);
        }

    }
    /**
     * @param activity 初始化Progress
     */
    private void initProgress(@NonNull Activity activity) {
        if (progress == null) {
            progress = new CustomDialog(activity, com.coinbene.common.R.style.CustomDialog);
            progress.setCancelable(displayProgress);
            progress.setCanceledOnTouchOutside(displayProgress);
        }
    }

    /**
     * 显示Loading
     */
    protected void displayProgress() {
        if (progress != null && !isDestroyed() && !progress.isShowing()) {
            progress.show();
        }
    }


    /**
     * 隐藏Loading
     */
    protected void hideProgress() {
        if (progress != null && !isDestroyed() && progress.isShowing()) {
            progress.dismiss();
        }
    }

    protected void showShareDialog(CoinbeneShareParam param) {
        if (shareDialog == null) {
            shareDialog = new ShareDialog(this);
        }
        shareDialog.setParam(param);
        shareDialog.show();
    }
}
