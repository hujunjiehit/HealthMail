package com.coinbene.manbiwang.record.miningrecord;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.common.widget.WrapperLinearLayoutManager;
import com.coinbene.manbiwang.model.http.MiningDetailResponse;
import com.coinbene.manbiwang.model.http.MiningSummaryResponse;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.miningrecord.adapter.MiningDetailAdapter;
import com.coinbene.manbiwang.record.miningrecord.adapter.MiningItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-08-06
 * <p>
 * 挖矿明细页面
 */
public class MiningRecordActivity extends CoinbeneBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

	@BindView(R2.id.mining_title)
	View mMiningTitle;
	@BindView(R2.id.mining_record_back)
	ImageView mMiningRecordBack;
	@BindView(R2.id.mining_record_title)
	TextView mMiningRecordTitle;
	@BindView(R2.id.mining_filter)
	CheckBox mMiningFilter;
	@BindView(R2.id.mining_record_recyclerView)
	RecyclerView mMiningRecordRecyclerView;
	@BindView(R2.id.mining_refresh)
	SwipeRefreshLayout mRefreshView;


	private List<MiningItem> list;
	private MiningDetailAdapter mMiningDetailAdapter;

	private int currentPage = 1;
	private final int PAZGE_SIZE = 10;

	MiningItem itemHeader;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, MiningRecordActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_mining_record;
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

	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void listener() {
		mMiningRecordBack.setOnClickListener(v -> finish());

		mRefreshView.setOnRefreshListener(this);
		mRefreshView.setColorSchemeColors(getResources().getColor(R.color.res_blue));
	}

	private void init() {
		list = new ArrayList<>();
		mMiningDetailAdapter = new MiningDetailAdapter(list);
		mMiningRecordRecyclerView.setLayoutManager(new WrapperLinearLayoutManager(this));
		mMiningDetailAdapter.setLoadMoreView(new LoadMoreView());
		mMiningDetailAdapter.bindToRecyclerView(mMiningRecordRecyclerView);


		mMiningDetailAdapter.disableLoadMoreIfNotFullPage();

		mMiningDetailAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
			@Override
			public void onLoadMoreRequested() {
				currentPage++;
				getMiningDetail();
			}
		}, mMiningRecordRecyclerView);

		//允许加载更多
//		mMiningDetailAdapter.enableLoadMoreEndClick(true);
		mMiningDetailAdapter.setEnableLoadMore(true);

		View view = LayoutInflater.from(this).inflate(R.layout.common_base_empty, null);
		mMiningDetailAdapter.setEmptyView(view);

		getMiningSummary();
	}

	/**
	 * 获取挖矿总数据
	 */
	private void getMiningSummary() {
		OkGo.<MiningSummaryResponse>get(Constants.CONTRACT_MINING_SUMMARY).tag(this).execute(new NewJsonSubCallBack<MiningSummaryResponse>() {
			@Override
			public void onSuc(Response<MiningSummaryResponse> response) {
				if (response.body() != null && response.body().getData() != null) {
					if (itemHeader == null) {
						itemHeader = new MiningItem(MiningItem.TYPE_SUMMARY);
					}
					itemHeader.setFrozenAmount(response.body().getData().getFrozenAmount());
					itemHeader.setOrderToken(response.body().getData().getOrderToken());
					itemHeader.setPaidAmount(response.body().getData().getPaidAmount());
					itemHeader.setSortToken(response.body().getData().getSortToken());
					itemHeader.setTokenAmount(response.body().getData().getTokenAmount());
					itemHeader.setTradeDayToken(response.body().getData().getTradeDayToken());
					itemHeader.setTradeMinuteToken(response.body().getData().getTradeMinuteToken());
					itemHeader.setYesterdayTokenAmount(response.body().getData().getYesterdayTokenAmount());
					getMiningDetail();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mRefreshView.isRefreshing()) {
					mRefreshView.setRefreshing(false);
				}
			}

			@Override
			public void onE(Response<MiningSummaryResponse> response) {
			}
		});
	}

	/**
	 * 获取挖矿明细
	 */
	private void getMiningDetail() {
		Map<String, String> parmas = new HashMap<>();
		parmas.put("pageNum", String.valueOf(currentPage));
		parmas.put("pageSize", String.valueOf(PAZGE_SIZE));
		OkGo.<MiningDetailResponse>get(Constants.CONTRACT_MINING_DETAIL).params(parmas).tag(this).execute(new NewJsonSubCallBack<MiningDetailResponse>() {
			@Override
			public void onSuc(Response<MiningDetailResponse> response) {
				if (response.body() != null && response.body().getData() != null && response.body().getData().getList() != null) {
					//遍历分页获取的数据
					List<MiningItem> itemList = new ArrayList<>();
					for (MiningDetailResponse.DataBean.ListBean data : response.body().getData().getList()) {
						MiningItem item = new MiningItem(MiningItem.TYPE_DETAIL);
						item.setFrozenAmount(data.getFrozenAmount());
						item.setOrderToken(data.getOrderToken());
						item.setPaidAmount(data.getPaidAmount());
						item.setSortToken(data.getSortToken());
						item.setTokenAmount(data.getTokenAmount());
						item.setTradeDayToken(data.getTradeDayToken());
						item.setTradeMinuteToken(data.getTradeMinuteToken());
						item.setDate(data.getDate());
						itemList.add(item);
					}

					if (currentPage == 1) {
						itemList.add(0, itemHeader);
						mMiningDetailAdapter.setNewData(itemList);
					} else {
						mMiningDetailAdapter.addData(itemList);
					}

					if (response.body().getData().getList().size() < PAZGE_SIZE) {
						//没有更多数据了
						if (currentPage == 1 && itemList.size() == 1) {
							//没有挖矿记录
							itemHeader.setShowMiningRecords(false);
							mMiningDetailAdapter.loadMoreEnd(true);
						} else {
							itemHeader.setShowMiningRecords(true);
							mMiningDetailAdapter.loadMoreEnd();
						}
					} else {
						//还有数据，继续请求
						itemHeader.setShowMiningRecords(true);
						mMiningDetailAdapter.loadMoreComplete();
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mRefreshView.isRefreshing()) {
					mRefreshView.setRefreshing(false);
				}
			}

			@Override
			public void onE(Response<MiningDetailResponse> response) {
			}
		});
	}


	@Override
	public void onRefresh() {
		currentPage = 1;

		getMiningSummary();
	}
}
