package com.coinbene.manbiwang.balance;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.aspect.annotation.PostClickData;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.Product;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.database.UserInfoTable_;
import com.coinbene.common.dialog.AlertDialogBuilder;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.MessageDialogBuilder;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SiteHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.FixSwipeRefreshLayout;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.balance.activity.spot.SelectCoinActivity;
import com.coinbene.manbiwang.model.http.TotalPreestimateModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIAppBarLayout;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;

import butterknife.BindView;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscriptionList;

import static com.qmuiteam.qmui.util.QMUIStatusBarHelper.getStatusbarHeight;

/**
 * Created by june
 * on 2019-08-14
 */
public class BalanceTotalFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.balance_invite_img)
	ImageView inviteImg;
	@BindView(R2.id.topbar)
	QMUITopBar mTopbar;
	@BindView(R2.id.collapsing_topbar_layout)
	QMUICollapsingTopBarLayout mCollapsingTopbarLayout;
	@BindView(R2.id.appbar_layout)
	QMUIAppBarLayout mAppbarLayout;
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.pull_refresh_layout)
	FixSwipeRefreshLayout mPullRefreshLayout;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;

	@BindView(R2.id.tv_balance_title)
	TextView mTvBalanceTitle;
	@BindView(R2.id.tv_total_balance)
	TextView mTvTotalBalance;
	@BindView(R2.id.tv_total_balance_local)
	TextView mTvTotalBalanceLocal;
	@BindView(R2.id.cb_eye)
	CheckBox mCheckboxEye;

	@BindView(R2.id.btn_recharge)
	QMUIRoundButton mBtnRecharge;
	@BindView(R2.id.btn_withdraw)
	QMUIRoundButton mBtnWithdraw;
	@BindView(R2.id.btn_transfer)
	QMUIRoundButton mBtnTransfer;

	View leftIcon;
	Button rightButton;

	AccountPageAdapter mAccountPageAdapter;

	private boolean hideValue; //是否隐藏资产信息

	private TotalPreestimateModel.DataBean mTotalAccountData;

	private ArrayList<Product> mAccountBeanList;
	private Product mCurrentProduct;
	private TextView tvVIP;
	private ShimmerFrameLayout mShimmerLayout;

	private DataSubscriptionList subscriptions;

	private String coinSwitch;

	private int clickCount = 0;

	@Override
	public int initLayout() {
		return R.layout.balance_total_fragment;
	}

	@Override
	public void initView(View rootView) {
		//给topBar设置topMargin
		QMUICollapsingTopBarLayout.LayoutParams layoutParams = (QMUICollapsingTopBarLayout.LayoutParams) mTopbar.getLayoutParams();
		layoutParams.topMargin = getStatusbarHeight(getContext());
		mTopbar.setLayoutParams(layoutParams);

		//设置白色状态栏
		setMerBarWhite();

		mTopbar.setTitle(getString(R.string.tab_balance)).setOnClickListener(v -> {
			clickCount++;
			if (clickCount >= 6 && CBRepository.getEnableDebug()) {
				clickCount = 0;
				goToDebug();
			}
		});

//		View view = View.inflate(getContext(), R.layout.layout_shimmer, null);
//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		lp.addRule(RelativeLayout.CENTER_VERTICAL);
//		mTopbar.addLeftView(view, R.id.iv_left_image, lp);
//		leftIcon = view.findViewById(R.id.layout_user);
//		tvVIP = view.findViewById(R.id.tv_VIP);
//		mShimmerLayout = view.findViewById(R.id.layout_Shimmer);

//		mShimmerLayout.useDefaults();
//		mShimmerLayout.setRepeatCount(-1);
//		mShimmerLayout.setBaseAlpha(0.6f);
//		mShimmerLayout.setDuration(1500);

//		leftIcon.setOnClickListener(v -> goUserCenter());
//		mShimmerLayout.setOnClickListener(v -> goUserCenter());

		rightButton = mTopbar.addRightTextButton(getString(R.string.myself_record_label), R.id.tv_right_button);
		rightButton.setAllCaps(false);
		rightButton.setTextColor(getResources().getColor(R.color.res_white));
		rightButton.setOnClickListener(v -> jumpRecordActivity(v));

		mPullRefreshLayout.setEnabled(true);
		mPullRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));

