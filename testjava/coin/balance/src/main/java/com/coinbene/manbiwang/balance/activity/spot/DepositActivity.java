package com.coinbene.manbiwang.balance.activity.spot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.balance.ChainsAdapter;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceChainTable;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.manbiwang.model.http.RechargeRes;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.SpaceItemDecoration;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.app.QrCodeDialog;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.google.zxing.client.result.ParsedResultType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.mylhyl.zxing.scanner.encode.QREncode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by mengxiangdong on 2017/11/28.
 * 充值页面
 */


@Route(path = RouteHub.Balance.depositActivity)
public class DepositActivity extends CoinbeneBaseActivity {
	@BindView(R2.id.iv_close)
	ImageView ivClose;
	@BindView(R2.id.search_img)
	ImageView searchImg;
	@BindView(R2.id.menu_line_view)
	View menuLineView;
	@BindView(R2.id.tv_tip1_title)
	TextView tvTip1Title;
	@BindView(R2.id.tv_tip1_describe)
	TextView tvTip1Describe;
	@BindView(R2.id.tb_tip1)
	ToggleButton tbTip1;
	@BindView(R2.id.tv_tip1_kown)
	TextView tvTip1Kown;
	@BindView(R2.id.rl_tip1)
	RelativeLayout rlTip1;
	@BindView(R2.id.tv_tip2_title)
	TextView tvTip2Title;
	@BindView(R2.id.tb_tip2)
	ToggleButton tbTip2;
	@BindView(R2.id.tv_tip2_kown)
	TextView tvTip2Kown;
	@BindView(R2.id.rl_tip2)
	RelativeLayout rlTip2;
	@BindView(R2.id.tv_dont_deposit)
	TextView tvDontDeposit;
	@BindView(R2.id.ll_do_deposit)
	LinearLayout llDoDeposit;
	@BindView(R2.id.view_center)
	View viewCenter;
	@BindView(R2.id.tv_deposit_address)
	TextView tvDepositAddress;
	@BindView(R2.id.iv_deposit_warn)
	ImageView ivDepositWarn;
	@BindView(R2.id.tv_dont_deposit1)
	TextView tvDontDeposit1;
	@BindView(R2.id.tv_deposit_address_value)
	TextView tvDepositAddressValue;
	@BindView(R2.id.tv_display_qr_code)
	TextView tvDisplayQrCode;
	@BindView(R2.id.tv_copy_address)
	TextView tvCopyAddress;
	@BindView(R2.id.rl_address)
	RelativeLayout rlAddress;
	@BindView(R2.id.view_center2)
	View viewCenter2;
	@BindView(R2.id.tv_tag_title)
	TextView tvTagTitle;
	@BindView(R2.id.tv_tag_value)
	TextView tvTagValue;
	@BindView(R2.id.tv_copy_tag)
	TextView tvCopyTag;
	@BindView(R2.id.ll_tag)
	LinearLayout llTag;
	@BindView(R2.id.tv_how_mach_sure)
	TextView tvHowMachSure;
	@BindView(R2.id.rl_tag)
	RelativeLayout rlTag;
	@BindView(R2.id.create_btn)
	TextView createBtn;
	@BindView(R2.id.ll_have_address)
	LinearLayout llHaveAddress;
	@BindView(R2.id.ll_tag_content)
	LinearLayout llTagContent;
	@BindView(R2.id.rl_chains)
	RecyclerView rlChains;
	@BindView(R2.id.ll_chains)
	LinearLayout llChains;
	private Unbinder mUnbinder;

	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.menu_right_tv)
	TextView rightView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.tv_deposit_address_tip)
	TextView tvDepositAddressTip;
	@BindView(R2.id.rl_deposit_address_tip)
	RelativeLayout rlDepositAddressTip;

	BalanceInfoTable model;
	private String title;
	QrCodeDialog qrCodeDialog;
	int logoSize;
	private int size;
	private String chain;
	private BalanceChainTable curTable;

	@Autowired(name = "model")
	String asset;

	public static void startMe(Context context, String asset) {
		Intent intent = new Intent(context, DepositActivity.class);
		intent.putExtra("model", asset);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_deposit;
	}

	@Override
	public void initView() {
		ARouter.getInstance().inject(this);
		logoSize = DensityUtil.dip2px(30);
		size = DensityUtil.dip2px(170);
		Intent intent = getIntent();
		model = BalanceController.getInstance().findByAsset(asset);
//        model = .(BalanceInfoTable) intent.getSerializableExtra("model");
		if (model == null) {
			ToastUtil.show(R.string.toast_data_transfer_error);
			finish();
			return;
		}
		init();
		initChainsData();
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
		backView.setOnClickListener(this);
		rightView.setOnClickListener(this);
		tvTip1Kown.setOnClickListener(this);
		tvTip2Kown.setOnClickListener(this);
		createBtn.setOnClickListener(this);
		tvCopyAddress.setOnClickListener(this);
		tvDisplayQrCode.setOnClickListener(this);
		tvCopyTag.setOnClickListener(this);
		qrCodeDialog = new QrCodeDialog(DepositActivity.this);
		qrCodeDialog.setOnClickListener(this::autoStoragePermission);
		rightView.setText(getText(R.string.recharge_coin_new));
		title = this.getResources().getString(R.string.recharge_title_new);
		title = String.format(title, model.asset);
		titleView.setText(title);

	}


	private void setDepositAddressTip() {
		if (model != null && !TextUtils.isEmpty(model.asset) && model.asset.equals("ETH")) {
			tvDepositAddressTip.setText(R.string.deposit_address_tip_eth);
			rlDepositAddressTip.setVisibility(View.VISIBLE);
		} else if (!TextUtils.isEmpty(chain) && chain.equals("ETH")) {
			tvDepositAddressTip.setText(R.string.deposit_address_tip_usdt_erc);
			rlDepositAddressTip.setVisibility(View.VISIBLE);
		} else {
			rlDepositAddressTip.setVisibility(View.GONE);
		}
	}



	private void initAssetData(BalanceInfoTable model) {
		chain = model.chain;
		String tips1 = getResources().getString(R.string.recharge_tips1_new);
		String tipStr1 = String.format(tips1, model.minDeposit, model.asset);
		tvTip1Title.setText(tipStr1);
//        String tips2 = getResources().getString(R.string.recharge_tips2_new);
//        String tipStr2 = String.format(tips2, model.localAssetName, model.asset, model.minDeposit);
//        tvTip1Describe.setText(tipStr2);
		String tips2 = getResources().getString(R.string.recharge_tips2_new_version);
		String tipStr2 = String.format(tips2, model.minDeposit);
		tvTip1Describe.setText(tipStr2);
		if (!TextUtils.isEmpty(model.depositMinConfirmations)) {
			tvHowMachSure.setVisibility(View.VISIBLE);
			String tips5 = getResources().getString(R.string.recharge_tips5_new);
			String tipStr5 = String.format(tips5, model.depositMinConfirmations);
			tvHowMachSure.setText(tipStr5);
		} else {
			tvHowMachSure.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(model.depositHints)) {
			tvTip2Title.setText(model.depositHints);
			rlTip2.setVisibility(View.VISIBLE);
		} else {
			rlTip2.setVisibility(View.GONE);
		}
		setDepositAddressTip();
	}

	private void initChainData(BalanceChainTable chainTable) {
		if (chainTable == null) {
			return;
		}
		chain = chainTable.chain;
		tbTip1.setChecked(false);
		tbTip2.setChecked(false);
		String tips1 = getResources().getString(R.string.recharge_tips1_new);
		String tipStr1 = String.format(tips1, chainTable.minDeposit, chainTable.asset);
		tvTip1Title.setText(tipStr1);
		String tips2 = getResources().getString(R.string.recharge_tips2_new_version);
		String tipStr2 = String.format(tips2, chainTable.minDeposit);
		tvTip1Describe.setText(tipStr2);
		if (!TextUtils.isEmpty(chainTable.depositMinConfirmations)) {
			tvHowMachSure.setVisibility(View.VISIBLE);
			String tips5 = getResources().getString(R.string.recharge_tips5_new);
			String tipStr5 = String.format(tips5, chainTable.depositMinConfirmations);
			tvHowMachSure.setText(tipStr5);
		} else {
			tvHowMachSure.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(chainTable.depositHints)) {
			tvTip2Title.setText(model.depositHints);
			rlTip2.setVisibility(View.VISIBLE);
		} else {
			rlTip2.setVisibility(View.GONE);
		}
		setDepositAddressTip();
	}


	private void initChainsData() {
		if (model.balanceChains != null && model.balanceChains.size() > 0) {
			GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
			rlChains.addItemDecoration(new SpaceItemDecoration(3, DensityUtil.dip2px(15)));
			rlChains.setLayoutManager(linearLayoutManager);
			ChainsAdapter chainsAdapter = new ChainsAdapter(ChainsAdapter.DEPOSIT_TYPE);
			rlChains.setAdapter(chainsAdapter);
			for (BalanceChainTable table : model.balanceChains) {
				if (table.deposit) {
					curTable = table;
					break;
				}
			}
			if (curTable != null) {
				chainsAdapter.setLists(model.balanceChains, curTable.protocolName);
			}else {
				chainsAdapter.setLists(model.balanceChains, "");
			}
			chainsAdapter.setOnItemClickLisenter((selectStr, position) -> {
				chainsAdapter.setSelect(selectStr);
				initChainData(model.balanceChains.get(position));
				queryRechargeAddress(model.balanceChains.get(position).chain, model.balanceChains.get(position).asset);
			});
			initChainData(curTable);
			queryRechargeAddress(curTable.chain, curTable.asset);
		} else {
			llChains.setVisibility(View.GONE);
			initAssetData(model);
			queryRechargeAddress(model.chain, model.asset);
		}


	}

	private void queryRechargeAddress(String chain, String asset) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", 1);
		httpParams.put("asset", asset);
		httpParams.put("chain", chain);
		OkGo.<RechargeRes>get(Constants.ACCOUNT_ADDRESS_GET_DEPOSIT).tag(this).params(httpParams).execute(new DialogCallback<RechargeRes>(DepositActivity.this) {
			@Override
			public void onSuc(Response<RechargeRes> response) {
				RechargeRes rechargeRes = response.body();
				if (rechargeRes.isSuccess()) {
					if (rechargeRes.data == null || TextUtils.isEmpty(rechargeRes.data.address)) {
						llDoDeposit.setVisibility(View.VISIBLE);
						llHaveAddress.setVisibility(View.GONE);
						//没有地址需要默认未选中
					} else {
						setQrImageView(rechargeRes.data);
						llHaveAddress.setVisibility(View.VISIBLE);
						llDoDeposit.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void onE(Response<RechargeRes> response) {

			}

		});
	}

	private void createRechargeAddress() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("accountType", 1);
		httpParams.put("asset", model.asset);
		httpParams.put("chain", chain);
		OkGo.<RechargeRes>post(Constants.ACCOUNT_ADDRESS_NEW_DEPOSIT).tag(this).params(httpParams).execute(new DialogCallback<RechargeRes>(this) {
			@Override
			public void onSuc(Response<RechargeRes> response) {
				RechargeRes rechargeRes = response.body();
				if (rechargeRes.isSuccess() && rechargeRes.data != null && !TextUtils.isEmpty(rechargeRes.data.address)) {
					setQrImageView(rechargeRes.data);
					llDoDeposit.setVisibility(View.GONE);
					llHaveAddress.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onE(Response<RechargeRes> response) {
			}

		});
	}

//    private ProgressDialog progress;

	//    private String xrpStr;
//
	public void setQrImageView(RechargeRes.DataBean dataBean) {
		if (model.useTag) {
			llTagContent.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(model.tagLabel)) {
				tvTagTitle.setText(model.tagLabel);
				tvCopyTag.setText(String.format(getString(R.string.copy_tag_title), model.tagLabel));
			}

			if (!TextUtils.isEmpty(dataBean.getTag())) {
				tvTagValue.setText(dataBean.getTag());
			}
		} else {
			llTagContent.setVisibility(View.GONE);
		}

		String addressStr = dataBean.address;
		if (null == addressStr) {
			return;
		}
		tvDepositAddressValue.setText(addressStr);

//        new Thread(runnable).start();
		bitmap = new QREncode.Builder(DepositActivity.this)
				.setMargin(0)//二维码边框
				//二维码类型
				.setParsedResultType(ParsedResultType.TEXT)
				//二维码内容
				.setContents(addressStr)
				.setSize(size)//二维码等比大小
//                .setLogoBitmap(bm, logoSize)
				.build().encodeAsBitmap();
	}

	private Bitmap bitmap;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.menu_right_tv) {
			ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.RECHARGE);
		} else if (v.getId() == R.id.create_btn) {
			if (rlTip2.getVisibility() == View.VISIBLE) {

				if (tbTip1.isChecked() && tbTip2.isChecked())
					createRechargeAddress();
				else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			} else {
				if (tbTip1.isChecked())
					createRechargeAddress();
				else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			}
		} else if (v.getId() == R.id.tv_tip1_kown) {
			if (tbTip1.isChecked()) {
				tbTip1.setChecked(false);
			} else {
				tbTip1.setChecked(true);
			}
		} else if (v.getId() == R.id.tv_tip2_kown) {
			if (tbTip2.isChecked()) {
				tbTip2.setChecked(false);
			} else {
				tbTip2.setChecked(true);
			}
		} else if (v.getId() == R.id.tv_copy_address) {
			if (rlTip2.getVisibility() == View.VISIBLE) {
				if (tbTip1.isChecked() && tbTip2.isChecked()) {
					StringUtils.copyStrToClip(tvDepositAddressValue.getText().toString());
				} else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			} else {
				if (tbTip1.isChecked()) {
					StringUtils.copyStrToClip(tvDepositAddressValue.getText().toString());
				} else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			}
		} else if (v.getId() == R.id.tv_copy_tag) {
			if (rlTip2.getVisibility() == View.VISIBLE) {
				if (tbTip1.isChecked() && tbTip2.isChecked()) {
					StringUtils.copyStrToClip(tvTagValue.getText().toString());
				} else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			} else {
				if (tbTip1.isChecked()) {
					StringUtils.copyStrToClip(tvTagValue.getText().toString());
				} else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			}
		} else if (v.getId() == R.id.tv_display_qr_code) {
			if (rlTip2.getVisibility() == View.VISIBLE) {
				if (tbTip1.isChecked() && tbTip2.isChecked()) {

					if (qrCodeDialog == null) {
						qrCodeDialog = new QrCodeDialog(DepositActivity.this);
					}
					qrCodeDialog.show();
					if (bitmap != null)
						qrCodeDialog.setBitmap(bitmap);
					if (!TextUtils.isEmpty(title)) {
						qrCodeDialog.setTitle(title);
					}

				} else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			} else {
				if (tbTip1.isChecked()) {
					if (qrCodeDialog == null) {
						qrCodeDialog = new QrCodeDialog(DepositActivity.this);
					}
					qrCodeDialog.show();
					if (bitmap != null)
						qrCodeDialog.setBitmap(bitmap);
					if (!TextUtils.isEmpty(title)) {
						qrCodeDialog.setTitle(title);
					}
				} else {
					ToastUtil.show(getString(R.string.please_read_tips));
				}
			}
		}
	}


	private static final int CODE_GALLERY_REQUEST = 0xa0;

	private void autoStoragePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_GALLERY_REQUEST);
		} else {
			if (bitmap != null) {
				if (PhotoUtils.saveImageToGallery(DepositActivity.this, bitmap)) {
					ToastUtil.show(getString(R.string.save_suc));
					if (qrCodeDialog != null && qrCodeDialog.isShowing())

						qrCodeDialog.dismiss();
				} else
					ToastUtil.show(getString(R.string.save_err));
			}
		}

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length == 0) {
			return;
		}
		switch (requestCode) {
			//调用系统相册申请Sdcard权限回调
			case CODE_GALLERY_REQUEST:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (bitmap != null) {
						if (PhotoUtils.saveImageToGallery(DepositActivity.this, bitmap)) {
							ToastUtil.show(getString(R.string.save_suc));
							if (qrCodeDialog != null && qrCodeDialog.isShowing())
								qrCodeDialog.dismiss();
						} else
							ToastUtil.show(getString(R.string.save_err));
					}
				}
				break;
			default:
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}
}
