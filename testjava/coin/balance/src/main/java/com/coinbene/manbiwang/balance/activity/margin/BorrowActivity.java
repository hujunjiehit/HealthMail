package com.coinbene.manbiwang.balance.activity.margin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.manbiwang.model.http.DataObjModel;
import com.coinbene.manbiwang.model.http.InitBorrowModel;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.margin.view.CurrencyPairSelectorDialog;
import com.coinbene.manbiwang.balance.activity.margin.view.CurrencySelectorDialog;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by june
 * on 2019-08-18
 */
public class BorrowActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.top_bar)
	QMUITopBarLayout mTopBar;
	@BindView(R2.id.layout_top_bar)
	QMUIWindowInsetLayout mLayoutTopBar;
	@BindView(R2.id.tv_coin_type)
	TextView mTvCoinType;
	@BindView(R2.id.spinner_coin_type)
	QMUIRoundButton mSpinnerCoinType;
	@BindView(R2.id.iv_coin_type)
	ImageView mIvCoinType;
	@BindView(R2.id.tv_coin_pair)
	TextView mTvCoinPair;
	@BindView(R2.id.spinner_coin_pair)
	QMUIRoundButton mSpinnerCoinPair;
	@BindView(R2.id.tv_hour_interest_rate)
	TextView mTvHourInterestRate;
	@BindView(R2.id.spinner_hour_interest_rate)
	QMUIRoundButton mSpinnerHourInterestRate;
	@BindView(R2.id.tv_borrow_number)
	TextView mTvBorrowNumber;
	@BindView(R2.id.edit_borrow_number)
	EditText mEditBorrowNumber;
	@BindView(R2.id.tv_all)
	TextView mTvAll;
	@BindView(R2.id.tv_max_borrow)
	TextView mTvMaxBorrow;
	@BindView(R2.id.btn_borrow)
	QMUIRoundButton mBtnBorrow;
	@BindView(R2.id.iv_coin_pair)
	ImageView mIvCoinPair;

	private InitBorrowModel.DataBean initBorrowModel;

	private static final int CURRENCY_CHANGED = 0;
	private static final int PAIR_CHANGED = 1;

	//币种集合
	private List<String> assetList;

	//币对集合
	public List<String> pairList;

	private String symbol;
	private String currentAsset;
	private String currentPair;

	private CurrencySelectorDialog currencySelectorDialog;
	private CurrencyPairSelectorDialog pairSelectorDialog;

	public static void startMe(Context context, String symbol) {
		Intent intent = new Intent(context, BorrowActivity.class);
		intent.putExtra("symbol", symbol);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_borrow;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void setListener() {
		listener();
	}

	@Override
	public void initData() {
		getData();
	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void init() {
		if (getIntent() == null && TextUtils.isEmpty(getIntent().getStringExtra("symbol"))) {
			finish();
			return;
		}
		symbol = getIntent().getStringExtra("symbol");
		mTopBar.setTitle(getString(R.string.res_borrow_asset));
		mTopBar.addLeftImageButton(R.drawable.res_icon_back, R.id.iv_left_image).setOnClickListener(v -> finish());

		Button rightTextButton = mTopBar.addRightTextButton(getString(R.string.res_history_borrow), R.id.tv_right_button);
		rightTextButton.setAllCaps(false);
		rightTextButton.setOnClickListener(v ->
			ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.MARGIN_HISTORY_BORROM)
		);

		//初始化币种和币对选择列表
		initSpinnerList();
		if (assetList == null || pairList == null || assetList.size() <= 0 || pairList.size() <= 0) {
			finish();
			return;
		}

		//初始化币种和币对选择弹窗
		initSelectorDialog();

		//初始化默认的币种和币对
		MarginSymbolTable marginSymbolTable = MarginSymbolController.getInstance().querySymbolByName(symbol);
		if (marginSymbolTable == null) {
			finish();
			return;
		}
		currentAsset = marginSymbolTable.base;
		currentPair = marginSymbolTable.symbol;
		currencySelectorDialog.setDefultCurrency(currentAsset);
		pairSelectorDialog.setDefaulPair(currentPair);

		mSpinnerCoinType.setText(currentAsset);
		mSpinnerCoinPair.setText(currentPair);

		currencySelectorDialog.setSelectListener((adapter, currency, positon) -> {
			currentAsset = currency;
			updateUI(CURRENCY_CHANGED);
		});

		pairSelectorDialog.setSelectListener((adapter, currencyPair, positon) -> {
			currentPair = currencyPair;
			updateUI(PAIR_CHANGED);
		});

	}

	private void updateUI(int changeType) {
		switch (changeType) {
			case CURRENCY_CHANGED:
				//币种变化
				if (!currentPair.contains(currentAsset)) {
					//币对需要重新设置，遍历pairList找到满足条件的币对
					for(String pairName : pairList) {
						if (pairName.contains(currentAsset)) {
							currentPair = pairName;
							break;
						}
					}
				}
				break;
			case PAIR_CHANGED:
				//币对变化
				if (!currentPair.contains(currentAsset)) {
					//币种需要重新设置, 币种设置为当前币对的分子
					MarginSymbolTable marginSymbolTable = MarginSymbolController.getInstance().querySymbolByName(currentPair);
					if (marginSymbolTable != null) {
						currentAsset = marginSymbolTable.base;
					}
				}
				break;
			default:
				break;
		}
		mSpinnerCoinType.setText(currentAsset);
		mSpinnerCoinPair.setText(currentPair);
		mEditBorrowNumber.setText("");

		OkGo.getInstance().cancelTag(this);
		initBorrowed();
	}


	private void initSpinnerList() {
		assetList = TradeUtils.getMarginAssetList();
		pairList = TradeUtils.getMarginPairList();
	}


	private void initSelectorDialog() {
		if (currencySelectorDialog == null) {
			currencySelectorDialog = new CurrencySelectorDialog(this);
		}
		currencySelectorDialog.setCurrencys(assetList);

		if (pairSelectorDialog == null) {
			pairSelectorDialog = new CurrencyPairSelectorDialog(this);
		}
		pairSelectorDialog.setCurrencyPairs(pairList);
	}

	private void listener() {
		mSpinnerCoinType.setOnClickListener(v -> {
			if (currencySelectorDialog != null && !TextUtils.isEmpty(currentAsset)) {
				currencySelectorDialog.setDefultCurrency(currentAsset);
				currencySelectorDialog.show();
			}
		});

		mSpinnerCoinPair.setOnClickListener(v -> {
			if (pairSelectorDialog != null && !TextUtils.isEmpty(currentPair)) {
				pairSelectorDialog.setDefaulPair(currentPair);
				pairSelectorDialog.show();
			}
		});

		mTvAll.setOnClickListener(v -> {
			if (initBorrowModel != null) {
				mEditBorrowNumber.setText(initBorrowModel.getMaxBorrow());
			}
		});

		mBtnBorrow.setOnClickListener(v -> doActionBorrow());

		mEditBorrowNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(mEditBorrowNumber, s, 8);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}


	private void getData() {
		initBorrowed();
	}

	private void initBorrowed() {
		Map<String, String> parmas = new HashMap<>();
		parmas.put("symbol", currentPair);
		parmas.put("asset", currentAsset);
		OkGo.<InitBorrowModel>get(Constants.MARGIN_INIT_BORROWED).params(parmas).tag(this).execute(new DialogCallback<InitBorrowModel>(this) {
			@Override
			public void onSuc(Response<InitBorrowModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					initBorrowModel = response.body().getData();
					if (mSpinnerHourInterestRate != null) {
						//除以24保留八位精度向上取整，然后转成百分比，六位精度
						mSpinnerHourInterestRate.setText(
								BigDecimalUtils.toPercentage(
										BigDecimalUtils.divideToStrUp(initBorrowModel.getRate(), "24", 8),
										6));
					}
					if (mTvMaxBorrow != null) {
						mTvMaxBorrow.setText(String.format("%s%s %s", getString(R.string.res_max_borrow_number), initBorrowModel.getMaxBorrow(), currentAsset));
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

			@Override
			public void onE(Response<InitBorrowModel> response) {

			}
		});
	}


	/**
	 * 调用借币接口借币
	 */
	private void doActionBorrow() {
		if (BigDecimalUtils.isEmptyOrZero(mEditBorrowNumber.getText().toString().trim())) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return;
		}

		if (initBorrowModel != null && BigDecimalUtils.isLessThan(mEditBorrowNumber.getText().toString().trim(), initBorrowModel.getMinBorrow())) {
			ToastUtil.show(String.format("%s%s", getString(R.string.res_min_borrow), initBorrowModel.getMinBorrow()));
			return;
		}

		Map<String, String> parmas = new HashMap<>();
		parmas.put("symbol", currentPair);
		parmas.put("quantity", mEditBorrowNumber.getText().toString().trim());
		parmas.put("asset", currentAsset);
		OkGo.<DataObjModel>post(Constants.MARGIN_BORROW_COIN).params(parmas).tag(this).execute(new DialogCallback<DataObjModel>(this) {
			@Override
			public void onSuc(Response<DataObjModel> response) {
				ToastUtil.show(getString(R.string.res_borrow_success));
				new Handler().postDelayed(() -> finish(), 1000);
			}

			@Override
			public void onE(Response<DataObjModel> response) {
			}
		});
	}


}
