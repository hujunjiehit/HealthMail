package com.coinbene.manbiwang.market;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.activities.SelectSearchPairActivity;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.database.TradePairOptionalTable;
import com.coinbene.common.database.TradePairOptionalTable_;
import com.coinbene.common.utils.ListUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.websocket.market.MarketType;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.widget.FixSwipeRefreshLayout;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.market.activity.EditOptionalActivity;
import com.coinbene.manbiwang.market.adapter.TradePairGroupBean;
import com.coinbene.manbiwang.market.adapter.TradePairPageAdapter;
import com.coinbene.manbiwang.market.manager.MarketDataManager;
import com.coinbene.manbiwang.market.presenter.MarketPresenter;
import com.coinbene.manbiwang.market.presenter.impl.MarketInterface;
import com.coinbene.manbiwang.market.widget.SortLayout;
import com.coinbene.manbiwang.model.http.BannerList;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import io.objectbox.reactive.DataSubscriptionList;

/**
 * Created by june
 * on 2019-10-10
 */
public class MarketFragment extends CoinbeneBaseFragment implements MarketInterface.View {
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;

	@BindView(R2.id.sort_layout)
	SortLayout mSortLayout;
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.tv_fresh)
	TextView mTvFresh;
	@BindView(R2.id.btn_fresh)
	Button mBtnFresh;
	@BindView(R2.id.fresh_layout)
	LinearLayout mFreshLayout;
	@BindView(R2.id.swipe_refresh)
	FixSwipeRefreshLayout mSwipeRefresh;

	private MarketInterface.Presenter mPresenter;

	private String mSortField;
	private String mSortType = Constants.SORT_BY_SORT;//排序方式  ASC升序   DESC降序 sort为默认

	private List<ZendeskArticlesResponse.ArticlesBean> mZendeskArticles = new ArrayList<>();

	private List<TradePairGroupBean> mTradePairGroupList;
	private TradePairPageAdapter mTradePairPageAdapter;
	private TradePairGroupBean mCurrentTradePairGroup;

	private Timer timer;
	private TimerTask timerTask;
	private boolean isLooperStart = false;
	private String tradePairList; //自选http请求需要用到
	private DataSubscriptionList subscriptionList;


	NewMarketWebsocket.MarketDataListener mSpotMarketDataListener;
	NewMarketWebsocket.MarketDataListener mContractBtcMarketDataListener;
	NewMarketWebsocket.MarketDataListener mContractUsdtMarketDataListener;

	private QMUIAlphaImageButton imgSreach;

	@Override
	public int initLayout() {
		return R.layout.fragment_market;
	}

	@Override
	public void initView(View rootView) {
		TextView tvTitle = mTopBar.setTitle(R.string.market_title);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		tvTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTopBar.getLayoutParams();
		layoutParams.topMargin = QMUIDisplayHelper.getStatusBarHeight(getContext());
		mTopBar.setLayoutParams(layoutParams);
		imgSreach = mTopBar.addRightImageButton(R.drawable.res_icon_search, R.id.res_title_left_image);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(QMUIDisplayHelper.dp2px(getContext(), 20), QMUIDisplayHelper.dp2px(getContext(), 20));
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.rightMargin = QMUIDisplayHelper.dp2px(getContext(), 15);
		imgSreach.setLayoutParams(params);

		mPresenter = new MarketPresenter(this);

		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_2));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));

		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		if (subscriptionList == null) {
			subscriptionList = new DataSubscriptionList();
		}

		//监听自选数据库变化，拿到tradePairList数据用于请求自选行情
		CBRepository.boxFor(TradePairOptionalTable.class)
				.query()
				.orderDesc(TradePairOptionalTable_.sort)
				.build()
				.subscribe(subscriptionList)
				.onError(error -> error.printStackTrace())
				.observer(data -> {
					if (data != null && data.size() > 0) {
						tradePairList = ListUtils.listToStringComma(data);
					} else {
						tradePairList = null;
					}
				});

	}


	@Override
	public void setListener() {
		mSortLayout.setClickType((type, sortType) -> {
			if (type == 4) {
				EditOptionalActivity.startMe(getContext());
				PostPointHandler.postClickData(PostPointHandler.market_sort_edit);
				return;
			}

			//币种名称
			if (type == 0) {
				mSortField = Constants.SORT_COIN_NAME;
				//24小时
			} else if (type == 1) {
				mSortField = Constants.SORT_V24_VOL;
				//最新价
			} else if (type == 2) {
				mSortField = Constants.SORT_LAST_PRICE;
				//涨跌幅
			} else if (type == 3) {
				mSortField = Constants.SORT_FALL_REISE;
			}
			mSortType = sortType;
			MarketDataManager.getInstance().dispatchSortTypeClick(mSortField, sortType);
		});

		mBtnFresh.setOnClickListener(v -> {
			showHasDataView();
			mPresenter.getTradePair(true);
		});


		//搜索交易对
		imgSreach.setOnClickListener(v -> {
			PostPointHandler.postClickData(PostPointHandler.market_search);

			Bundle bundle = new Bundle();
			bundle.putInt("type", SelectSearchPairActivity.TYPE_SEARCH);
			SelectSearchPairActivity.startMe(v.getContext(), bundle);
		});

		//下拉刷新
		mSwipeRefresh.setOnRefreshListener(() -> {
			refreshMarketInfo();
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});
	}

	@Override
	public void initData() {

		initTradePairTab();
	}

	@Override
	public void onFragmentShow() {
		setMerBarBlack();
		PostPointHandler.postBrowerData(PostPointHandler.market_brower);

		if (mPresenter != null) {
			mPresenter.getTradePair(false);
			mPresenter.getNewMarginSymbol();
		}

		if (mSpotMarketDataListener == null) {
			mSpotMarketDataListener = map -> MarketDataManager.getInstance().dispatchSpotMarketData();
		}

		if (mContractBtcMarketDataListener == null) {
			mContractBtcMarketDataListener = map -> MarketDataManager.getInstance().dispatchContractBtcMarketData();
		}

		if (mContractUsdtMarketDataListener == null) {
			mContractUsdtMarketDataListener = map -> MarketDataManager.getInstance().dispatchContractUsdtMarketData();
		}

		NewMarketWebsocket.getInstance().registerMarketDataListener(mSpotMarketDataListener, MarketType.SPOT);
		NewMarketWebsocket.getInstance().registerMarketDataListener(mContractBtcMarketDataListener, MarketType.BTC_CONTRACT);
		NewMarketWebsocket.getInstance().registerMarketDataListener(mContractUsdtMarketDataListener, MarketType.USDT_CONTRACT);

	}


	@Override
	public void onFragmentHide() {
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(mSpotMarketDataListener, MarketType.SPOT);
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(mContractBtcMarketDataListener, MarketType.BTC_CONTRACT);
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(mContractUsdtMarketDataListener, MarketType.USDT_CONTRACT);

	}

	/**
	 * 初始化自选tab
	 */
	private void initTradePairTab() {
		//获取其它交易对列表（现货交易对和合约交易对）
		List<TradePairGroupTable> tradePairGroups;
		if (SwitchUtils.isOpenContract()) {
			tradePairGroups = TradePairGroupController.getInstance().getTradePairGroups();
		} else {
			tradePairGroups = TradePairGroupController.getInstance().getTradePairGroupsFilterContrack();
		}

		if (SwitchUtils.isOpenContract() && !ContractInfoController.getInstance().checkExistContrackList()) {
			//如果数据库中不存在合约，从网络获取合约交易对列表
			mPresenter.getContractList();
		}

		if (tradePairGroups == null || tradePairGroups.isEmpty()) {
			//从数据库中未读取到交易对信息
			mPresenter.getTradePair(true);
		} else {
			//从数据库中读取到交易对信息
			setTabData(tradePairGroups);
		}
	}

	@Override
	public void setTabData(List<TradePairGroupTable> pairGroups) {
		if (pairGroups == null || pairGroups.size() == 0) {
			showEmptyView();
			return;
		}

		showHasDataView();

		//初始化tabSegment
		if (mTradePairGroupList == null) {
			mTradePairGroupList = new ArrayList<>();
		}
		int currentPosition = 1;
		for (int i = 0; i < pairGroups.size(); i++) {
			TradePairGroupTable item = pairGroups.get(i);
			TradePairGroupBean tradePairGroupBean = new TradePairGroupBean();
			if (item.groupName.equals(TradePairGroupTable.SELF_GROUP)) {
				tradePairGroupBean.setGroupTitle(getString(R.string.self));
			} else if (item.groupName.equals(TradePairGroupTable.CONTRACT_GROUP)) {
				tradePairGroupBean.setGroupTitle(getString(R.string.contract_title));
				currentPosition = 2;
			} else {
				tradePairGroupBean.setGroupTitle(item.groupName);
			}
			tradePairGroupBean.setGroupName(item.groupName);
			tradePairGroupBean.setGroupId(String.valueOf(i));
			mTradePairGroupList.add(tradePairGroupBean);
		}
		mTradePairPageAdapter = new TradePairPageAdapter(getChildFragmentManager(), mTradePairGroupList);
		mViewPager.setOffscreenPageLimit(mTradePairGroupList.size());
		mViewPager.setAdapter(mTradePairPageAdapter);
		mTabSegment.setupWithViewPager(mViewPager);

		//默认选中的tab
		if (TradePairOptionalController.getInstance().queryTradePairOptional().size() != 0) {
			currentPosition = 0;
		}
		mCurrentTradePairGroup = mTradePairGroupList.get(currentPosition);
		setSortView(Tools.parseInt(mCurrentTradePairGroup.getGroupId()));

		mTabSegment.addOnTabSelectedListener(new QMUITabSelectedListener() {
			@Override
			public void onTabSelected(int index) {
				if (mTradePairGroupList != null && index < mTradePairGroupList.size()) {
					mCurrentTradePairGroup = mTradePairGroupList.get(index);
					setSortView(Tools.parseInt(mCurrentTradePairGroup.getGroupId()));
				}
			}
		});

		mTabSegment.selectTab(Tools.parseInt(mCurrentTradePairGroup.getGroupId()), true, false);
	}

	private void refreshMarketInfo() {
		if (mPresenter == null) {
			return;
		}
		mPresenter.getTradePair(false);
		mPresenter.getContractList();
		mPresenter.getNewMarginSymbol();
	}


	private void setSortView(int currentPosition) {
		if (SwitchUtils.isOpenContract() && currentPosition == 1) {
			mSortLayout.setVisibility(View.GONE);
		} else {
			mSortLayout.setVisibility(View.VISIBLE);
			if (currentPosition == 0) {
				mSortLayout.setOption(true);
			} else {
				mSortLayout.setOption(false);
			}
		}
	}



	@Override
	public void setTabDataError() {
		showEmptyView();
	}

	@Override
	public void refreshMarketDataError(String errorCode) {

	}

	@Override
	public void onGetBannerList(List<BannerList.DataBean> bannerList) {

	}

	@Override
	public void onGetZendeskArticleList(List<ZendeskArticlesResponse.ArticlesBean> articles) {

	}

	@Override
	public void setPresenter(MarketInterface.Presenter presenter) {

	}

	private void showEmptyView() {
		//fix bug
		if (mTabSegment == null) {
			return;
		}
		mTabSegment.setVisibility(View.GONE);
		mViewPager.setVisibility(View.GONE);
		mSortLayout.setVisibility(View.GONE);
		mFreshLayout.setVisibility(View.VISIBLE);
	}

	private void showHasDataView() {
		if (mTabSegment != null && mTabSegment.getVisibility() == View.GONE) {
			mTabSegment.setVisibility(View.VISIBLE);
		}
		if (mViewPager != null && mViewPager.getVisibility() == View.GONE) {
			mViewPager.setVisibility(View.VISIBLE);
		}
		if (mSortLayout != null && mSortLayout.getVisibility() == View.GONE) {
			mSortLayout.setVisibility(View.VISIBLE);
		}

		if (mFreshLayout != null && mFreshLayout.getVisibility() == View.VISIBLE) {
			mFreshLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (subscriptionList != null) {
			subscriptionList.cancel();
		}
	}
}
