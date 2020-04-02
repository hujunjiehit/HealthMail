package com.coinbene.manbiwang.balance.activity.spot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.balance.ChainsAdapter;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.BalanceChainTable;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.manbiwang.model.http.BalanceOneRes;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SpaceItemDecoration;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.common.zxing.ScannerActivity;
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

public class WithDrawFragment extends BaseFragment {
	@BindView(R2.id.min_withDraw)
	EditText minWithDraw;
	@BindView(R2.id.text_tv2)
	TextView coinNameTv;
	@BindView(R2.id.fee_tv_value)
	TextView feeTv;
	@BindView(R2.id.zxing_icon)
	View zxingView;
	@BindView(R2.id.withdraw_address_value)
	EditText addressValueEText;
	@BindView(R2.id.able_num_tv)
	TextView ableNumTv;
	@BindView(R2.id.xrp_line)
	View xrpLine;
	@BindView(R2.id.xrp_layout)
	View xrpLayout;
	@BindView(R2.id.submmit_btn)
	QMUIRoundButton submmitBtn;
	@BindView(R2.id.arrow_right)
	ImageView arrowRightView;
	@BindView(R2.id.cap_pwd_value)
	EditTextTwoIcon capInputView;
	@BindView(R2.id.text_all)
	TextView textAll;
	@BindView(R2.id.tv_coin_tip_content)
	TextView tvCoinTipContent;

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
	@BindView(R2.id.destination_tg_tv)
	TextView destinationTgTv;
	@BindView(R2.id.tv_remark)
	TextView tvRemark;
	@BindView(R2.id.actual_account_value)
	TextView actualCccountValue;
	@BindView(R2.id.rl_chains)
	RecyclerView rlChains;
	@BindView(R2.id.ll_chains)
	LinearLayout llChains;
	@BindView(R2.id.ll_zxing)
	LinearLayout llZxing;
	@BindView(R2.id.ll_arrow_right)
	LinearLayout llArrowRight;
	@BindView(R2.id.layout_remark)
	LinearLayout layoutRemark;
	@BindView(R2.id.img_RemarkClear)
	ImageView imgRemarkClear;


	private Unbinder unbinder;
	private BalanceInfoTable model;
	private String sendMsgWait;
	private String sendMsgAgain;
	private String avaibleBalance;
	private String tagInput;
	private String chain;
	private TextWatcher textWatcher;
	private int selectChainPosition;
	private BalanceChainTable curTable;

	private String currentPrecision;

	//是否是手机用户
	private boolean isPhoneUser;

