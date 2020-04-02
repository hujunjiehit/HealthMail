package com.coinbene.manbiwang.user.safe.pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mpchart.com.coinbene.patternlocker.IHitCellView;
import mpchart.com.coinbene.patternlocker.OnPatternChangeListener;
import mpchart.com.coinbene.patternlocker.PatternIndicatorView;
import mpchart.com.coinbene.patternlocker.PatternLockerView;

/**
 * 设置页面，手势的设置和校验
 */
public class PatternSettingActivity extends CoinbeneBaseActivity {
	public static final int CODE_RESULT = 111;
	public static final int TYPE_SET_OPEN = 1;
	public static final int TYPE_SET_CHECK_CLOSE = 2;

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
	private int type;
	private Animation shake;
	PatternIndicatorView indicator_view;
	@BindView(R2.id.menu_right_tv)
	TextView rightMenuTv;
	private int patternSetCount = 0;

	public static void startMeForResult(Activity activity, int code, int type) {
		Intent intent = new Intent(activity, PatternSettingActivity.class);
		intent.putExtra("type", type);
		activity.startActivityForResult(intent, code);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		mUnbinder = ButterKnife.bind(this);
	}

	@Override
	public int initLayout() {
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getIntExtra("type", 0);
		}

		if (type == TYPE_SET_OPEN) {
			return R.layout.settings_activity_pattern_setting_new;
		} else {
			return R.layout.settings_activity_pattern_setting;
		}

	}

	@Override
	public void initView() {
		if (type == TYPE_SET_OPEN) {
			indicator_view = (PatternIndicatorView) findViewById(R.id.pattern_indicator_view);
		}

		setSwipeBackEnable(false);//禁止滑动，否则出现问题
		this.patternHelper = new PatternHelper();
		int errorCount = patternHelper.getErrorTimes();

		titleView.setText(getString(R.string.gesture_ac_title));
		rightMenuTv.setText(getString(R.string.pattern_reset));
		rightMenuTv.setVisibility(View.GONE);
		rightMenuTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetPatternView();
			}
		});
		backView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressedThis();
			}
		});
		shake = AnimationUtils.loadAnimation(this, R.anim.left_to_right_shake);
		if (type == TYPE_SET_OPEN) {
			titleView.setText(getString(R.string.gesture_ac_title));
			this.textMsg.setText(this.getString(R.string.gesture_set_tips));
			bottomBtnView.setVisibility(View.GONE);
			indicator_view.setVisibility(View.VISIBLE);
		} else if (type == TYPE_SET_CHECK_CLOSE) {
			titleView.setText(getString(R.string.gestrue_close_check_title));
			this.textMsg.setText(this.getString(R.string.gestrue_close_check_tips));
			bottomBtnView.setVisibility(View.GONE);
			if (errorCount > 0) {
				this.textMsg.setText(patternHelper.getPwdErrorMsg(this));
				this.textMsg.setTextColor(getResources().getColor(R.color.res_red));
			}
		}

		final IHitCellView hitCellView = new RippleLockerHitCellView()
				.setHitColor(this.patternLockerView.getHitColor())
				.setErrorColor(this.patternLockerView.getErrorColor());

		this.patternLockerView.setHitCellView(hitCellView)
				.build();
		this.patternLockerView.setOnPatternChangedListener(changeListener);

	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return true;
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
			if (type == TYPE_SET_OPEN) {
				boolean isOk = isPatternSetOk(hitList);
				view.updateStatus(!isOk);
				if (patternSetCount == 0) {
					indicator_view.updateState(hitList, false);
					rightMenuTv.setVisibility(View.GONE);
				} else if (patternSetCount > 0 && (isOk == false) && (hitList.size() >= PatternHelper.MAX_SIZE)) {
					rightMenuTv.setVisibility(View.VISIBLE);
				}
				if (hitList.size() >= PatternHelper.MAX_SIZE) {
					patternSetCount++;
				}
				updateMsg();
				finishSetIfNeeded();
			} else if (type == TYPE_SET_CHECK_CLOSE) {
				boolean isError = !isPatternCloseCheck(hitList);
				view.updateStatus(isError);
				updateMsg();
				finishCheckIfNeeded();
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

	private boolean isPatternCloseCheck(List<Integer> hitList) {
		this.patternHelper.validateForClose(hitList,this);
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
			if (type == TYPE_SET_OPEN) {
				Intent intent = new Intent();
				intent.putExtra("type", type);
				setResult(Activity.RESULT_OK, intent);
			}
			finish();
		}
	}

	private void finishCheckIfNeeded() {
		if (this.patternHelper.isFinish() && this.patternHelper.isOk()) {//解锁成功,设置页面关闭锁的状态
			ToastUtil.show(R.string.gesture_set_success);
			Intent intent = new Intent();
			intent.putExtra("type", type);
			intent.putExtra("retry_error", false);
			setResult(Activity.RESULT_OK, intent);
			finish();
		} else if (this.patternHelper.isFinish()) {//超过错误次数，跳转至登录页面，手势解锁关闭
			UserInfoController.getInstance().clearGesturePwd();
			Intent intent = new Intent();
			intent.putExtra("type", type);
			intent.putExtra("retry_error", true);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			onBackPressedThis();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressedThis() {
		if (type != 0) {
			Intent intent = new Intent();
			intent.putExtra("type", type);
			setResult(Activity.RESULT_CANCELED, intent);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUnbinder != null) {
			mUnbinder.unbind();
		}
	}
}
