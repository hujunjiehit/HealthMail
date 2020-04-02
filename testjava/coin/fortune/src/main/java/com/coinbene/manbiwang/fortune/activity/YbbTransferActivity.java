package com.coinbene.manbiwang.fortune.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.manbiwang.model.http.YbbTransferModel;
import com.coinbene.manbiwang.model.http.YbbTransferPageInfoModel;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.fortune.R;
import com.coinbene.manbiwang.fortune.R2;
import com.coinbene.manbiwang.fortune.manager.FortuneManager;
import com.coinbene.manbiwang.service.RouteHub;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.coinbene.manbiwang.service.RouteHub.Fortune.TRANSFER_TYPE_IN;


/**
 * 余币宝转入转出页面，理财账户内部划转
 */
@Route(path = RouteHub.Fortune.ybbTransferActivity)
public class YbbTransferActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.btn_select_asset)
	QMUIRoundButton mBtnSelectAsset;
	@BindView(R2.id.editText)
	EditText mEditText;
	@BindView(R2.id.tv_available)
	TextView mTvAvailable;
	@BindView(R2.id.tv_personal_quota)
	TextView mTvPersonalQuota;
	@BindView(R2.id.btn_confirm)
	QMUIRoundButton mBtnConfirm;
	@BindView(R2.id.tv_transfer_amount_desc)
	TextView mTvTransferAmountDesc;
	@BindView(R2.id.tv_all)
	TextView mTvAll;
	@BindView(R2.id.tv_personal_quota_value)
	TextView tvPersonalQuotaValue;

	@Autowired
	int type;

	@Autowired
	String asset;

	SelectorDialog<String> assetSelectDialog;

	YbbTransferPageInfoModel.DataBean mData;

	@Override
	public int initLayout() {
		return R.layout.fortune_activity_transfer;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(type == TRANSFER_TYPE_IN ? R.string.res_transfer_to_ybb : R.string.res_transfer_from_ybb);
		if (!TextUtils.isEmpty(asset)) {
			mBtnSelectAsset.setText(asset);
		}
	}

	@Override
	public void setListener() {
		mBtnSelectAsset.setOnClickListener(v -> {
			if (assetSelectDialog != null) {
				assetSelectDialog.setDefaultData(asset);
				assetSelectDialog.show();
			}
		});

		mTvAll.setOnClickListener(v -> {
			if (mData != null && !TextUtils.isEmpty(mData.getAvailableBalance())) {
				mEditText.setText(mData.getAvailableBalance());
			}
		});

		mBtnConfirm.setOnClickListener(v -> doActionTransfer(asset, mEditText.getText().toString().trim()));
	}

	@Override
	public void initData() {
		FortuneManager.getInstance().getYbbAssetList(assetList -> {
			initAssetSelectorDialog(assetList);

			if (TextUtils.isEmpty(asset)) {
				asset = assetList.get(0);
				mBtnSelectAsset.setText(asset);
			}

			initTransferPageInfo();
		});
	}

	private void initAssetSelectorDialog(List<String> assetList) {
		if (assetSelectDialog == null) {
			assetSelectDialog = new SelectorDialog<>(this);
			assetSelectDialog.setSelectListener((data, positon) -> {
				asset = data;
				mBtnSelectAsset.setText(asset);
				initTransferPageInfo();
			});
		}
		assetSelectDialog.setDefaultData(asset);
		assetSelectDialog.setDatas(assetList);
	}

	/**
	 * 余币宝划转页面初始化接口
	 */
	@NeedLogin
	private void initTransferPageInfo() {
		String url = type == TRANSFER_TYPE_IN ? Constants.YBB_TRANSFER_IN_PAGEINFO : Constants.YBB_TRANSFER_OUT_PAGEINFO;
		OkGo.<YbbTransferPageInfoModel>get(url).params("asset", asset).tag(this).execute(new DialogCallback<YbbTransferPageInfoModel>(this) {
			@Override
			public void onSuc(Response<YbbTransferPageInfoModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					mData = response.body().getData();
					initPageInfo(mData);
				}
			}

			@Override
			public void onE(Response<YbbTransferPageInfoModel> response) {

			}
		});
	}

	/**
	 * 发起转账请求
	 */
	private void doActionTransfer(String asset, String amount) {
		if (BigDecimalUtils.isEmptyOrZero(amount)) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return;
		}

		Map<String, String> params = new HashMap<>();
		params.put("asset", asset);
		params.put("type", type == TRANSFER_TYPE_IN ? "3" : "4");
		params.put("quantity", mEditText.getText().toString().trim());

		OkGo.<YbbTransferModel>post(Constants.YBB_TRANSFER_ACTION)
				.params(params).tag(this).execute(new DialogCallback<YbbTransferModel>(this) {
			@Override
			public void onSuc(Response<YbbTransferModel> response) {
				if (response.body() != null) {
					//转账成功，清空输入框
					mEditText.setText("");

					ToastUtil.show(type == TRANSFER_TYPE_IN ? R.string.res_transfer_in_success : R.string.res_transfer_out_success);

					//重新获取可转余额
					initTransferPageInfo();
				}
			}

			@Override
			public void onE(Response<YbbTransferModel> response) {

			}
		});
	}


	private void initPageInfo(YbbTransferPageInfoModel.DataBean data) {
		if (type == TRANSFER_TYPE_IN) {
			//转入界面初始化
			mEditText.setHint(R.string.res_transfer_in_amount_hint);
			mTvTransferAmountDesc.setText(R.string.res_transfer_in_amount);
			mBtnConfirm.setText(R.string.res_confirm_transfer_in);

			mTvAvailable.setVisibility(View.VISIBLE);
			mTvPersonalQuota.setVisibility(View.VISIBLE);

			mTvAvailable.setText(String.format("%s: %s %s",
					getString(R.string.res_available_transfer_amount), data.getAvailableBalance(), asset));

			tvPersonalQuotaValue.setText(
					String.format("%s %s/%s %s", data.getUserTotalLeft(), asset, data.getUserMaxTotal(), asset));

			mTvPersonalQuota.setText(getString(R.string.res_person_quote));

		} else {
			//转出界面初始化
			mEditText.setHint(R.string.res_transfer_out_amount_hint);
			mTvTransferAmountDesc.setText(R.string.res_transfer_out_amount);
			mBtnConfirm.setText(R.string.res_confirm_transfer_out);

			mTvAvailable.setVisibility(View.VISIBLE);
			mTvPersonalQuota.setVisibility(View.GONE);

			mTvAvailable.setText(String.format("%s: %s %s",
					getString(R.string.res_available_transfer_amount), data.getAvailableBalance(), asset));

		}
	}

	@Override
	public boolean needLock() {
		return true;
	}
}
