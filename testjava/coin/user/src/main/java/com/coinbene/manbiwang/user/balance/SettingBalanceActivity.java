package com.coinbene.manbiwang.user.balance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.MessageDialogBuilder;
import com.coinbene.manbiwang.model.http.OtcBindInfo;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.safe.SettingSafeActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by mengxiangdong on 2018/10/8.
 * 资产设置页面
 */
@Route(path = RouteHub.User.settingBalanceActivity)
public class SettingBalanceActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	View backView;

	@BindView(R2.id.rl_bind_pay)
	RelativeLayout rl_bind_pay;
	@BindView(R2.id.tv_pay_state)
	TextView tv_pay_state;
	private boolean otcSwitch;
	@BindView(R2.id.line_bind_view)
	View bindView;
	@BindView(R2.id.address_layout)
	View addressLayout;
	@BindView(R2.id.modify_cap_layout)
	View modifyCapLayout;
	@BindView(R2.id.capital_tv)
	TextView capitalTV;
	@BindView(R2.id.one_img)
	ImageView oneImg;
	@BindView(R2.id.two_img)
	ImageView twoImg;
	@BindView(R2.id.three_img)
	ImageView threeImg;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, SettingBalanceActivity.class);
		context.startActivity(intent);
	}


	@Override
	public int initLayout() {
		return R.layout.setting_balance;
	}

	@Override
	public void initView() {
		setSwipeBackEnable(false);
		backView.setOnClickListener(this);

		rl_bind_pay.setOnClickListener(this);
		addressLayout.setOnClickListener(this);
		modifyCapLayout.setOnClickListener(this);
		init();
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


	private void init() {
		if (capitalTV == null) {
			return;
		}

		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null) {
			ToastUtil.show(R.string.user_info_fail);
			return;
		}

		//资金密码
		if (userTable.pinSetting) {
			capitalTV.setText(R.string.reset_password);
			capitalTV.setTextColor(capitalTV.getContext().getResources().getColor(R.color.res_blue));
		} else {
			capitalTV.setText(getString(R.string.setting_item_set));
			capitalTV.setTextColor(capitalTV.getContext().getResources().getColor(R.color.res_blue));
		}

		otcSwitch = SwitchUtils.isOpenOTC();
		if (otcSwitch) {
			rl_bind_pay.setVisibility(View.VISIBLE);
			bindView.setVisibility(View.VISIBLE);
		} else {
			rl_bind_pay.setVisibility(View.GONE);
			bindView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CommonUtil.isLoginAndUnLocked()) {
			queryUserInfo();
			if (otcSwitch)
				getBindPayInfo();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void queryUserInfo() {
		OkGo.<UserInfoResponse>get(Constants.USER_GET_USERINFO).tag(this).execute(new NewJsonSubCallBack<UserInfoResponse>() {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {
				UserInfoResponse baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (SettingBalanceActivity.this.isFinishing()) {
						return;
					}
					init();//重新刷新Ui
				}
			}

			@Override
			public UserInfoResponse dealJSONConvertedResult(UserInfoResponse userInfoResponse) {
				if (userInfoResponse.isSuccess()) {
					UserInfoController.getInstance().updateUserInfo(userInfoResponse.data);
				}
				return super.dealJSONConvertedResult(userInfoResponse);
			}

			@Override
			public void onE(Response<UserInfoResponse> response) {

			}

		});
	}

	private void getBindPayInfo() {
		OkGo.<OtcBindInfo>get(Constants.OTC_BIND_INFO).tag(this).execute(new NewJsonSubCallBack<OtcBindInfo>() {
			@Override
			public void onSuc(Response<OtcBindInfo> response) {

				if (SettingBalanceActivity.this.isFinishing() || tv_pay_state == null) {
					return;
				}
				OtcBindInfo otcBindInfo = response.body();

				List<Integer> bangStates = otcBindInfo.getData().getBangStates();
				if (bangStates != null && bangStates.size() > 0) {
					for (int i = 0; i < bangStates.size(); i++) {
						if (bangStates.get(i) == 1) {
							tv_pay_state.setVisibility(View.GONE);
//                            tv_pay_state.setText("已绑定");
							SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_OTC_IS_BINDED, true);
							break;
						}
						if (i == bangStates.size() - 1) {
							tv_pay_state.setVisibility(View.VISIBLE);
							tv_pay_state.setText(R.string.setting_bind);
							tv_pay_state.setTextColor(getResources().getColor(R.color.res_blue));
							SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_OTC_IS_BINDED, false);
						}
					}
				}
				OtcBindInfo.DataBean.OtcPaymentWayBean otcPaymentWay = otcBindInfo.getData().getOtcPaymentWay();
				if (otcPaymentWay != null) {
					//如果是绑定的状态，隐藏tv_pay_state,显示不同的支付方式
					List<Integer> otcArray = new ArrayList<>();
					if (!TextUtils.isEmpty(otcPaymentWay.getBankAccount())) {
						otcArray.add(R.drawable.icon_bank);
					}
					if (!TextUtils.isEmpty(otcPaymentWay.getAlipayAccount())) {
						otcArray.add(R.drawable.icon_alipay);
					}
					if (!TextUtils.isEmpty(otcPaymentWay.getWechatAccount())) {
						otcArray.add(R.drawable.icon_wechat);
					}
					if (otcArray.size() > 0) {
						if (otcArray.size() == 3) {
							oneImg.setVisibility(View.VISIBLE);
							twoImg.setVisibility(View.VISIBLE);
							threeImg.setVisibility(View.VISIBLE);

							oneImg.setBackgroundResource(otcArray.get(0));
							twoImg.setBackgroundResource(otcArray.get(1));
							threeImg.setBackgroundResource(otcArray.get(2));
						} else if (otcArray.size() == 2) {
							oneImg.setVisibility(View.GONE);
							twoImg.setVisibility(View.VISIBLE);
							threeImg.setVisibility(View.VISIBLE);
							twoImg.setBackgroundResource(otcArray.get(0));
							threeImg.setBackgroundResource(otcArray.get(1));
						} else {
							oneImg.setVisibility(View.GONE);
							twoImg.setVisibility(View.GONE);
							threeImg.setVisibility(View.VISIBLE);
							threeImg.setBackgroundResource(otcArray.get(0));
						}
					}
				}

			}

			@Override
			public void onE(Response<OtcBindInfo> response) {

			}

			@Override
			public void onFail(String msg) {

			}
		});
	}

	boolean goToSafe = false;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.address_layout) {
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (userTable == null) {
				return;
			}
			StringBuilder dialogContent = new StringBuilder();

			if (TextUtils.isEmpty(userTable.phone)) {
				goToSafe = false;
				if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
					dialogContent.append(getString(R.string.dialog_email_verify));
					goToSafe = true;
				}
				if (!userTable.googleBind) {
					dialogContent.append("\n").append(getString(R.string.dialog_bind_google_verify));
					goToSafe = true;
				}
				if (!userTable.pinSetting) {
					dialogContent.append("\n").append(getString(R.string.dialog_cap_modify_verify));
				}

				if (!TextUtils.isEmpty(dialogContent)) {
					MessageDialogBuilder builder = DialogManager.getMessageDialogBuilder(this);
					builder.setTitle(R.string.dialog_withdraw_title);
					builder.setMessage(dialogContent.toString());

					if (goToSafe) {
						builder.setPositiveButton(R.string.go_setting);
						builder.setNegativeButton(R.string.btn_cancel);
					} else {
						builder.setPositiveButton(R.string.btn_ok);
					}

					builder.showDialog();

					builder.setListener(new DialogListener() {
						@Override
						public void clickNegative() {

						}

						@Override
						public void clickPositive() {
							if (goToSafe) SettingSafeActivity.startMe(v.getContext());
						}
					});
					return;
				}
				ARouter.getInstance().build(RouteHub.Balance.coinAddressActivity).navigation(this);
			} else {
				if (!userTable.pinSetting) {
					dialogContent.append("\n").append(getString(R.string.dialog_cap_pwd_label));

					MessageDialogBuilder builder = DialogManager.getMessageDialogBuilder(this);
					builder.setMessage(dialogContent.toString());
					builder.setPositiveButton(R.string.btn_ok);
					builder.showDialog();
					return;
				}
				ARouter.getInstance().build(RouteHub.Balance.coinAddressActivity).navigation(this);
			}

		} else if (v.getId() == R.id.modify_cap_layout) {
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (!userTable.googleBind) {
				MessageDialogBuilder builder = DialogManager.getMessageDialogBuilder(v.getContext());
				builder.setTitle(R.string.dialog_withdraw_title);
				builder.setMessage(R.string.setting_google_auth);
				builder.setPositiveButton(R.string.go_setting);
				builder.showDialog();

				builder.setListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {
						SettingSafeActivity.startMe(v.getContext());
					}
				});
				return;
			}

			if (!userTable.pinSetting) {
				//没有设置资金密码
				if ((TextUtils.isEmpty(userTable.phone) && !String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) || TextUtils.isEmpty(userTable.verifyWay)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
					String dialogContent = v.getContext().getString(R.string.dialog_setting_cap_tips);
					dialog.setMessage(dialogContent);
					dialog.setPositiveButton(R.string.go_setting, (dialog16, which) -> {
						SettingSafeActivity.startMe(v.getContext());
						dialog16.dismiss();
					});
					dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog13, which) -> dialog13.dismiss());
					dialog.show();
				} else {
					//设置资金密码
					SetCapPwdActivity.startMeForResult(SettingBalanceActivity.this, SetCapPwdActivity.CODE_RESULT);
				}
			} else {
				//修改资金密码
				AmendmentFundPasswordActivity.startActivity(this);
			}
		} else if (v.getId() == R.id.rl_bind_pay) {//支付绑定信息
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (userTable == null) {
				return;
			}
			int count_line = 0;
			goToSafe = false;
			StringBuilder dialogContent = new StringBuilder();
			if (TextUtils.isEmpty(userTable.phone)) {
				if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
					count_line++;
					dialogContent.append(v.getContext().getString(R.string.dialog_email_verify));
				}
				if (dialogContent.toString().length() > 0) {
					dialogContent.append("\n");
				}
				count_line++;
				dialogContent.append(v.getContext().getString(R.string.dialog_bing_phone));
				goToSafe = true;
			}
			if (!userTable.pinSetting) {
				if (dialogContent.toString().length() > 0) {
					dialogContent.append("\n");
				}
				count_line++;
				dialogContent.append(v.getContext().getString(R.string.dialog_cap_modify_verify));
			}

			if (!userTable.kyc) {
				if (dialogContent.toString().length() > 0) {
					dialogContent.append("\n");
				}
				count_line++;
				dialogContent.append(getString(R.string.dialog_real_name_auth));
				goToSafe = true;
			}

			if (dialogContent.toString().length() > 0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
				if (count_line > 1) {
					dialog.setTitle(getString(R.string.dialog_withdraw_title));
					dialog.setMessage(dialogContent);
				} else {
					dialog.setMessage(getString(R.string.please_over_setting) + dialogContent.toString());
				}
				if (goToSafe) {
					dialog.setPositiveButton(R.string.go_setting, (dialog16, which) -> {
						SettingSafeActivity.startMe(v.getContext());
						dialog16.dismiss();
					});
					dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog13, which) -> dialog13.dismiss());
				} else {
					dialog.setNegativeButton(getString(R.string.btn_ok), (dialog14, which) -> dialog14.dismiss());
				}
				dialog.show();
				return;
			}
			UserPayTypesActivity.startMe(v.getContext());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		if (requestCode == SetCapPwdActivity.CODE_RESULT) {
			capitalTV.setText("");
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OkGo.getInstance().cancelTag(this);
	}


}
