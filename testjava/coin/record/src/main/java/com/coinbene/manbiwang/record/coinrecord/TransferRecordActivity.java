package com.coinbene.manbiwang.record.coinrecord;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.http.TransferRecordResponse;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.coinrecord.adapter.TransferRecordAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-12
 */
public class TransferRecordActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.top_bar)
	QMUITopBarLayout mTopBar;
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	private TransferRecordAdapter mAdapter;

	private int pageNum = 1;
	private int pageSize = 10;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, TransferRecordActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_new_transfer_record;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.transfer_record);
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new TransferRecordAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);
		mAdapter.setEnableLoadMore(true);
		mAdapter.setUpFetchEnable(false);
		mAdapter.setLoadMoreView(new LoadMoreView());
		mAdapter.disableLoadMoreIfNotFullPage();
	}

	@Override
	public void setListener() {
		mAdapter.setOnLoadMoreListener(() -> {
			pageNum++;
			getTransferRecord();
		}, mRecyclerView);

		mSwipeRefresh.setOnRefreshListener(() -> {
			pageNum = 1;
			getTransferRecord();
		});
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		pageNum = 1;
		getTransferRecord();
	}

	public void getTransferRecord(){
		OkGo.<TransferRecordResponse>get(Constants.TRANSFER_RECORD)
				.params("pageNum",pageNum)
				.params("pageSize",pageSize)
				.tag(this)
				.execute(new NewJsonSubCallBack<TransferRecordResponse>() {
					@Override
					public void onSuc(Response<TransferRecordResponse> response) {
						if (response.body() != null && response.body().getData() != null) {
							setAdapterData(mAdapter, response.body().getData().getList(), pageNum, pageSize);
						}
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (mSwipeRefresh != null && mSwipeRefresh.isRefreshing()) {
							mSwipeRefresh.setRefreshing(false);
						}
					}

					@Override
					public void onE(Response<TransferRecordResponse> response) {

					}
				});
	}

	@Override
	public boolean needLock() {
		return true;
	}
}