//		if (SiteHelper.isBrSite()) {
//			inviteImg.setVisibility(View.GONE);
//		}

		hideValue = SpUtil.getPreHideAssetEstimation();
		mCheckboxEye.setChecked(hideValue);

//		initInviteImage();

		initTabSegment();
	}

	@AddFlowControl
	private void goToDebug() {
		ARouter.getInstance().build(RouteHub.App.debugActivity).navigation(getActivity());
	}

	@NeedLogin(jump = true)
	private void jumpRecordActivity(View v) {
		PostPointHandler.postClickData(PostPointHandler.balance_record);
		ARouter.getInstance().build(RouteHub.Record.recordActivity).navigation(v.getContext());
	}

	private void goUserCenter() {

		ARouter.getInstance().build(RouteHub.User.mySelfActivity).navigation(getContext());

		PostPointHandler.postClickData(PostPointHandler.balance_user_center);
	}

	@Override
	public void setListener() {
		mCollapsingTopbarLayout.setScrimUpdateListener(animation -> {
			//255是按钮隐藏，0是按钮显示
			if ((Integer) animation.getAnimatedValue() == 0) {
				mBtnRecharge.setEnabled(true);
				mBtnWithdraw.setEnabled(true);
				mBtnTransfer.setEnabled(true);
			} else if ((Integer) animation.getAnimatedValue() == 255) {
				mBtnRecharge.setEnabled(false);
				mBtnWithdraw.setEnabled(false);
				mBtnTransfer.setEnabled(false);
			}
		});

		mAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				//verticalOffset from 0 -> -50dp
				if (verticalOffset < 0) {
					mPullRefreshLayout.setEnabled(false);
					mPullRefreshLayout.setAppbarExpanded(false);
				} else {
					mPullRefreshLayout.setEnabled(true);
					mPullRefreshLayout.setAppbarExpanded(true);
				}

				int target = QMUIDisplayHelper.dp2px(getContext(), 70);

				float percent;

				int current = Math.abs(verticalOffset);
				if (current >= target) {
					percent = 0.0f;
				} else {
					if (current <= QMUIDisplayHelper.dp2px(getContext(), 20)) {
						percent = 1.0f;
					} else {
						percent = 1.0f - current * 1.0f / target;
					}
				}

				mTvTotalBalance.setAlpha(percent);
				mTvTotalBalanceLocal.setAlpha(percent);
				mCheckboxEye.setAlpha(percent);
				mTvBalanceTitle.setAlpha(percent);
			}
		});


		mPullRefreshLayout.setOnRefreshListener(() -> {
			getBalanceList();

			//分发下拉刷新事件
			AssetManager.getInstance().dispatchPullRefresh(mCurrentProduct);
		});


		//充币
		mBtnRecharge.setOnClickListener(v -> rechargeClick());

		//提币
		mBtnWithdraw.setOnClickListener(v -> withdrawClick());

		//划转
		mBtnTransfer.setOnClickListener(v -> transferClick());

		mCheckboxEye.setOnClickListener(v -> hideAsset(v));

		if (mTabSegment != null) {
			mTabSegment.addOnTabSelectedListener(new QMUITabSelectedListener() {
				@Override
				public void onTabSelected(int index) {
					if (mAccountBeanList == null || mAccountBeanList.size() <= index) {
						return;
					}
					mCurrentProduct = mAccountBeanList.get(index);

					//切换tab的时候，将每个账户的fragment recyclerView滑动到顶部
					AssetManager.getInstance().dispatchTabSelected(index);


					if (mTabSegment.getTab(index).getContentLeft() > QMUIDisplayHelper.getScreenWidth(getContext()) / 2) {
						mTabSegment.scrollBy(mTabSegment.getTab(index).getContentWidth(), 0);
					}

					if (mTabSegment.getTab(index).getContentLeft() < QMUIDisplayHelper.getScreenWidth(getContext()) / 2) {
						if (index != 0) {
							mTabSegment.scrollBy(-mTabSegment.getTab(index).getContentWidth(), 0);
						}
					}
				}
			});
		}
	}

	/**
	 * 显示隐藏资产
	 */
	@NeedLogin(jump = true)
	private void hideAsset(View v) {
		hideValue = mCheckboxEye.isChecked();
		if (hideValue) {
			PostPointHandler.postClickData(PostPointHandler.balance_hide_balance);
		} else {
			PostPointHandler.postClickData(PostPointHandler.balance_show_balance);
		}
		SpUtil.setPreHideAssetEstimation(hideValue);
		AssetManager.getInstance().setHideValue(hideValue);
		updateAssetValue();
	}

	@Override
	public void initData() {
	}

	/**
	 * 邀请返金
	 */
	private void initInviteImage() {
		if (SiteHelper.isBrSite()) {
			inviteImg.setVisibility(View.GONE);
		}
		if (LanguageHelper.isChinese(getContext())) {
			inviteImg.setBackgroundResource(R.drawable.icon_invite_zh);
		} else if (LanguageHelper.isKorean(getContext())) {
			inviteImg.setBackgroundResource(R.drawable.icon_invite_ko);
		} else if (LanguageHelper.isJapanese(getContext())) {
			inviteImg.setBackgroundResource(R.drawable.icon_invite_ja);
		} else {
			inviteImg.setBackgroundResource(R.drawable.icon_invite_en);
		}

		inviteImg.setOnClickListener(
				v -> UIBusService.getInstance().openUri(getContext(), "coinbene://" + UIRouter.HOST_INVITE_REBATE, null)
		);
	}

	/**
	 * 初始化资产账户tab
	 */
	private void initTabSegment() {
		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_2));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));

		if (mAccountBeanList == null) {
			mAccountBeanList = new ArrayList<>();
		}
		if (mAccountPageAdapter == null) {
			//币币账户
			mAccountBeanList.add(
					new Product.Builder()
							.setType(Product.TYPE_SPOT)
							.setProductName(getString(R.string.balance_coin_str))
							.build());

			//杠杆账户
			if (SwitchUtils.isOpenMarginAsset()) {
				mAccountBeanList.add(
						new Product.Builder()
								.setType(Product.TYPE_MARGIN)
								.setProductName(getString(R.string.res_margin_account))
								.build());
			}

			if (SwitchUtils.isOpenContract_Asset()) {
				//USDT合约账户
				mAccountBeanList.add(
						new Product.Builder()
								.setType(Product.TYPE_USDT_CONTRACT)
								.setProductName(getString(R.string.usdt_contract))
								.build());
				//BTC合约账户
				mAccountBeanList.add(
						new Product.Builder()
								.setType(Product.TYPE_BTC_CONTRACT)
								.setProductName(getString(R.string.btc_contract))
								.build());
			}

			if (SwitchUtils.isOpenFortuneAsset()) {
				//财富账户
				mAccountBeanList.add(
						new Product.Builder()
								.setType(Product.TYPE_FORTUNE)
								.setProductName(getString(R.string.res_fortune_account))
								.build());
			}

//			if (SwitchUtils.isOpenOPT_Asset()) {
//				//猜涨跌账户
//				mAccountBeanList.add(
//						new Product.Builder()
//								.setType(Product.TYPE_OPTIONS)
//								.setProductName(getString(R.string.balance_up_down_str))
//								.build());
//			}

//			if (SwitchUtils.isOpenGameAsset()) {
//				mAccountBeanList.add(
//						new Product.Builder()
//								.setType(Product.TYPE_GAME)
//								.setProductName(getString(R.string.res_game_account))
//								.build());
//			}

			if (mAccountBeanList.size() == 1) {
				//只有一个币币账户的时候，隐藏划转按钮
				mBtnTransfer.setVisibility(View.GONE);
				mTabSegment.setVisibility(View.GONE);
			}
			mAccountPageAdapter = new AccountPageAdapter(getChildFragmentManager(), mAccountBeanList);
		}
		mCurrentProduct = mAccountBeanList.get(0);
		mViewPager.setOffscreenPageLimit(mAccountBeanList.size());
		mViewPager.setAdapter(mAccountPageAdapter);
		mTabSegment.setupWithViewPager(mViewPager);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onFragmentShow() {
		//设置白色状态栏
		setMerBarWhite();

		PostPointHandler.postClickData(PostPointHandler.balance_brower);

		if (!AssetManager.getInstance().isLogin()) {
			mTvTotalBalance.setText("-- BTC");
			mTvTotalBalanceLocal.setText("≈ --");
			if (tvVIP != null) {
				tvVIP.setVisibility(View.GONE);
			}
			mCheckboxEye.setChecked(false);
		} else {
			updateAssetValue();
			if (tvVIP != null) {
				tvVIP.setVisibility(View.VISIBLE);
			}
		}

		getBalanceList();

		if (mShimmerLayout != null) {
			mShimmerLayout.startShimmerAnimation();
		}


		if (subscriptions == null) {
			subscriptions = new DataSubscriptionList();
		}

		if (ServiceRepo.getUserService().isLogin()) {
			//监听user数据库变化，刷新coin手续费开关
			CBRepository.boxFor(UserInfoTable.class)
					.query()
					.equal(UserInfoTable_.userId, UserInfoController.getInstance().getUserInfo().userId)
					.build()
					.subscribe(subscriptions)
					.on(AndroidScheduler.mainThread())
					.onError(error -> error.printStackTrace())
					.observer(data -> {
						if (coinSwitch == null || !data.get(0).coniDiscountSwitch.equals(coinSwitch)) {
							coinSwitch = data.get(0).coniDiscountSwitch;
							AssetManager.getInstance().dispatchConiSwitchChanged();
						}
					});
		}
	}

	@Override
	public void onFragmentHide() {

		if (mShimmerLayout != null) {
			mShimmerLayout.stopShimmerAnimation();
		}
		if (subscriptions != null) {
			subscriptions.cancel();
		}
		subscriptions = null;
	}


	/**
	 * 更新顶部资产详情
	 */
	private void updateAssetValue() {
		hideValue = AssetManager.getInstance().isHideValue();
		mCheckboxEye.setChecked(hideValue);
		if (mTotalAccountData != null) {
			if (hideValue) {
				mTvTotalBalance.setText("*****");
				mTvTotalBalanceLocal.setText("*****");
			} else {
				mTvTotalBalance.setText(String.format("%s BTC", mTotalAccountData.getBtcTotalPreestimate()));
				mTvTotalBalanceLocal.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(mTotalAccountData.getCurrencySymbol()),
						mTotalAccountData.getLocalTotalPreestimate()));
			}
		}
	}

	/**
	 * 获取全部的资产列表
	 */
	private void getBalanceList() {

		if (!isActivityExist()) {
			if (mPullRefreshLayout != null) {
				mPullRefreshLayout.setRefreshing(false);
			}
			return;
		}

		if (!CommonUtil.isLoginAndUnLocked()) {
			if (mPullRefreshLayout != null) {
				mPullRefreshLayout.setRefreshing(false);
			}
			return;
		}

		OkGo.<TotalPreestimateModel>get(Constants.BALANCE_TOTAL_ACCOUNT).tag(this).execute(new NewJsonSubCallBack<TotalPreestimateModel>() {
			@Override
			public void onSuc(Response<TotalPreestimateModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					mTotalAccountData = response.body().getData();
					updateAssetValue();
				}
			}

			@Override
			public void onE(Response<TotalPreestimateModel> response) {
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mPullRefreshLayout != null) {
					mPullRefreshLayout.setRefreshing(false);
				}
			}
		});
	}

	/**
	 * 充币
	 */
	@NeedLogin(jump = true)
	private void rechargeClick() {
		PostPointHandler.postClickData(PostPointHandler.balance_deposit);

		//充值
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null) {
			return;
		}

		//巴西站kyc未认证 不能充币
		if (SiteController.getInstance().isBrSite() && !UserInfoController.getInstance().checkKycStatus()) {
			ToastUtil.show(R.string.br_kyc_tip);
			return;
		}

		if (!userTable.deposit) {
			AlertDialogBuilder builder = DialogManager.getAlertDialogBuilder(getContext());
			builder.setMessage(R.string.restricted_deposit);
			builder.setPositiveButton(R.string.btn_ok);
			builder.showDialog();
			return;
		}
		//手机号已经绑定，可以进入
		else if (!TextUtils.isEmpty(userTable.phone)) {
			Bundle bundle = new Bundle();
			bundle.putInt("from", SelectCoinActivity.FROM_DEPOSIT);
			SelectCoinActivity.startMe(getContext(), bundle);
			return;
		} else if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
			AlertDialogBuilder builder = DialogManager.getAlertDialogBuilder(getContext());
			builder.setMessage(R.string.dialog_recharge_content);
			builder.setNegativeButton(R.string.btn_cancel);
			builder.setPositiveButton(R.string.btn_ok);
			builder.showDialog();

			builder.setListener(new DialogListener() {
				@Override
				public void clickNegative() {

				}

				@Override
				public void clickPositive() {
					ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(getContext());
				}
			});

			return;
		}
		Bundle bundle = new Bundle();
		bundle.putInt("from", SelectCoinActivity.FROM_DEPOSIT);
		SelectCoinActivity.startMe(getContext(), bundle);
	}

	/**
	 * 提币
	 */
	@NeedLogin(jump = true)
	private void withdrawClick() {
		PostPointHandler.postClickData(PostPointHandler.balance_withdraw);

		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null) {
			return;
		}

		//巴西站kyc未认证 不能提币
		if (SiteController.getInstance().isBrSite() && !UserInfoController.getInstance().checkKycStatus()) {
			ToastUtil.show(R.string.br_kyc_tip);
			return;
		}

		//提现被限制
		if (!userTable.withdraw) {
			//换绑手机号 24小时限制提币
			if (!TextUtils.isEmpty(userTable.withdrawBanReason) && Constants.CHANGE_PHONE.equals(userTable.withdrawBanReason)) {
				AlertDialogBuilder builder = DialogManager.getAlertDialogBuilder(getContext());
				builder.setMessage(R.string.twentyFour_dont_withdray);
				builder.setPositiveButton(R.string.btn_ok);
				builder.showDialog();
			} else {
				AlertDialogBuilder builder = DialogManager.getAlertDialogBuilder(getContext());
				builder.setMessage(R.string.restricted_withdrawing);
				builder.setPositiveButton(R.string.btn_ok);
				builder.showDialog();
			}
			return;
		}

		StringBuilder dialogContent = new StringBuilder();
		if (TextUtils.isEmpty(userTable.phone)) {
			//邮箱用户
			boolean isOnlyPin = false;
			if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
				dialogContent.append(getString(R.string.dialog_email_verify));
			}
			if (!userTable.googleBind) {
				if (dialogContent.length() > 0) {
					dialogContent.append("\n");
				}
				dialogContent.append(getString(R.string.dialog_bind_google_verify));
			}
			if (!userTable.pinSetting) {
				//如果含有其他字段，表示需要先进入安全中心；否则直接进入资产设置
				isOnlyPin = dialogContent.length() <= 0;
				if (dialogContent.length() > 0) {
					dialogContent.append("\n");
				}
				dialogContent.append(getString(R.string.dialog_cap_modify_verify));
			}

			if (!TextUtils.isEmpty(dialogContent)) {
				boolean finalIsOnlyPin = isOnlyPin;

				MessageDialogBuilder builder = DialogManager.getMessageDialogBuilder(getContext());
				builder.setTitle(R.string.dialog_withdraw_title);
				builder.setNegativeButton(R.string.btn_cancel);
				builder.setMessage(dialogContent.toString());
				builder.setPositiveButton(R.string.btn_ok);
				builder.showDialog();

				builder.setListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {
						if (finalIsOnlyPin) {
							ARouter.getInstance().build(RouteHub.User.settingBalanceActivity).navigation(getContext());
						} else {
							ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(getContext());
						}
					}
				});
				return;
			}
		} else {
			//手机用户
			boolean isOnlyPin = false;
			if (!userTable.googleBind) {
				dialogContent.append(getString(R.string.dialog_bind_google_verify));
			}

			if (!userTable.pinSetting) {
				isOnlyPin = dialogContent.length() <= 0;
				if (dialogContent.length() > 0) {
					dialogContent.append("\n");
				}
				dialogContent.append(getString(R.string.dialog_cap_modify_verify));
			}


			if (!TextUtils.isEmpty(dialogContent)) {
				boolean finalIsOnlyPin = isOnlyPin;

				MessageDialogBuilder builder = DialogManager.getMessageDialogBuilder(getContext());
				builder.setTitle(R.string.dialog_withdraw_title);
				builder.setPositiveButton(R.string.go_setting);
				builder.setMessage(dialogContent.toString());
				builder.showDialog();
				builder.setListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {
						if (finalIsOnlyPin) {
							ARouter.getInstance().build(RouteHub.User.settingBalanceActivity).navigation(getContext());
						} else {
							ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(getContext());
						}
					}
				});
				return;
			}
		}

		Bundle bundle = new Bundle();
		bundle.putInt("from", SelectCoinActivity.FROM_WITHDRAW);
		SelectCoinActivity.startMe(getContext(), bundle);
	}

	/**
	 * 划转
	 */
	@PostClickData(value = PostPointHandler.balance_transfer)
	private void transferClick() {
		switch (mCurrentProduct.getType()) {
			case Product.TYPE_SPOT:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setFrom(Product.NAME_SPOT)
								.toBundle());
				break;
			case Product.TYPE_BTC_CONTRACT:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setAsset("BTC")
								.setFrom(Product.NAME_SPOT)
								.setTo(Product.NAME_BTC_CONTRACT)
								.toBundle());
				break;
			case Product.TYPE_USDT_CONTRACT:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setAsset("USDT")
								.setFrom(Product.NAME_SPOT)
								.setTo(Product.NAME_USDT_CONTRACT)
								.toBundle());
				break;
			case Product.TYPE_MARGIN:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setFrom(Product.NAME_SPOT)
								.setTo(Product.NAME_MARGIN)
								.toBundle());
				break;
			case Product.TYPE_OPTIONS:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setFrom(Product.NAME_SPOT)
								.setTo(Product.NAME_OPTIONS)
								.toBundle());
				break;
			case Product.TYPE_GAME:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setAsset("USDT")
								.setFrom(Product.NAME_SPOT)
								.setTo(Product.NAME_GAME)
								.toBundle());
				break;

			case Product.TYPE_FORTUNE:
				UIBusService.getInstance().openUri(getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
						new TransferParams()
								.setFrom(Product.NAME_SPOT)
								.setTo(Product.NAME_FORTUNE)
								.toBundle());
				break;
		}
	}
}
