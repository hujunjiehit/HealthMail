package com.coinbene.manbiwang.fortune.activity;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.manbiwang.model.http.YbbRecordModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.fortune.R;
import com.coinbene.manbiwang.fortune.R2;
import com.coinbene.manbiwang.fortune.activity.adapter.YbbRecordAdapter;
import com.coinbene.manbiwang.fortune.manager.FortuneManager;
import com.coinbene.manbiwang.service.RouteHub;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-10-18
 * 余币宝记录页面
 */
@Route(path = RouteHub.Fortune.ybbRecordActivity)
public class YbbRecordActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.view_select_asset)
	View mViewSelectAsset;
	@BindView(R2.id.view_select_biztype)
	View mViewSelectBiztype;
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;
	@BindView(R2.id.tv_asset)
	TextView mTvAsset;
	@BindView(R2.id.tv_biz_type)
	TextView mTvBizType;

	private int pageNum = 1;
	private int pageSize = 10;
	private String asset = "";
	private String bizType = "";

	private YbbRecordAdapter mAdapter;

	private SelectorDialog<String> mAssetSelectorDialog;
	private SelectorDialog<String> mBizTypeSelectorDialog;

	private SparseArray<String> bizTypeMap;

	@Override
	public int initLayout() {
		return R.layout.fortune_activity_ybb_record;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.myself_record_label);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mAdapter = new YbbRecordAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();

		initSelectorDialog();
	}

	private void initSelectorDialog() {
		//初始化币种选择弹窗
		FortuneManager.getInstance().getYbbAssetList(new FortuneManager.CallBack() {
			@Override
			public void onAssetList(List<String> assetList) {
				List<String> assets = new ArrayList<>();
				assets.add(getString(R.string.part_100));
				assets.addAll(assetList);
				initAssetSelectorDialog(assets);
			}
		});

		//初始化bizType选择弹窗
		List<String> typeList = new ArrayList<>();
		bizTypeMap = new SparseArray<>();
		bizTypeMap.put(3, getString(R.string.transferIn));
		bizTypeMap.put(4, getString(R.string.transferOut));
		bizTypeMap.put(5, getString(R.string.options_profit));
		typeList.add(getString(R.string.part_100));
		typeList.add(bizTypeMap.get(3));
		typeList.add(bizTypeMap.get(4));
		typeList.add(bizTypeMap.get(5));
		initBizTypeSelectorDialog(typeList);
	}

	private void initAssetSelectorDialog(List<String> assetList) {
		if (mAssetSelectorDialog == null) {
			mAssetSelectorDialog = new SelectorDialog<>(this);
			mAssetSelectorDialog.setSelectListener(new SelectorDialog.SelectListener<String>() {
				@Override
				public void onItemSelected(String data, int positon) {
					if (positon == 0) {
						asset = "";
						mTvAsset.setText( getString(R.string.res_all_asset));
					} else {
						asset = data;
						mTvAsset.setText(asset);
					}
					refreshData();
				}
			});
		}
		mAssetSelectorDialog.setDatas(assetList);
	}

	private void initBizTypeSelectorDialog(List<String> typeList) {
		if (mBizTypeSelectorDialog == null) {
			mBizTypeSelectorDialog = new SelectorDialog<>(this);
			mBizTypeSelectorDialog.setSelectListener(new SelectorDialog.SelectListener<String>() {
				@Override
				public void onItemSelected(String data, int positon) {
					if (positon == 0) {
						bizType = "";
						mTvBizType.setText(getString(R.string.res_all_type));
					} else {
						bizType = String.valueOf(bizTypeMap.keyAt(bizTypeMap.indexOfValue(data)));
						mTvBizType.setText(bizTypeMap.get(Integer.valueOf(bizType)));
					}
					refreshData();
				}
			});
		}
		mBizTypeSelectorDialog.setDatas(typeList);
	}

	@Override
	public void setListener() {
		mAdapter.setOnLoadMoreListener(() -> {
			pageNum++;
			getYbbRecord();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			refreshData();
		});

		mViewSelectAsset.setOnClickListener( v -> {
			if (mAssetSelectorDialog != null) {
				if (TextUtils.isEmpty(asset)) {
					mAssetSelectorDialog.setDefaultPosition(0);
				} else {
					mAssetSelectorDialog.setDefaultData(asset);
				}
				mAssetSelectorDialog.show();
			}
		});

		mViewSelectBiztype.setOnClickListener(v -> {
			if (mBizTypeSelectorDialog != null) {
				if (TextUtils.isEmpty(bizType)) {
					mBizTypeSelectorDialog.setDefaultPosition(0);
				} else {
					mBizTypeSelectorDialog.setDefaultData(bizType);
				}
				mBizTypeSelectorDialog.show();
			}
		});
	}

	@Override
	public void initData() {
		refreshData();
	}


	private void refreshData() {
		pageNum = 1;
		getYbbRecord();
	}

	private void getYbbRecord() {
		HttpParams params = new HttpParams();
		params.put("asset", asset);
		params.put("bizType", bizType);
		params.put("isLastThree", 0); //固定传0
		params.put("pageNum", pageNum);
		params.put("pageSize", pageSize);

		OkGo.<YbbRecordModel>get(Constants.YBB_RECORD_LIST).params(params).tag(this).execute(new NewJsonSubCallBack<YbbRecordModel>() {
			@Override
			public void onSuc(Response<YbbRecordModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					setAdapterData(mAdapter, response.body().getData().getList(), pageNum, pageSize);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
			}

			@Override
			public void onE(Response<YbbRecordModel> response) {

			}
		});
	}


	@Override
	public boolean needLock() {
		return true;
	}
}
