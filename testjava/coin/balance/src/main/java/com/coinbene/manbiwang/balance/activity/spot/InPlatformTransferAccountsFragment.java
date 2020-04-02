package com.coinbene.manbiwang.balance.activity.spot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.manbiwang.model.http.BalanceOneRes;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 提供基础内容和生命周期控制
 */
public class InPlatformTransferAccountsFragment extends BaseFragment {
	@BindView(R2.id.min_withDraw)
	EditText minWithDraw;
	@BindView(R2.id.text_tv2)
	TextView coinNameTv;
	@BindView(R2.id.fee_tv_value)
	TextView feeTv;
	@BindView(R2.id.withdraw_address_value)
	EditText addressValueEText;
	@BindView(R2.id.able_num_tv)
	TextView ableNumTv;
	@BindView(R2.id.xrp_line)
	View xrpLine;
	@BindView(R2.id.text_all)
	TextView text_all;
	//    @BindView(R.id.xrp_layout)
//    View xrpLayout;
	@BindView(R2.id.submmit_btn)
	QMUIRoundButton submmitBtn;
	@BindView(R2.id.arrow_right)
	ImageView arrowRightView;
	@BindView(R2.id.cap_pwd_value)
	EditTextTwoIcon capInputView;

	@BindView(R2.id.google_line_view)
	View googleLineView;
	@BindView(R2.id.google_layout)
	View googleLayout;
	@BindView(R2.id.msg_code_line)
	View msgCodeLineView;
	@BindView(R2.id.msg_code_layout)
	View msgCodeLayout;
	@BindView(R2.id.msg_code_input)
	EditText msgCodeInput;
	@BindView(R2.id.google_code_inpout)
	EditText googleCodeInput;
	@BindView(R2.id.send_msgcode_tv)
	TextView sendMsgCodeTv;
	@BindView(R2.id.left_msg_code_tv)
	TextView leftMsgCodeTv;
	@BindView(R2.id.destination_tg_value)
	EditText destinationInput;
	@BindView(R2.id.close_view)
	ImageView closeView;
	@BindView(R2.id.actual_account_value)
	TextView actualCccountValue;
	@BindView(R2.id.layout_remark)
	LinearLayout layoutRemark;
	@BindView(R2.id.tv_remark)
	TextView tvRemark;

	private Unbinder unbinder;
	private BalanceInfoTable model;
	private String sendMsgWait;
	private String sendMsgAgain;
	private String avaibleBalance;
	private String tagInput;

	//是否是手机用户
	private boolean isPhoneUser;

	public static InPlatformTransferAccountsFragment newInstance(String asset) {
		Bundle args = new Bundle();
		args.putSerializable("model", asset);
		InPlatformTransferAccountsFragment fragment = new InPlatformTransferAccountsFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@LayoutRes
	protected int getLayoutId() {
		return R.layout.fr_transfer_accounts;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(getLayoutId(), container, false);
		unbinder = ButterKnife.bind(this, root);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		submmitBtn.setOnClickListener(this);
		arrowRightView.setOnClickListener(this);
		closeView.setOnClickListener(this);
		text_all.setOnClickListener(this);

		String asset = getArguments().getString("model");
		model = BalanceController.getInstance().findByAsset(asset);

		if (!model.transfer) {
			layoutRemark.setVisibility(View.VISIBLE);
			submmitBtn.setEnabled(false);
			if (!TextUtils.isEmpty(model.banTransferReason)) {
				tvRemark.setText(String.format("%s，%s", model.banTransferReason, getString(R.string.transfer_suspend)));
			} else {
				tvRemark.setText(String.format("%s%s", asset, getString(R.string.transfer_suspend)));
			}
		}

		minWithDraw.setHint(getString(R.string.transfer_amount));
		minWithDraw.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String precisionStr = model.transferPrecision;
				if (!TextUtils.isEmpty(precisionStr)) {
					PrecisionUtils.setPrecision(minWithDraw, s, Integer.valueOf(precisionStr));
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
				String input = minWithDraw.getText().toString();
				if (input.length() == 1 && !Character.isDigit(input.charAt(0))) {
					minWithDraw.setText("");
					return;
				}
				if (!TextUtils.isEmpty(minWithDraw.getText().toString().trim())) {
					minWithDraw.setSelection(minWithDraw.getText().toString().length());
				}

				if (!TextUtils.isEmpty(input.trim())) {
					actualCccountValue.setText(input);
				} else {
					actualCccountValue.setText("--");
				}

			}
		});
//
		coinNameTv.setText(model.asset);
		capInputView.setSecondRightPwdEyeHint();
//

//        //平台内转账区块费用为0
//        feeTv.setText(String.valueOf(0));

//        if (model.useTag) {
//            xrpLine.setVisibility(View.VISIBLE);
//            xrpLayout.setVisibility(View.VISIBLE);
//        } else {
//            xrpLine.setVisibility(View.GONE);
//            xrpLayout.setVisibility(View.GONE);
//        }
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
//        BalanceInfoTable balanceTable = BalanceController.getInstance().findByAsset(model.asset);
		if (model != null) {
//            String balance = CommonUtil.getRoundDown(CommonUtil.subtract(model.totalBalance, model.frozenBalance), model.transferPrecision);
			String balance = PrecisionUtils.getRoundDownPrecision(BigDecimalUtils.subtract(model.totalBalance, model.frozenBalance), Integer.valueOf(model.transferPrecision));
			balance = TextUtils.isEmpty(balance) ? "0" : balance;
//            if (balance.equals("false") || balance.equals("true")) {
//                balance = "0";
//            }
			//减去费用
			balance = StringUtils.rePlaceDot(balance);
			subtractAndSetStr(balance);
		}

