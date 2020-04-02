package com.coinbene.manbiwang.balance.products.options;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.Product;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.manbiwang.model.http.OptionsTotalInfoModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.DLog;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.products.options.adapter.OptionHeaderViewBinder;
import com.coinbene.manbiwang.balance.products.options.adapter.OptionItemViewBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class BalanceOptionFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.asset_list)
	RecyclerView recyclerView;
	@BindView(R2.id.login_regis_tv)
	TextView loginRegisTv;
	@BindView(R2.id.login_layout)
	LinearLayout loginLayout;

	private String currencySymbol;//$
	private Items newOriginItems = new Items();

	private OptionItemViewBinder optionItemViewBinder;
	private OptionHeaderViewBinder optionHeaderViewBinder;
	private MultiTypeAdapter mContentAdapter;

	private AssetManager.TotalAccountListener mTotalAccountListener;

	public static BalanceOptionFragment newInstance() {
		Bundle args = new Bundle();
		BalanceOptionFragment fragment = new BalanceOptionFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.balance_option_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		optionItemViewBinder = new OptionItemViewBinder();
		optionHeaderViewBinder = new OptionHeaderViewBinder();
		mContentAdapter.register(OptionsTotalInfoModel.DataBean.class, optionHeaderViewBinder);
		mContentAdapter.register(OptionsTotalInfoModel.DataBean.ListBean.class, optionItemViewBinder);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {
		loginRegisTv.setOnClickListener(v -> {
			gotoLoginOrLock();
		});
	}
	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}

	@Override
	public void initData() {
	}

	@Override
	public void onFragmentHide() {
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
					if (currentProduct.getType() == Product.TYPE_OPTIONS) {
						getOptionsBalance();
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
	public void onFragmentShow() {
		PostPointHandler.postClickData(PostPointHandler.balance_tab_option);

		getOptionsBalance();
	}

	@NeedLogin
	private void getOptionsBalance() {
		if (!isActivityExist()) {
			return;
		}

		OkGo.<OptionsTotalInfoModel>get(Constants.BALANCE_OPTIONS).tag(this).execute(new NewJsonSubCallBack<OptionsTotalInfoModel>() {
			@Override
			public void onSuc(Response<OptionsTotalInfoModel> response) {

			}

			@Override
			public OptionsTotalInfoModel dealJSONConvertedResult(OptionsTotalInfoModel optionsTotalInfoModel) {
				if (optionsTotalInfoModel != null && optionsTotalInfoModel.getData() != null) {
					handleOptionsData(optionsTotalInfoModel.getData());
				}
				return super.dealJSONConvertedResult(optionsTotalInfoModel);
			}

			@Override
			public void onE(Response<OptionsTotalInfoModel> response) {

			}
		});
	}

	private void handleOptionsData(OptionsTotalInfoModel.DataBean data) {
		List<OptionsTotalInfoModel.DataBean.ListBean> originItems = data.getList();
		Collections.sort(originItems, (o1, o2) -> {
			int i = 0;
			try {
				i = o1.getSort() - o2.getSort();
			} catch (Exception e) {
			}
			return i;
		});

		if (newOriginItems != null && newOriginItems.size() > 0) {
			newOriginItems.clear();
		}
		newOriginItems.add(data);
		newOriginItems.addAll(originItems);
		currencySymbol = data.getCurrencySymbol();
		if (optionItemViewBinder != null) {
			optionItemViewBinder.setCurrencySymbol(currencySymbol);
		}

		getActivity().runOnUiThread(() -> {
			mContentAdapter.setItems(newOriginItems);
			mContentAdapter.notifyDataSetChanged();
		});
	}


}
