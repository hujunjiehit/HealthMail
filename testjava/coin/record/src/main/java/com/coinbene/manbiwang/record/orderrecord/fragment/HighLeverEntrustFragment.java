package com.coinbene.manbiwang.record.orderrecord.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.HighLeverOrderModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.adapter.HighLeverOrderAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


/**
 * 历史委托页面
 *
 * @author huyong
 */
public class HighLeverEntrustFragment extends CoinbeneBaseFragment {
	@BindView(R2.id.rl_category)
	RecyclerView rlvHistory;
	@BindView(R2.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;
	@BindView(R2.id.tv_cancel_all_order)
	TextView tvCancelAllOrder;
	private HighLeverOrderAdapter highLeverOrderAdapter;

	private String inputBaseAsset = "", quoteAsset = "", beginTime = "", endTime = "";
	private int direction = 0;
	private boolean ignoreCancelled = false;
	private String accountType;


	@Override
	public void initData() {
		accountType = getArguments().getString("accountType");
		highLeverOrderAdapter = new HighLeverOrderAdapter();
		rlvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		rlvHistory.setAdapter(highLeverOrderAdapter);
		highLeverOrderAdapter.setLoadMoreView(new LoadMoreView());
		highLeverOrderAdapter.bindToRecyclerView(rlvHistory);

		swipeRefreshLayout.setOnRefreshListener(() -> {
			getRefreshData();
		});

		highLeverOrderAdapter.setOnLoadMoreListener(() -> {
			page++;
			loadMoreData();
		},rlvHistory);


		getRefreshData();

		highLeverOrderAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//			if (accountType == AccountType.SPOT) {
			HighLeverOrderModel.DataBean.ListBean item = (HighLeverOrderModel.DataBean.ListBean) adapter.getItem(position);

			cancelOrder(item.getPlanOrderId());
//			}
		});
	}

	@Override
	public void onFragmentShow() {

	}

	@Override
	public void onFragmentHide() {

	}


	private int page = 1;
	private int pageSize = 10;

	private void getRefreshData() {
		page = 1;
		doNormalFilter();
	}

	private void loadMoreData() {
		doNormalFilter();
	}

	@Override
	public int initLayout() {
		return R.layout.record_trade_layout;
	}

	@Override
	public void initView(View rootView) {
		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));

//		highLeverOrderAdapter.setOnLoadMoreListener();
//		rlvHistory.addOnScrollListener(new LoadMoreListener() {
//			@Override
//			public void loadMore() {
//
//			}
//		});
	}

	@Override
	public void setListener() {

	}

	public void cancelOrder(String orderId) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("planOrderId", orderId);
		OkGo.<BaseRes>post(Constants.TRADE_HIGH_LEVER_ORDER_CANCEL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes t = response.body();
				if (t.isSuccess()) {
					ToastUtil.show(R.string.cancel_entrust_success);
					getRefreshData();
//					mAdapter.removeTradeById(orderId);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 第一次进入默认获取的情况，下拉或者加载更多的时候，获取的信息
	 */
	public void doNormalFilter() {
		doFilterBegin();
	}

	/**
	 * 从弹窗中，点击确定后，发送的请求
	 *
	 * @param inputBaseAsset
	 * @param quoteAsset
	 * @param beginTime
	 * @param endTime
	 * @param typeDirection
	 * @param ignoreCancelled
	 */
	public void doFilter(String inputBaseAsset, String quoteAsset, String beginTime, String endTime, int typeDirection, boolean ignoreCancelled) {
		page = 1;
		if (TextUtils.isEmpty(accountType)) {//现货
			this.inputBaseAsset = inputBaseAsset;
			this.quoteAsset = quoteAsset;
		} else {//杠杆  没有inputBaseAsset    只有  quoteAsset，实际是symbol  查询库之后 重新赋值  兼容之前的版本
			MarginSymbolTable table = MarginSymbolController.getInstance().querySymbolByName(quoteAsset);
			this.inputBaseAsset = table.base;
			this.quoteAsset = table.quote;
		}

		this.beginTime = beginTime;
		this.endTime = endTime;
		this.direction = typeDirection;
		this.ignoreCancelled = ignoreCancelled;
		doFilterBegin();
	}

	public void doFilterBegin() {
//		if (page == 1) {
//			swipeRefreshLayout.setRefreshing(true);
//		}
//
//		if (TextUtils.isEmpty(beginTime)) {
//			endTime = "";
//		} else {
//			endTime = "" + System.currentTimeMillis();
//		}
//		HttpParams httpParams = new HttpParams();
//		if (!TextUtils.isEmpty(accountType))
//			httpParams.put("accountType", accountType);
//		if (!TextUtils.isEmpty(inputBaseAsset)) {
//			inputBaseAsset = inputBaseAsset.trim();
//			httpParams.put("baseAsset", inputBaseAsset.toUpperCase());
//		}
//		if (!TextUtils.isEmpty(quoteAsset)) {
//			httpParams.put("quoteAsset", quoteAsset.toUpperCase());
//		}
//		if (!TextUtils.isEmpty(beginTime)) {
//			httpParams.put("beginTime", beginTime);
//			httpParams.put("endTime", endTime);
//		}
//		if (direction != 0) {
//			httpParams.put("orderDirection", direction);
//		}
//		if (ignoreCancelled) {
//			httpParams.put("ignoreCancelled", ignoreCancelled);
//		}


		HttpParams params = new HttpParams();
		params.put("pageNum", page);
		params.put("pageSize", pageSize);
		params.put("accountType", accountType);
		OkGo.<HighLeverOrderModel>get(Constants.TRADE_HIGH_lEVER_LIST).params(params).tag(this).execute(new NewJsonSubCallBack<HighLeverOrderModel>() {

			@Override
			public void onSuc(Response<HighLeverOrderModel> response) {




				if (response.body() != null) {
					if (response.body().getData() != null) {
						setAdapterData(highLeverOrderAdapter, response.body().getData().getList(), page, pageSize);
					}
				}
			}

			@Override
			public void onE(Response<HighLeverOrderModel> response) {
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
					swipeRefreshLayout.setRefreshing(false);
				}
			}
		});
	}

}

