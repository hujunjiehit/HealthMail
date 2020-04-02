package com.coinbene.manbiwang.balance.products.margin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.widget.WrapperLinearLayoutManager;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.products.margin.adapter.MarginHeaderViewBinder;
import com.coinbene.manbiwang.balance.products.margin.adapter.MarginItemViewBinder;
import com.coinbene.manbiwang.model.http.MarginTotalInfoModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-08-17
 */
public class BalanceMarginFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.asset_list)
	RecyclerView recyclerView;
	@BindView(R2.id.login_regis_tv)
	TextView mLoginRegisTv;
	@BindView(R2.id.login_layout)
	LinearLayout loginLayout;
	@BindView(R2.id.footer_view)
	ConstraintLayout footerView;
	private MarginHeaderViewBinder marginHeaderViewBinder;
	private MarginItemViewBinder marginItemViewBinder;
	private MultiTypeAdapter mContentAdapter;

	private Items newOriginItems;

	private AssetManager.TotalAccountListener mTotalAccountListener;
	private Items filterItems;
	private String filterStr;

	public static BalanceMarginFragment newInstance() {
		Bundle args = new Bundle();
		BalanceMarginFragment fragment = new BalanceMarginFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public int initLayout() {
		return R.layout.balance_margin_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		marginHeaderViewBinder = new MarginHeaderViewBinder();
		marginItemViewBinder = new MarginItemViewBinder();
		mContentAdapter.register(MarginTotalInfoModel.DataBean.class, marginHeaderViewBinder);
		mContentAdapter.register(MarginTotalInfoModel.DataBean.AccountListBean.class, marginItemViewBinder);
		recyclerView.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
		recyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {
		loginLayout.setOnClickListener(v -> gotoLoginOrLock());

		marginHeaderViewBinder.setMarginHeaderListener(s -> {
			filter(s, newOriginItems);
		});
	}

	@AddFlowControl(timeInterval = 200, strategy = FlowControlStrategy.debounce)
	private void filter(String s, Items items) {
		this.filterStr = s;
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
			int totalInfoFlag = 0;
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i) instanceof MarginTotalInfoModel.DataBean) {
					filterItems.add(items.get(totalInfoFlag));
					continue;
				}
				if (items.get(i) instanceof MarginTotalInfoModel.DataBean.AccountListBean) {
					MarginTotalInfoModel.DataBean.AccountListBean item = (MarginTotalInfoModel.DataBean.AccountListBean) items.get(i);
					if (item.getSymbol().toLowerCase().contains(s1)) {
						filterItems.add(items.get(i));
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
	public void onStop() {
		super.onStop();
		AssetManager.getInstance().unregisterTotalAccountListener(mTotalAccountListener);
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
					if (currentProduct.getType() == Product.TYPE_MARGIN) {
						getMarginBalance();
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
	public void onFragmentShow() {
		KeyboardUtils.hideKeyboard(recyclerView);
		if (marginHeaderViewBinder != null) {
			marginHeaderViewBinder.clearFocus();
		}

		PostPointHandler.postClickData(PostPointHandler.balance_tab_margin);

		//获取杠杆资产信息
		getMarginBalance();
	}


	@Override
	public void onFragmentHide() {
//		KeyboardUtils.hideKeyboard(recyclerView);
//		if (marginHeaderViewBinder != null) {
//			marginHeaderViewBinder.clearFocus();
//		}
	}


	/**
	 * //获取杠杆资产信息 子线程
	 */
	@NeedLogin
	private void getMarginBalance() {
		if (!isActivityExist()) {
			return;
		}

		OkGo.<MarginTotalInfoModel>get(Constants.BALANCE_MARGIN).tag(this).execute(new NewJsonSubCallBack<MarginTotalInfoModel>() {
			@Override
			public void onSuc(Response<MarginTotalInfoModel> response) {

			}

			@Override
			public MarginTotalInfoModel dealJSONConvertedResult(MarginTotalInfoModel marginTotalInfoModel) {
				if (marginTotalInfoModel != null && marginTotalInfoModel.getData() != null) {
					handlerMarginData(marginTotalInfoModel.getData());
				}
				return super.dealJSONConvertedResult(marginTotalInfoModel);
			}

			@Override
			public void onE(Response<MarginTotalInfoModel> response) {

			}
		});
	}

	private void handlerMarginData(MarginTotalInfoModel.DataBean data) {
		if (newOriginItems == null) {
			newOriginItems = new Items();
		}

		if (newOriginItems != null && newOriginItems.size() > 0) {
			newOriginItems.clear();
		}

		newOriginItems.add(data);
		newOriginItems.addAll(data.getAccountList());
		filter(filterStr, newOriginItems);
	}

}
