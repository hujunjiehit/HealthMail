package com.coinbene.manbiwang.market.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.activities.SelectSearchPairActivity;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairInfoTable_;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.database.TradePairOptionalTable;
import com.coinbene.common.database.TradePairOptionalTable_;
import com.coinbene.common.utils.MarketSortUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;
import com.coinbene.manbiwang.market.manager.AbsMarketDataListener;
import com.coinbene.manbiwang.market.manager.MarketDataManager;
import com.coinbene.common.utils.RecycleViewAnimUtil;
import com.coinbene.manbiwang.market.util.TradePairDiffCallback;
import com.coinbene.manbiwang.market.viewbinder.NewMarketItemBinder;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.objectbox.Box;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscriptionList;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 自选行情Fragment
 */
public class OptionalTradePairFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.market_recyclerview)
	RecyclerView mRecyclerview;
	@BindView(R2.id.iv_null)
	ImageView mIvNull;
	@BindView(R2.id.add_self_selection)
	TextView mAddSelfSelection;
	@BindView(R2.id.layout_option_footer)
	LinearLayout mLayoutOptionFooter;

	private MultiTypeAdapter mContentAdapter;
	private NewMarketItemBinder marketItemBinder;

	private String groupName;

	private MarketDataManager.MarketDataListener mMarketDataListener;

	private Items itemsOld;

	public List<WsMarketData> tradePairList;

	private boolean isScrolling = false;

	private MarketSortUtil mSortUtil;
	private String mSortField;//排序字段
	private String mSortType = Constants.SORT_BY_SORT;//排序方式  ASC升序   DESC降序 SORT默认排序

	private DataSubscriptionList subscriptions;

	private boolean forceFullRefresh = false;
	private boolean isRedRise = SpUtil.isRedRise();

	public static OptionalTradePairFragment newInstance(String groupName) {
		Bundle args = new Bundle();
		args.putString(Constants.BUNDLE_KEY_GROUPNAME, groupName);
		OptionalTradePairFragment fragment = new OptionalTradePairFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.market_optional_tradepair_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		marketItemBinder = new NewMarketItemBinder();
		mContentAdapter.register(WsMarketData.class, marketItemBinder);

		mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
		RecycleViewAnimUtil.closeDefaultAnimator(mRecyclerview);
		mRecyclerview.setAdapter(mContentAdapter);
	}


	@Override
	public void setListener() {
		mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					isScrolling = false;
					//滑动停止的时候拉一次数据
					NewMarketWebsocket.getInstance().pullMarketData();
				} else {
					isScrolling = true;
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}
		});

		mAddSelfSelection.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putInt("type", SelectSearchPairActivity.TYPE_SEARCH);
			SelectSearchPairActivity.startMe(v.getContext(), bundle);
		});
	}

	@Override
	public void initData() {
		tradePairList = Collections.synchronizedList(new ArrayList<>());

		Bundle bundle = this.getArguments();
		groupName = bundle.getString(Constants.BUNDLE_KEY_GROUPNAME, "");
		marketItemBinder.setGroupName(groupName);

		if (subscriptions == null) {
			subscriptions = new DataSubscriptionList();
		}

		onTradePairChanged(TradePairOptionalController.getInstance().queryTradePairOptional());

		//改成从监听的方式获取自选数据
		CBRepository.boxFor(TradePairOptionalTable.class)
				.query()
				.orderDesc(TradePairOptionalTable_.sort)
				.build()
				.subscribe(subscriptions)
				.transform(source -> {
					List<TradePairInfoTable> tradePairInfoTableList = new ArrayList<>();
					for (int i = 0; i < source.size(); i++) {
						TradePairOptionalTable optionalTable = source.get(i);
						Box<TradePairInfoTable> tableBox_tradePairTable = CBRepository.boxFor(TradePairInfoTable.class);
						TradePairInfoTable tradePairInfoTable = tableBox_tradePairTable.query().equal(TradePairInfoTable_.tradePair, optionalTable.tradePair).build().findFirst();
						if (tradePairInfoTable != null) {
							tradePairInfoTableList.add(tradePairInfoTable);
						}
					}
					return tradePairInfoTableList;
				})
				.on(AndroidScheduler.mainThread())
				.onError(error -> error.printStackTrace())
				.observer(new DataObserver<List<TradePairInfoTable>>() {
					@Override
					public void onData(List<TradePairInfoTable> data) {
						//第一次subscribe 或者 自选数据变化的时候会进来

						onTradePairChanged(data);
					}
				});
	}

	@Override
	public void onFragmentShow() {

		NewMarketWebsocket.getInstance().pullMarketData();

		if (isRedRise != SpUtil.isRedRise()) {
			isRedRise = SpUtil.isRedRise();
			forceFullRefresh = true;
		}
	}

	@Override
	public void onFragmentHide() {

	}

	private void onTradePairChanged(List<TradePairInfoTable> data) {
		if (data == null) {
			return;
		}

		if (tradePairList != null && tradePairList.size() > 0) {
			tradePairList.clear();
		}

		for (int i = 0; i < data.size(); i++) {
			TradePairInfoTable pairInfoTable = data.get(i);
			if (pairInfoTable == null) {
				continue;
			}
			WsMarketData bean = new WsMarketData();
			bean.setEnglishName(pairInfoTable.englishBaseAsset);
			bean.setChineseName(pairInfoTable.localBaseAsset);
			bean.setTradePairName(pairInfoTable.tradePairName);
			bean.setPrecision(pairInfoTable.pricePrecision);
			bean.setSymbol(pairInfoTable.tradePair);
			bean.setLatest(pairInfoTable.isLatest);
			bean.setHot(pairInfoTable.isHot);
			bean.setSort(i);
			bean.setTags(pairInfoTable.tags);

			//是否是杠杆
			MarginSymbolTable marginTable = MarginSymbolController.getInstance().getMarginTable(bean.getTradePairName());
			if (marginTable != null) {
				bean.setLeverage(marginTable.leverage);
				bean.setMargin(true);
			} else {
				bean.setMargin(false);
			}


			if (NewMarketWebsocket.getInstance().getSpotMarketMap().size() > 0) {
				TradeUtils.getMarketDataFromMap(bean, NewMarketWebsocket.getInstance().getSpotMarketMap());
			}
			tradePairList.add(bean);
		}


		//加载完数据之后排序，然后再刷新页面
		sortTradePair();

		freshFragmentBySort();
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mMarketDataListener == null) {
			mMarketDataListener = new AbsMarketDataListener() {

				@Override
				public void onReceiveSpotMarketData(Map<String, WsMarketData> spotTradePairMap) {
					if (!isShowing) return;

					//收到现货行情数据
					synchronized (tradePairList) {
						for (int i = 0; i < tradePairList.size(); i++) {
							WsMarketData bean = tradePairList.get(i);
							if (spotTradePairMap.get(bean.getSymbol()) == null) {
								continue;
							}
							TradeUtils.getMarketDataFromMap(bean, spotTradePairMap);
						}
					}

					//重新排序
					sortTradePair();

					getActivity().runOnUiThread(() -> freshFragment());
				}

				@Override
				public void onSortTypeClick(String sortField, String sortType) {
					mSortField = sortField;
					mSortType = sortType;

					sortTradePair();

					//排序之后刷新界面
					getActivity().runOnUiThread(() -> freshFragmentBySort());
				}

				@Override
				public void onReceiveHttpMarketData(String groupId, Map<String, TradePairMarketRes.DataBean> httpDataMap) {
//					if (groupName.equals(groupId) || groupId.equals(getString(R.string.self)) ) {
//						synchronized (tradePairList) {
//							for (int i = 0; i < tradePairList.size(); i++) {
//								TradePairMarketRes.DataBean bean = tradePairList.get(i);
//								if (httpDataMap.get(bean.getPairID()) == null) {
//									continue;
//								}
//								TradeUtils.getMarketDataFromMap(bean, httpDataMap);
//							}
//
//							//重新排序
//							sortTradePair();
//
//							forceFullRefresh = true;
//
//							getActivity().runOnUiThread(() -> freshFragment());
//						}
//					}
				}
			};
		}
		MarketDataManager.getInstance().registerMarketDataListener(mMarketDataListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		MarketDataManager.getInstance().unregisterMarketDataListener(mMarketDataListener);
	}

	public void freshFragmentBySort() {

		forceFullRefresh = true;

		freshFragment();
		if (mRecyclerview != null) {
			mRecyclerview.scrollToPosition(0);
		}
	}

	public void freshFragment() {
		if (isScrolling || TextUtils.isEmpty(groupName)) {
			//RecycleView正在滑动的时候，不刷新界面，滑动结束之后会再拉取一次数据
			return;
		}

		if (mRecyclerview != null) {
			if (mRecyclerview.getAdapter() == null) {
				mRecyclerview.setAdapter(mContentAdapter);
			}

			if (tradePairList != null && tradePairList.size() > 0) {

				//tradePairList是新来的数据

				if (itemsOld == null) {
					itemsOld = new Items();
				}

				mRecyclerview.setVisibility(View.VISIBLE);
				mLayoutOptionFooter.setVisibility(View.GONE);

				if (itemsOld.size() == 0 || forceFullRefresh == true) {
					//第一次全量刷新 或者红涨绿跌变化全量刷新
					itemsOld.clear();
					itemsOld.addAll(deepCopyTradePairList());
					mContentAdapter.setItems(itemsOld);
					mContentAdapter.notifyDataSetChanged();

					forceFullRefresh = false;
				} else {
					//局部刷新
					Items itemsNew = new Items();
					itemsNew.addAll(deepCopyTradePairList());

					TradePairDiffCallback.notifyDataSetChanged(itemsOld, itemsNew, mContentAdapter);

					itemsOld.clear();
					itemsOld.addAll(itemsNew);
				}
			} else {
				//自选为空
				if (itemsOld == null) {
					itemsOld = new Items();
				}
				mContentAdapter.setItems(itemsOld);
				mContentAdapter.notifyDataSetChanged();

				mRecyclerview.setVisibility(View.GONE);
				mLayoutOptionFooter.setVisibility(View.VISIBLE);
			}
		}
	}

	private List<WsMarketData> deepCopyTradePairList() {
		List<WsMarketData> copyList = Collections.synchronizedList(new ArrayList<>());
		synchronized (tradePairList) {
			for (WsMarketData dataBean : tradePairList) {
				copyList.add((WsMarketData) dataBean.clone());
			}
		}
		return copyList;
	}

	/**
	 * 交易对排序
	 */
	private void sortTradePair() {
		if (TextUtils.isEmpty(groupName)) {
			return;
		}
		synchronized (tradePairList) {
			if (tradePairList != null && tradePairList.size() > 0) {
				if (mSortUtil == null) {
					mSortUtil = new MarketSortUtil();
				}

				//如果是自选 并且是24小时额   则根据sort排序
				if (groupName.equals(TradePairGroupTable.SELF_GROUP) && Constants.SORT_V24_VOL.equals(mSortField)) {
					mSortUtil.setSort(true);
					Collections.sort(tradePairList, mSortUtil);
				}
				//默认sort排序
				else if (Constants.SORT_BY_SORT.equals(mSortType)) {
					mSortUtil.setSort(true);
					Collections.sort(tradePairList, mSortUtil);
				}
				//其他排序规则
				else if (!TextUtils.isEmpty(mSortField) && !TextUtils.isEmpty(mSortType)) {
					mSortUtil.setSort(false);
					mSortUtil.setASC(mSortType);
					mSortUtil.setSortField(mSortField);
					Collections.sort(tradePairList, mSortUtil);
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (subscriptions != null) {
			subscriptions.cancel();
		}
	}
}
