package com.coinbene.manbiwang.fortune.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.http.YbbAccountListModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.manbiwang.fortune.R;
import com.coinbene.manbiwang.fortune.R2;
import com.coinbene.manbiwang.fortune.activity.adapter.YbbAccountListAdapter;
import com.coinbene.manbiwang.service.RouteHub;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-10-18
 * 余币宝资产一览界面
 */
@Route(path = RouteHub.Fortune.ybbDetailActivity)
public class YbbDetailActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;

	YbbAccountListAdapter mAdapter;

	@Override
	public int initLayout() {
		return R.layout.fortune_activity_ybb_detail;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.res_ybb_asset_detail);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mAdapter = new YbbAccountListAdapter();
		mAdapter.bindToRecyclerView(mRecyclerView);

		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));
	}

	@Override
	public void setListener() {

		mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			YbbAccountListModel.DataBean.CurrentPreestimateBean.CurrentAccountListBean item = (YbbAccountListModel.DataBean.CurrentPreestimateBean.CurrentAccountListBean) adapter.getItem(position);
			if (view.getId() == R.id.btn_transfer_in) {
				ARouter.getInstance().build(RouteHub.Fortune.ybbTransferActivity)
						.withInt("type",RouteHub.Fortune.TRANSFER_TYPE_IN)
						.withString("asset", item.getAsset())
						.navigation(this);
			} else if (view.getId() == R.id.btn_transfer_out) {
				ARouter.getInstance().build(RouteHub.Fortune.ybbTransferActivity)
						.withInt("type",RouteHub.Fortune.TRANSFER_TYPE_OUT)
						.withString("asset", item.getAsset())
						.navigation(this);
			}
		});

		mSwipeRefresh.setOnRefreshListener(() -> {
			getYbbAccountList();
		});
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSwipeRefresh.post(() -> {
			mSwipeRefresh.setRefreshing(true);
			getYbbAccountList();
		});
	}

	@NeedLogin
	private void getYbbAccountList() {
		OkGo.<YbbAccountListModel>get(Constants.YBB_ACCOUNT_LIST).tag(this).execute(new NewJsonSubCallBack<YbbAccountListModel>() {
			@Override
			public void onSuc(Response<YbbAccountListModel> response) {
				if (response.body() != null && response.body().getData() != null && response.body().getData().getCurrentPreestimate() != null) {
					setAdapterData(mAdapter, response.body().getData().getCurrentPreestimate().getCurrentAccountList());
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
			public void onE(Response<YbbAccountListModel> response) {

			}
		});
	}

	@Override
	public boolean needLock() {
		return true;
	}
}
