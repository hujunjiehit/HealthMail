package com.coinbene.manbiwang.fortune;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.Product;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.LockUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.fortune.activity.adapter.AssetListAdapter;
import com.coinbene.manbiwang.model.http.YbbAccountTotalModel;
import com.coinbene.manbiwang.model.http.YbbAssetConfigListModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.google.android.material.appbar.AppBarLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.util.QMUIColorHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIAppBarLayout;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;

@Route(path = RouteHub.Fortune.fortuneActivity)
public class FortuneActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;
	@BindView(R2.id.tv_ybb_presstimate)
	TextView mTvYbbPresstimate;
	@BindView(R2.id.cb_eye)
	CheckBox mCbEye;
	@BindView(R2.id.tv_ybb_account_balance)
	TextView mTvYbbAccountBalance;
	@BindView(R2.id.tv_ybb_local_balance)
	TextView mTvYbbLocalBalance;
	@BindView(R2.id.tv_yesterday_interest_key)
	TextView mTvYesterdayInterestKey;
	@BindView(R2.id.tv_total_interest_key)
	TextView mTvTotalInterestKey;
	@BindView(R2.id.tv_yesterday_interest)
	TextView mTvYesterdayInterest;
	@BindView(R2.id.tv_total_interest)
	TextView mTvTotalInterest;
	@BindView(R2.id.view_text_container)
	View mViewTextContainer;
	@BindView(R2.id.layout_top_info)
	ConstraintLayout mLayoutTopInfo;
	@BindView(R2.id.topbar)
	QMUITopBar mTopBar;
	@BindView(R2.id.collapsing_topbar_layout)
	QMUICollapsingTopBarLayout mCollapsingTopbarLayout;
	@BindView(R2.id.appbar_layout)
	QMUIAppBarLayout mAppbarLayout;
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.tv_count_down)
	TextView mTvCountDown;
	@BindView(R2.id.divider_line)
	View mDividerLine;

	private TextView mRightText;
	private TextView mTitle;
	private ImageView mLeftImage;

	private AssetListAdapter mAdapter;

	private View.OnClickListener onTopInfoClick;

	private YbbAccountTotalModel.DataBean mTopData;

	private CountDownTimer mCountDownTimer;

	@Override
	public int initLayout() {
		return R.layout.fortune_activity;
	}

	@Override
	public void initView() {
		setTopbarMargin(mTopBar);

		setFitsSystemWindows(false);


		mLeftImage = mTopBar.addLeftImageButton(R.drawable.res_icon_back_white, R.id.iv_left_image);
		mTopBar.setBackgroundColor(getResources().getColor(com.coinbene.common.R.color.transparent));

		mTitle = mTopBar.setTitle(R.string.res_fortune);

		mRightText = mTopBar.addRightTextButton(R.string.res_fortune_guide, R.id.res_title_right_text);
		mRightText.setTextColor(QMUIColorHelper.setColorAlpha(getResources().getColor(R.color.res_white), 0.8f));

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mAdapter = new AssetListAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);

		mCbEye.setChecked(AssetManager.getInstance().isHideValue());

		mTvYbbPresstimate.setText(getString(R.string.res_ybb_presstimate).replace(":", ""));
	}

	@Override
	public void setListener() {
		mLeftImage.setOnClickListener(v -> onBack());

		if (mRightText != null) {
			mRightText.setOnClickListener(v -> {
				Bundle bundle = new Bundle();
				bundle.putString("title", getString(R.string.res_fortune_guide));
				UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getYbbGuideUrl(), bundle);
			});
		}

		mAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				//verticalOffset from 0 -> -50dp
				if (verticalOffset < 0) {
					mSwipeRefresh.setEnabled(false);
				} else {
					mSwipeRefresh.setEnabled(true);
				}

				int target = QMUIDisplayHelper.dp2px(FortuneActivity.this, 90);

				float percent;

				int current = Math.abs(verticalOffset);
				if (current >= target) {
					percent = 0.0f;
				} else {
					if (current <= QMUIDisplayHelper.dp2px(FortuneActivity.this, 30)) {
						percent = 1.0f;
					} else {
						percent = 1.0f - current * 1.0f / target;
					}
				}

				mTvYbbPresstimate.setAlpha(percent);
				mTvTotalInterestKey.setAlpha(percent);
				mTvYesterdayInterestKey.setAlpha(percent);
				mTvYbbAccountBalance.setAlpha(percent);
				mTvYbbLocalBalance.setAlpha(percent);
				mCbEye.setAlpha(percent);
				mTvYesterdayInterest.setAlpha(percent);
				mTvTotalInterest.setAlpha(percent);
				mDividerLine.setAlpha(percent);
			}
		});

		mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			YbbAssetConfigListModel.DataBean item = (YbbAssetConfigListModel.DataBean) adapter.getItem(position);
			gotoYbbTransfer(item.getAsset());
		});

		if (onTopInfoClick == null) {
			onTopInfoClick = v -> ARouter.getInstance().build(RouteHub.Fortune.ybbDetailActivity).navigation(v.getContext());
		}
		mTvYbbAccountBalance.setOnClickListener(onTopInfoClick);
		mTvYbbLocalBalance.setOnClickListener(onTopInfoClick);

		mSwipeRefresh.setOnRefreshListener(() -> {
			refreshData();
			mSwipeRefresh.postDelayed(() -> {
				if (mSwipeRefresh != null)
					mSwipeRefresh.setRefreshing(false);
			}, 500);
		});

		mCbEye.setOnClickListener(v -> {
			boolean hideValue = mCbEye.isChecked();
			SpUtil.setPreHideAssetEstimation(hideValue);
			AssetManager.getInstance().setHideValue(hideValue);
			updateTopinfo();
		});

	}

	private void refreshData() {
		getAssetList();
		getYbbAccountTotal();
	}

	@NeedLogin(jump = true)
	private void gotoYbbTransfer(String asset) {
		SpUtil.setTransferToYbb(true);
		UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl("transfer"),
				new TransferParams()
						.setFrom(Product.NAME_SPOT)
						.setTo(Product.NAME_FORTUNE)
						.setAsset(asset)
						.toBundle());
	}


	@Override
	public void initData() {

	}


	@Override
	protected void onResume() {
		super.onResume();

		switch (mUserStatus) {
			case UN_LOGIN:
				mLayoutTopInfo.setVisibility(View.GONE);
				mAppbarLayout.setBackgroundColor(getResources().getColor(R.color.res_background));
				mTitle.setTextColor(getResources().getColor(R.color.res_textColor_1));
				mRightText.setTextColor(getResources().getColor(R.color.res_blue));
				mLeftImage.setImageDrawable(getDrawable(R.drawable.res_icon_back));
				break;
			case LOCKED:
				//如果页面锁住，且当前tab不是行情，需要弹出解锁页面
				LockUtils.showLockPage(this);
				return;
			case LOGIN:
				mLayoutTopInfo.setVisibility(View.VISIBLE);
				mAppbarLayout.setBackground(getResources().getDrawable(R.drawable.fortune_card_background));
				mTitle.setTextColor(getResources().getColor(R.color.white));
				mRightText.setTextColor(QMUIColorHelper.setColorAlpha(getResources().getColor(R.color.res_white), 0.8f));
				mLeftImage.setImageDrawable(getDrawable(R.drawable.res_icon_back_white));
				setMerBarWhite();
				break;
		}

//		mSwipeRefresh.post(() -> {
//			mSwipeRefresh.setRefreshing(true);
//			refreshData();
//		});
		refreshData();
	}

	/**
	 * 获取资产配置列表
	 */
	public void getAssetList() {
		OkGo.<YbbAssetConfigListModel>get(Constants.YBB_ASSET_CONFIG_LIST).tag(this).execute(new NewJsonSubCallBack<YbbAssetConfigListModel>() {
			@Override
			public void onSuc(Response<YbbAssetConfigListModel> response) {
				if (response.body() != null) {
					setAdapterData(mAdapter, response.body().getData());
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
			}

			@Override
			public void onE(Response<YbbAssetConfigListModel> response) {

			}
		});
	}

	/**
	 * 获取余币宝资产详情
	 */
	@NeedLogin
	public void getYbbAccountTotal() {
		OkGo.<YbbAccountTotalModel>get(Constants.YBB_ACCOUNT_TOTAL).tag(this).execute(new NewJsonSubCallBack<YbbAccountTotalModel>() {
			@Override
			public void onSuc(Response<YbbAccountTotalModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					mTopData = response.body().getData();
					updateTopinfo();
					if (!TextUtils.isEmpty(mTopData.getProfitTime())) {
						startCountDown(mTopData.getProfitTime());
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
			}

			@Override
			public void onE(Response<YbbAccountTotalModel> response) {
				updateTopinfo();
			}
		});
	}

	/**
	 * @param profitTime 单位秒
	 */
	private void startCountDown(String profitTime) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}

		mCountDownTimer = new CountDownTimer(Tools.parseLong(profitTime) * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				if (mTvCountDown.getVisibility() == View.GONE) {
					mTvCountDown.setVisibility(View.VISIBLE);
				}
				mTvCountDown.setText(String.format(getString(R.string.res_count_down_info), TimeUtils.getCountDownHMS(millisUntilFinished / 1000)));
			}

			@Override
			public void onFinish() {
				//倒计时结束重新请求一次数据
				refreshData();
			}
		};
		mCountDownTimer.start();
	}

	private void updateTopinfo() {
		if (mTopData != null) {
			if (AssetManager.getInstance().isHideValue()) {
				mTvYbbAccountBalance.setText("*****");
				mTvYbbLocalBalance.setText("*****");
				mTvYesterdayInterest.setText("*****");
				mTvTotalInterest.setText("*****");
			} else {
				mTvYbbAccountBalance.setText(String.format("%s USDT", mTopData.getCurrentValuation()));
				mTvYbbLocalBalance.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(mTopData.getCurrencySymbol()), mTopData.getLocalPreestimate()));
				mTvYesterdayInterest.setText(mTopData.getYesterdayValuation());
				mTvTotalInterest.setText(mTopData.getProfitValuation());
			}
		} else {
			mTvYbbAccountBalance.setText(String.format("%s BTC", "--"));
			mTvYbbLocalBalance.setText(String.format("≈ %s", "--"));
			mTvYesterdayInterest.setText("--");
			mTvTotalInterest.setText("--");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}

	@Override
	public boolean needLock() {
		return false;
	}
}
