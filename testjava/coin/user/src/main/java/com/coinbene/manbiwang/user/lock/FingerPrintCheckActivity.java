package com.coinbene.manbiwang.user.lock;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * 指纹解锁页面
 */
@Route(path = RouteHub.User.fingerprintCheckActivity)
public class FingerPrintCheckActivity extends CoinbeneBaseActivity {
    @BindView(R2.id.pwd_login_tv)
    TextView pwdLoginTv;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.finger_icon_img)
    ImageView fingerIconImg;

    private Unbinder mUnbinder;
    private FingerCheckDialog fingerDialog;
    private FingerprintIdentify mFingerprintIdentify;
    private boolean isDeviceLocked;

    private boolean isFirst = true;

    @Autowired
    int index;

    @Autowired
    String routeUrl;

    @Override
    public int initLayout() {
        return R.layout.activity_fingerprint_check;
    }

    @Override
    public void initView() {
        ARouter.getInstance().inject(this);

        setSwipeBackEnable(false);//禁止滑动，否则出现问题
        isFirst = true;

        titleView.setText("");
        backView.setVisibility(View.GONE);
        backView.setOnClickListener(null);
        fingerIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFingerprintIdentify.isRegisteredFingerprint()) {
                    ToastUtil.show(R.string.finger_not_enable);
                    return;
                }
                if (!mFingerprintIdentify.isFingerprintEnable()) {
                    ToastUtil.show(R.string.finger_check_fail_try_again);
                    return;
                }
                fingerDialog = new FingerCheckDialog(v.getContext());
                fingerDialog.setCanceledOnTouchOutside(false);
                fingerDialog.setFingerListener(fingerListener);
                fingerDialog.show();
            }
        });
        pwdLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除用户信息，但是手势密码不清
                CommonUtil.exitLoginClearData();

                ARouter.getInstance().build(RouteHub.
                        User.loginActivity)
                        .withBoolean("forceQuit", true)
                        .withInt("tabIndex", index)
                        .navigation(v.getContext());

                finish();
            }
        });
        mFingerprintIdentify = new FingerprintIdentify(CBRepository.getContext());
        mFingerprintIdentify.init();
        fingerIconImg.performClick();//进入当前页面时，显示指纹对话框
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return false;
    }

    private FingerCheckDialog.FingerListener fingerListener = new FingerCheckDialog.FingerListener() {
        @Override
        public void verifySuccess() {
            UserInfoController.getInstance().resetPwdErrorCount();
            if (mFingerprintIdentify != null) {
                mFingerprintIdentify.cancelIdentify();
            }
            isDeviceLocked = false;
            onSuccessFinish();
        }

        @Override
        public void verifyFail(boolean deviceLocked) {
            isDeviceLocked = deviceLocked;
            if (mFingerprintIdentify != null) {
                mFingerprintIdentify.cancelIdentify();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            return;
        } else {
            if (mFingerprintIdentify != null) {
                mFingerprintIdentify.resumeIdentify();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onSuccessFinish() {
        UserInfoController.getInstance().setLock(false);

        //登陆状态
        AssetManager.getInstance().setLogin(true);

        ServiceRepo.getAppService().onUserLoginSuccess(index);

        DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appUnlock, "");

        finish();
        if (!TextUtils.isEmpty(routeUrl)) {
            UIBusService.getInstance().openUri(this, routeUrl, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
            mFingerprintIdentify = null;
        }
        if (fingerListener != null) {
            fingerListener = null;
        }
        if (fingerDialog != null && fingerDialog.isShowing()) {
            fingerDialog.dismiss();
        }
        if (fingerDialog != null) {
            fingerDialog.setFingerListener(null);
            fingerDialog = null;
        }
    }
}
