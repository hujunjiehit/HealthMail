package com.coinbene.manbiwang.contract.contractusdt.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.Product;

import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.ProtocolDialog;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.dialog.CommuinityDialog;
import com.coinbene.manbiwang.contract.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

/**
 * ding
 * 2019-05-13
 * com.coinbene.manbiwang.widget
 */
public class ContractUsdtMenu extends PopupWindow implements View.OnClickListener {


	private View itemTransfer;
	private View itemGuide;
	private View itemJoinGroup;
	private View contentView;
	private View itemUnitSwitch;
	private View menuMargin;

	private String symbol;
	private String lever;

	private Context context;

	private CommuinityDialog commuinityDialog;
	private onMenuClickListener onMenuClickListener;
	private boolean canChangeMode;

	public ContractUsdtMenu(Context context) {
		super(context);
		this.context = context;

		View view = View.inflate(context, R.layout.menu_contract_usdt, null);

		setFocusable(true);

		setClippingEnabled(false);

		setOutsideTouchable(true);

		setHeight(WindowManager.LayoutParams.MATCH_PARENT);

		setWidth(WindowManager.LayoutParams.MATCH_PARENT);

		setContentView(view);

		setBackgroundDrawable(new BitmapDrawable());

		init();

		listener();
	}


	private void init() {

		contentView = getContentView();
		itemTransfer = contentView.findViewById(R.id.menu_Transfer);
		itemGuide = contentView.findViewById(R.id.menu_Guide);
		itemJoinGroup = contentView.findViewById(R.id.menu_join_group);
		itemUnitSwitch = contentView.findViewById(R.id.menu_unit_switch);
		menuMargin = contentView.findViewById(R.id.menu_margin);
	}

	private void listener() {
		contentView.setOnClickListener(this);
		itemTransfer.setOnClickListener(this);
		itemGuide.setOnClickListener(this);
		itemJoinGroup.setOnClickListener(this);
		itemUnitSwitch.setOnClickListener(this);
		menuMargin.setOnClickListener(this);
	}


	public void setOnMenuClickListener(ContractUsdtMenu.onMenuClickListener onMenuClickListener) {
		this.onMenuClickListener = onMenuClickListener;
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.menu_Transfer) {
			dismiss();

			doTransFer(v);


		} else if (id == R.id.menu_margin) {


			changeMarginMode(v);

		} else if (id == R.id.menu_Guide) {
			dismiss();
			//ContractGuideActivity.startMe(v.getContext());
			Bundle bundle = new Bundle();
			bundle.putString(Constants.WEB_EXTRA_TITLE, context.getResources().getString(R.string.res_usdt_contract_guide));
			bundle.putInt(Constants.WEB_EXTRA_RIGHT_IMAGE, R.drawable.guide_title_rigth);
			bundle.putString(Constants.WEB_EXTRA_RIGHT_URL, "coinbene://" + UIRouter.HOST_CUSTOMER);
			UIBusService.getInstance().openUri(context, UrlUtil.getUsdtContractGuideUrl(), bundle);
		} else if (id == R.id.menu_join_group) {
			dismiss();
			if (commuinityDialog == null) {
				commuinityDialog = new CommuinityDialog(context);
			}
			commuinityDialog.setQrCode(R.drawable.community_wechat_code_future);
			commuinityDialog.setWechatNumber("CoinBene_Future");
			commuinityDialog.show();


			dismiss();
		} else if (id == R.id.menu_unit_switch) {
			if (onMenuClickListener != null) {
				onMenuClickListener.clickUnitSwitch();
			}
			dismiss();
		} else {
			dismiss();
		}


	}


	@NeedLogin(jump = true)
	private void changeMarginMode(View v) {
		if (!canChangeMode) {
			ToastUtil.show(v.getContext().getString(R.string.marginMode_change_remind));
			return;
		}

		dismiss();
		if (onMenuClickListener != null) {
			onMenuClickListener.marginMode();
		}
	}


	@NeedLogin(jump = true)
	private void doTransFer(View v) {
		if (!SpUtil.getProtocolStatusOfContract("usdt")) {
			ProtocolDialog dialog = new ProtocolDialog(v.getContext());
			dialog.setTitle(v.getContext().getResources().getString(R.string.usdt_protocol_title));
			dialog.setContent(v.getContext().getResources().getString(R.string.usdt_protocol_content));
			dialog.setPositiveText(v.getContext().getResources().getString(R.string.go_trading));
			dialog.setNegativeText(v.getContext().getResources().getString(R.string.not_trading));
			dialog.setProtocolText(v.getContext().getResources().getString(R.string.usdt_protocol_text));
			dialog.setProtocolUrl(UrlUtil.getUsdtProtocol());
			dialog.show();

			dialog.setDialogListener(new DialogListener() {
				@Override
				public void clickNegative() {

				}

				@Override
				public void clickPositive() {
					userConfigCreate("usdtContract_protocol", "1");
				}
			});
			return;
		}


		//AccountTransferActivity.startActivity(context);
		UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
				new TransferParams()
						.setAsset("USDT")
						.setFrom(Product.NAME_SPOT)
						.setTo(Product.NAME_USDT_CONTRACT)
						.toBundle());
	}

	private void userConfigCreate(String key, String value) {
		HttpParams params = new HttpParams();
		params.put("settingKey", key);
		params.put("settingValue", value);

		OkGo.<BaseRes>post(Constants.UPDATE_USER_CONFIG).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				if (response.isSuccessful()) {
					SpUtil.setProtocolStatusForContract("usdt", true);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}


	public void showAtLocation(View parent) {
		showAtLocation(parent, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	public void setDefaultSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setDefaultLever(String lever) {
		this.lever = lever;
	}

	public void setCanChangeMode(String marginMode) {
		if (TextUtils.isEmpty(marginMode)) {
			canChangeMode = true;
		} else {
			canChangeMode = false;
		}
	}


	public interface onMenuClickListener {
		void clickUnitSwitch();

		void marginMode();
	}
}
