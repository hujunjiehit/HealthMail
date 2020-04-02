package com.coinbene.manbiwang.user.lock;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
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
import com.coinbene.manbiwang.user.safe.pattern.PatternHelper;
import com.coinbene.manbiwang.user.safe.pattern.RippleLockerHitCellView;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mpchart.com.coinbene.patternlocker.IHitCellView;
import mpchart.com.coinbene.patternlocker.OnPatternChangeListener;
import mpchart.com.coinbene.patternlocker.PatternLockerView;

/**
 * 出现该页面的情况，
 * 如果已经设置了指纹和手势，或者只设置了指纹，则 下次登录的时候，进入这个页面。
 * 如果设置了指纹，就弹出来，否则不弹出
 */
@Route(path = RouteHub.User.patternCheckActivity)
public class PatternCheckActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 111;
    public static final int TYPE_LOGIN_CHECK = 4;

    @BindView(R2.id.pattern_lock_view)
    PatternLockerView patternLockerView;
    @BindView(R2.id.text_msg)
    TextView textMsg;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.cancel_tv)
    TextView bottomBtnView;

    private PatternHelper patternHelper;
    private Unbinder mUnbinder;
    private Animation shake;

    private FingerCheckDialog fingerDialog;
    private FingerprintIdentify mFingerprintIdentify;

//    private boolean isFrom15Min;//如果是15分钟弹出的情况，点击返回，都要销毁到二级页面；否则，直接退出当前页面

//    public static boolean isLive_pattern = true;

    private Context context;

    @Autowired
    String routeUrl;

    @Autowired
    int index = Constants.TAB_INDEX_DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public int initLayout() {
        return R.layout.settings_activity_pattern_setting;
    }

    @Override
    public void initView() {
        ARouter.getInstance().inject(this);

        setSwipeBackEnable(false);//禁止滑动，否则出现问题
        mUnbinder = ButterKnife.bind(this);
        context = this;

        this.patternHelper = new PatternHelper();
        int errorCount = patternHelper.getErrorTimes();

        titleView.setText(getString(R.string.gesture_ac_title));
        backView.setVisibility(View.GONE);
        backView.setOnClickListener(null);
        shake = AnimationUtils.loadAnimation(this, R.anim.left_to_right_shake);

        titleView.setText(getString(R.string.gestrue_login_check_title));
        this.textMsg.setText(this.getString(R.string.gesture_login_check_tips));
        bottomBtnView.setText(getString(R.string.gestrue_login_label));
        bottomBtnView.setVisibility(View.VISIBLE);

        if (errorCount > 0) {
            this.textMsg.setText(patternHelper.getPwdErrorMsg(this));
            this.textMsg.setTextColor(getResources().getColor(R.color.res_red));
        }
        bottomBtnView.setOnClickListener(v -> {
            //清除用户信息，但是手势密码不清
            CommonUtil.exitLoginClearData();

            ARouter.getInstance().build(RouteHub.
                    User.loginActivity)
                    .withBoolean("forceQuit", true)
                    .withInt("tabIndex", index)
                    .navigation(v.getContext());

            finish();
        });

        final IHitCellView hitCellView = new RippleLockerHitCellView()
                .setHitColor(this.patternLockerView.getHitColor())
                .setErrorColor(this.patternLockerView.getErrorColor());

        this.patternLockerView.setHitCellView(hitCellView)
                .build();
        this.patternLockerView.setOnPatternChangedListener(changeListener);
        showFingerLoginCheckDialog();
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

    private OnPatternChangeListener changeListener = new OnPatternChangeListener() {
        @Override
        public void onStart(PatternLockerView view) {
        }

        @Override
        public void onChange(PatternLockerView view, List<Integer> hitList) {
        }

        @Override
        public void onComplete(PatternLockerView view, List<Integer> hitList) {
            boolean isError = !isPatternLoginCheck(hitList);
            view.updateStatus(isError);
            updateMsg();
            checkResultForLoginCheck();
        }

        @Override
        public void onClear(PatternLockerView view) {

        }
    };

    private boolean isPatternLoginCheck(List<Integer> hitList) {
        this.patternHelper.validateForCheck(hitList,this);
        return this.patternHelper.isOk();
    }

    private void updateMsg() {
        this.textMsg.setText(this.patternHelper.getMessage());
        this.textMsg.setTextColor(this.patternHelper.isOk() ?
                getResources().getColor(R.color.res_textColor_1) :
                getResources().getColor(R.color.res_red));
        if (!this.patternHelper.isOk()) {
            shakeView(this.textMsg);
        }
    }

    private void shakeView(TextView textMsg) {
        textMsg.startAnimation(shake);
    }

    private void checkResultForLoginCheck() {
        if (this.patternHelper.isFinish() && this.patternHelper.isOk()) {//解锁成功,设置页面关闭锁的状态
            onSuccessFinish();
        } else if (this.patternHelper.isFinish()) {//超过错误次数，跳转至登录页面，手势解锁关闭
            ToastUtil.show(R.string.gesture_login_check_error);
            UserInfoController.getInstance().clearGesturePwd();

            CommonUtil.exitLoginClearData();

            ARouter.getInstance().build(RouteHub.
                    User.loginActivity)
                    .withBoolean("forceQuit", true)
                    .withInt("tabIndex", index)
                    .navigation(this);

            finish();
        }
    }

    private void showFingerLoginCheckDialog() {
        //如果不是15分钟后，登录进入的该页面,返回
//        if (type != TYPE_LOGIN_CHECK) {
//            return;
//        }
        //如果没有设置过指纹，返回
        if (!UserInfoController.getInstance().isSetFingerPrint()) {
            return;
        }
        mFingerprintIdentify = new FingerprintIdentify(CBRepository.getContext());
        mFingerprintIdentify.init();
        //如果系统硬件不支持，返回
        if (!mFingerprintIdentify.isHardwareEnable()) {
            return;
        }
        //如果系统没有设置过指纹信息，就会返回
        if (!mFingerprintIdentify.isRegisteredFingerprint()) {
            return;
        }
        fingerDialog = new FingerCheckDialog(context);
        fingerDialog.setCanceledOnTouchOutside(false);
        fingerDialog.setFingerListener(fingerListener);
        fingerDialog.show();
    }


    private FingerCheckDialog.FingerListener fingerListener = new FingerCheckDialog.FingerListener() {
        @Override
        public void verifySuccess() {
            UserInfoController.getInstance().resetPwdErrorCount();
            //如果指纹登录成功后，退出当前页面
            onSuccessFinish();
            if (mFingerprintIdentify != null) {
                mFingerprintIdentify.cancelIdentify();
            }
        }

        @Override
        public void verifyFail(boolean deviceLocked) {
            if (mFingerprintIdentify != null) {
                mFingerprintIdentify.cancelIdentify();
            }
        }
    };


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
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (changeListener != null) {
            changeListener = null;
        }
        if (fingerListener != null) {
            fingerListener = null;
        }
        if (patternLockerView != null) {
            patternLockerView.setOnPatternChangedListener(null);
            patternLockerView = null;
        }
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
            mFingerprintIdentify = null;
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
