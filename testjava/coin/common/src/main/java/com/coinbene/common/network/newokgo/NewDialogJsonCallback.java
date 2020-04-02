package com.coinbene.common.network.newokgo;

import android.app.Activity;
import android.app.ProgressDialog;

import com.coinbene.common.R;
import com.coinbene.common.widget.CustomDialog;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

/**
 * Created by mengxiangdong on 2018/4/6.
 */

public abstract class NewDialogJsonCallback<T> extends NewJsonSubCallBack<T> {
    private ProgressDialog progress;
    private boolean needThemeDialog;

    public NewDialogJsonCallback(Activity activity) {
        super();
        initDialog(activity);
    }

    public NewDialogJsonCallback(Activity activity, boolean needThemeDialog) {
        super();
        setDialogTheme(needThemeDialog);
        initDialog(activity);
    }

    public void setDialogTheme(boolean need) {
        this.needThemeDialog = need;
    }

    private void initDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

//        if (needThemeDialog) {
//            progress = new CustomDialog(activity, R.style.CustomDialog);
//        } else {
//            progress = new ProgressDialog(activity);
//            progress.setMessage(activity.getResources().getString(R.string.dialog_loading));
//        }
        progress = new CustomDialog(activity, R.style.CustomDialog);
    }

    @Override
    public void onStart(Request request) {
        super.onStart(request);
        if (progress != null && !progress.isShowing()) {
            progress.show();
            progress.setCancelable(getCancelAble());
        }
    }

    public boolean getCancelAble() {
        return true;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public abstract void onSuc(Response<T> response);

    public abstract void onE(Response<T> response);

}
