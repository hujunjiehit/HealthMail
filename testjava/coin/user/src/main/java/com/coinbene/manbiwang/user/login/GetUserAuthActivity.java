package com.coinbene.manbiwang.user.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.SelectCountryActivity;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.SiteHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.manbiwang.model.http.GetUserAuthModel;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by june
 * on 2020-02-15
 * <p>
 * 获取用户安全状态页面
 * <p>
 * 根据手机号，判断用户：1、是否绑定了手机 2、是否绑定了google
 */
public class GetUserAuthActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.left_phone_tv)
	TextView mLeftPhoneTv;
	@BindView(R2.id.select_country_tv)
	TextView mSelectCountryTv;
	@BindView(R2.id.arrow_img1)
	ImageView mArrowImg1;
	@BindView(R2.id.account_view)
	EditTextOneIcon mAccountView;
	@BindView(R2.id.country_layout)
	RelativeLayout mCountryLayout;
	@BindView(R2.id.ok_btn)
	TextView mOkBtn;

	private String country;
	private String code;
	private String countryAreaCode = Constants.CODE_CHINA_PHONE;

	private String phoneNo = "";

	public static void startMe(Context context) {
		Intent intent = new Intent(context, GetUserAuthActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.user_activity_get_user_auth;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.user_find_pwd_title);
		mAccountView.getInputText().setHint(R.string.user_input_phone_hint);
		mAccountView.setInputTypeNumer();

		String site = SpUtil.get(this, SpUtil.PRE_SITE_SELECTED, "");
		code = Constants.CODE_CHINA_PHONE;
		if ("MAIN".equals(site)) {
			country = getString(R.string.ch_str);
			code = "86";
		} else if ("KO".equals(site)) {
			country = getString(R.string.country_Korea_key);
			code = "82";
		} else if ("BR".equals(site)) {
			country = getString(R.string.country_Brazil);
			code = "55";
		} else if (SiteHelper.isVietSite()) {
			country = getString(R.string.country_Vietnam_key);
			code = "84";
		}

		countryAreaCode = code;
		mLeftPhoneTv.setText(country);
		mSelectCountryTv.setText("+" + code.trim());
	}

	@Override
	public void setListener() {
		mLeftPhoneTv.setOnClickListener(v -> selectCountry());
		mSelectCountryTv.setOnClickListener(v -> selectCountry());

		mOkBtn.setOnClickListener(v -> getUserAuth());
	}

	private void getUserAuth() {
		if (TextUtils.isEmpty(countryAreaCode)) {
			ToastUtil.show(R.string.country_select_tips);
			return;
		}
		phoneNo = mAccountView.getInputStr();
		if (TextUtils.isEmpty(phoneNo)) {
			ToastUtil.show(R.string.user_input_phone);
			return;
		}
		if (!CheckMatcherUtils.checkPhoneNumber(phoneNo)) {
			ToastUtil.show(R.string.phone_is_not_right);
			return;
		}

		OkGo.<GetUserAuthModel>post(Constants.GET_USER_AUTH)
				.params("phone", phoneNo)
				.tag(this)
				.execute(new NewDialogJsonCallback<GetUserAuthModel>(this) {
					@Override
					public void onSuc(Response<GetUserAuthModel> response) {
						if (response.body() != null && response.body().getData() != null) {
							Intent intent = new Intent(GetUserAuthActivity.this,
									ForgetPwdActivity.class);
							intent.putExtra("bindGoogle", response.body().getData().getBindGoogle());
							intent.putExtra("phone", phoneNo);

							new InlineActivityResult(GetUserAuthActivity.this)
									.startForResult(intent)
									.onSuccess(result -> {
										if (result.getResultCode() == RESULT_OK) {
											finish();
										}
									});
						}
					}

					@Override
					public void onE(Response<GetUserAuthModel> response) {
					}
				});

	}

	private void selectCountry() {
		Intent intent = new Intent(this, SelectCountryActivity.class);
		new InlineActivityResult(this)
				.startForResult(intent)
				.onSuccess(result -> {
					if (result.getResultCode() == Activity.RESULT_OK) {
						country = result.getData().getStringExtra("countryName");
						code = result.getData().getStringExtra("countryArea");
						if (!code.trim().equals(countryAreaCode)) {
							mAccountView.setInputText("");
						}
						countryAreaCode = code.trim();
						mLeftPhoneTv.setText(country);
						mSelectCountryTv.setText("+" + code.trim());
					}
				});
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}
}
