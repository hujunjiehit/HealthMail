package com.coinbene.manbiwang.record.orderrecord.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.wrapper.LoadMoreListener;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.adapter.HisEntrustAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.coinbene.common.base.BaseAdapter.LOADING;
import static com.coinbene.common.base.BaseAdapter.LOADING_END;


/**
 * 历史委托页面
 *
 * @author huyong
 */
public class HistoryEntrustFragment extends BaseFragment {
	@BindView(R2.id.rl_category)
	RecyclerView rlvHistory;
	@BindView(R2.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;
	@BindView(R2.id.tv_cancel_all_order)
	TextView tvCancelAllOrder;
	private Unbinder unbinder;
	private HisEntrustAdapter hisAdapter;

	private String inputBaseAsset = "", quoteAsset = "", beginTime = "", endTime = "";
	private int direction = 0;
	private boolean ignoreCancelled = false;
	private String accountType;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@LayoutRes
	protected int getLayoutId() {
		return R.layout.record_trade_layout;
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(getLayoutId(), container, false);
		unbinder = ButterKnife.bind(this, root);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();

		initData();


	}

	private void initData() {
		accountType = getArguments().getString("accountType");
		hisAdapter = new HisEntrustAdapter();
		rlvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		rlvHistory.setAdapter(hisAdapter);
		getRefreshData();
	}

	private void initView() {
		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
		swipeRefreshLayout.setOnRefreshListener(() -> {
			//如果是暂时没有记录的时候，不再去请求
			if (isValideFlag) {
				ToastUtil.show(R.string.invalid_input_baseAsset);
				swipeRefreshLayout.setRefreshing(false);
			} else {
				getRefreshData();
			}
		});
		rlvHistory.addOnScrollListener(new LoadMoreListener() {
			@Override
			public void loadMore() {
				page++;
				loadMoreData();
			}
		});
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
	public void onDestroyView() {
		super.onDestroyView();
		if (unbinder == null) {
			return;
		}
		unbinder.unbind();

	}

	public void entrustDataResponse(CurOrderListModel entrustData) {

		if (getActivity() == null) {
			return;
		}
		if (getActivity() != null && getActivity().isDestroyed()) {
			return;
		}
		if (entrustData == null || entrustData.data == null) {

			return;
		}
		if (page == 1) {
			hisAdapter.setItem(entrustData.getData().list);
		} else {
			hisAdapter.appendItem(entrustData.getData().list);
		}

		if (entrustData.getData().list == null || entrustData.getData().list.size() < pageSize) {
			hisAdapter.setState(LOADING_END);
		} else {
			hisAdapter.setState(LOADING);
		}
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
		isValideFlag = false;
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
		if (page == 1) {
			swipeRefreshLayout.setRefreshing(true);
		}

		if (TextUtils.isEmpty(beginTime)) {
			endTime = "";
		} else {
			endTime = "" + System.currentTimeMillis();
		}
		HttpParams httpParams = new HttpParams();
		if (!TextUtils.isEmpty(accountType))
			httpParams.put("accountType", accountType);
		if (!TextUtils.isEmpty(inputBaseAsset)) {
			inputBaseAsset = inputBaseAsset.trim();
			httpParams.put("baseAsset", inputBaseAsset.toUpperCase());
		}
		if (!TextUtils.isEmpty(quoteAsset)) {
			httpParams.put("quoteAsset", quoteAsset.toUpperCase());
		}
		if (!TextUtils.isEmpty(beginTime)) {
			httpParams.put("beginTime", beginTime);
			httpParams.put("endTime", endTime);
		}
		if (direction != 0) {
			httpParams.put("orderDirection", direction);
		}
		if (ignoreCancelled) {
			httpParams.put("ignoreCancelled", ignoreCancelled);
		}

		httpParams.put("pageNum", page);
		httpParams.put("pageSize", pageSize);
		OkGo.<CurOrderListModel>get(Constants.TRADE_HISORDER_LIST).params(httpParams).tag(this).execute(new NewJsonSubCallBack<CurOrderListModel>() {

			@Override
			public void onSuc(Response<CurOrderListModel> response) {

				entrustDataResponse(response.body());
			}

			@Override
			public void onE(Response<CurOrderListModel> response) {
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

	/**
	 * 暂无记录
	 */
	private boolean isValideFlag;

	public void invalidTradepair() {
		isValideFlag = true;
		page = 1;
		entrustDataResponse(null);
	}
}

