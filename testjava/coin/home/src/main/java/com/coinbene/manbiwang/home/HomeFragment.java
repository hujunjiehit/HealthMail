package com.coinbene.manbiwang.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.Group;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.banner.BannerAdapter;
import com.coinbene.common.widget.marquee.MarqueeView;
import com.coinbene.manbiwang.home.adapter.HotCoinAdapter;
import com.coinbene.manbiwang.home.view.NavigationLayout;
import com.coinbene.manbiwang.home.view.StickerLayout;
import com.coinbene.manbiwang.home.view.TransformerLayout;
import com.coinbene.manbiwang.model.http.AppConfigModel;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;
import com.coinbene.manbiwang.service.RouteHub;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.erwa.sourceset.view.banner.BannerView;

/**
 * ding
 * 2019-12-24
 * com.coinbene.manbiwang.home
 */
public class HomeFragment extends CoinbeneBaseFragment {
	static final String TAG = "HomeFragment";

	@BindView(R2.id.navigation_RecyclerView)
	NavigationLayout mNavigation;
	@BindView(R2.id.banner)
	BannerView mBanner;
	@BindView(R2.id.notice_center)
	MarqueeView mNoticeCenter;
	@BindView(R2.id.imageView)
	ImageView imgNoticeIcon;
	@BindView(R2.id.img_User)
	ImageView imgUserIcon;
	@BindView(R2.id.swipeRefreshLayout)
	SwipeRefreshLayout mRefreshLayout;
	@BindView(R2.id.sticker_RecyclerView)
	StickerLayout mSticker;
	@BindView(R2.id.hot_RecyclerView)
	RecyclerView mHotRecyclerView;
	@BindView(R2.id.scrollView)
	NestedScrollView mScrollView;
	@BindView(R2.id.transformerLayout)
	TransformerLayout mTransformer;
	@BindView(R2.id.iv_home_title)
	ImageView mIvHomeTitle;
	@BindView(R2.id.group_big_coin)
	Group groupBigCoin;

	private HomeViewModel homeViewMode;
	private BannerAdapter bannerAdapter;
	private HotCoinAdapter hotCoinAdapter;

	private List<ZendeskArticlesResponse.ArticlesBean> noticeData;
	private NavigationLayout.OnPageChangeListener onPageChangeListener1;
	private StickerLayout.OnPageChangeListener onPageChangeListener;

	@Override
	public int initLayout() {
		return R.layout.fragment_home;
	}

	@Override
	public void initView(View rootView) {


		ViewGroup.LayoutParams params = mIvHomeTitle.getLayoutParams();
		if (LanguageHelper.isChinese(getContext())) {
			params.width = QMUIDisplayHelper.dp2px(getContext(), 170);
		} else {
			params.width = QMUIDisplayHelper.dp2px(getContext(), 128);
		}
		params.height = QMUIDisplayHelper.dp2px(getContext(), 25);
		mIvHomeTitle.setLayoutParams(params);


		homeViewMode = new ViewModelProvider(this).get(HomeViewModel.class);

		mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		//热门币种
		LinearLayoutManager hotLayoutManager = new LinearLayoutManager(getContext());
		hotLayoutManager.setSmoothScrollbarEnabled(true);
		mHotRecyclerView.setHasFixedSize(true);
		mHotRecyclerView.setNestedScrollingEnabled(false);
		mHotRecyclerView.setLayoutManager(hotLayoutManager);
//		mHotRecyclerView.addItemDecoration(new ItemDivider(getContext()));

		hotCoinAdapter = new HotCoinAdapter(getContext());
		hotCoinAdapter.bindToRecyclerView(mHotRecyclerView);

		//Banner
		bannerAdapter = new BannerAdapter();
		mBanner.setBannerViewImpl(bannerAdapter);
	}


	@Override
	public void onResume() {
		super.onResume();

		//Home页面导航
		int radius = QMUIDisplayHelper.dp2px(getContext(), 5);
		int color = ResourceProvider.getColor(getContext(), R.color.res_background);
		DLog.d(TAG, "color==========>" + Integer.toHexString(color));
		mNavigation.setBackground(ResourceProvider.getRectShape(radius, color, color));
	}

	@Override
	public void setListener() {

		//banner点击事件
		bannerAdapter.setBannerListener(url -> {
			if (!TextUtils.isEmpty(url)) {
				String finalUrl = UrlUtil.parseUrl(url);
				Bundle bundle = new Bundle();
				if (finalUrl.contains("/notice/") || finalUrl.contains(".zendesk.com")) {
					bundle.putBoolean("isNotice", true);
				}
				bundle.putString("SchemeFrom", SchemeFrom.APP_BANNER.name());
				UIBusService.getInstance().openUri(getContext(), finalUrl, bundle);
				PostPointHandler.postClickData(PostPointHandler.market_banner + finalUrl);
			}
		});

		//公告点击事件
		mNoticeCenter.setOnItemClickListener((position, textView) -> {
			if (noticeData == null || position >= noticeData.size()) {
				return;
			}

			ZendeskArticlesResponse.ArticlesBean dataBean = noticeData.get(position);
			if (dataBean != null && !TextUtils.isEmpty(dataBean.getHtml_url())) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNotice", true);
				UIBusService.getInstance().openUri(getContext(), dataBean.getHtml_url(), bundle);
				PostPointHandler.postClickData(PostPointHandler.market_notice + dataBean.getHtml_url());
			}
		});