		//平台内转账必须验证google，如果是手机用户还需要验证短信
		if (TextUtils.isEmpty(userTable.phone)) {
			//邮箱用户 用邮箱验证码
			isPhoneUser = false;
			leftMsgCodeTv.setText(getString(R.string.res_email_verify));
			msgCodeInput.setHint(getString(R.string.res_please_input_email_code));
		} else {
			//手机用户 用短信验证码
			isPhoneUser = true;
			leftMsgCodeTv.setText(getString(R.string.set_reset_cap_phone_label));
			msgCodeInput.setHint(getString(R.string.please_input_msg_code));
		}
		sendMsgCodeTv.setOnClickListener(this);

//        addressValueEText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s == null) {
//                    return;
//                }
//                if (s.toString().length() > 0) {
//                    closeView.setVisibility(View.VISIBLE);
//                } else {
//                    closeView.setVisibility(View.GONE);
//                }
//            }
//        });
//        String str = this.getResources().getString(R.string.with_draw_address_hint);
//        str = String.format(str, model.asset);//model.ChineseName
//        addressValueEText.setHint(str);

		sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
		sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);
		getBalanceListRequest();
	}

	public void subtractAndSetStr(String balance) {
		BigDecimal balanceD = new BigDecimal(balance);
//        BigDecimal feeD = new BigDecimal(model.withdrawFee);
		double balance_double = balanceD.doubleValue();//toString();
		if (balance_double < 0) {
			balance_double = 0;
			balance = String.valueOf(balance_double);
		}
//        else
//            balance = balanceD.subtract(feeD).toString();
		avaibleBalance = balance;
		if (ableNumTv != null) {
			ableNumTv.setText(balance);
		}
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (model != null && !TextUtils.isEmpty(model.asset)) {
				getBalanceListRequest();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
		OkGo.getInstance().cancelTag(this);
		if (unbinder != null) {
			unbinder.unbind();
		}


	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.submmit_btn) {
			submmit();
		} else if (v.getId() == R.id.arrow_right) {
			Bundle bundle = new Bundle();
			bundle.putString("assetId", model.asset);
			bundle.putString("code", model.asset);
			TransferAddressActivity.startMe(getActivity(), TransferAddressActivity.CODE_REQUEST);
		} else if (v.getId() == R.id.send_msgcode_tv) {
			if (isPhoneUser) {
				//发送短信验证码
				sendMsgCode();
			} else {
				//发送邮箱验证码
				sendEmailCode();
			}
		} else if (v.getId() == R.id.close_view) {
			addressValueEText.setText("");
		} else if (v.getId() == R.id.text_all) {
			if (Float.valueOf(ableNumTv.getText().toString()) > 0) {
				minWithDraw.setText(ableNumTv.getText().toString());
			}
		}
	}

	private void submmit() {
		String minDraw = minWithDraw.getText().toString();
		if (TextUtils.isEmpty(minDraw)) {
			ToastUtil.show(R.string.transfer_amount);
			return;
		}
		if (avaibleBalance != null) {
			try {
				avaibleBalance = StringUtils.rePlaceDot(avaibleBalance);
				double avaibleBalanceD = Double.parseDouble(avaibleBalance);

				minDraw = StringUtils.rePlaceDot(minDraw);
				double minWithDrawD = Double.parseDouble(minDraw);

				if (avaibleBalanceD < minWithDrawD) {
					ToastUtil.show(R.string.more_than_ablenum_tip);
					return;
				}
			} catch (Exception ex) {
			}
		}
		String address = addressValueEText.getText().toString();
		if (TextUtils.isEmpty(address)) {
			ToastUtil.show(R.string.input_account_number);
			return;
		}
		String capPwdInput = capInputView.getInputStr();
		if (TextUtils.isEmpty(capPwdInput)) {
			ToastUtil.show(R.string.capital_pwd_is_empty);
			return;
		}
		if (capPwdInput.length() > 8 || capPwdInput.length() < 5) {//兼容旧密码
			ToastUtil.show(R.string.please_set_cap_num_hint);
			return;
		}

		String msgCode = msgCodeInput.getText().toString();
		if (TextUtils.isEmpty(msgCode)) {
			if (isPhoneUser) {
				ToastUtil.show(R.string.msg_code_is_empty);
			} else {
				ToastUtil.show(R.string.res_please_input_email_code);
			}
			return;
		}

		String googleCode = googleCodeInput.getText().toString();
		if (TextUtils.isEmpty(googleCode)) {
			ToastUtil.show(R.string.please_input_google_code);
			return;
		}

//        if (xrpLayout.getVisibility() == View.VISIBLE) {
//            tagInput = destinationInput.getText().toString();
//        }
		submmit(minDraw, address, capPwdInput);
	}


	private void submmit(String amount, String address, String moneyPasswd) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", 1);
		httpParams.put("asset", model.asset);

		httpParams.put("amount", amount);
		httpParams.put("toLoginId", address);
		httpParams.put("pin", MD5Util.MD5(moneyPasswd));

		httpParams.put("verifyCode", msgCodeInput.getText().toString());

		httpParams.put("googleCode", googleCodeInput.getText().toString());

		if (!TextUtils.isEmpty(tagInput)) {
			httpParams.put("tag", tagInput);
		}
