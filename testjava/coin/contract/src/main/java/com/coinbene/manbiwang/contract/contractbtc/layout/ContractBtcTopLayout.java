package com.coinbene.manbiwang.contract.contractbtc.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.Product;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.ProtocolDialog;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.dialog.PositionModelDialog;
import com.coinbene.manbiwang.contract.listener.TopViewGroupListener;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContractBtcTopLayout extends LinearLayout implements View.OnClickListener {
	@BindView(R2.id.tv_available_balance_value)
	TextView tvAvailableBalanceValue;
	@BindView(R2.id.tv_available_balance)
	TextView tvAvailableBalance;

	//    @BindView(R.id.tv_estimate_force_price_value)
//    TextView tvEstimateForcePriceValue;
	@BindView(R2.id.tv_position_model)
	TextView tvPositionModel;

	@BindView(R2.id.tv_return_percent_value)
	TextView tvReturnPercentValue;
	@BindView(R2.id.tv_account_balance_value)
	TextView tvAccountBalanceValue;
	@BindView(R2.id.tv_unrealized_profit_loss_value)
	TextView tvUnrealizedProfitLossValue;
	@BindView(R2.id.tv_current_balance_value)
	TextView tvCurrentBalanceValue;
	@BindView(R2.id.rl_display_hide)
	RelativeLayout rlDisplayHide;
	@BindView(R2.id.iv_display_hide)
	ImageView ivDisplayHide;
	@BindView(R2.id.iv_balance_transfer)
	ImageView ivBalanceTransfer;

	@BindView(R2.id.ll_display_hide)
	LinearLayout llDisplayHide;
	@BindView(R2.id.ll_transfer)
	LinearLayout llTransfer;
	@BindView(R2.id.ll_model)
	LinearLayout llModel;

	private TopViewGroupListener listener;

	static final String TAG = "ContractTopLayout";


	/**
	 * 当前全逐仓模式
	 */
	String marginMode = Constants.MODE_CROSSED;
	String mMode;

	/**
	 * 是否可切换全逐仓
	 */
	boolean isChange = true;
	private PositionModelDialog dialog;

	public ContractBtcTopLayout(Context context) {
		super(context);
	}

	public ContractBtcTopLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ContractBtcTopLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
		initView();
		initListener();
	}

	private void initListener() {
		llDisplayHide.setOnClickListener(this);
		llTransfer.setOnClickListener(this);
		tvPositionModel.setOnClickListener(this);
		llModel.setOnClickListener(this);
	}

	private void initView() {
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_display_hide) {
			if (rlDisplayHide.getVisibility() == View.VISIBLE) {
				rlDisplayHide.setVisibility(GONE);
				ivDisplayHide.setImageResource(R.drawable.icon_bottom_arrow);
			} else {
				rlDisplayHide.setVisibility(VISIBLE);
				ivDisplayHide.setImageResource(R.drawable.icon_top_arrow);
			}
		} else if (id == R.id.ll_transfer) {

			doTranfer();


		} else if (id == R.id.ll_model) {
			showChangeModelDialog();
		} else if (id == R.id.tv_position_model) {
			showChangeModelDialog();
		}
	}

	@NeedLogin(jump = true)
	private void doTranfer() {

		if (!SpUtil.getProtocolStatusOfContract("btc")) {
			ProtocolDialog dialog = new ProtocolDialog(getContext());
			dialog.setTitle(getContext().getResources().getString(R.string.btc_protocol_title));
			dialog.setContent(getContext().getResources().getString(R.string.btc_protocol_content));
			dialog.setPositiveText(getContext().getResources().getString(R.string.go_trading));
			dialog.setNegativeText(getContext().getResources().getString(R.string.not_trading));
			dialog.setProtocolText(getContext().getResources().getString(R.string.btc_protocol_text));
			dialog.setProtocolUrl(UrlUtil.getBtcProtocol());
			dialog.show();

			dialog.setDialogListener(new DialogListener() {
				@Override
				public void clickNegative() {

				}

				@Override
				public void clickPositive() {
					listener.openContract();
				}
			});
			return;
		}
		UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
				new TransferParams()
						.setAsset("BTC")
						.setFrom(Product.NAME_SPOT)
						.setTo(Product.NAME_BTC_CONTRACT)
						.toBundle());
	}

	@NeedLogin(jump = true)
	private void showChangeModelDialog() {

		if (!isChange) {
			ToastUtil.show(getContext().getResources().getString(R.string.marginMode_change_remind));
			return;
		}

		dialog = new PositionModelDialog(getContext());

		dialog.setDefaultMode(marginMode);

		dialog.setChangeListener(mode -> {
			mMode = mode;
			if (listener != null && !mode.equals(marginMode)) {
				listener.updatePositonMode(mode);
			}
		});

		dialog.show();
	}

	/**
	 * 未登录或者未解锁的时候
	 */
	public void setNoUser() {

		tvAvailableBalanceValue.setText("0");
		tvReturnPercentValue.setText("0%");
		tvAccountBalanceValue.setText("0");
		tvUnrealizedProfitLossValue.setText("0");
		tvCurrentBalanceValue.setText("0");
		tvReturnPercentValue.setTextColor(getResources().getColor(R.color.res_textColor_1));
		tvUnrealizedProfitLossValue.setTextColor(getResources().getColor(R.color.res_textColor_1));
		tvPositionModel.setText(getContext().getString(R.string.crossed_position));

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

	}

	public void setData(ContractAccountInfoModel.DataBean dataBean) {
		if (dataBean == null) {
			return;
		}
		tvAvailableBalanceValue.setText(TextUtils.isEmpty(dataBean.getAvailableBalance()) ? "0" : dataBean.getAvailableBalance());
//        tvEstimateForcePriceValue.setText(TextUtils.isEmpty(dataBean.getLiquidationPrice()) ? "0" : dataBean.getLiquidationPrice());
		tvReturnPercentValue.setText(TextUtils.isEmpty(dataBean.getRoe()) ? "0%" : BigDecimalUtils.toPercentage(dataBean.getRoe()));
		tvAccountBalanceValue.setText(TextUtils.isEmpty(dataBean.getBalance()) ? "0" : dataBean.getBalance());
		tvUnrealizedProfitLossValue.setText(TextUtils.isEmpty(dataBean.getUnrealisedPnl()) ? "0" : dataBean.getUnrealisedPnl());
		tvCurrentBalanceValue.setText(TextUtils.isEmpty(dataBean.getMarginBalance()) ? "0" : dataBean.getMarginBalance());

		if (BigDecimalUtils.lessThanToZero(dataBean.getUnrealisedPnl())) {
			tvUnrealizedProfitLossValue.setTextColor(SwitchUtils.isRedRise() ?
					getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		} else {
			tvUnrealizedProfitLossValue.setTextColor(SwitchUtils.isRedRise() ?
					getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		}
		if (BigDecimalUtils.lessThanToZero(dataBean.getRoe())) {
			tvReturnPercentValue.setTextColor(SwitchUtils.isRedRise() ?
					getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		} else {
			tvReturnPercentValue.setTextColor(SwitchUtils.isRedRise() ?
					getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		}
	}

	public void setMarginMode(String mode, String modeSetting) {

		if (TextUtils.isEmpty(mode)) {
			isChange = true;
		} else {
			isChange = false;
		}

		if (!TextUtils.isEmpty(modeSetting)) {
			marginMode = modeSetting.equals(Constants.MODE_CROSSED) ? Constants.MODE_CROSSED : Constants.MODE_FIXED;
			tvPositionModel.setText(marginMode.equals(Constants.MODE_FIXED) ? getContext().getString(R.string.gradually_position) : getContext().getString(R.string.crossed_position));
		}
	}

	/**
	 * @return 返回当前保证金模式
	 */
	public String getMarginMode() {
		return marginMode;
	}

	public void setTopViewGroupListener(TopViewGroupListener listener) {
		this.listener = listener;
	}

	public void updateModeSuccess() {
		marginMode = mMode;
		tvPositionModel.setText(marginMode.equals(Constants.MODE_FIXED) ? getContext().getString(R.string.gradually_position) : getContext().getString(R.string.crossed_position));
		ToastUtil.show(R.string.finger_set_success);
	}
}
