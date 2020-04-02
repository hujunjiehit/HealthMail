package com.coinbene.manbiwang.contract.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.rxjava.FlowControl;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.widget.dialog.BaseDialog;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.ForceCalculateModel;
import com.coinbene.manbiwang.model.http.MarginInfoModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

/**
 * ding
 * 2019-05-16
 * com.coinbene.manbiwang.widget.dialog
 */
public class UpdateMarginDialog extends BaseDialog {

	private TextView textIncrease;
	private TextView textDecrease;
	private EditText input;
	private TextView cancel;
	private TextView confirm;
	public static final String TYPE_INCREASE = "add";
	public static final String TYPE_DECREASE = "sub";
	private String symbol;
	private String pid;
	private TextView tvPriceUnit,textView19;

	private String mType = TYPE_INCREASE;
	private TextView symbolMOL;
	private TextView symbolForce;
	private TextView textVol;
	private MarginInfoModel.DataBean data;
	private TextView textForce;
	private Activity mAcitivity;
	private FlowControl mFlowControl;
	private ContractType contractType;

	public UpdateMarginDialog(@NonNull Context context) {
		super(context);
		mAcitivity = (Activity) context;
		initDialog();
	}

	private void initDialog() {
		Window window = getWindow();
		if (window == null) {
			return;
		}
		WindowManager.LayoutParams wmlp = window.getAttributes();
		wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wmlp.gravity = Gravity.CENTER;
		window.setAttributes(wmlp);
	}

	@Override
	public int getLayoutRes() {
		return R.layout.common_dialog_update_margin;
	}


	@Override
	public void initView() {
		textIncrease = findViewById(R.id.update_increase);
		textDecrease = findViewById(R.id.update_decrease);
		cancel = findViewById(R.id.update_cancel);
		confirm = findViewById(R.id.update_confirm);
		input = findViewById(R.id.update_input);
		symbolMOL = findViewById(R.id.symbol_MOL);
		textVol = findViewById(R.id.mol_vol);
		textForce = findViewById(R.id.force_price);
		symbolForce = findViewById(R.id.symbol_force);
		tvPriceUnit = findViewById(R.id.tv_price_unit);
		textView19 =  findViewById(R.id.textView19);
		selectTab(mType);
	}

	@Override
	public void listener() {
		textDecrease.setOnClickListener(v -> selectTab(TYPE_DECREASE));
		textIncrease.setOnClickListener(v -> selectTab(TYPE_INCREASE));
		cancel.setOnClickListener(v -> {
			dismiss();
			KeyboardUtils.hideKeyboard(cancel);
		});
		confirm.setOnClickListener(v -> updateMargin());

		input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				PrecisionUtils.setPrecision(input, s, 4);
				if (TextUtils.isEmpty(input.getText().toString())) {
					textForce.setText("--");
					return;
				}


				if (TextUtils.isEmpty(textVol.getText().toString()) || textVol.getText().toString().equals("--") || s.toString().equals(".")) {
					return;
				}

				if (BigDecimalUtils.isLessThan(textVol.getText().toString(), s.toString())) {
					input.setText(textVol.getText());
					input.setSelection(textVol.getText().length());
				}

				if (TextUtils.isEmpty(input.getText().toString())) {
					textForce.setText("--");
					return;
				}

				getForcePrice(input.getText().toString(), mType);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void selectTab(String type) {

		textIncrease.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
		textDecrease.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));

		textIncrease.setTextColor(getContext().getResources().getColor(R.color.res_textColor_1));
		textDecrease.setTextColor(getContext().getResources().getColor(R.color.res_textColor_1));

		if (type.equals(TYPE_INCREASE)) {
			textIncrease.setBackgroundResource(R.drawable.shape_iod_left);
			textIncrease.setTextColor(getContext().getResources().getColor(R.color.res_white));
		} else {
			textDecrease.setBackgroundResource(R.drawable.shape_iod_right);
			textDecrease.setTextColor(getContext().getResources().getColor(R.color.res_white));
		}

		mType = type;

		input.setText("");