	public static WithDrawFragment newInstance(String asset) {
		Bundle args = new Bundle();
		args.putString("model", asset);
		WithDrawFragment fragment = new WithDrawFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@LayoutRes
	protected int getLayoutId() {
		return R.layout.fr_with_draw;
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

		initListener();

		String asset = getArguments().getString("model");
		model = BalanceController.getInstance().findByAsset(asset);
		if (model == null) {
			ToastUtil.show("not found asset");
			return;
		}

		if (!model.withdraw) {
			layoutRemark.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(model.banWithdrawReason)){
				tvRemark.setText(String.format("%s%s", model.asset, getString(R.string.cannot_withdraw_tips_new)));
			}else {
				tvRemark.setText(String.format("%s%s", model.banWithdrawReason, getResources().getString(R.string.cannot_withdraw_tips_new)));
			}
		}

		submmitBtn.setEnabled(model.withdraw);
		coinNameTv.setText(model.asset);
		capInputView.setSecondRightPwdEyeHint();

		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		//提币必须验证google，如果是手机用户还需要验证短信
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

		sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
		sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);
		getBalanceListRequest();
		initChainsData();
	}

	private void initListener() {
		zxingView.setOnClickListener(this);
		submmitBtn.setOnClickListener(this);
		arrowRightView.setOnClickListener(this);
		closeView.setOnClickListener(this);
		textAll.setOnClickListener(this);
		llZxing.setOnClickListener(this);
		llArrowRight.setOnClickListener(this);

		imgRemarkClear.setOnClickListener(v -> destinationInput.setText(""));
		destinationInput.addTextChangedListener(new BaseTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				if (TextUtils.isEmpty(s.toString())) {
					imgRemarkClear.setVisibility(View.GONE);
				} else {
					imgRemarkClear.setVisibility(View.VISIBLE);
				}
			}
		});


	}

	private void setAssetData() {
		chain = model.chain;
		feeTv.setText(String.valueOf(model.withdrawFee));
		minWithDraw.removeTextChangedListener(textWatcher);
		textWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String precisionStr = model.withdrawPrecision;
				if (!TextUtils.isEmpty(precisionStr)) {
					PrecisionUtils.setPrecision(minWithDraw, s, Integer.valueOf(precisionStr));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				String input = minWithDraw.getText().toString();
				String fee = feeTv.getText().toString();
				if (input.length() == 1 && !Character.isDigit(input.charAt(0))) {
					minWithDraw.setText("");
					return;
				}

				if (!TextUtils.isEmpty(minWithDraw.getText().toString().trim())) {
					minWithDraw.setSelection(minWithDraw.getText().toString().length());
				}
				calculationRealAccount(input.trim(), fee);
			}
		};
		minWithDraw.addTextChangedListener(textWatcher);


		if (!TextUtils.isEmpty(model.withdrawHints)) {
			tvCoinTipContent.setVisibility(View.VISIBLE);
			tvCoinTipContent.setText(model.withdrawHints);
		} else {
			tvCoinTipContent.setVisibility(View.GONE);
		}
		String minHint = getString(R.string.withdraw_mincount);
		String minStr = String.format(minHint, model.minWithdraw);
		minWithDraw.setHint(minStr);
		String str = this.getResources().getString(R.string.with_draw_address_hint);
		str = String.format(str, model.asset);
		addressValueEText.setHint(str);

		if (model.useTag) {
			xrpLine.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(model.tagLabel)) {
				if (model.withdrawTag) {
					destinationTgTv.setText(model.tagLabel);
				} else {
					String format = String.format(getString(R.string.optional), model.tagLabel);
					destinationTgTv.setText(format);
				}
			}
			xrpLayout.setVisibility(View.VISIBLE);
		} else {
			xrpLine.setVisibility(View.GONE);
			xrpLayout.setVisibility(View.GONE);
		}
		if (model != null) {
			String balance = BigDecimalUtils.subtract(model.totalBalance, model.frozenBalance);

			balance = TextUtils.isEmpty(balance) ? "0" : balance;
			//减去费用
			balance = StringUtils.rePlaceDot(balance);
			currentPrecision = model.withdrawPrecision;
			subtractAndSetStr(balance);
		}
//        calculationRealAccount(minWithDraw.getText().toString(), feeTv.getText().toString());
	}

	private void setChainData(BalanceChainTable table) {
		addressValueEText.setText("");
		minWithDraw.setText("");
		chain = table.chain;
		feeTv.setText(String.valueOf(table.withdrawFee));
		minWithDraw.removeTextChangedListener(textWatcher);
		textWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String precisionStr = table.withdrawPrecision;
				if (!TextUtils.isEmpty(precisionStr)) {
					PrecisionUtils.setPrecision(minWithDraw, s, Integer.valueOf(precisionStr));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				String input = minWithDraw.getText().toString();
				String fee = feeTv.getText().toString();
				if (input.length() == 1 && !Character.isDigit(input.charAt(0))) {
					minWithDraw.setText("");
					return;
				}

				if (!TextUtils.isEmpty(minWithDraw.getText().toString().trim())) {
					minWithDraw.setSelection(minWithDraw.getText().toString().length());
				}
				calculationRealAccount(input.trim(), fee);
			}
		};
		minWithDraw.addTextChangedListener(textWatcher);

		if (!TextUtils.isEmpty(table.withdrawHints)) {
			tvCoinTipContent.setVisibility(View.VISIBLE);
			tvCoinTipContent.setText(table.withdrawHints);
		} else {
			tvCoinTipContent.setVisibility(View.GONE);
		}
		String minHint = getString(R.string.withdraw_mincount);
		String minStr = String.format(minHint, table.minWithdraw);
		minWithDraw.setHint(minStr);
		String str = this.getResources().getString(R.string.with_draw_address_hint);
		str = String.format(str, table.asset);
		addressValueEText.setHint(str);

		if (table.useTag) {
			xrpLine.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(table.tagLabel)) {
				if (model.withdrawTag) {
					destinationTgTv.setText(table.tagLabel);
				} else {
					String format = String.format(getString(R.string.optional), table.tagLabel);
					destinationTgTv.setText(format);
				}
			}
			xrpLayout.setVisibility(View.VISIBLE);
		} else {
			xrpLine.setVisibility(View.GONE);
			xrpLayout.setVisibility(View.GONE);
		}
		if (model != null) {
			String balance = BigDecimalUtils.subtract(model.totalBalance, model.frozenBalance);

			balance = TextUtils.isEmpty(balance) ? "0" : balance;
			//减去费用
			balance = StringUtils.rePlaceDot(balance);
			currentPrecision = table.withdrawPrecision;
			subtractAndSetStr(balance);
		}
