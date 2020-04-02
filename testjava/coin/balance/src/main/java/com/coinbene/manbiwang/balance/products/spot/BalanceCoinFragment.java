package com.coinbene.manbiwang.balance.products.spot;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.Product;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.widget.WrapperLinearLayoutManager;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.products.spot.adapter.CoinHeaderViewBinder;
import com.coinbene.manbiwang.balance.products.spot.adapter.CoinItemViewBinder;
import com.coinbene.manbiwang.model.http.CoinTotalInfoModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class BalanceCoinFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.asset_list)
	RecyclerView recyclerView;
	@BindView(R2.id.login_regis_tv)
	TextView loginRegisTv;
	@BindView(R2.id.login_layout)
	LinearLayout loginLayout;
	@BindView(R2.id.footer_view)
	ConstraintLayout footerView;
	@BindView(R2.id.rl_root)
	RelativeLayout rlRoot;


	private boolean hideValue;

	//隐藏或者显示资产为0的情况

	private Items newOriginItems = new Items();
	private Items newAfterItems = new Items();
	private String currencySymbol;//$

	private MultiTypeAdapter mContentAdapter;
	private CoinItemViewBinder coinItemViewBinder;
	private CoinHeaderViewBinder coinHeaderViewBinder;

	CoinTotalInfoModel.DataBean mCoinData;

	AssetManager.TotalAccountListener mTotalAccountListener;
	private Items filterItems;
	private String filterStr;

	public static BalanceCoinFragment newInstance() {
		Bundle args = new Bundle();
		BalanceCoinFragment fragment = new BalanceCoinFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.balance_coin_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();

		coinHeaderViewBinder = new CoinHeaderViewBinder();
		coinItemViewBinder = new CoinItemViewBinder();
		mContentAdapter.register(CoinTotalInfoModel.DataBean.class, coinHeaderViewBinder);
		mContentAdapter.register(CoinTotalInfoModel.DataBean.ListBean.class, coinItemViewBinder);

		recyclerView.setLayoutManager(new WrapperLinearLayoutManager(getContext()));

		recyclerView.setNestedScrollingEnabled(true);

		recyclerView.setAdapter(mContentAdapter);

		mContentAdapter.setItems(newOriginItems);
		mContentAdapter.notifyDataSetChanged();
	}

	@Override
	public void setListener() {
		loginRegisTv.setOnClickListener(v -> gotoLoginOrLock());

		if (coinHeaderViewBinder != null) {
			coinHeaderViewBinder.setAssetHeaderListener(new CoinHeaderViewBinder.AssetHeaderListener() {
				@Override
				public void onAssetHideChanged(boolean hide) {
					SpUtil.setHideAssetZero(hide);
					if (mContentAdapter == null) {
						return;
					}
					//隐藏或者显示资产为0的情况
					if (hide) {
						filter(filterStr, newAfterItems);
//						mContentAdapter.setItems(newAfterItems);
					} else {
//						mContentAdapter.setItems(newOriginItems);
						filter(filterStr, newOriginItems);
					}
//					mContentAdapter.notifyDataSetChanged();
				}

				@Override
				public void doFilter(String s) {
					if (SpUtil.getHideAssetZero()) {
						filter(s, newAfterItems);
					} else {
						filter(s, newOriginItems);
					}

				}
			});
		}
	}


	@AddFlowControl(timeInterval = 200, strategy = FlowControlStrategy.debounce)
	private void filter(String s, Items items) {
		filterStr = s;
		if (items == null || items.size() == 0) {
			return;
		}
		if (filterItems == null)
			filterItems = new Items();
		else
			filterItems.clear();

		if (TextUtils.isEmpty(s)) {
			filterItems.addAll(items);
		} else {
			String s1 = s.toLowerCase();
//			int totalInfoFlag = 0;
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i) instanceof CoinTotalInfoModel.DataBean) {
					filterItems.add(items.get(i));
					continue;
				}
				if (items.get(i) instanceof CoinTotalInfoModel.DataBean.ListBean) {
					CoinTotalInfoModel.DataBean.ListBean item = (CoinTotalInfoModel.DataBean.ListBean) items.get(i);
					if (item.getAsset().toLowerCase().contains(s1) || item.getLocalAssetName().toLowerCase().contains(s1) || item.getEnglishAssetName().toLowerCase().contains(s1)) {
//						if (item.getAsset().toLowerCase().startsWith(s1) || item.getLocalAssetName().toLowerCase().startsWith(s1) || item.getEnglishAssetName().toLowerCase().startsWith(s1)) {
//							filterItems.add(0, items.get(i));
//						} else {
						filterItems.add(items.get(i));
//						}
					}
				}

			}

		}

		setAdapter();

	}

	private void setAdapter() {
		if (!AppUtil.isMainThread()) {
			getActivity().runOnUiThread(() -> doAdapter());

		} else {
			doAdapter();
		}


	}

	private void doAdapter() {
		mContentAdapter.setItems(filterItems);
		mContentAdapter.notifyDataSetChanged();

		if (filterItems.size() == 1) {
			footerView.setVisibility(View.VISIBLE);
		} else {
			footerView.setVisibility(View.GONE);
		}
	}


	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}

	@Override
	public void initData() {
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mTotalAccountListener == null) {
			mTotalAccountListener = new AssetManager.TotalAccountListener() {
				@Override
				public void onHideValueChanged(boolean hide) {
					if (mContentAdapter != null) {
						mContentAdapter.notifyDataSetChanged();
					}
				}

				@Override
				public void onLoginStatusChanged(boolean isLogin) {
					if (isLogin) {
						recyclerView.setVisibility(View.VISIBLE);
						loginLayout.setVisibility(View.GONE);
					} else {
						recyclerView.setVisibility(View.GONE);
						loginLayout.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onPullRefresh(Product currentProduct) {
					if (currentProduct.getType() == Product.TYPE_SPOT) {
						getCoinBalance();
					}
				}

				@Override
				public void onTabSelected(int index) {
					if (recyclerView != null) {
						recyclerView.scrollToPosition(0);
					}
				}

				@Override
				public void onConiSwitchChanged() {
					if (mContentAdapter != null) {
						mContentAdapter.notifyDataSetChanged();
					}
				}
			};
		}
		AssetManager.getInstance().registerTotalAccountListener(mTotalAccountListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		AssetManager.getInstance().unregisterTotalAccountListener(mTotalAccountListener);
	}

	@Override
	public void onResume() {
		super.onResume();

		//获取币币资产信息
		getCoinBalance();
	}

	@Override
	public void onFragmentShow() {
		KeyboardUtils.hideKeyboard(rlRoot);

		if (coinHeaderViewBinder != null) {
			coinHeaderViewBinder.clearFocus();
		}

		PostPointHandler.postClickData(PostPointHandler.balance_tab_coin);

		//获取币币资产信息
		getCoinBalance();
	}

	@Override
	public void onFragmentHide() {

	}

	/**
	 * 获取币币账户资产信息
	 */
	@AddFlowControl
	@NeedLogin
	private void getCoinBalance() {
		if (!isActivityExist()) {
			return;
		}

		OkGo.<CoinTotalInfoModel>get(Constants.BALANCE_SPOT_COIN).tag(this).execute(new NewJsonSubCallBack<CoinTotalInfoModel>() {
			@Override
			public void onSuc(Response<CoinTotalInfoModel> response) {

			}

			@Override
			public CoinTotalInfoModel dealJSONConvertedResult(CoinTotalInfoModel coinTotalInfoModel) {
				if (coinTotalInfoModel != null && coinTotalInfoModel.getData() != null) {

					handlerCoinData(coinTotalInfoModel.getData());

				}
				return super.dealJSONConvertedResult(coinTotalInfoModel);
			}

			@Override
			public void onE(Response<CoinTotalInfoModel> response) {

			}
		});
	}

	/**
	 * 处理币币数据，子线程
	 *
	 * @param data
	 */
	private void handlerCoinData(CoinTotalInfoModel.DataBean data) {
		this.mCoinData = data;

		List<CoinTotalInfoModel.DataBean.ListBean> originItems = data.getList();

		BalanceController.getInstance().addInToDatabase(originItems);

		Collections.sort(originItems, (o1, o2) -> {
			int i = 0;
			try {
				i = new BigDecimal(o2.getPreestimateBTC()).compareTo(new BigDecimal(o1.getPreestimateBTC()));
			} catch (Exception e) {

			}
			if (i == 0) {
				i = o1.getSort() - o2.getSort();
			}
			return i;
		});

		//把固定的几个币种放到最前面；usdt,eth,btc,coni
		List<CoinTotalInfoModel.DataBean.ListBean> headerItems = new ArrayList<>(4);
		for (int i = 0; i < 4; i++) {
			headerItems.add(new CoinTotalInfoModel.DataBean.ListBean());
		}

		List<CoinTotalInfoModel.DataBean.ListBean> afterItems = new ArrayList<>();
		for (int i = 0; i < originItems.size(); i++) {
			CoinTotalInfoModel.DataBean.ListBean balanceModel = originItems.get(i);
			if (balanceModel.getAsset() == null) {
				return;
			}
			boolean isFind = false;
			if (balanceModel.getAsset().toUpperCase().equals("USDT")) {
				headerItems.set(0, balanceModel);
				isFind = true;
			} else if (balanceModel.getAsset().toUpperCase().equals("ETH")) {
				headerItems.set(1, balanceModel);
				isFind = true;
			} else if (balanceModel.getAsset().toUpperCase().equals("BTC")) {
				headerItems.set(2, balanceModel);
				isFind = true;
			} else if (balanceModel.getAsset().toUpperCase().equals("CONI")) {
				headerItems.set(3, balanceModel);
				isFind = true;
			}

			if (isFind) {
				originItems.remove(i);
				i--;
				continue;
			}
			//把这里的资产是0和非0的区分开
			float balance;
			try {
				balance = Float.parseFloat(StringUtils.rePlaceDot(originItems.get(i).getAvailableBalance()));
			} catch (Exception e) {
				balance = 0.00f;
			}

			float freezeBalance = Float.parseFloat(StringUtils.rePlaceDot(originItems.get(i).getFrozenBalance()));
			if (balance > 0 || freezeBalance > 0) {
				afterItems.add(originItems.get(i));
			}
		}


		if (headerItems.size() > 0) {
			//原始的数组结构，直接添加
			originItems.addAll(0, headerItems);

			List<CoinTotalInfoModel.DataBean.ListBean> tempHeaderItems = new ArrayList<>();
			for (int i = 0; i < headerItems.size(); i++) {
				float balance;
				try {
					balance = Float.parseFloat(StringUtils.rePlaceDot(headerItems.get(i).getAvailableBalance()));
				} catch (Exception e) {
					balance = 0.00f;
				}
				if (headerItems.get(i).getFrozenBalance() == null) {
					continue;
				}

				float freezeBalance = Float.parseFloat(StringUtils.rePlaceDot(headerItems.get(i).getFrozenBalance()));
				if (balance > 0 || freezeBalance > 0) {
					tempHeaderItems.add(originItems.get(i));
				}
			}
			//处理后的数组结构，把资产为0的，去掉,再添加
			afterItems.addAll(0, tempHeaderItems);
		}

		//先把第一条取出来，然后清空；再把第一条重新赋值，活动的部分不变；在加上其他的资产信息
		if (newOriginItems != null && newOriginItems.size() > 0) {
			newOriginItems.clear();
		}
		if (newAfterItems != null && newAfterItems.size() > 0) {
			newAfterItems.clear();
		}

		newAfterItems.add(data);
		newAfterItems.addAll(afterItems);

		newOriginItems.add(data);
		newOriginItems.addAll(originItems);

		//最外层的符号和货币字母表示
		currencySymbol = data.getCurrencySymbol();

		if (coinItemViewBinder != null) {
			coinItemViewBinder.setCurrencySymbol(currencySymbol);
		}

		boolean hideZeroAsset = SpUtil.getHideAssetZero();
		if (hideZeroAsset) {
			filter(filterStr, newAfterItems);
//				mContentAdapter.setItems(newAfterItems);
		} else {
			filter(filterStr, newOriginItems);
//				mContentAdapter.setItems(newOriginItems);
		}
//			mContentAdapter.notifyDataSetChanged();
	}
}
