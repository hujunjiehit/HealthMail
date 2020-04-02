package com.coinbene.manbiwang.balance.activity.margin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.http.BillingDetailsModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.margin.adapter.BillingDetailsAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 杠杆账单详情页面
 */
public class BillingDetailsActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout imgBack;
	@BindView(R2.id.menu_title_tv)
	TextView mTitle;
	@BindView(R2.id.billing_detail_RecyclerView)
	RecyclerView mRecyclerView;
	@BindView(R2.id.billing_detail_RefreshLayout)
	SwipeRefreshLayout mRefreshLayout;
	private Unbinder bind;
	private BillingDetailsAdapter detailsAdapter;

	private int page = 1;
	private int size = 10;

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, BillingDetailsActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_billing_details;
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
		getBillingDetails();
	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void init() {
		mTitle.setText(R.string.res_leverage_bill_detail);
		View emptyView = View.inflate(this, R.layout.common_base_empty, null);
		mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		detailsAdapter = new BillingDetailsAdapter();
		detailsAdapter.bindToRecyclerView(mRecyclerView);
		detailsAdapter.setEmptyView(emptyView);
		detailsAdapter.setLoadMoreView(new LoadMoreView());
		detailsAdapter.disableLoadMoreIfNotFullPage();
		detailsAdapter.setEnableLoadMore(true);
		detailsAdapter.setUpFetchEnable(false);
	}

	private void listener() {
		imgBack.setOnClickListener(v -> finish());

		//  刷新
		mRefreshLayout.setOnRefreshListener(() -> {
			page = 1;
			getBillingDetails();
		});

		// 加载
		detailsAdapter.setOnLoadMoreListener(() -> {
			page++;
			getBillingDetails();
		}, mRecyclerView);
	}


	public void getBillingDetails() {
		HttpParams params = new HttpParams();
		params.put("pageNum", page);
		params.put("pageSize", size);
		OkGo.<BillingDetailsModel>get(Constants.MARGIN_BILLING_DETAILS).tag(this).params(params).execute(new NewJsonSubCallBack<BillingDetailsModel>() {
			@Override
			public void onSuc(Response<BillingDetailsModel> response) {
				BillingDetailsModel.DataBean data = response.body().getData();

				if (data == null || data.getList() == null) {
					if (page > 1) detailsAdapter.loadMoreEnd();
					return;
				}

				if (page > 1) {
					detailsAdapter.addData(data.getList());
				} else {
					detailsAdapter.setNewData(data.getList());
				}

				if (data.getList().size() < size) {
					detailsAdapter.loadMoreEnd();
				} else {
					detailsAdapter.loadMoreComplete();
				}

			}

			@Override
			public void onE(Response<BillingDetailsModel> response) {
				if (page > 1) {
					detailsAdapter.loadMoreFail();
					page--;
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
					mRefreshLayout.setRefreshing(false);
				}
			}
		});
	}
}