//        calculationRealAccount(minWithDraw.getText().toString(), feeTv.getText().toString());

	}

	private void calculationRealAccount(String input, String fee) {
		if (!TextUtils.isEmpty(input.trim())) {
			if (TextUtils.isEmpty(fee)) {
				actualCccountValue.setText(input);
			} else {
				String actual = BigDecimalUtils.subtract(input, fee);
				if (BigDecimalUtils.lessThanToZero(actual)) {
					actualCccountValue.setText("--");
				} else {
					actualCccountValue.setText(actual);
				}
			}

		} else {
			actualCccountValue.setText("--");
		}
	}

	public void subtractAndSetStr(String balance) {
		BigDecimal balanceD = new BigDecimal(balance);
		avaibleBalance = PrecisionUtils.getRoundDownPrecision(balanceD.toString(), Tools.parseInt(currentPrecision));
		if (ableNumTv != null) {
			ableNumTv.setText(avaibleBalance);
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

	private void initChainsData() {
		if (model.balanceChains != null && model.balanceChains.size() > 0) {
			GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
			rlChains.addItemDecoration(new SpaceItemDecoration(3, DensityUtil.dip2px(15)));
			rlChains.setLayoutManager(linearLayoutManager);

			ChainsAdapter chainsAdapter = new ChainsAdapter(ChainsAdapter.WITHDARW_TYPE);
			rlChains.setAdapter(chainsAdapter);
			for (BalanceChainTable table : model.balanceChains) {
				if (table.withdraw) {
					curTable = table;
					break;
				}
			}
			if (curTable != null) {
				chainsAdapter.setLists(model.balanceChains, curTable.protocolName);
			} else {
				curTable = model.balanceChains.get(0);
				chainsAdapter.setLists(model.balanceChains, "");
			}

			chainsAdapter.setOnItemClickLisenter((selectStr, position) -> {
				chainsAdapter.setSelect(selectStr);
				selectChainPosition = position;
				setChainData(model.balanceChains.get(position));
			});
			setChainData(curTable);
		} else {
			llChains.setVisibility(View.GONE);
			setAssetData();
		}
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (unbinder == null) {
			return;
		}
		unbinder.unbind();
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		OkGo.getInstance().cancelTag(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.zxing_icon) {
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getActivity(),
					Manifest.permission.CAMERA)) {
				//权限还没有授予，需要在这里写申请权限的代码
				ActivityCompat.requestPermissions(getActivity(),
						new String[]{Manifest.permission.CAMERA}, 60);
			} else {
				//权限已经被授予，在这里直接写要执行的相应方法即可
				ScannerActivity.startMeForResult(getActivity(), ScannerActivity.CODE_RESULT_ADDRESS);
			}
		}
		if (v.getId() == R.id.ll_zxing) {
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getActivity(),
					Manifest.permission.CAMERA)) {
				//权限还没有授予，需要在这里写申请权限的代码
				ActivityCompat.requestPermissions(getActivity(),
						new String[]{Manifest.permission.CAMERA}, 60);
			} else {
				//权限已经被授予，在这里直接写要执行的相应方法即可
				ScannerActivity.startMeForResult(getActivity(), ScannerActivity.CODE_RESULT_ADDRESS);
			}
		} else if (v.getId() == R.id.submmit_btn) {
			submmit();
		} else if (v.getId() == R.id.arrow_right) {
			Bundle bundle = new Bundle();
			bundle.putString("assetId", model.asset);
			bundle.putString("code", model.asset);
			bundle.putInt("chain", selectChainPosition);
			bundle.putString("chainName", chain);
			CoinAddressActivity.startMe(getActivity(), CoinAddressActivity.FROM_WITHDRAW, CoinAddressActivity.CODE_REQUEST, bundle);
		} else if (v.getId() == R.id.ll_arrow_right) {
			Bundle bundle = new Bundle();
			bundle.putString("assetId", model.asset);
			bundle.putString("code", model.asset);
			bundle.putInt("chain", selectChainPosition);
			bundle.putString("chainName", chain);
			CoinAddressActivity.startMe(getActivity(), CoinAddressActivity.FROM_WITHDRAW, CoinAddressActivity.CODE_REQUEST, bundle);
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
//            addressValueEText.setText("");
			if (ableNumTv.getText().toString().equals("--")) {
				return;
			}
			if (Float.valueOf(ableNumTv.getText().toString()) > 0) {
				minWithDraw.setText(ableNumTv.getText().toString());
			}
		}
	}

	private void submmit() {
		String minDraw = minWithDraw.getText().toString();
		if (TextUtils.isEmpty(minDraw)) {
			ToastUtil.show(R.string.with_draw_num_is_empty);
			return;
		}
		String actralValue = actualCccountValue.getText().toString();

		if ("--".equals(actralValue)) {
			ToastUtil.show(R.string.min_withdraw);
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
		String address = addressValueEText.getText().toString().trim();
		if (TextUtils.isEmpty(address)) {
			ToastUtil.show(R.string.with_draw_address_is_empty);
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

		if (xrpLayout.getVisibility() == View.VISIBLE) {
			tagInput = destinationInput.getText().toString().trim();
			if (model.withdrawTag && TextUtils.isEmpty(tagInput)) {
				ToastUtil.show(String.format(getString(R.string.please_input), model.tagLabel));
				return;
			}
		}
		//巴西站kyc未认证 不能提币
		if (SiteController.getInstance().isBrSite() && !UserInfoController.getInstance().checkKycStatus()) {
			ToastUtil.show(R.string.br_kyc_tip);
			return;
		}


		submmit(actralValue, address, capPwdInput);
	}


	private void submmit(String amount, String address, String moneyPasswd) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", 1);
		httpParams.put("asset", model.asset);
		httpParams.put("quantity", amount);
		httpParams.put("address", address);
		httpParams.put("pin", MD5Util.MD5(moneyPasswd));
		httpParams.put("chain", chain);
		httpParams.put("verifyCode", msgCodeInput.getText().toString());

		httpParams.put("googleCode", googleCodeInput.getText().toString());

		if (!TextUtils.isEmpty(tagInput)) {
			httpParams.put("tag", tagInput);
		}
		OkGo.<BaseRes>post(Constants.ACCOUNT_WITHDARW_APPLY).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.withdraw_success);
				minWithDraw.setText("");
				addressValueEText.setText("");
				capInputView.setInputText("");
				msgCodeInput.setText("");
				googleCodeInput.setText("");
				destinationInput.setText("");
				//重新获取用户账号余额
				handler.sendEmptyMessageDelayed(sendDelayWhat, 500);
			}

			@Override
			public void onE(Response<BaseRes> response) {
			}

		});

	}


	public void setAddressValue(String addressValue) {
		addressValueEText.setText(TextUtils.isEmpty(addressValue) ? "" : addressValue);
	}

	public void setTagValue(String tagValue) {
		destinationInput.setText(TextUtils.isEmpty(tagValue) ? "" : tagValue);
	}


	private int count = 59;
	private int sendMsgWhat = 10;
	private int sendDelayWhat = 11;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (getActivity() != null) {
				if (msg.what == sendMsgWhat) {
					sendMsgCodeTv.setText(String.format(sendMsgWait, count));
					sendMsgCodeTv.setTextColor(getResources().getColor(R.color.res_textColor_2));
					sendMsgCodeTv.setOnClickListener(null);
					count--;
					if (count > 0) {
						handler.sendEmptyMessageDelayed(sendMsgWhat, 1000);
					} else {
						sendMsgCodeTv.setText(sendMsgAgain);
						sendMsgCodeTv.setTextColor(getResources().getColor(R.color.res_blue));
						sendMsgCodeTv.setOnClickListener(WithDrawFragment.this::onClick);
					}
				} else if (msg.what == sendDelayWhat) {
					getBalanceListRequest();
				}
			}
		}
	};

	private void sendMsgCode() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("type", Constants.CODE_TYPE_WITHDRAW);
		OkGo.<BaseRes>post(Constants.USER_SENDSMS).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.send_msg_code_success);
				count = 59;
				handler.sendEmptyMessage(sendMsgWhat);
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	private void sendEmailCode() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("mailType", Constants.MAIL_WITHDRAW);
		OkGo.<BaseRes>post(Constants.USER_SEND_MAIL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.send_msg_code_success);
				count = 59;
				handler.sendEmptyMessage(sendMsgWhat);
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

						String balance = BigDecimalUtils.subtract(balanceTable.totalBalance, balanceTable.frozenBalance);
						balance = TextUtils.isEmpty(balance) ? "0" : balance;

						subtractAndSetStr(balance);
					}
				} else {
					if (ableNumTv != null) {
						ableNumTv.setText("--");
					}
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


}
