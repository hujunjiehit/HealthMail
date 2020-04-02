package com.coinbene.manbiwang.market.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;
import com.coinbene.manbiwang.market.manager.AbsMarketDataListener;
import com.coinbene.manbiwang.market.manager.MarketDataManager;
import com.coinbene.common.utils.RecycleViewAnimUtil;
import com.coinbene.manbiwang.market.viewbinder.ContractGroupViewBinder;
import com.coinbene.manbiwang.market.viewbinder.NewMarketItemBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscriptionList;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 合约行情Fragment
 */
public class ContractTradePairFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.market_recyclerview)
	RecyclerView mRecyclerview;

	public List<WsMarketData> btcTradePairList;
	public List<WsMarketData> usdtTradePairList;

	private Items items;

	private NewMarketItemBinder marketViewBinder;
	private ContractGroupViewBinder marketGroupBinder;

	private MultiTypeAdapter mContentAdapter;

	private boolean isScrolling = false;

	private String groupName;

	private MarketDataManager.MarketDataListener mMarketDataListener;

	private DataSubscriptionList subscriptions;

	public static ContractTradePairFragment newInstance(String groupName) {
		Bundle args = new Bundle();
		args.putString(Constants.BUNDLE_KEY_GROUPNAME, groupName);
		ContractTradePairFragment fragment = new ContractTradePairFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.fragment_contract_market;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		marketViewBinder = new NewMarketItemBinder();
		marketGroupBinder = new ContractGroupViewBinder();

		mContentAdapter.register(WsMarketData.class, marketViewBinder);
		mContentAdapter.register(String.class, marketGroupBinder);

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
	}

	@Override
	public void initData() {
		btcTradePairList = Collections.synchronizedList(new ArrayList<>());
		usdtTradePairList = Collections.synchronizedList(new ArrayList<>());

		Bundle bundle = this.getArguments();
		groupName = bundle.getString(Constants.BUNDLE_KEY_GROUPNAME, "");
		marketViewBinder.setGroupName(groupName);

		if (subscriptions == null) {
			subscriptions = new DataSubscriptionList();
		}

		//查询并监听Btc合约数据库
		CBRepository.boxFor(ContractInfoTable.class)
				.query()
				.build()
				.subscribe(subscriptions)
				.on(AndroidScheduler.mainThread())
				.onError(error -> error.printStackTrace())
				.observer(data -> {

					if (data.size() == 0) {
						return;
					}

					if (btcTradePairList != null && btcTradePairList.size() > 0) {
						btcTradePairList.clear();
					}

					for (int i = 0; i < data.size(); i++) {
						ContractInfoTable contractInfoTable = data.get(i);
						if (contractInfoTable == null) {
							continue;
						}
						WsMarketData bean = new WsMarketData();
						bean.setSymbol(contractInfoTable.name);
						TradeUtils.getMarketDataFromMap(bean, NewMarketWebsocket.getInstance().getContractMarketMap());
						btcTradePairList.add(bean);
					}

					freshFragment();
				});

		//查询并监听Usdt合约数据库
		CBRepository.boxFor(ContractUsdtInfoTable.class)
				.query()
				.build()
				.subscribe(subscriptions)
				.on(AndroidScheduler.mainThread())
				.onError(error -> error.printStackTrace())
				.observer(data -> {

					if (usdtTradePairList != null && usdtTradePairList.size() > 0) {
						usdtTradePairList.clear();
					}

					for (int i = 0; i < data.size(); i++) {
						ContractUsdtInfoTable contractInfoTable = data.get(i);
						if (contractInfoTable == null) {
							continue;
						}
						WsMarketData bean = new WsMarketData();
						bean.setBaseAsset(contractInfoTable.baseAsset);
						bean.setSymbol(contractInfoTable.name);
						TradeUtils.getMarketDataFromMap(bean, NewMarketWebsocket.getInstance().getContractMarketMap());
						usdtTradePairList.add(bean);
					}

					freshFragment();
				});
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

			if ((btcTradePairList != null && btcTradePairList.size() > 0) || (usdtTradePairList != null && usdtTradePairList.size() > 0)) {

				if (items == null) {
					items = new Items();
				}
				items.clear();

				if (usdtTradePairList.size() > 0) {
					items.add(getString(R.string.usdt_contract));
					items.addAll(deepCopyTradePairList(usdtTradePairList));
				}

				if (btcTradePairList.size() > 0) {
					items.add(getString(R.string.btc_contract));
					items.addAll(deepCopyTradePairList(btcTradePairList));
				}

				mContentAdapter.setItems(items);
				mContentAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onFragmentHide() {

	}

	@Override
	public void onFragmentShow() {
		NewMarketWebsocket.getInstance().pullMarketData();
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mMarketDataListener == null) {
			mMarketDataListener = new AbsMarketDataListener() {

				@Override
				public void onReceiveContractMarketData(Map<String, WsMarketData> contractTradePairMap, int contractType) {
					if (!isShowing) return;

					if (contractType == Constants.CONTRACT_TYPE_BTC) {
						//收到BTC合约行情数据
						synchronized (btcTradePairList) {
							for (int i = 0; i < btcTradePairList.size(); i++) {
								WsMarketData bean = btcTradePairList.get(i);
								if (contractTradePairMap.get(bean.getSymbol()) == null) {
									continue;
								}
								TradeUtils.getMarketDataFromMap(bean, contractTradePairMap);
							}
						}
					} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
						//收到USDT合约行情数据
						synchronized (usdtTradePairList) {
							for (int i = 0; i < usdtTradePairList.size(); i++) {
								WsMarketData bean = usdtTradePairList.get(i);
								if (contractTradePairMap.get(bean.getSymbol()) == null) {
									continue;
								}
								TradeUtils.getMarketDataFromMap(bean, contractTradePairMap);
							}
						}
					}
					getActivity().runOnUiThread(() -> freshFragment());
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

	private List<WsMarketData> deepCopyTradePairList(List<WsMarketData> list) {
		List<WsMarketData> copyList = Collections.synchronizedList(new ArrayList<>());
		synchronized (list) {
			for (WsMarketData dataBean : list) {
				copyList.add((WsMarketData) dataBean.clone());
			}
		}
		return copyList;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (subscriptions != null) {
			subscriptions.cancel();
		}
	}
}
