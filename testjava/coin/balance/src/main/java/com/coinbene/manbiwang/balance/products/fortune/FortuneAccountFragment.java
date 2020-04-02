package com.coinbene.manbiwang.balance.products.fortune;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.Product;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.manbiwang.model.http.FortuneTotalInfoModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.products.fortune.adapter.FortuneBottomViewBinder;
import com.coinbene.manbiwang.balance.products.fortune.adapter.FortuneHeaderViewBinder;
import com.coinbene.manbiwang.balance.products.fortune.adapter.FortuneItemViewBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-10-15
 */
public class FortuneAccountFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.login_regis_tv)
	TextView mLoginRegisTv;
	@BindView(R2.id.login_layout)
	LinearLayout mLoginLayout;

	private MultiTypeAdapter mContentAdapter;

	private FortuneHeaderViewBinder fortuneHeaderViewBinder;
	private FortuneItemViewBinder fortuneItemViewBinder;
	private FortuneBottomViewBinder fortuneBottomViewBinder;

	private AssetManager.TotalAccountListener mTotalAccountListener;

	private String currencySymbol;//$
	private Items newOriginItems = new Items();

	public static FortuneAccountFragment newInstance() {

		Bundle args = new Bundle();

		FortuneAccountFragment fragment = new FortuneAccountFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.balance_fortune_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();

		fortuneHeaderViewBinder = new FortuneHeaderViewBinder();
		fortuneItemViewBinder = new FortuneItemViewBinder();
		fortuneBottomViewBinder = new FortuneBottomViewBinder();

		mContentAdapter.register(FortuneTotalInfoModel.DataBean.class, fortuneHeaderViewBinder);
		mContentAdapter.register(FortuneTotalInfoModel.DataBean.FinancialAccountListBean.class, fortuneItemViewBinder);
		mContentAdapter.register(FortuneTotalInfoModel.DataBean.CurrentPreestimateBean.class, fortuneBottomViewBinder);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {
		mLoginRegisTv.setOnClickListener(v -> {
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
	public void onFragmentShow() {

		getFortuneBalance();
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
						mRecyclerView.setVisibility(View.VISIBLE);
						mLoginLayout.setVisibility(View.GONE);
					} else {
						mRecyclerView.setVisibility(View.GONE);
						mLoginLayout.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onPullRefresh(Product currentProduct) {
					if (currentProduct.getType() == Product.TYPE_FORTUNE) {
						getFortuneBalance();
					}
				}

				@Override
				public void onTabSelected(int index) {
					if (mRecyclerView != null) {
						mRecyclerView.scrollToPosition(0);
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


	@NeedLogin
	private void getFortuneBalance() {
		if (!isActivityExist()) {
			return;
		}

		OkGo.<FortuneTotalInfoModel>get(Constants.BALANCE_FORTUNE).tag(this).execute(new NewJsonSubCallBack<FortuneTotalInfoModel>() {
			@Override
			public void onSuc(Response<FortuneTotalInfoModel> response) {

			}

			@Override
			public FortuneTotalInfoModel dealJSONConvertedResult(FortuneTotalInfoModel fortuneTotalInfoModel) {
				if (fortuneTotalInfoModel != null && fortuneTotalInfoModel.getData() != null) {
					handleFortuneData(fortuneTotalInfoModel.getData());
				}
				return super.dealJSONConvertedResult(fortuneTotalInfoModel);
			}

			@Override
			public void onE(Response<FortuneTotalInfoModel> response) {

			}
		});
	}

	private void handleFortuneData(FortuneTotalInfoModel.DataBean data) {
		List<FortuneTotalInfoModel.DataBean.FinancialAccountListBean> originItems = data.getFinancialAccountList();
		if (originItems == null) {
			return;
		}

		currencySymbol = data.getCurrencySymbol();
		//先把第一条取出来，然后清空；再把第一条重新赋值，活动的部分不变；在加上其他的资产信息
		if (newOriginItems != null && newOriginItems.size() > 0) {
			newOriginItems.clear();
		}
		newOriginItems.add(data);
		newOriginItems.addAll(originItems);
		newOriginItems.add(data.getCurrentPreestimate());

		currencySymbol = data.getCurrencySymbol();
		if (fortuneBottomViewBinder != null) {
			fortuneBottomViewBinder.setCurrencySymbol(currencySymbol);
		}

		getActivity().runOnUiThread(() -> {
			mContentAdapter.setItems(newOriginItems);
			mContentAdapter.notifyDataSetChanged();
		});
	}
}
