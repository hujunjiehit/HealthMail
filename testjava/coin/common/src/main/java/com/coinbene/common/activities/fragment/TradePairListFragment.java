package com.coinbene.common.activities.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.activities.fragment.adapter.SelectPairAdapter;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.widget.EmptyLayout;

import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-06
 */
public class TradePairListFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private SelectPairAdapter mAdapter;
	private String mGroupName;

	public static TradePairListFragment newInstance(String groupName) {
		Bundle args = new Bundle();
		args.putString("groupName", groupName);
		TradePairListFragment fragment = new TradePairListFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.common_fragment_trade_pair_list;
	}

	@Override
	public void initView(View rootView) {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mAdapter = new SelectPairAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(false);
		mAdapter.setUpFetchEnable(false);
		mAdapter.disableLoadMoreIfNotFullPage();

		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));
	}

	@Override
	public void setListener() {
		mSwipeRefresh.setOnRefreshListener(() -> {
			getTradePairList(mGroupName);
			mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
		});

		mAdapter.setOnItemClickListener((adapter, view, position) -> {
			TradePairInfoTable pairInfo = (TradePairInfoTable) adapter.getData().get(position);
			Intent intent = new Intent();
			intent.putExtra("tradePairName", pairInfo.tradePairName);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		});
	}

	@Override
	public void initData() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			mGroupName = bundle.getString("groupName");
		}

		getTradePairList(mGroupName);
	}

	private void getTradePairList(String mGroupName) {
		if (TextUtils.isEmpty(mGroupName)) {
			return;
		}
		List<TradePairInfoTable> infoTables = null;
		if (!mGroupName.equals(TradePairGroupTable.SELF_GROUP)) {
			infoTables = TradePairInfoController.getInstance().getTradePairInfoList(mGroupName);
		} else {
			infoTables = TradePairOptionalController.getInstance().queryTradePairOptional();
		}
		if (infoTables.size() == 0) {
			mAdapter.setEmptyView(new EmptyLayout(getActivity()));
		} else {
			mAdapter.setNewData(infoTables);
		}
	}

	@Override
	public void onFragmentHide() {

	}

	@Override
	public void onFragmentShow() {
		getTradePairList(mGroupName);
	}
}
