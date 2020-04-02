package com.coinbene.manbiwang.debug.crash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.coinbene.common.base.BaseActivity;
import com.coinbene.common.model.http.CrashModel;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by june
 * on 2019-07-30
 */
public class CrashDetailActivity extends BaseActivity {

	public static final String CRASH_MODEL = "crash_model";
	@BindView(R2.id.textMessage)
	TextView mTextMessage;
	@BindView(R2.id.tv_packageName)
	TextView mTvPackageName;
	@BindView(R2.id.tv_className)
	TextView mTvClassName;
	@BindView(R2.id.tv_methodName)
	TextView mTvMethodName;
	@BindView(R2.id.tv_lineNumber)
	TextView mTvLineNumber;
	@BindView(R2.id.tv_exceptionType)
	TextView mTvExceptionType;
	@BindView(R2.id.tv_time)
	TextView mTvTime;
	@BindView(R2.id.tv_model)
	TextView mTvModel;
	@BindView(R2.id.tv_brand)
	TextView mTvBrand;
	@BindView(R2.id.tv_version)
	TextView mTvVersion;
	@BindView(R2.id.tv_fullException)
	TextView mTvFullException;

	Unbinder mUnbinder;

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private CrashModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity_crash_detail);
		bindViews();
		setListener();
		init();
	}

	protected void bindViews() {
		mUnbinder = ButterKnife.bind(this);
	}

	protected void setListener() {

	}

	protected void init() {
		model = getIntent().getParcelableExtra(CRASH_MODEL);
		if (model == null) {
			return;
		}
		Log.e("CrashDetail", Log.getStackTraceString(model.getEx()));

		mTvPackageName.setText(model.getClassName());
		mTextMessage.setText(model.getExceptionMsg());
		mTvClassName.setText(model.getFileName());
		mTvMethodName.setText(model.getMethodName());
		mTvLineNumber.setText(String.valueOf(model.getLineNumber()));
		mTvExceptionType.setText(model.getExceptionType());
		mTvFullException.setText(model.getFullException());
		mTvTime.setText(df.format(model.getTime()));

		mTvModel.setText(model.getDevice().getModel());
		mTvBrand.setText(model.getDevice().getBrand());
		mTvVersion.setText(model.getDevice().getVersion());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUnbinder != null) {
			mUnbinder.unbind();
		}
	}

	public static void startMe(Context context, CrashModel model) {
		Intent intent = new Intent(context, CrashDetailActivity.class);
		intent.putExtra(CrashDetailActivity.CRASH_MODEL, model);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
