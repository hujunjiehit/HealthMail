package com.coinbene.manbiwang.spot.otc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.OrderListModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.wrapper.EndlessRecyclerOnScrollListener;
import com.coinbene.common.widget.wrapper.LoadMoreWrapper;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.otc.adapter.OtcOrderListBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

public class ShopsOrderStatuFragment extends BaseFragment {
    private RecyclerView rl_list_order;
    private LinearLayout empty_layout;
    private SwipeRefreshLayout swipe_refresh_layout;
    private boolean isEnd = false;
    private int current_page = 1;
    private int pageSize = 10;
    private OtcOrderListBinder binder;
    private LoadMoreWrapper loadMoreWrapper;
    private int orderType = 1;
    private int orderStutas;//1：买入 2：卖出 3：供应商作为普通用户

    public static ShopsOrderStatuFragment newInstance(int orderType, int orderStutas) {
        Bundle args = new Bundle();
        args.putInt("orderType", orderType);
        args.putInt("orderStutas", orderStutas);
        ShopsOrderStatuFragment fragment = new ShopsOrderStatuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        empty_layout = view.findViewById(R.id.empty_layout);
        rl_list_order = view.findViewById(R.id.rl_list_order);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        initData();
    }

    private void initAdapter() {
        binder = new OtcOrderListBinder();
        rl_list_order.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadMoreWrapper = new LoadMoreWrapper(binder);
        rl_list_order.setAdapter(loadMoreWrapper);
        binder.setItemClickListener(itemClickListener);
        swipe_refresh_layout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
    }

    private OtcOrderListBinder.ItemClickListener itemClickListener = adID -> OtcOrderDetailActivity.startMe(getActivity(), adID, orderType);

    private void initData() {

        IntentFilter filterIntent = new IntentFilter();
        filterIntent.addAction(Constants.ORDER_LSIT_REFRESH);
        LocalBroadcastManager.getInstance(CBRepository.getContext()).registerReceiver(broadcastReceiver, filterIntent);
        Bundle bundle = this.getArguments();
        orderType = bundle.getInt("orderType", 0);
        orderStutas = bundle.getInt("orderStutas", 0);
        swipe_refresh_layout.setOnRefreshListener(() -> refreshData());
        rl_list_order.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isEnd) {
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);
                    loadMoreData();
                }
            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constants.ORDER_LSIT_REFRESH) {
                if (orderType == intent.getIntExtra("orderType", 1)) {
                    refreshData();
                }
            }
        }
    };

    private void loadMoreData() {
        current_page++;
        getOrderList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderList();
    }

    private void refreshData() {
        current_page = 1;
        getOrderList();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private int getLayoutId() {
        return R.layout.spot_fragment_order_statu;
    }


    private void getOrderList() {
        HttpParams params = new HttpParams();
        params.put("type", orderType);
        params.put("status", orderStutas);
        params.put("currentPage", current_page);
        OkGo.<OrderListModel>post(Constants.OTC_GET_SHOPS_ORDER_List).params(params).tag(this).execute(new NewJsonSubCallBack<OrderListModel>() {
            @Override
            public void onSuc(Response<OrderListModel> response) {
                setRefreshFalse();
                if (getActivity() != null && getActivity().isDestroyed()) {
                    return;
                }
                if (ShopsOrderStatuFragment.this.isDetached()) {
                    return;
                }
                OrderListModel t = response.body();
                if (t != null && t.getData() != null && t.getData().getResult() != null && t.getData().getResult().size() > 0) {
                    empty_layout.setVisibility(View.GONE);
                    rl_list_order.setVisibility(View.VISIBLE);
                    List<OrderListModel.DataBean.ResultBean> list = t.getData().getResult();
                    if (list.size() < 10) {
                        isEnd = true;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    } else {
                        isEnd = false;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                    }
                    if (current_page > 1) {
                        binder.appendItems(t.getData().getResult());
                    } else {
                        binder.setItems(t.getData().getResult());
                    }
                    loadMoreWrapper.notifyDataSetChanged();
                } else {
                    binder.destroy();
                    if (current_page == 1) {
                        empty_layout.setVisibility(View.VISIBLE);
                        rl_list_order.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onE(Response<OrderListModel> response) {
                binder.destroy();
                setRefreshFalse();
                if (current_page == 1) {
                    empty_layout.setVisibility(View.VISIBLE);
                    rl_list_order.setVisibility(View.GONE);
                } else {
                    current_page--;
                }
            }
        });
    }

    private void setRefreshFalse() {
        if (swipe_refresh_layout != null && swipe_refresh_layout.isRefreshing()) {
            swipe_refresh_layout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(this);
        binder.destroy();
        LocalBroadcastManager.getInstance(CBRepository.getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
