

package com.coinbene.manbiwang.record.orderrecord.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.wrapper.LoadMoreListener;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.CurrentEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.adapter.CurEntrustAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.coinbene.common.base.BaseAdapter.LOADING;
import static com.coinbene.common.base.BaseAdapter.LOADING_END;


/**
 * 当前的委托记录页面
 */
public class CurrentEntrustFragment extends BaseFragment {
	// 数据
	@BindView(R2.id.rl_category)
	RecyclerView rlvCurEntrust;
	@BindView(R2.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;


	private Unbinder unbinder;
	private CurEntrustAdapter adapter;

	private String accountType;

	// 数据
	public static CurrentEntrustFragment newInstance() {
		Bundle args = new Bundle();
		CurrentEntrustFragment fragment = new CurrentEntrustFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
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
		accountType = getArguments().getString("accountType");


		initView();

		initLisenter();
		initData();
	}

	private void initLisenter() {
		if (null != getActivity()) {
			CurrentEntrustRecordActivity activity = (CurrentEntrustRecordActivity) getActivity();
			activity.getRightMenuTv().setOnClickListener(v -> {
				if (adapter != null && adapter.getList() != null && adapter.getList().size() > 0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setMessage(String.format(getString(R.string.sure_cancel_all), ""));
					dialog.setPositiveButton(getString(R.string.btn_ok), (dialog12, which) -> {
						cancelEntrustAllData();
						dialog12.dismiss();
					});
					dialog.setNegativeButton(v.getContext().getString(R.string.btn_cancel), (dialog16, which) -> dialog16.dismiss());
					dialog.show();
				}
			});
		}
	}

	public void cancelEntrustAllData() {
		HttpParams params = new HttpParams();
		params.put("accountType", accountType);
		OkGo.<BaseRes>post(Constants.TRADE_ORDER_CANCEL_ALL).params(params).tag(this).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.cancel_success);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getRefreshData();
					}
				}, 1000);

			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	private void initData() {
		adapter = new CurEntrustAdapter();
		adapter.setCancelOrderListener(orderId -> doCancelOrder(orderId));
		rlvCurEntrust.setLayoutManager(new LinearLayoutManager(getActivity()));
		rlvCurEntrust.setAdapter(adapter);
		swipeRefreshLayout.post(() -> {
			swipeRefreshLayout.setRefreshing(true);
			getRefreshData();
		});
		rlvCurEntrust.addOnScrollListener(new LoadMoreListener() {
			@Override
			public void loadMore() {
				page++;
				getCurrentEntrustData(page, pageSize);
			}
		});
	}

	private void initView() {
		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
		swipeRefreshLayout.setOnRefreshListener(() -> getRefreshData());
	}

	private int page = 1;
	private int pageSize = 10;

	private void getRefreshData() {
		page = 1;
		getCurrentEntrustData(page, pageSize);
	}

	public void getCurrentEntrustData(int page, int pageSize) {
		HttpParams params = new HttpParams();
		params.put("pageNum", page);
		params.put("pageSize", pageSize);
		if (!TextUtils.isEmpty(accountType)) {
			params.put("accountType", accountType);
		}


		OkGo.<CurOrderListModel>get(Constants.USER_CURORDER_LIST).params(params).tag(this).execute(new NewJsonSubCallBack<CurOrderListModel>() {

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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		OkGo.getInstance().cancelTag(this);
		if (unbinder == null) return;
		unbinder.unbind();

	}


	public void entrustDataResponse(CurOrderListModel entrustData) {
		if (getActivity() == null)
			return;
		if (getActivity() != null && getActivity().isDestroyed()) {
			return;
		}
		if (entrustData == null) {
			return;
		}
		if (entrustData.getData() == null && page == 1) {
			adapter.setItem(null);
			adapter.setState(LOADING_END);
			return;
		}


		if (page == 1) {
			adapter.setItem(entrustData.getData().list);
		} else {
			adapter.appendItem(entrustData.getData().list);
		}

		if (entrustData.getData().list == null || entrustData.getData().list.size() < pageSize) {
			adapter.setState(LOADING_END);
		} else {
			adapter.setState(LOADING);
		}
	}

	public void doCancelOrder(String orderId) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("orderId", orderId);
		OkGo.<BaseRes>post(Constants.TRADE_ORDER_CANCEL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes t = response.body();
				if (t.isSuccess()) {
					ToastUtil.show(R.string.cancel_entrust_success);
					adapter.removeTradeById(orderId);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});

	}
}
