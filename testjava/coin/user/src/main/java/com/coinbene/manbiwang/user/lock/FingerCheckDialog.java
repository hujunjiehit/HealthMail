package com.coinbene.manbiwang.user.lock;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

public class FingerCheckDialog extends Dialog {
    private int MAX_RETRY_COUNT = 3;
    private TextView fingerLabelTv;
    private TextView cancelTv;
    private FingerprintIdentify mFingerprintIdentify;
    private boolean isVerifySuccess = false;
    private boolean deviceLocked = false;

    private BaseFingerprint.IdentifyListener fingerprintIdentifyListener;

    public FingerCheckDialog(@NonNull Context context) {
        super(context);
        init();
        mFingerprintIdentify = new FingerprintIdentify(CBRepository.getContext());
        mFingerprintIdentify.init();
    }

    private void init() {
        float margin = 15;
        float marginTop = 15;
        // 在构造方法里, 传入主题
        Window window = getWindow();
//        window.getDecorView().setPadding((int) margin, (int) marginTop, (int) margin, (int) marginTop);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity = Gravity.CENTER;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_finger_dialog_layout);
        initView();
    }

    private void initView() {
        fingerLabelTv = (TextView) this.findViewById(R.id.finger_label_tv);
        cancelTv = (TextView) this.findViewById(R.id.cancel_tv);
        cancelTv.setOnClickListener(cancelClick);
        fingerprintIdentifyListener = new BaseFingerprint.IdentifyListener() {
            @Override
            public void onSucceed() {
                // succeed, release hardware automatically
                isVerifySuccess = true;
                FingerCheckDialog.this.dismiss();
            }

            @Override
            public void onNotMatch(int availableTimes) {
                // not match, try again automatically
                fingerLabelTv.setText(R.string.finger_fail_retry);
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
                // failed, release hardware automatically
                // isDeviceLocked: is device locked temporarily
                deviceLocked = isDeviceLocked;
                if (deviceLocked) {
                    ToastUtil.show(R.string.finger_check_fail_more_than_5);
                } else {
                    ToastUtil.show(R.string.finger_verify_fail);
                }
                FingerCheckDialog.this.dismiss();
            }

            @Override
            public void onStartFailedByDeviceLocked() {
                // the first start failed because the device was locked temporarily
                ToastUtil.show(R.string.finger_check_fail_try_again);
                FingerCheckDialog.this.dismiss();
            }
        };
    }

    @Override
    public void show() {
        super.show();
        doFingerCheckStart();
    }

    private void doFingerCheckStart() {
        mFingerprintIdentify.startIdentify(MAX_RETRY_COUNT, fingerprintIdentifyListener);
    }

    private View.OnClickListener cancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FingerCheckDialog.this.dismiss();
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            if (isVerifySuccess) {
                listener.verifySuccess();
            } else {
                listener.verifyFail(deviceLocked);
            }
        }
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
            mFingerprintIdentify = null;
        }
        if (fingerprintIdentifyListener != null) {
            fingerprintIdentifyListener = null;
        }
    }

    private FingerListener listener;

    public void setFingerListener(FingerListener listener) {
        this.listener = listener;
    }

    interface FingerListener {
        void verifySuccess();

        void verifyFail(boolean deviceLocked);
    }

}
