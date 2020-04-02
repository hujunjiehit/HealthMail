package com.coinbene.manbiwang.user.lock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.safe.finger.FingerCheckDialog;
import com.coinbene.manbiwang.user.safe.pattern.PatternHelper;
import com.coinbene.manbiwang.user.safe.pattern.RippleLockerHitCellView;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mpchart.com.coinbene.patternlocker.IHitCellView;
import mpchart.com.coinbene.patternlocker.OnPatternChangeListener;
import mpchart.com.coinbene.patternlocker.PatternIndicatorView;
import mpchart.com.coinbene.patternlocker.PatternLockerView;

/**
 * 出现该页面的情况，
 * 第一次登陆成功 或者 二次验证成功，还没有设置手势，指纹，进入当前页面
 * 这个页面会同时弹出手势和指纹
 */
public class PatternFingerPrintSetActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 111;
    public static final int TYPE_LOGIN_SET = 3;

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
    @BindView(R2.id.pattern_indicator_view)
    PatternIndicatorView indicator_view;
    @BindView(R2.id.menu_right_tv)
    TextView rightMenuTv;

    private PatternHelper patternHelper;
    private Unbinder mUnbinder;
    private int type;
    Animation shake;
    private int index_default = -1;
    private FingerCheckDialog fingerDialog;
    private FingerprintIdentify mFingerprintIdentify;
    private int patternSetCount = 0;
    private Context context;

    public static void startMe(Context context, int type) {
        Intent intent = new Intent(context, PatternFingerPrintSetActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int initLayout() {
        return R.layout.settings_activity_pattern_setting_new;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);//禁止滑动，否则出现问题
        context = this;
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra("type", 0);
        }

        this.patternHelper = new PatternHelper();
        int errorCount = patternHelper.getErrorTimes();

        titleView.setText(getString(R.string.gesture_ac_title));
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightMenuTv.setText(getString(R.string.pattern_reset));
        rightMenuTv.setVisibility(View.GONE);
        rightMenuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPatternView();
            }
        });

        shake = AnimationUtils.loadAnimation(this, R.anim.left_to_right_shake);
        titleView.setText(getString(R.string.gesture_ac_title));
        this.textMsg.setText(this.getString(R.string.gesture_login_set_tips));
        bottomBtnView.setText(getString(R.string.gesture_cancel));
        bottomBtnView.setVisibility(View.VISIBLE);

        bottomBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final IHitCellView hitCellView = new RippleLockerHitCellView()
                .setHitColor(this.patternLockerView.getHitColor())
                .setErrorColor(this.patternLockerView.getErrorColor());

        this.patternLockerView.setHitCellView(hitCellView)
                .build();
        this.patternLockerView.setOnPatternChangedListener(changeListener);
        showFingerLoginSetDialog();
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
            if (type == TYPE_LOGIN_SET) {
                boolean isOk = isPatternSetOk(hitList);
                view.updateStatus(!isOk);
                updateMsg();
                //添加重设置的逻辑
                if (patternSetCount == 0) {
                    indicator_view.updateState(hitList, false);
                    rightMenuTv.setVisibility(View.GONE);
                } else if (patternSetCount > 0 && (isOk == false) && (hitList.size() >= PatternHelper.MAX_SIZE)) {
                    rightMenuTv.setVisibility(View.VISIBLE);
                }
                if (hitList.size() >= PatternHelper.MAX_SIZE) {
                    patternSetCount++;
                }
                finishSetIfNeeded();
            }
        }

        @Override
        public void onClear(PatternLockerView view) {

        }
    };

    private boolean isPatternSetOk(List<Integer> hitList) {
        this.patternHelper.validateForSetting(hitList,this);
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

    private void finishSetIfNeeded() {
        if (this.patternHelper.isFinish()) {
            ToastUtil.show(R.string.gesture_set_success);
            finish();
        }
    }

    /**
     * 登录账号成功后，如果没有设置过指纹，则弹出
     */
    private void showFingerLoginSetDialog() {
        //如果设置过指纹，返回
        if (UserInfoController.getInstance().isSetFingerPrint()) {
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
            if (mFingerprintIdentify != null) {
                mFingerprintIdentify.cancelIdentify();
            }
            UserInfoController.getInstance().setFingerPrint(true);
            ToastUtil.show(R.string.finger_set_success);
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
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void resetPatternView() {
        patternSetCount = 0;
        rightMenuTv.setVisibility(View.GONE);
        indicator_view.resetState();
        patternLockerView.resetHitState();
        this.patternHelper.resetPwd();
        this.textMsg.setTextColor(getResources().getColor(R.color.res_textColor_1));
        this.textMsg.setText(getString(R.string.gesture_set_tips));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (changeListener != null) {
            changeListener = null;
        }
        if (patternLockerView != null) {
            patternLockerView.setOnPatternChangedListener(null);
            patternLockerView = null;
        }
        if (fingerListener != null) {
            fingerListener = null;
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