		setConfig(mType);

	}


	@Override
	public void show() {
		super.show();
		if(contractType==ContractType.USDT){
			tvPriceUnit.setText("USDT");
			textView19.setText("USDT");
		}else {
			tvPriceUnit.setText("BTC");
			textView19.setText("BTC");
		}
		input.setText("");
		getMarginInfo();
	}

	/**
	 * @param type 设置专属文案
	 */
	private void setConfig(String type) {
		if (type.equals(TYPE_INCREASE)) {
			input.setHint(R.string.update_bail_increase);
			symbolMOL.setText(R.string.update_bail_most);
			symbolForce.setText(R.string.updated_bail_increase);
		} else {
			input.setHint(R.string.update_bail_decrease);
			symbolMOL.setText(R.string.update_bail_least);
			symbolForce.setText(R.string.updated_bail_decrease);
		}

		getMarginInfo();

	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
		if (input != null && textForce != null && TextUtils.isEmpty(input.getText().toString())) {
			textForce.setText("--");
			if (!TextUtils.isEmpty(input.getText().toString()))
				getForcePrice(input.getText().toString(), mType);
		}

	}

	public void setPositionId(String id) {
		pid = id;
	}


	/**
	 * 获取最多或最少的保证金信息
	 */
	private void getMarginInfo() {

		if (TextUtils.isEmpty(pid) || TextUtils.isEmpty(symbol)) {
			return;
		}

		String url = contractType == ContractType.USDT ? Constants.CONTRACT_MARGIN_INFO_USDT : Constants.CONTRACT_MARGIN_INFO;


		HttpParams params = new HttpParams();
		params.put("positionId", pid);
		params.put("symbol", symbol);

		OkGo.<MarginInfoModel>get(url).tag(this).params(params).execute(new NewJsonSubCallBack<MarginInfoModel>() {
			@Override
			public void onSuc(Response<MarginInfoModel> response) {

				if (response.body().getData() == null) {
					return;
				}

				data = response.body().getData();

				if (mType.equals(TYPE_INCREASE)) {
					textVol.setText(data.getMaxAdd());
				} else {
					textVol.setText(data.getMaxSub());
				}

			}

			@Override
			public void onE(Response<MarginInfoModel> response) {

			}
		});
	}

	/**
	 * @param amount
	 * @param type   计算强平价格
	 */
	@AddFlowControl(strategy = FlowControlStrategy.debounce, timeInterval = 300)
	private void getForcePrice(String amount, String type) {
		if (data == null || TextUtils.isEmpty(amount)) {
			return;
		}
		String url = contractType == ContractType.USDT ? Constants.CONTRACT_CALCULATE_FROCE_USDT : Constants.CONTRACT_CALCULATE_FROCE;

		HttpParams params = new HttpParams();
		params.put("symbol", data.getSymbol());
		params.put("positionId", data.getPositionId());
		params.put("adjustType", type);
		params.put("amount", amount);
		params.put("side", data.getSide());
		params.put("quatity", data.getQuatity());
		params.put("unfrozenBalance", data.getUnfrozenBalance());
		params.put("margin", data.getMargin());
		params.put("serviceFee", data.getServiceFee());
		params.put("avgPrice", data.getAvgPrice());

		OkGo.<ForceCalculateModel>get(url).params(params).tag(this).execute(new NewJsonSubCallBack<ForceCalculateModel>() {
			@Override
			public void onSuc(Response<ForceCalculateModel> response) {

				if (TextUtils.isEmpty(response.body().getData())) {
					return;
				}

				String forcePrice = response.body().getData();

				textForce.setText(forcePrice);

			}

			@Override
			public void onE(Response<ForceCalculateModel> response) {

			}
		});


	}


	private void updateMargin() {

		if (BigDecimalUtils.isEmptyOrZero(input.getText().toString())) {
			if (mType.equals(TYPE_INCREASE)) {
				ToastUtil.show(getContext().getResources().getString(R.string.update_bail_increase));
			} else {
				ToastUtil.show(getContext().getResources().getString(R.string.update_bail_decrease));
			}
			return;
		}
		String url = contractType == ContractType.USDT ? Constants.CONTRACT_UPDATE_MARGIN_USDT : Constants.CONTRACT_UPDATE_MARGIN;


		HttpParams params = new HttpParams();
		params.put("symbol", data.getSymbol());
		params.put("positionId", data.getPositionId());
		params.put("adjustType", mType);
		params.put("amount", input.getText().toString());
		params.put("side", data.getSide());

		OkGo.<BaseRes>post(url).params(params).tag(this).execute(new NewDialogCallback<BaseRes>(mAcitivity) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(getContext().getResources().getString(R.string.submit_success));
				KeyboardUtils.hideKeyboard(input);
				dismiss();
			}

			@Override
			public void onE(Response<BaseRes> response) {
//                ToastUtil.show(getContext().getResources().getString(R.string.submit_fail));
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});

	}


	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		OkGo.getInstance().cancelTag(this);
		KeyboardUtils.hideKeyboard(input);
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;



	}
}