//        if (!TextUtils.isEmpty(tagInput)) {
//            httpParams.put("tag", tagInput);
//        }
		OkGo.<BaseRes>post(Constants.ACCOUNT_TRANSFER).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.transfer_success);
				minWithDraw.setText("");
				addressValueEText.setText("");
				capInputView.setInputText("");
				msgCodeInput.setText("");
				googleCodeInput.setText("");
				destinationInput.setText("");
				//重新获取用户账号余额
				handler.sendEmptyMessageDelayed(send_delay_what, 500);
			}

			@Override
			public void onE(Response<BaseRes> response) {
			}

			@Override
			public void onFail(String msg) {
				super.onFail(msg);
			}
		});

	}


	private int count = 59;
	private int send_msg_what = 10;
	private int send_delay_what = 11;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (getActivity() != null) {
				if (msg.what == send_msg_what) {
					sendMsgCodeTv.setText(String.format(sendMsgWait, count));
					sendMsgCodeTv.setTextColor(getResources().getColor(R.color.res_textColor_2));
					sendMsgCodeTv.setOnClickListener(null);
					count--;
					if (count > 0) {
						handler.sendEmptyMessageDelayed(send_msg_what, 1000);
					} else {
						sendMsgCodeTv.setText(sendMsgAgain);
						sendMsgCodeTv.setTextColor(getResources().getColor(R.color.res_blue));
						sendMsgCodeTv.setOnClickListener(InPlatformTransferAccountsFragment.this::onClick);
					}
				} else if (msg.what == send_delay_what) {
					getBalanceListRequest();
				}
			}
		}
	};

	private void sendMsgCode() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("type", Constants.CODE_TYPE_PLATFORM_TRANSFER);
		OkGo.<BaseRes>post(Constants.USER_SENDSMS).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.send_msg_code_success);
				count = 59;
				handler.sendEmptyMessage(send_msg_what);
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}

			@Override
			public void onFail(String msg) {

			}
		});
	}

	private void sendEmailCode() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("mailType", Constants.MAIL_PLATFORM_TRANSFER);
		OkGo.<BaseRes>post(Constants.USER_SEND_MAIL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.send_msg_code_success);
				count = 59;
				handler.sendEmptyMessage(send_msg_what);
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	public void getBalanceListRequest() {
		if (getActivity() != null && getActivity().isDestroyed()) {
			return;
		}
		HttpParams params = new HttpParams();
		params.put("asset", model.asset);
		params.put("accountType", "1");

		OkGo.<BalanceOneRes>get(Constants.ACCOUNT_BALANCE_ONE).tag(this).params(params).execute(new NewJsonSubCallBack<BalanceOneRes>() {
			@Override
			public void onSuc(Response<BalanceOneRes> response) {
				if (response.body().getData() != null
						&& !TextUtils.isEmpty(response.body().getData().getTotalBalance())
						&& !TextUtils.isEmpty(response.body().getData().getFrozenBalance())) {
					BalanceController.getInstance().updateBalancebyAsset(model.asset, response.body().getData().getTotalBalance(), response.body().getData().getFrozenBalance(), response.body().getData().getAvailableBalance());
					BalanceInfoTable balanceTable = BalanceController.getInstance().findByAsset(model.asset);
					if (balanceTable != null) {
						String balance = PrecisionUtils.getRoundDownPrecision(BigDecimalUtils.subtract(balanceTable.totalBalance, balanceTable.frozenBalance), Integer.valueOf(model.transferPrecision));
						balance = TextUtils.isEmpty(balance) ? "0" : balance;

						subtractAndSetStr(balance);
					}
				} else {
					if (ableNumTv != null)
						ableNumTv.setText("--");
					ToastUtil.show(R.string.get_balance_fail);
				}
			}

			@Override
			public void onE(Response<BalanceOneRes> response) {
				if (ableNumTv != null) {
					ableNumTv.setText("--");
				}
			}

		});

	}


	public void setTargetId(String targetId) {
		addressValueEText.setText(targetId);
	}
}
