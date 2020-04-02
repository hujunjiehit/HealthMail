package com.coinbene.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coinbene.common.R;
import com.coinbene.manbiwang.model.http.CheckVersionResponse;



public class UpdateDialog extends Dialog {

    private UpdateDialogClickListener clickLisenter;
    private String desStr, downUrl;
    private boolean isForceUpdate;
    private boolean notNeedForce;//关于页面不需要强制升级


    public void setClickLisenter(UpdateDialogClickListener clickLisenter) {
        this.clickLisenter = clickLisenter;
    }

    public UpdateDialog(Context context) {
        this(context, R.style.CoinBene_BottomSheet);
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.common_dialog_update);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.onCancel();
            }
        });

        Button okBtn = findViewById(R.id.btn_update);
        okBtn.setOnClickListener(v -> {
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.goToUpdate(downUrl, isForceUpdate);
            }
        });
        TextView messageTv = (TextView) findViewById(R.id.tv_message);
        if (!TextUtils.isEmpty(desStr)) {
            messageTv.setText(desStr);
        }
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (isForceUpdate) {
            findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        }
    }

    public void notNeedForceUpdate(boolean not_needForceUpdate) {
        notNeedForce = not_needForceUpdate;
    }

    public void setData(CheckVersionResponse.DataBean dataBean) {
        if (dataBean == null) {
            return;
        }
        desStr = dataBean.getDes();
        downUrl = dataBean.getDownUrl();
        if (notNeedForce) {
            isForceUpdate = false;
        } else {
            isForceUpdate = dataBean.isForcedUpdate();
        }
    }

    public interface UpdateDialogClickListener {
        void onCancel();

        void goToUpdate(String downUrl, boolean isForceUpdate);
    }
}