		//点击用户Icon
		imgUserIcon.setOnClickListener(v -> {
			ARouter.getInstance().build(RouteHub.User.mySelfActivity).navigation(getContext());
			PostPointHandler.postClickData(PostPointHandler.balance_user_center);
		});

		mRefreshLayout.setOnRefreshListener(() -> {
			getHomeData();
			mRefreshLayout.postDelayed(() -> mRefreshLayout.setRefreshing(false), 500);
		});

		onPageChangeListener = page -> {
			mNavigation.setOnPageChangeListener(null);
			mSticker.setOnPageChangeListener(null);
			mNavigation.scroolToPage(page);
			mSticker.setOnPageChangeListener(onPageChangeListener);
			mNavigation.setOnPageChangeListener(onPageChangeListener1);
		};
		mSticker.setOnPageChangeListener(onPageChangeListener);
		onPageChangeListener1 = page -> {
			mNavigation.setOnPageChangeListener(null);
			mSticker.setOnPageChangeListener(null);
			mSticker.scroolToPage(page);
			mSticker.setOnPageChangeListener(onPageChangeListener);
			mNavigation.setOnPageChangeListener(onPageChangeListener1);
		};
		mNavigation.setOnPageChangeListener(onPageChangeListener1);


		mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
			if (scrollY >= QMUIDisplayHelper.dpToPx(107) && homeViewMode.isShowNavigation()) {
				mSticker.setVisibility(View.VISIBLE);
				setMerBarBlack();

			} else {
				mSticker.setVisibility(View.INVISIBLE);
				setMerBarWhite();
			}
		});

		mTransformer.setOnItemClickListener((adapter, view, position) -> {
			WsMarketData item = (WsMarketData) adapter.getData().get(position);
			UIBusService.getInstance().openUri(getContext(), "coinbene://SpotKline?pairName=" + item.getTradePairName(), null);
		});
		hotCoinAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			WsMarketData item = (WsMarketData) adapter.getData().get(position);
			UIBusService.getInstance().openUri(getContext(), "coinbene://SpotKline?pairName=" + item.getTradePairName(), null);
		});
	}

	private void getHomeData() {
		homeViewMode.getBanner();
		homeViewMode.getNotice();
		homeViewMode.getNavigation();
		homeViewMode.getHotCoin();
		homeViewMode.getBigCoin();
	}


	@Override
	public void initData() {

		//订阅公告数据
		homeViewMode.getNoticeData().observe(this, this::setNoticeData);

		//订阅热门数据
		homeViewMode.getHotData().observe(this, hotData -> {
			hotCoinAdapter.setNewData(hotData);
		});

		//订阅banner数据
		homeViewMode.getBannerData().observe(this, bannerData -> {
			bannerAdapter.setData(bannerData);
			mBanner.post(() -> mBanner.doRecreate());
		});

		//订阅快速导航数据
		homeViewMode.getNavData().observe(this, navData -> {
			if (navData == null || navData.size() == 0) {
				mNavigation.setVisibility(View.GONE);
				return;
			}
			if (mNavigation.getVisibility() == View.GONE) {
				mNavigation.setVisibility(View.VISIBLE);
			}
			mSticker.setData(navData);
			mNavigation.setData(navData);

		});
		mNavigation.setOnItemClickListener((adapter, view, position) -> {
			AppConfigModel.MainNavigationBean navigationBean = (AppConfigModel.MainNavigationBean) adapter.getData().get(position);
			UIBusService.getInstance().openUri(getContext(), navigationBean.getSchema(), null);
		});
		mSticker.setOnItemClickListener((adapter, view, position) -> {
			AppConfigModel.MainNavigationBean navigationBean = (AppConfigModel.MainNavigationBean) adapter.getData().get(position);
			UIBusService.getInstance().openUri(getContext(), navigationBean.getSchema(), null);
		});


		//订阅大币种数据
		homeViewMode.getBigData().observe(this, data -> {

			if (data == null) {
				groupBigCoin.setVisibility(View.GONE);
			} else {
				if (groupBigCoin.getVisibility() == View.GONE) {
					groupBigCoin.setVisibility(View.VISIBLE);
				}
				mTransformer.setData(data);
			}
		});
	}

	@Override
	public void onFragmentShow() {
		if (mSticker.getVisibility() == View.VISIBLE) {
			setMerBarBlack();
		} else {
			setMerBarWhite();
		}
		getHomeData();

		homeViewMode.registerMarketData();
		mBanner.startAutoScroll();
		mNoticeCenter.startFlipping();
	}

	@Override
	public void onFragmentHide() {
		homeViewMode.unRegisterMarketData();
		mBanner.stopAutoScroll();
		mNoticeCenter.clearAnimation();
		mNoticeCenter.stopFlipping();
	}

	/**
	 * @param noticeData 公告数据
	 *                   设置公告数据
	 */
	private void setNoticeData(List<ZendeskArticlesResponse.ArticlesBean> noticeData) {
		this.noticeData = noticeData;

//		DLog.d(TAG, noticeData.toString());

		if (noticeData == null || noticeData.size() == 0) {
			imgNoticeIcon.setVisibility(View.GONE);
			mNoticeCenter.setVisibility(View.GONE);
		} else {
			imgNoticeIcon.setVisibility(View.VISIBLE);
			mNoticeCenter.setVisibility(View.VISIBLE);

			List<String> titles = new ArrayList<>();

			for (ZendeskArticlesResponse.ArticlesBean articles : noticeData) {
				titles.add(articles.getTitle());
			}

			mNoticeCenter.startWithList(titles);
		}
	}
}
