package com.coinbene.manbiwang.balance.transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.balance.Product;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.balance.TransferUtils;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.model.http.TransferAssetModel;
import com.coinbene.manbiwang.model.http.TransferInfoModel;
import com.coinbene.manbiwang.model.http.TransferResponse;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-10
 */
public class TransferActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.top_bar)
	QMUITopBarLayout mTopBar;
	@BindView(R2.id.spinner_asset)
	QMUIRoundButton mSpinnerAsset;
	@BindView(R2.id.tv_up)
	TextView mTvUp;
	@BindView(R2.id.tv_down)
	TextView mTvDown;
	@BindView(R2.id.view_up)
	View mViewUp;
	@BindView(R2.id.view_down)
	View mViewDown;
	@BindView(R2.id.iv_down_disable)
	ImageView mIvDownDisable;
	@BindView(R2.id.iv_up_disable)
	ImageView mIvUpDisable;
	@BindView(R2.id.iv_select_symbol)
	ImageView mIvSelectSymbol;
	@BindView(R2.id.iv_select_from_symbol)
	ImageView mIvSelectFromSymbol;
	@BindView(R2.id.iv_select_to_symbol)
	ImageView mIvSelectToSymbol;
	@BindView(R2.id.tv_disable_desc)
	TextView mTvDisableDesc;
	@BindView(R2.id.btn_reverse)
	QMUIRoundButton mBtnReverse;
	@BindView(R2.id.img_exchange)
	ImageView mImgExchange;
	@BindView(R2.id.spinner_single_symbol)
	QMUIRoundButton mSpinnerSingleSymbol;
	@BindView(R2.id.spinner_from_symbol)
	QMUIRoundButton mSpinnerFromSymbol;
	@BindView(R2.id.spinner_to_symbol)
	QMUIRoundButton mSpinnerToSymbol;
	@BindView(R2.id.img_tradepair_reverse)
	ImageView mIvTradepairReverse;
	@BindView(R2.id.edit_vol)
	EditText mEditVol;
	@BindView(R2.id.tv_all)
	TextView mTvAll;
	@BindView(R2.id.tv_can_use)
	TextView mTvCanUse;
	@BindView(R2.id.btn_transfer)
	QMUIRoundButton mBtnTransfer;

	@BindView(R2.id.group_pair)
	Group mGroupPair;
	@BindView(R2.id.group_pair_double)
	Group mGroupPairDouble;
	@BindView(R2.id.tv_tradepair_title)
	TextView mTvTradepairTitle;

	@BindView(R2.id.constraintLayout)
	ConstraintLayout mConstraintLayout;
	@BindView(R2.id.check_box_ybb)
	CheckBox mCheckBoxYbb;
	@BindView(R2.id.layout_checkbox_area)
	RelativeLayout mLayoutCheckboxArea;
	@BindView(R2.id.tv_ybb_left_value)
	TextView mTvYbbLeftValue;
	@BindView(R2.id.layout_ybb_area)
	LinearLayout mLayoutYbbArea;
	@BindView(R2.id.tv_ybb_hint)
	TextView mTvYbbHint;

	private SelectorDialog<String> mAssetSelectorDialog;
	private SelectorDialog<Product> mProductSelectorDialog;

	private SelectorDialog<String> mFromSymbolSelectorDialog;
	private SelectorDialog<String> mToSymbolSelectorDialog;

	//是否翻转过转入转出账户
	private boolean isReverse = false;

	private SparseArray<Product> mProductMap;

	private List<String> mAssetList;
	private List<Product> mProductList;

	//杠杆支持的币对列表
	private List<String> mSymbolList;

	//转账请求需要用到的参数
	private Product mFromProduct;
	private Product mToProduct;

	private String mFromSymbol;
	private String mToSymbol;

	//转账页面初始化参数
	private String initAsset;
	private String initFrom;
	private String initTo;
	private String initFromSymbol;
	private String initToSymbol;
	private String availableBalance;

	private String mYbbTotalLeft = ""; //当前资产转入余币宝的剩余可用额度

	@AddFlowControl
	public static void startMe(Context context, Bundle bundle) {
		Intent intent = new Intent(context, TransferActivity.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_transfer;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.fund_transfer_title);

		Button rightTextButton = mTopBar.addRightTextButton(getString(R.string.res_transfer_record_short), R.id.tv_right_button);
		rightTextButton.setAllCaps(false);
		rightTextButton.setOnClickListener(v -> ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.TRANSFER));
	}

	@Override
	public void setListener() {

		//选择资产
		mSpinnerAsset.setOnClickListener(v -> selectAsset());

		mSpinnerSingleSymbol.setOnClickListener(v -> {
			//转入转出账户只有一个是杠杆账户
			if (mFromProduct.getType() == Product.TYPE_MARGIN) {
				//选择转入币对
				selectFromSymbol();
			} else if (mToProduct.getType() == Product.TYPE_MARGIN) {
				//选择转出币对
				selectToSymbol();
			}
		});

		mSpinnerFromSymbol.setOnClickListener(v -> {
			//转入转出账户两个都是杠杆账户
			selectFromSymbol();
		});

		mSpinnerToSymbol.setOnClickListener(v -> {
			//转入转出账户两个都是杠杆账户
			selectToSymbol();
		});

		mViewUp.setOnClickListener(v -> {
			if (!isReverse) {
				selectFromProduct();
			} else {
				selectToProduct();
			}
		});

		mViewDown.setOnClickListener(v -> {
			if (!isReverse) {
				selectToProduct();
			} else {
				selectFromProduct();
			}
		});

		//转入转出账户对换
		mBtnReverse.setOnClickListener(v -> reverse());

		//杠杆fromSymbol和toSymbol对换
		mIvTradepairReverse.setOnClickListener(v -> swapMarginSymbol());

		//发起转账请求
		mBtnTransfer.setOnClickListener(v -> doActionTransfer(mSpinnerAsset.getText().toString().trim(), mEditVol.getText().toString().trim()));

		mTvAll.setOnClickListener(v -> {
			if (!TextUtils.isEmpty(availableBalance)) {
				mEditVol.setText(availableBalance);
			}
		});

		//数量精度八位
		mEditVol.addTextChangedListener(new BaseTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(mEditVol, s, 8);

				if (mToProduct != null && mToProduct.getType() == Product.TYPE_FORTUNE && mToProduct.canTransferIn()) {
					showYbbHint();
				}
			}
		});

		mLayoutCheckboxArea.setOnClickListener(v -> {
			if(!mCheckBoxYbb.isEnabled()) {
				return;
			}
			mCheckBoxYbb.setChecked(!mCheckBoxYbb.isChecked());
			SpUtil.setTransferToYbb(mCheckBoxYbb.isChecked());
			showYbbHint();
		});
	}

	@Override
	public void initData() {
		if (getIntent() != null) {
			initAsset = getIntent().getStringExtra(TransferParams.EXTRA_ASSET);
			initFrom = getIntent().getStringExtra(TransferParams.EXTRA_FROM_ACCOUNT);
			initTo = getIntent().getStringExtra(TransferParams.EXTRA_TO_ACCOUNT);
			initFromSymbol = getIntent().getStringExtra(TransferParams.EXTRA_FROM_SYMBOL);
			initToSymbol = getIntent().getStringExtra(TransferParams.EXTRA_TO_SYMBOL);
		}


		//初始化币种列表
		initAsset();
	}

	/**
	 * 展示币种选择弹窗
	 */
	private void selectAsset() {
		if (mAssetList == null || mAssetList.size() == 0) {
			return;
		}

		if (mAssetSelectorDialog == null) {
			mAssetSelectorDialog = new SelectorDialog<>(this);
			mAssetSelectorDialog.setSelectListener((asset, position) -> {

				mSpinnerAsset.setText(asset);

				//币种变化之后，转入转出账户都要重新设置
				mFromProduct = null;
				mToProduct = null;

				mFromSymbol = "";
				mToSymbol = "";
				mYbbTotalLeft = "";

				getTransferInfo(mSpinnerAsset.getText().toString().trim());
			});
		}

		mAssetSelectorDialog.setDatas(mAssetList);
		mAssetSelectorDialog.setDefaultData(mSpinnerAsset.getText().toString());
		mAssetSelectorDialog.show();
	}

	/**
	 * 选择 fromProduct -- 转出方向的账户
	 */
	private void selectFromProduct() {
		if (mProductSelectorDialog == null) {
			mProductSelectorDialog = new SelectorDialog<>(this);
		}

		mProductSelectorDialog.setSelectListener((data, position) -> {
			mFromProduct = data;
			getTransferInfo(mSpinnerAsset.getText().toString().trim(), String.valueOf(mFromProduct.getType()));
		});

		if (mProductList != null && mProductList.size() > 0) {
			mProductSelectorDialog.setDatas(mProductList);
			mProductSelectorDialog.setDefaultData(mFromProduct == null ? mProductList.get(0) : mFromProduct);
			mProductSelectorDialog.show();
		}
	}

	/**
	 * 选择 toProduct -- 转入方向的账户
	 */
	private void selectToProduct() {
		if (mProductSelectorDialog == null) {
			mProductSelectorDialog = new SelectorDialog(this);
		}

		mProductSelectorDialog.setSelectListener((data, position) -> {
			mToProduct = data;
			updateUI(null);
		});

		if (mProductList != null && mProductList.size() > 0) {
			mProductSelectorDialog.setDatas(mProductList);
			mProductSelectorDialog.setDefaultData(mToProduct == null ? mProductList.get(0) : mToProduct);
			mProductSelectorDialog.show();
		}
	}


	/**
	 * 选择杠杆转入币对
	 */
	private void selectFromSymbol() {
		if (mFromSymbolSelectorDialog == null) {
			mFromSymbolSelectorDialog = new SelectorDialog<>(this);

			mFromSymbolSelectorDialog.setSelectListener(new SelectorDialog.SelectListener<String>() {
				@Override
				public void onItemSelected(String data, int positon) {
					mFromSymbol = data;
					updateMarginSymbol();
					getTransferInfo(mSpinnerAsset.getText().toString().trim(), String.valueOf(mFromProduct.getType()));
				}
			});
		}
		if (mSymbolList == null || mSymbolList.size() <= 1) {
			return;
		}
		if (mSymbolList != null && mSymbolList.size() > 0) {
			mFromSymbolSelectorDialog.setDatas(mSymbolList);
			mFromSymbolSelectorDialog.setDefaultData(TextUtils.isEmpty(mFromSymbol) ? mSymbolList.get(0) : mFromSymbol);
			mFromSymbolSelectorDialog.show();
		}
	}

	/**
	 * 选择杠杆转出币对
	 */
	private void selectToSymbol() {
		if (mToSymbolSelectorDialog == null) {
			mToSymbolSelectorDialog = new SelectorDialog<>(this);

			mToSymbolSelectorDialog.setSelectListener(new SelectorDialog.SelectListener<String>() {
				@Override
				public void onItemSelected(String data, int positon) {
					mToSymbol = data;
					updateMarginSymbol();
				}
			});
		}
		if (mSymbolList == null || mSymbolList.size() <= 1) {
			return;
		}
		if (mSymbolList != null && mSymbolList.size() > 0) {
			mToSymbolSelectorDialog.setDatas(mSymbolList);
			mToSymbolSelectorDialog.setDefaultData(TextUtils.isEmpty(mToSymbol) ? mSymbolList.get(0) : mToSymbol);
			mToSymbolSelectorDialog.show();
		}
	}


	/**
	 * 初始化币种列表
	 */
	private void initAsset() {
		OkGo.<TransferAssetModel>get(Constants.TRANSFER_ASSET_LIST).tag(this).execute(new NewDialogJsonCallback<TransferAssetModel>(this) {
			@Override
			public void onSuc(Response<TransferAssetModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					if (response.body().getData().size() > 0) {
						if (mAssetList == null) {
							mAssetList = new ArrayList<>();
						}
						mAssetList.clear();
						mAssetList.addAll(response.body().getData());
						if (!TextUtils.isEmpty(initAsset) && mAssetList.contains(initAsset)) {
							mSpinnerAsset.setText(initAsset);
							initAsset = ""; //initAsset只在第一次使用
						} else {
							//默认选中列表中的第一个币种
							mSpinnerAsset.setText(mAssetList.get(0));
						}

						int initProductId = -1;
						//initFrom不为空的时候，查询资产信息需要传入fromProduct
						if (!TextUtils.isEmpty(initFrom)) {
							//根据initFrom找到对应的productId
							for (int key : Product.nameMap.keySet()) {
								if (Product.nameMap.get(key).equals(initFrom)) {
									initProductId = key;
									break;
								}
							}
						}

						if (initProductId != -1) {
							getTransferInfo(mSpinnerAsset.getText().toString().trim(), String.valueOf(initProductId));
						} else {
							//请求TRANSFER_INFO接口--转账前查询接口
							getTransferInfo(mSpinnerAsset.getText().toString().trim());
						}
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

			@Override
			public void onE(Response<TransferAssetModel> response) {

			}
		});
	}

	/**
	 * 转账前查询接口--不指定转出账户
	 */
	private void getTransferInfo(String asset) {
		getTransferInfo(asset, "");
	}

	/**
	 * 转账前查询接口--指定转出账户
	 */
	private void getTransferInfo(String asset, String fromProduct) {
		OkGo.<TransferInfoModel>get(Constants.TRANSFER_INFO)
				.params("asset", asset)
				.params("fromProduct", fromProduct)
				.params("fromSubProduct", TextUtils.isEmpty(mFromSymbol) ? "" : mFromSymbol)
				.tag(this)
				.execute(new NewDialogJsonCallback<TransferInfoModel>(this) {
					@Override
					public void onSuc(Response<TransferInfoModel> response) {
						if (response.body() != null && response.body().getData() != null) {
							updateUI(response.body().getData());
						}
					}

					@Override
					public void onE(Response<TransferInfoModel> response) {
						setAvailableBalance("");
					}
				});
	}

	/**
	 * 发起转账请求
	 */
	private void doActionTransfer(String asset, String amount) {
		if (mFromProduct == null || mToProduct == null) {
			return;
		}
		if (BigDecimalUtils.isEmptyOrZero(amount)) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return;
		}

		Map<String, String> params = new HashMap<>();
		//必填参数
		params.put("asset", asset);
		params.put("amount", amount);
		params.put("fromProduct", String.valueOf(mFromProduct.getType()));
		params.put("toProduct", String.valueOf(mToProduct.getType()));

		//杠杆账户需要的参数
		if (mFromProduct.getType() == Product.TYPE_MARGIN && mToProduct.getType() == Product.TYPE_MARGIN) {
			params.put("fromSubProduct", mFromSymbol);
			params.put("toSubProduct", mToSymbol);
		} else {
			if (mFromProduct.getType() == Product.TYPE_MARGIN) {
				params.put("fromSubProduct", mFromSymbol);
			} else if (mToProduct.getType() == Product.TYPE_MARGIN) {
				params.put("toSubProduct", mToSymbol);
			}
		}

		//财富账户需要的参数
		if (mToProduct.getType() == Product.TYPE_FORTUNE) {
			//是否直接转入余币宝
			if (mCheckBoxYbb.isEnabled() && mCheckBoxYbb.isChecked()) {
				params.put("toSubProduct", "6-1");
			}
		}

		OkGo.<TransferResponse>post(Constants.TRANSFER_ACTION)
				.params(params)
				.tag(this)
				.execute(new NewDialogJsonCallback<TransferResponse>(this) {
					@Override
					public void onSuc(Response<TransferResponse> response) {
						if (response.body() != null) {
							//转账成功，清空输入框
							mEditVol.setText("");
							ToastUtil.show(R.string.contracts_transfer_successful);

							//重新获取可转数量
							getTransferInfo(mSpinnerAsset.getText().toString().trim(), String.valueOf(mFromProduct.getType()));
						}
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}

					@Override
					public void onE(Response<TransferResponse> response) {
					}
				});
	}

	/**
	 * 更新界面UI
	 *
	 * @param data
	 */
	private void updateUI(TransferInfoModel.DataBean data) {
		if (data != null) {
			//初始化账户Map
			if (mProductMap == null) {
				mProductMap = new SparseArray<>();
			}
			mProductMap.clear();
			TransferUtils.initTransferProductMap(TransferActivity.this, mProductMap, data.getProducts());

			if (mProductList == null) {
				mProductList = new ArrayList<>();
			}
			mProductList.clear();
			for (int i = 0; i < mProductMap.size(); i++) {
				if (!TextUtils.isEmpty(initFrom) && initFrom.equals(mProductMap.valueAt(i).getName())) {
					//进入页面初始化默认的转出账户
					mFromProduct = mProductMap.valueAt(i);
					initFrom = "";
				}
				if (!TextUtils.isEmpty(initTo) && initTo.equals(mProductMap.valueAt(i).getName())) {
					//进入页面初始化默认的转入账户
					mToProduct = mProductMap.valueAt(i);
					initTo = "";
				}
				mProductList.add(mProductMap.valueAt(i));
			}

			if (mFromProduct == null && mProductList.size() > 0) {
				//设置默认的转出账户
				mFromProduct = mProductList.get(0);
			}
			if (mToProduct == null && mProductList.size() > 0) {
				//设置默认的转入账户
				mToProduct = mProductList.size() > 1 ? mProductList.get(1) : mProductList.get(0);
			}
			availableBalance = data.getAvailableBalance();
			setAvailableBalance(availableBalance);

			if (data.getMarginSymbols() != null) {
				//初始化转入转出币对
				initMarginSymbols(data.getMarginSymbols());
			}
		}

		//根据mFromProduct 和 mToProduct 的状态设置账户信息
		updateProductInfo();

		//根据mFromProduct 和 mToProduct 杠杆账户数量显示交易对选择UI
		updateMarginSymbol();
	}

	private void initMarginSymbols(List<String> marginSymbols) {
		if (mSymbolList == null) {
			mSymbolList = new ArrayList<>();
		}

		if (marginSymbols != null) {
			mSymbolList.clear();
			mSymbolList.addAll(marginSymbols);
		}

		if (TextUtils.isEmpty(mFromSymbol) && TextUtils.isEmpty(mToSymbol)) {
			if (mSymbolList.size() == 0) {
				mFromSymbol = mToSymbol = "";
			} else if (mSymbolList.size() == 1) {
				mFromSymbol = mToSymbol = mSymbolList.get(0);
			} else {
				//初始化杠杆币对参数
				if (!TextUtils.isEmpty(initFromSymbol) && mSymbolList.contains(initFromSymbol)) {
					mFromSymbol = initFromSymbol;
					initFromSymbol = "";
				} else {
					mFromSymbol = mSymbolList.get(0);
				}
				if (!TextUtils.isEmpty(initToSymbol) && mSymbolList.contains(initToSymbol)) {
					mToSymbol = initToSymbol;
					initToSymbol = "";
				} else {
					mToSymbol = mSymbolList.get(1);
				}
			}
		}
	}


	/**
	 * 设置可用资产
	 */
	private void setAvailableBalance(String availableBalance) {
		if (!TextUtils.isEmpty(availableBalance)) {
			if (BigDecimalUtils.lessThanToZero(availableBalance)) {
				mTvCanUse.setText("--");
				ToastUtil.show(R.string.fail_to_check_balance);
				return;
			}
			String palaceHolder = getString(R.string.res_can_use);
			String format = String.format(palaceHolder, BigDecimalUtils.setScaleDown(availableBalance, 8), mSpinnerAsset.getText().toString());
			mTvCanUse.setText(format);
			mTvCanUse.setVisibility(View.VISIBLE);
		} else {
			mTvCanUse.setText("--");
		}
	}


	/**
	 * 设置账户信息
	 */
	private void updateProductInfo() {
		getFromTextView().setText(mFromProduct.getProductName());
		getToTextView().setText(mToProduct.getProductName());
		if (mFromProduct.canTransferOut() && mToProduct.canTransferIn()) {
			//fromProduct可以转出, toProduct可以转入，两个条件同时满足
			getFromTextView().setTextColor(getResources().getColor(R.color.res_textColor_1));
			getToTextView().setTextColor(getResources().getColor(R.color.res_textColor_1));
			getFromDisableIcon().setVisibility(View.GONE);
			getToDisableIcon().setVisibility(View.GONE);
			mTvDisableDesc.setVisibility(View.GONE);
		} else {
			//from和to有一个不满足转账条件
			mTvDisableDesc.setVisibility(View.VISIBLE);
			if (mFromProduct.canTransferOut()) {
				//from可以转出
				getFromTextView().setTextColor(getResources().getColor(R.color.res_textColor_1));
				getFromDisableIcon().setVisibility(View.GONE);
			} else {
				//from不能转出
				getFromTextView().setTextColor(getResources().getColor(R.color.res_textColor_3));
				getFromDisableIcon().setVisibility(View.VISIBLE);
				mTvDisableDesc.setText(mFromProduct.getDisableDesc(this));
			}

			if (mToProduct.canTransferIn()) {
				//to可以转入
				getToTextView().setTextColor(getResources().getColor(R.color.res_textColor_1));
				getToDisableIcon().setVisibility(View.GONE);
			} else {
				//to不能转入
				getToTextView().setTextColor(getResources().getColor(R.color.res_textColor_3));
				getToDisableIcon().setVisibility(View.VISIBLE);
				mTvDisableDesc.setText(mToProduct.getDisableDesc(this));
			}
		}

		if (mToProduct.getType() == Product.TYPE_FORTUNE && mToProduct.canTransferIn()) {
			//如果转入账户是理财账户, 并且理财账户可以转入
			mLayoutYbbArea.postDelayed(() -> mLayoutYbbArea.setVisibility(View.VISIBLE),300);
			updateFortuneView();
		} else {
			//其它情况不显示直接转入余币宝选项
			mLayoutYbbArea.setVisibility(View.GONE);
		}
	}

	private void updateFortuneView() {
		if (ServiceRepo.getFortuneService() != null) {
			//判断当前币种在不在支持的币种列表里面
			ServiceRepo.getFortuneService().getYbbAssetList(assetList -> {
				if (assetList.contains(mSpinnerAsset.getText().toString().trim())) {
					//获取当前币种的额度
					ServiceRepo.getFortuneService().getYbbTotalLeft(mSpinnerAsset.getText().toString().trim(),
							totalLeft -> {
								if (mCheckBoxYbb == null) {
									return;
								}
								if (BigDecimalUtils.isGreaterThan(totalLeft, "0")) {
									mYbbTotalLeft = totalLeft;
									mCheckBoxYbb.setEnabled(true);
									mCheckBoxYbb.setChecked(SpUtil.isTransferToYbb());
									setYbbTotalLeft(totalLeft);
								} else {
									//可用额度小于0
									mCheckBoxYbb.setChecked(false);
									mCheckBoxYbb.setEnabled(false);
									setYbbTotalLeft("0");
								}
							});
				} else {
					mCheckBoxYbb.setChecked(false);
					mCheckBoxYbb.setEnabled(false);
					setYbbTotalLeft("--");
				}
			});
		} else {
			//显示额度--
			mCheckBoxYbb.setChecked(false);
			mCheckBoxYbb.setEnabled(false);
			setYbbTotalLeft("--");
		}
	}

	private void setYbbTotalLeft(String totalLeft) {
		mTvYbbLeftValue.setText(String.format("%s: %s %s",
				getString(R.string.res_left_value), totalLeft, mSpinnerAsset.getText().toString().trim()));
	}

	private void showYbbHint() {
		//比较输入数量和可转额度
		if(TextUtils.isEmpty(mYbbTotalLeft) || !mCheckBoxYbb.isEnabled() || (mCheckBoxYbb.isEnabled() && !mCheckBoxYbb.isChecked())) {
			mTvYbbHint.setVisibility(View.GONE);
			return;
		}

		if (BigDecimalUtils.isGreaterThan(mEditVol.getText().toString().trim(), mYbbTotalLeft)) {
			mTvYbbHint.setVisibility(View.VISIBLE);

			mTvYbbHint.setText(String.format(getResources().getString(R.string.res_transfer_ybb_hint),
					mYbbTotalLeft,
					mSpinnerAsset.getText().toString().trim(),
					BigDecimalUtils.subtract(mEditVol.getText().toString().trim(), mYbbTotalLeft),
					mSpinnerAsset.getText().toString().trim()));
		} else {
			mTvYbbHint.setVisibility(View.GONE);
		}
	}


	/**
	 * 根据mFromProduct 和 mToProduct 杠杆账户数量显示交易对选择UI
	 */
	private void updateMarginSymbol() {
		if (mFromProduct == null || mToProduct == null) {
			return;
		}
		if (mFromProduct.getType() == Product.TYPE_MARGIN && mToProduct.getType() == Product.TYPE_MARGIN) {
			//转入转出账户都是杠杆账户
			mTvTradepairTitle.setVisibility(View.VISIBLE);
			mGroupPair.setVisibility(View.GONE);
			mGroupPairDouble.setVisibility(View.VISIBLE);

			//小三角形显示隐藏逻辑
			mIvSelectSymbol.setVisibility(View.GONE);
			if (mSymbolList == null || mSymbolList.size() <= 1) {
				mIvSelectFromSymbol.setVisibility(View.GONE);
				mIvSelectToSymbol.setVisibility(View.GONE);
			} else {
				mIvSelectFromSymbol.setVisibility(View.VISIBLE);
				mIvSelectToSymbol.setVisibility(View.VISIBLE);
			}

			mSpinnerFromSymbol.setText(mFromSymbol);
			mSpinnerToSymbol.setText(mToSymbol);
		} else if (mFromProduct.getType() == Product.TYPE_MARGIN || mToProduct.getType() == Product.TYPE_MARGIN) {
			//转入转出账户只有一个是杠杆账户
			mTvTradepairTitle.setVisibility(View.VISIBLE);
			mGroupPair.setVisibility(View.VISIBLE);
			mGroupPairDouble.setVisibility(View.GONE);

			//小三角形显示隐藏逻辑
			mIvSelectFromSymbol.setVisibility(View.GONE);
			mIvSelectToSymbol.setVisibility(View.GONE);
			if (mSymbolList == null || mSymbolList.size() <= 1) {
				mIvSelectSymbol.setVisibility(View.GONE);
			} else {
				mIvSelectSymbol.setVisibility(View.VISIBLE);
			}


			mSpinnerSingleSymbol.setText(mFromProduct.getType() == Product.TYPE_MARGIN ? mFromSymbol : mToSymbol);
		} else {
			//其它情况
			mTvTradepairTitle.setVisibility(View.GONE);
			mGroupPair.setVisibility(View.GONE);
			mGroupPairDouble.setVisibility(View.GONE);
			mIvSelectSymbol.setVisibility(View.GONE);
			mIvSelectFromSymbol.setVisibility(View.GONE);
			mIvSelectToSymbol.setVisibility(View.GONE);
		}


		if (mFromProduct.getType() == Product.TYPE_MARGIN || mToProduct.getType() == Product.TYPE_MARGIN) {
		}


	}


	/**
	 * 汇款账户与收款账户转换
	 */
	private void reverse() {
		//设置转换动画
		ConstraintSet constraintSet = new ConstraintSet();
		constraintSet.clone(mConstraintLayout);
		constraintSet.clear(R.id.view_up);
		constraintSet.clear(R.id.view_down);

		int leftMargin = QMUIDisplayHelper.dp2px(this, 20);

		if (isReverse) {
			//tvUp约束到上方
			constraintSet.connect(R.id.view_up, ConstraintSet.START, R.id.tv1, ConstraintSet.END, leftMargin);
			constraintSet.connect(R.id.view_up, ConstraintSet.END, R.id.btn_reverse, ConstraintSet.START);
			constraintSet.connect(R.id.view_up, ConstraintSet.BOTTOM, R.id.tv1, ConstraintSet.BOTTOM);
			constraintSet.connect(R.id.view_up, ConstraintSet.TOP, R.id.tv1, ConstraintSet.TOP);

			//tvDown约束到下方
			constraintSet.connect(R.id.view_down, ConstraintSet.START, R.id.tv2, ConstraintSet.END, leftMargin);
			constraintSet.connect(R.id.view_down, ConstraintSet.END, R.id.btn_reverse, ConstraintSet.START);
			constraintSet.connect(R.id.view_down, ConstraintSet.BOTTOM, R.id.tv2, ConstraintSet.BOTTOM);
			constraintSet.connect(R.id.view_down, ConstraintSet.TOP, R.id.tv2, ConstraintSet.TOP);
		} else {
			//tvUp约束到下方
			constraintSet.connect(R.id.view_up, ConstraintSet.START, R.id.tv2, ConstraintSet.END, leftMargin);
			constraintSet.connect(R.id.view_up, ConstraintSet.END, R.id.btn_reverse, ConstraintSet.START);
			constraintSet.connect(R.id.view_up, ConstraintSet.BOTTOM, R.id.tv2, ConstraintSet.BOTTOM);
			constraintSet.connect(R.id.view_up, ConstraintSet.TOP, R.id.tv2, ConstraintSet.TOP);

			//tvDown约束到上方
			constraintSet.connect(R.id.view_down, ConstraintSet.START, R.id.tv1, ConstraintSet.END, leftMargin);
			constraintSet.connect(R.id.view_down, ConstraintSet.END, R.id.btn_reverse, ConstraintSet.START);
			constraintSet.connect(R.id.view_down, ConstraintSet.BOTTOM, R.id.tv1, ConstraintSet.BOTTOM);
			constraintSet.connect(R.id.view_down, ConstraintSet.TOP, R.id.tv1, ConstraintSet.TOP);
		}

		//开始动画
		TransitionManager.beginDelayedTransition(mConstraintLayout);
		constraintSet.applyTo(mConstraintLayout);

		isReverse = !isReverse;

		//转入转出账户互换
		Product temp = mFromProduct;
		mFromProduct = mToProduct;
		mToProduct = temp;

		mEditVol.setText("");


		//查询转账信息
		if (mFromProduct != null) {
			getTransferInfo(mSpinnerAsset.getText().toString().trim(), String.valueOf(mFromProduct.getType()));
		}
	}

	/**
	 * 杠杆账户 fromSymbol 和 toSymbol 互换
	 * 互换之后需要重新请求可用余额
	 */
	private void swapMarginSymbol() {
		//交换 mFromSymbol和 mToSymbol
		String temp = mFromSymbol;
		mFromSymbol = mToSymbol;
		mToSymbol = temp;

		updateMarginSymbol();

		getTransferInfo(mSpinnerAsset.getText().toString().trim(), String.valueOf(mFromProduct.getType()));
	}


	private ImageView getFromDisableIcon() {
		return isReverse == false ? mIvUpDisable : mIvDownDisable;
	}

	private ImageView getToDisableIcon() {
		return isReverse == false ? mIvDownDisable : mIvUpDisable;
	}

	private TextView getFromTextView() {
		return isReverse == false ? mTvUp : mTvDown;
	}

	private TextView getToTextView() {
		return isReverse == false ? mTvDown : mTvUp;
	}


	@Override
	public boolean needLock() {
		return true;
	}
}
