package com.coinbene.manbiwang.record.orderrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.model.http.CurrentLoanModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.adapter.LoanHistoryAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * 历史借币
 */
public class LoanHistoryActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout imgBack;
	@BindView(R2.id.menu_title_tv)
	TextView mTitle;
	@BindView(R2.id.layout_refresh)
	SwipeRefreshLayout mRefresh;
	@BindView(R2.id.loan_current_RecyclerView)
	RecyclerView mRecyclerView;
	private Unbinder bind;
	private LoanHistoryAdapter currentAdapter;

	private int currentPage = 1;
	private int pageSize = 10;

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, LoanHistoryActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_loan_current;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void setListener() {
		listener();
	}

	@Override
	public void initData() {
		getData();
	}

	@Override
	public boolean needLock() {
		return true;
	}

	public void init() {
		mTitle.setText(R.string.loan_history);
		mRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		currentAdapter = new LoanHistoryAdapter();
		currentAdapter.bindToRecyclerView(mRecyclerView);
		currentAdapter.setEnableLoadMore(true);
		currentAdapter.setUpFetchEnable(false);
		currentAdapter.setLoadMoreView(new LoadMoreView());
		currentAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.common_base_empty, null));
		currentAdapter.disableLoadMoreIfNotFullPage();
	}

	private void listener() {
		imgBack.setOnClickListener(v -> finish());

		//下来刷新
		mRefresh.setOnRefreshListener(() -> {
			currentPage = 1;
			getData();
		});

		//加载更多
		currentAdapter.setOnLoadMoreListener(() -> {
			currentPage++;
			getData();
		}, mRecyclerView);
	}

	private void getData() {

		HttpParams params = new HttpParams();
		params.put("pageNum", currentPage);
		params.put("pageSize", pageSize);

		OkGo.<CurrentLoanModel>get(Constants.LEVERAGE_HISTORY_LOAN).params(params).tag(this).execute(new NewJsonSubCallBack<CurrentLoanModel>() {

			@Override
			public void onSuc(Response<CurrentLoanModel> response) {
				CurrentLoanModel.DataBean data = response.body().getData();

				if (data == null || data.getList() == null) {

					if (currentPage > 1) {
						currentAdapter.loadMoreEnd();
					}

					return;
				}

				if (currentPage > 1) {
					currentAdapter.addData(data.getList());
				} else {
					currentAdapter.setNewData(data.getList());
				}

				if (data.getList().size() < pageSize) {
					currentAdapter.loadMoreEnd();
				} else {
					currentAdapter.loadMoreComplete();
				}
			}

			@Override
			public void onE(Response<CurrentLoanModel> response) {
				if (currentPage > 1) {
					currentAdapter.loadMoreFail();
					currentPage--;
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				//结束刷新
				if (mRefresh.isRefreshing()) {
					mRefresh.setRefreshing(false);
				}
			}
		});

	}
}
